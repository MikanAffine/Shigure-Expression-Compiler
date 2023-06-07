package org.owari.shigure.test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

object Benchmarks {
    @Test
    @DisplayName("Benchmark - FullCompile")
    fun fullCompile() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - DirectEval")
    fun directEval() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - CompiledEval")
    fun compiledEval() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - Tokenizer")
    fun tokenizer() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - Parser")
    fun parser() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - ASTInterpreter")
    fun astInterpreter() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - CodeGenerator")
    fun codeGenerator() {
        val start = System.currentTimeMillis()

        val end = System.currentTimeMillis()
        println("Elapsed time: ${end - start}ms")
    }
}