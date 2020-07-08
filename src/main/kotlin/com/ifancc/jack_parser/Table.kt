package com.ifancc.jack_parser

val CompTable = listOf(Triple("0", CComp._0, 42 shl 6), Triple("1", CComp._1, 63 shl 6),
        Triple("-1", CComp._neg1, 58 shl 6), Triple("D", CComp.D, 12 shl 6),
        Triple("A", CComp.A, 48 shl 6), Triple("M", CComp.M, 112 shl 6),
        Triple("!D", CComp.notD, 13 shl 6), Triple("!A", CComp.notA, 49 shl 6),
        Triple("!M", CComp.notM, 113 shl 6), Triple("-D", CComp.negD, 15 shl 6),
        Triple("-A", CComp.negA, 51 shl 6), Triple("-M", CComp.negM, 115 shl 6),
        Triple("D+1", CComp.Dplus1, 31 shl 6), Triple("A+1", CComp.Aplus1, 55 shl 6),
        Triple("M+1", CComp.Mplus1, 119 shl 6), Triple("D-1", CComp.Dminus1, 14 shl 6),
        Triple("A-1", CComp.Aminus1, 50 shl 6), Triple("M-1", CComp.Mminus1, 114 shl 6),
        Triple("D+A", CComp.DplusA, 0b0000010 shl 6), Triple("D+M", CComp.DplusM, 0b1000010 shl 6),
        Triple("D-A", CComp.DminusA, 19 shl 6), Triple("D-M", CComp.DminusM, 83 shl 6),
        Triple("A-D", CComp.AminusD, 7 shl 6), Triple("M-D", CComp.MminusD, 71 shl 6),
        Triple("D&A", CComp.DandA, 0), Triple("D&M", CComp.DandM, 64 shl 6),
        Triple("D|A", CComp.DorA, 21 shl 6), Triple("D|M", CComp.DorM, 85 shl 6)
)

val JumpTable = listOf(Triple("JGT", CJump.JGT, 1),
        Triple("JEQ", CJump.JEQ, 2), Triple("JGE", CJump.JGE, 3),
        Triple("JLT", CJump.JLT, 4), Triple("JNE", CJump.JNE, 5),
        Triple("JLE", CJump.JLE, 6), Triple("JMP", CJump.JMP, 7))

val DestTable = listOf(Triple("M", CDest.M, 1 shl 3), Triple("D", CDest.D, 2 shl 3),
        Triple("MD", CDest.MD, 3 shl 3), Triple("A", CDest.A, 4 shl 3),
        Triple("AM", CDest.AM, 5 shl 3), Triple("AD", CDest.AD, 6 shl 3),
        Triple("AMD", CDest.AMD, 7 shl 3))

typealias CCompTriple = Triple<String, CComp, Int>
typealias CJumpTriple = Triple<String, CJump, Int>
typealias CDestTriple = Triple<String, CDest, Int>

object EnumToObjTable {
    var compTable: HashMap<CComp, CCompTriple> = HashMap()
    var jumpTable: HashMap<CJump, CJumpTriple> = HashMap()
    var destTable: HashMap<CDest, CDestTriple> = HashMap()

    init {
        for (item in CompTable) {
            compTable[item.second] = item
        }
        for (item in JumpTable) {
            jumpTable[item.second] = item
        }
        for (item in DestTable) {
            destTable[item.second] = item
        }
    }
}

object StrToObjTable {
    var compTable: HashMap<String, CCompTriple> = HashMap()
    var jumpTable: HashMap<String, CJumpTriple> = HashMap()
    var destTable: HashMap<String, CDestTriple> = HashMap()

    init {
        for (item in CompTable) {
            compTable[item.first] = item
        }
        for (item in JumpTable) {
            jumpTable[item.first] = item
        }
        for (item in DestTable) {
            destTable[item.first] = item
        }
    }
}