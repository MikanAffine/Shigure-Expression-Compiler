package org.owari.shigure.test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.owari.shigure.parse.SParser

import org.owari.shigure.tokenize.STokenizer

object Tests {
    @Test
    @DisplayName("Test - Tokenizer")
    fun testTokenizer() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = STokenizer.tokenize(str)
        println(tokens.toList())
        // println(tokens.joinToString(" ", transform = SToken::text))
    }

    @Test
    @DisplayName("Test - Parser")
    fun testParser() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokenStream = STokenizer.tokenize(str)
        println(tokenStream.toList())
        val tree = SParser.parse(tokenStream)
        println(tree)
        // println(tokens.joinToString(" ", transform = SToken::text))
    }
}
