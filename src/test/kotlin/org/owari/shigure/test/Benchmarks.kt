package org.owari.shigure.test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.owari.shigure.Context
import org.owari.shigure.Shigure
import org.owari.shigure.impl.*
import kotlin.contracts.*
import kotlin.math.*

object Benchmarks {
    @OptIn(ExperimentalContracts::class)
    private inline fun timeTrack(prefix:String, block: () -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val start = System.currentTimeMillis()
        block()
        val end = System.currentTimeMillis()
        println("[$prefix] Elapsed time: ${end - start} ms")
    }

    private const val simpleExpr = "1 * 3 + 4 ^ 2"
    private const val complexExpr = "1 + a * 3.0 ^ 中文变量 // 32 - log2(c6\$_) % ハルカ"
    private val ctx = Context.of(
        "a" to 2.0,
        "中文变量" to 3.0,
        "c6\$_" to 16.0,
        "ハルカ" to 7.0,
    )

    @Test
    @DisplayName("Baseline - Java Eval")
    fun javaEval() {
        val a = 2.0
        val b = 3.0
        val c = 16.0
        val d = 7.0
        repeat(100_000_000) {
            1.0 + floor(a * 3.0.pow(b) / 32) - log2(c) % d
        }


        timeTrack("(Baseline) Java Eval 100M") {
            repeat(100_000_000) {
                1.0 + floor(a * 3.0.pow(b) / 32) - log2(c) % d
            }
        }
    }

    @Test
    @DisplayName("Benchmark - CompiledEval 100M")
    fun compiledEval() {
        val expr = Shigure.createExpr(complexExpr, true)
        /*
        val os = Parser(Tokenizer(complexExpr)).parse()
        val ass = Assembler()
        val n = ass.newClassName()
        val cg = CodeGenerator(os, n)
        val expr = ass.assemble(n, cg.generate()).newInstance() as CalcFunc

         */
        repeat(100_000_000) {
            expr.invoke(ctx)
        }

        timeTrack("Compiled Eval 100M") {
            repeat(100_000_000) {
                expr.invoke(ctx)
            }
        }
    }

    @Test
    @DisplayName("Benchmark - InterpretedEval 100M")
    fun interpretedEval() {
        val code = Parser(Tokenizer(complexExpr)).parse()
        repeat(100_000_000) {
            Interpreter.eval(code, ctx)
        }

        timeTrack("Interpreted Eval 100M") {
            repeat(100_000_000) {
                Interpreter.eval(code, ctx)
            }
        }
    }

    @Test
    @DisplayName("Benchmark - MixedEval 100M")
    fun mixedEval() {
        val expr = Shigure.createExpr(complexExpr)
        repeat(100_000_000) {
            expr.invoke(ctx)
        }

        timeTrack("Mixed Eval 100M") {
            repeat(100_000_000) {
                expr.invoke(ctx)
            }
        }
    }

    @Test
    @DisplayName("Benchmark - Tokenize + Parse 1M")
    fun tokenizeAndParse() {
        val tkz = Tokenizer(complexExpr)
        val parser = Parser(tkz)
        repeat(1_000_000) {
            parser.parse()
        }
        timeTrack("Tokenize + Parse 1M") {
            repeat(1_000_000) {
                parser.parse()
            }
        }
    }

    @Test
    @DisplayName("Benchmark - CodeGenerate 1M")
    fun codegen() {
        val code = Parser(Tokenizer(complexExpr)).parse()
        val a = Assembler()
        repeat(1_000_000) {
            val cg = CodeGenerator(code, a.newClassName()).generate()
        }
        timeTrack("CodeGenerate 1M") {
            repeat(1_000_000) {
                val cg = CodeGenerator(code, a.newClassName()).generate()
            }
        }
    }
}
