package com.ifancc.jack_parser


fun main(args: Array<String>) {
    val parser = Parser()
    parser.open("data\\rect\\RectL.asm")
    val assembler = Assembler()
    assembler.write("data\\rect\\RectL.hack", parser.astRoot)
    println("Hello World!")
}