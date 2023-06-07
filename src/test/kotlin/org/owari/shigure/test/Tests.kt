package org.owari.shigure.test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.owari.shigure.codegen.SCodeGenerator
import org.owari.shigure.parse.SParser

import org.owari.shigure.tokenize.STokenizer

object Tests {
    @Test
    @DisplayName("Test - Tokenizer")
    fun testTokenizer() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = STokenizer(str).result
        println(tokens.toList())
        // println(tokens.joinToString(" ", transform = SToken::text))
    }

    @Test
    @DisplayName("Test - Parser")
    fun testParser() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = STokenizer(str).result
        println(tokens.toList())
        val tree = SParser(tokens).result
        println(tree)
    }

    @Test
    @DisplayName("Test - CodeGenerator")
    fun testCodeGenerator() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = STokenizer(str).result
        println(tokens.toList())
        val tree = SParser(tokens).result
        println(tree)
        val impl = SCodeGenerator(tree).result
        // println(tokens.joinToString(" ", transform = SToken::text))
    }
}
