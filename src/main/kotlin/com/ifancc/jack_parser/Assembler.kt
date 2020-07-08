package com.ifancc.jack_parser

import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.RuntimeException

class BadAstException(reason: String, data: BaseASTNode?) : RuntimeException()

const val MSB = 1.shl(15)

fun translateA(node: ANode): Int {
    val directVal = node.directValue
    if (directVal is Int) {
        if ((directVal and MSB) > 0) {
            throw BadAstException("instruction direct number overflow", node)
        }
        return directVal
    } else {
        throw BadAstException("empty direct value", node)
    }
}

const val C_PREFIX = 7.shl(13)

fun translateC(node: CNode): Int {
    val comp = if (node.comp != null) EnumToObjTable.compTable[node!!.comp]!!.third else 0
    val dest = if (node.dest != null) EnumToObjTable.destTable[node!!.dest]!!.third else 0
    val jump = if (node.jump != null) EnumToObjTable.jumpTable[node!!.jump]!!.third else 0
    return C_PREFIX or comp or dest or jump
}

fun intToBinaryStr(bin: Int): String {
    val zeros = Integer.numberOfLeadingZeros(bin)
    return when(zeros){
        32 -> "0".repeat(16)
        else ->  "0".repeat(zeros - 16) + Integer.toBinaryString(bin)
    }
}

fun translate(node: BaseASTNode): Int {
    return when (node) {
        is ANode -> translateA(node)
        is CNode -> translateC(node)
        else -> throw BadAstException("unexpected node type", node)
    }
}

class Assembler {
    fun write(filepath: String, nodeRoot: BaseASTNode, writeBinary: Boolean = false) {
        if (writeBinary) {
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(filepath)
                var curr = nodeRoot.next
                while (curr != null) {
                    fileOutputStream.write(translate(curr))
                    curr = curr.next
                }
            } finally {
                fileOutputStream?.close()
            }
        } else {
            var printStream: PrintStream? = null
            try {
                printStream = PrintStream(FileOutputStream(filepath))
                var curr = nodeRoot.next
                while (curr != null) {
                    val bin = translate(curr)
                    val line = intToBinaryStr(bin)
                    printStream.println(line)
                    curr = curr.next
                }
            } finally {
                printStream?.close()
            }
        }

    }
}