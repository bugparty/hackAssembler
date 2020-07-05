package com.ifancc.jack_parser

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

fun isValidLine(str: String): Boolean {
    if (str.startsWith("//")) return false
    if (str.isEmpty()) return false
    return true
}

fun isValidJackChar(char: Char):Boolean{
    return when(char){
        in "@()=;_-+|&!" -> true
        in "0123456789" -> true
        in "abcdefghijklmnopqrstuvwsyz"-> true
        in "ABCDEFGHIJKLMNOPQRSTUVWSYZ" -> true
        else -> false
    }
}

fun removeInlineComment(str: String):String{
    var i =0
    while(i < str.length){
        if(isValidJackChar(str[i])){
            i++
        }else{
            break
        }
    }
    if (i!=str.length && isValidJackChar(str[i])){
        i++
    }
    return str.substring(0,i)
}

class Parser {
    val lines = ArrayList<String>()
    var index = 0
    fun read(filename: String) {
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
        }
    }
}