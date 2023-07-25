package org.owari.shigure.test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.owari.shigure.impl.ContextImpl
import org.owari.shigure.Shigure
import org.owari.shigure.impl.CodeGenerator
import org.owari.shigure.impl.Parser

import org.owari.shigure.impl.ActiveTokenizer
import kotlin.math.*

object Tests {
    @Test
    @DisplayName("Test - Tokenizer")
    fun testTokenizer() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = ActiveTokenizer(str).result
    }

    @Test
    @DisplayName("Test - Parser")
    fun testParser() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = ActiveTokenizer(str).result
        val tree = Parser(tokens).result
    }

    @Test
    @DisplayName("Test - CodeGenerator")
    fun testCodeGenerator() {
        val str = "1 + a * 3.3 ^ 中文变量 // 32 - log2(4.0, c6\$_) % ハルカ"
        val tokens = ActiveTokenizer(str).result
        val tree = Parser(tokens).result
        val impl = CodeGenerator(tree).result
    }

    @Test
    @DisplayName("Test - ExprEvaluate")
    fun testExprEvaluate() {
        val str = "1 + a * 3.0 ^ 中文变量 // 32 - log2(c6\$_) % ハルカ"
        val tokens = ActiveTokenizer(str).result
        val tree = Parser(tokens).result
        val impl = CodeGenerator(tree).result
        val ctx = ContextImpl.of(
            "a" to 2.0,
            "中文变量" to 3.0,
            "c6\$_" to 16.0,
            "ハルカ" to 7.0,
        )
        ctx["log2"] = { log2(it[0]) }
        val result = impl.eval(ctx)
        val expect = 1.0 + floor(2.0 * 3.0.pow(3.0) / 32) - log2(16.0) % 7.0
        assert(result == expect) { "Expect $expect, but got $result" }
    }

    @Test
    @DisplayName("Test - API")
    fun testAPI() {
        assert(Shigure.eval("1 * 3 + 4 ^ 2") == 19.0)
        val expr1 = Shigure.createExpr("1 * 3 + 4 ^ 2")
        assert(expr1.eval() == 19.0)
        val expr2 = Shigure.compile("1 * 3 + 4 ^ 2")
        assert(expr2.eval() == 19.0)

        val expr3 = Shigure.compile("1 + a * 3.0 ^ 中文变量 // 32 - log2(c6\$_) % ハルカ")
        val expect = 1.0 + floor(2.0 * 3.0.pow(3.0) / 32) - log2(16.0) % 7.0
        assert(expr3.evalWith(
            "a" to 2.0,
            "中文变量" to 3.0,
            "c6\$_" to 16.0,
            "ハルカ" to 7.0,
        ) == expect)
    }
}
