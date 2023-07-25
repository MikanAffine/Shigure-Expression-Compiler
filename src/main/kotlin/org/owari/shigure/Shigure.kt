package org.owari.shigure

import org.owari.shigure.impl.ContextImpl

/**
 * @author Mochizuki Haruka
 * 使用 Shigure 的主要接口
 */
object Shigure {
    @JvmStatic
    fun eval(source: String) = SimpleExpression(source).eval(ContextImpl())

    @JvmStatic
    fun eval(source: String, ctx: ContextImpl) = SimpleExpression(source).eval(ctx)

    @JvmStatic
    fun createExpr(source: String) = SimpleExpression(source)

    @JvmStatic
    fun compile(source: String) = SimpleExpression(source).also(SimpleExpression::compileNow)
}
