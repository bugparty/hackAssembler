package com.ifancc.jack_parser

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.RuntimeException

fun isValidLine(str: String): Boolean {
    if (str.startsWith("//")) return false
    if (str.isEmpty()) return false
    return true
}

fun isValidJackChar(char: Char): Boolean {
    return when (char) {
        in "@()=;_-+|&!" -> true
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

val jumpTable = listOf(Triple("JGT", CJump.JGT, 1),
        Triple("JEQ", CJump.JEQ, 2), Triple("JGE", CJump.JGE, 3),
        Triple("JLT", CJump.JLT, 4), Triple("JNE", CJump.JNE, 5),
        Triple("JLE", CJump.JLE, 6), Triple("JMP", CJump.JMP, 7))
val destTable = listOf(Triple("M", CDest.M, 1.shl(3)), Triple("D", CDest.D, 2.shl(3)),
        Triple("MD", CDest.MD, 3.shl(3)), Triple("A", CDest.A, 4.shl(3)),
        Triple("AM", CDest.AM, 5.shl(3)), Triple("AD", CDest.AD, 6.shl(3)),
        Triple("AMD", CDest.AMD, 7.shl(3)))

val compTable = listOf(Triple("0", CComp._0, (42.shl(6))), Triple("1", CComp._1, 63.shl(6)),
        Triple("-1", CComp._neg1, 58.shl(6)), Triple("D", CComp.D, 12.shl(6)),
        Triple("A", CComp.A, 48.shl(6)), Triple("M", CComp.M, 112.shl(6)),
        Triple("!D", CComp.notD, 13.shl(6)), Triple("!A", CComp.notA, 49.shl(6)),
        Triple("!M", CComp.notM, 113.shl(6)), Triple("-D", CComp.negD, 15.shl(6)),
        Triple("-A", CComp.negA, 51.shl(6)), Triple("-M", CComp.negM, 115.shl(6)),
        Triple("D+1", CComp.Dplus1, 31.shl(6)), Triple("A+1", CComp.Aplus1, 55.shl(6)),
        Triple("M+1", CComp.Mplus1, 119.shl(6)), Triple("D-1", CComp.Dminus1, 14.shl(6)),
        Triple("A-1", CComp.Aminus1, 50.shl(6)), Triple("M-1", CComp.Mminus1, 114.shl(6)),
        Triple("D+A", CComp.DplusA, 2.shl(6)), Triple("D+M", CComp.DminusM, 66.shl(6)),
        Triple("D-A", CComp.DminusA, 19.shl(6)), Triple("D-M", CComp.DminusM, 83.shl(6)),
        Triple("A-D", CComp.AminusD, 7.shl(6)), Triple("M-D", CComp.MminusD, 71.shl(6)),
        Triple("D&A", CComp.DandA, 0), Triple("D&M", CComp.DandM, 64.shl(6)),
        Triple("D|A", CComp.DandA, 21.shl(6)), Triple("D|M", CComp.DandM, 85.shl(6))
)

fun parseJump(line: String, node: CNode) {
    for (predefined in jumpTable) {
        if (line.equals(predefined.first)) {
            node.jump = predefined.second
            break
        }
    }
}

fun parseComp(line: String, node: CNode) {
    for (predefined in compTable) {
        if (predefined.first.equals(line)) {
            node.comp = predefined.second
            break
        }
    }
}

fun parseDest(line: String, node: CNode) {
    for (predefined in destTable) {
        if (predefined.first.equals(line)) {
            node.dest = predefined.second
            break
        }
    }
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
    class BadAstException(rawLine: String, reason: String) : RuntimeException()

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
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let {
                    val striped = it.trim()
                    if (isValidLine(striped)) {
                        lines.add(removeInlineComment(striped))
                    }
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
                    val right = line.lastIndexOf(')')
                    val symbol = line.substring(1, right).trim()
                    symbols.insert(symbol, lineNumber + 1)
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