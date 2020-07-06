package com.ifancc.jack_parser

enum class Type {
    A, C, None
}

enum class AType {
    DIRECT_NUMBER,
    SYMBOL
}

enum class CDest {
    M, D, MD, A, AM, AD, AMD
}

enum class CComp {
    _1, _0, _neg1, D, A, notD, notA, negD, negA, Dplus1, Aplus1, Dminus1, Aminus1, DplusA, DminusA, DandA, DorA, M, notM, negM, Mplus1,
    Mminus1, DplusM, DminusM, MminusD, DandM, DorM, AminusD, AminusM
}

enum class CJump {
    JGT, JEQ, JGE, JLT, JNE, JLE, JMP
}

open class BaseASTNode(val type: Type, next: BaseASTNode? = null) {
    var next = next
}

class ANode(type: Type = Type.A, subType: AType, next: BaseASTNode? = null, symbol: String? = null, directValue: Int? = null)
    : BaseASTNode(type, next) {
    var symbol = symbol
    var directValue = directValue
    var subType = subType
}

class CNode(type: Type = Type.C, next: BaseASTNode? = null, comp: CComp? = null, dest: CDest? = null, jump: CJump? = null)
    : BaseASTNode(type, next) {
    var comp = comp
    var dest = dest
    var jump = jump
    override fun equals(other: Any?): Boolean {
        if(other is CNode){
            return other.comp == comp && other.dest == dest && other.jump == jump
        }else{
            return false
        }
    }

    override fun toString(): String {
        return super.toString() + "comp:$comp,dest:$dest,jump:$jump"
    }
}
