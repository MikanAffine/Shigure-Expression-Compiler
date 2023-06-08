package org.owari.shigure

/**
 * @author Mochizuki Haruka
 * 使用 Shigure 的主要接口
 */
object Shigure {
    @JvmStatic
    fun eval(source: String) = SExpression(source).eval(SContext())

    @JvmStatic
    fun eval(source: String, ctx: SContext) = SExpression(source).eval(ctx)

    @JvmStatic
    fun createExpr(source: String) = SExpression(source)

    @JvmStatic
    fun compile(source: String) = SExpression(source).also(SExpression::compileNow)
}