package com.ifancc.jack_parser

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.RuntimeException

class BadAstParseException(rawLine: String, reason: String, lineno: Int?=null) : RuntimeException(){
    val rawLine = rawLine
    val reason = reason
    val lineno = lineno
    override fun toString(): String {
        return super.toString()+" lineno:${lineno ?:"unknown"} rawLine:$rawLine,reason:$reason"
    }
}

fun isValidLine(str: String): Boolean {
    if (str.startsWith("//")) return false
    if (str.isEmpty()) return false
    return true
}

fun isValidJackChar(char: Char): Boolean {
    return when (char) {
        in "@()=;_-+|&!." -> true
        in "0123456789" -> true
        in "abcdefghijklmnopqrstuvwsyz" -> true
        in "ABCDEFGHIJKLMNOPQRSTUVWSYZ" -> true
        else -> false
    }
}

fun removeInlineComment(str: String): String {
    var i = 0
    while (i < str.length) {
        if (isValidJackChar(str[i])) {
            i++
        } else {
            break
        }
    }
    if (i != str.length && isValidJackChar(str[i])) {
        i++
    }
    return str.substring(0, i)
}

fun parseJump(line: String, node: CNode) {
    node.jump = StrToObjTable.jumpTable[line]?.second ?: throw BadAstParseException(line, "instruction not found")
}

fun parseComp(line: String, node: CNode) {
    node.comp = StrToObjTable.compTable[line]?.second ?: throw BadAstParseException(line, "instruction not found")
}

fun parseDest(line: String, node: CNode) {
    node.dest = StrToObjTable.destTable[line]?.second ?: throw BadAstParseException(line, "instruction not found")
}

/*
only parse single C instruction such as "JGT" or "A=D-1"
 */
fun parseCinstruction(line: String, node:CNode){
    when (line[0]) {
        'J' -> {
            //jump command
            parseJump(line, node)
        }
        else -> {
            val equalIdx = line.indexOf('=')
            if (equalIdx == -1) {
                //only comp
                parseComp(line, node)
            } else {
                val destLine = line.substring(0, equalIdx)
                val compLine = line.substring(equalIdx + 1)
                parseDest(destLine, node)
                parseComp(compLine, node)
            }
        }
    }
}

class Parser {

    val lines = ArrayList<String>()
    val astRoot = BaseASTNode(Type.None, null)
    val symbols = SymbolTable()
    var curAstNode = astRoot
    var index = 0
    fun open(filename: String) {
        readAndPreprocess(filename)
        parseSymbols()
        parseAST()
    }

    private fun readAndPreprocess(filename: String) {
        lines.clear()
        index = 0
        try {
            val file = File(filename)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            var line: String?
            var lineCount = 0
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let {
                    val striped = it.trim()
                    if (isValidLine(striped)) {
                        lines.add(removeInlineComment(striped))
                    }else{
                        //throw BadAstParseException(it, "preprocess error", lineno = lineCount)
                    }
                    lineCount++
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }

    /*
    only process label
     */
    private fun parseSymbols() {
        var i = 0
        var lineNumber = 0
        for ((index, line) in lines.withIndex()) {
            when (line[0]) {
                '(' -> {
                    try{
                        val right = line.lastIndexOf(')')
                        val symbol = line.substring(1, right).trim()
                        symbols.insert(symbol, lineNumber)
                    }catch (e: StringIndexOutOfBoundsException){
                        throw BadAstParseException(line, "failed to match parenthesis", lineno=index)
                    }

                }
                else -> {
                    lineNumber++
                }
            }
        }
    }

    private fun parseAST() {
        var varOffset = 16
        var i = 0
        var lineNumber = 0
        while (i < lines.size) {
            val line = lines[i]
            when (line[0]) {
                '@' -> {
                    if (line[1].isDigit()) {
                        val directNumber = line.substring(1).toInt()
                        val node = ANode(Type.A, AType.DIRECT_NUMBER, next = null, directValue = directNumber)
                        curAstNode.next = node
                        curAstNode = curAstNode.next as ANode

                    } else {
                        //Symbol like
                        val symbol = line.substring(1).trim()
                        val node = ANode(Type.A, AType.SYMBOL, next = null, symbol = symbol)
                        var symbolVal: Int?
                        if (symbols.getOrNull(symbol).also { symbolVal = it } != null) {
                            node.directValue = symbolVal
                        } else {
                            //this is a symbol var
                            symbols.insert(symbol, varOffset)
                            node.directValue = varOffset++
                        }
                        curAstNode.next = node
                        curAstNode = curAstNode.next as ANode
                    }
                    i++
                }
                '(' -> {
                    i++
                    //skip
                }
                else -> {
                    //C instruction
                    val semicolon = line.indexOf(';')
                    val node = CNode()
                    if (semicolon == -1) {
                        parseCinstruction(line, node)
                    } else {
                        val line1 = line.substring(0, semicolon)
                        val line2 = line.substring(semicolon + 1)
                        parseCinstruction(line1, node)
                        parseCinstruction(line2, node)
                    }
                    curAstNode.next = node
                    curAstNode = curAstNode.next as CNode
                    i++
                }
            }
        }
    }

}