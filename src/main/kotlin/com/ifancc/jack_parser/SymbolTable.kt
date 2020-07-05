package com.ifancc.jack_parser

class SymbolTable() {
    var symbols = HashMap<String, Int>()

    init {
        //add r0-r15
        for(i in 0 until 16){
            insert("R$i",i)
        }
        insert("SCREEN",16384)
        insert("KBD", 24576)
        insert("SP", 0)
        insert("LCL", 1)
        insert("ARG", 2)
        insert("THIS", 3)
        insert("THAT", 4)
    }
    fun insert(name:String, value:Int){
        symbols.put(name, value)
    }

}