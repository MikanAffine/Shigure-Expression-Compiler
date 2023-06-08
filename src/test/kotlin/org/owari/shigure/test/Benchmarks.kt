package org.owari.shigure.test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.owari.shigure.SContext
import org.owari.shigure.Shigure
import org.owari.shigure.codegen.SCodeGenerator
import org.owari.shigure.parse.SParser
import org.owari.shigure.runtime.SASTEvaluator
import org.owari.shigure.tokenize.STokenizer
import kotlin.math.*

object Benchmarks {
    private const val simpleExpr = "1 * 3 + 4 ^ 2"
    private const val complexExpr = "1 + a * 3.0 ^ 中文变量 // 32 - log2(c6\$_) % ハルカ"
    private val ctx = SContext.of(
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
        repeat(1_000_000) {
            1.0 + floor(a * 3.0.pow(b) / 32) - log2(c) % d
        }

        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            1.0 + floor(a * 3.0.pow(b) / 32) - log2(c) % d
        }
        val end = System.currentTimeMillis()
        println("(Baseline) Java Eval: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - CompiledEval")
    fun compiledEval() {
        val expr = Shigure.compile(complexExpr)
        repeat(1_000_000) {
            expr.eval(ctx)
        }

        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            expr.eval(ctx)
        }
        val end = System.currentTimeMillis()
        println("CompiledEval: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - JITEval")
    fun directEval() {
        repeat(1_000_000) {
            Shigure.eval(complexExpr, ctx)
        }

        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            Shigure.eval(complexExpr, ctx)
        }
        val end = System.currentTimeMillis()
        println("JITEval: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - Tokenizer")
    fun tokenizer() {
        val tkz = STokenizer(complexExpr)
        repeat(1_000_000) {
            tkz.tokenizeAll()
        }
        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            tkz.tokenizeAll()
        }
        val end = System.currentTimeMillis()
        println("Tokenizer: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - Parser")
    fun parser() {
        val tkz = STokenizer(complexExpr)
        val ts = tkz.tokenizeAll()
        val parser = SParser(ts)
        repeat(1_000_000) {
            parser.parse()
        }
        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            parser.parse()
        }
        val end = System.currentTimeMillis()
        println("Parser: Elapsed time: ${end - start}ms")
    }

    // Shigure 暂时还没有写 ASTEvaluator, 所以这个测试暂时无法进行
    @Test
    @DisplayName("Benchmark - ASTInterpreter")
    fun astInterpreter() {
        val tkz = STokenizer(complexExpr)
        val parser = SParser(tkz.tokenizeAll())
        val evaluator = SASTEvaluator(parser.parse())
        repeat(1_000_000) {
            evaluator.eval(ctx)
        }
        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            evaluator.eval(ctx)
        }
        val end = System.currentTimeMillis()
        println("ASTInterpreter: Elapsed time: ${end - start}ms")
    }

    /**
     * 加载 1M 个 Class 进入 JVM 是不合理的..
     * 仅测试生成代码的性能
     */
    @Test
    @DisplayName("Benchmark - CodeGenerator")
    fun codeGenerator() {
        val tkz = STokenizer(complexExpr)
        val parser = SParser(tkz.tokenizeAll())
        repeat(1_000_000) {
            SCodeGenerator(parser.parse()).generateCode()
        }
        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            SCodeGenerator(parser.parse()).generateCode()
        }
        val end = System.currentTimeMillis()
        println("CodeGenerator: Elapsed time: ${end - start}ms")
    }

    /**
     * 仅测试全量编译到字节码的性能
     */
    @Test
    @DisplayName("Benchmark - FullCompile 1M Simple")
    fun fullCompileSimple() {
        repeat(1_000_000) {
            SCodeGenerator(SParser(STokenizer(simpleExpr).tokenizeAll()).parse()).generateCode()
        }

        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            SCodeGenerator(SParser(STokenizer(simpleExpr).tokenizeAll()).parse()).generateCode()
        }
        val end = System.currentTimeMillis()
        println("FullCompile 1M Simple: Elapsed time: ${end - start}ms")
    }

    /**
     * 仅测试全量编译到字节码的性能
     */
    @Test
    @DisplayName("Benchmark - FullCompile 1M Complex")
    fun fullCompileComplex() {
        repeat(1_000_000) {
            SCodeGenerator(SParser(STokenizer(complexExpr).tokenizeAll()).parse()).generateCode()
        }

        val start = System.currentTimeMillis()
        repeat(1_000_000) {
            SCodeGenerator(SParser(STokenizer(complexExpr).tokenizeAll()).parse()).generateCode()
        }
        val end = System.currentTimeMillis()
        println("FullCompile 1M Complex: Elapsed time: ${end - start}ms")
    }

    // 在有 LoadClass 的情况下仍然做 1M 的测试是不合理的. 大部分时间都放在 gc 上了
    @Test
    @DisplayName("Benchmark - FullCompileEval 10K")
    fun fullCompile10K() {
        repeat(10_000) {
            Shigure.compile(complexExpr).eval(ctx)
        }

        val start = System.currentTimeMillis()
        repeat(10_000) {
            Shigure.compile(complexExpr).eval(ctx)
        }
        val end = System.currentTimeMillis()
        println("FullCompileEval 10K: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - NoCompileEval 10K")
    fun noJIT10K() {
        run {
            val tkz = STokenizer(complexExpr)
            val parser = SParser(tkz.tokenizeAll())
            val evaluator = SASTEvaluator(parser.parse())
            repeat(10_000) {
                evaluator.eval(ctx)
            }
        }

        val start = System.currentTimeMillis()
        run {
            val tkz = STokenizer(complexExpr)
            val parser = SParser(tkz.tokenizeAll())
            val evaluator = SASTEvaluator(parser.parse())
            repeat(10_000) {
                evaluator.eval(ctx)
            }
        }
        val end = System.currentTimeMillis()
        println("NoCompileEval 10K: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - FullCompileEval 1K * 10K")
    fun fullCompile100K() {
        repeat(1_000) {
            val expr = Shigure.compile(complexExpr)
            repeat(10_000) {
                expr.eval(ctx)
            }
        }

        val start = System.currentTimeMillis()
        repeat(1_000) {
            val expr = Shigure.compile(complexExpr)
            repeat(10_000) {
                expr.eval(ctx)
            }
        }
        val end = System.currentTimeMillis()
        println("FullCompile 1K * 10K: Elapsed time: ${end - start}ms")
    }

    @Test
    @DisplayName("Benchmark - NoCompileEval 1K * 10K")
    fun noCompile100K() {
        repeat(1_000) {
            val tkz = STokenizer(complexExpr)
            val parser = SParser(tkz.tokenizeAll())
            val evaluator = SASTEvaluator(parser.parse())
            repeat(10_000) {
                evaluator.eval(ctx)
            }
        }

        val start = System.currentTimeMillis()
        repeat(1_000) {
            val tkz = STokenizer(complexExpr)
            val parser = SParser(tkz.tokenizeAll())
            val evaluator = SASTEvaluator(parser.parse())
            repeat(10_000) {
                evaluator.eval(ctx)
            }
        }
        val end = System.currentTimeMillis()
        println("NoCompileEval 1K * 10K: Elapsed time: ${end - start}ms")
    }
}
