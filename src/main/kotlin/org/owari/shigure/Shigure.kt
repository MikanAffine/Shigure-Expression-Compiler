package org.owari.shigure

import org.owari.shigure.impl.Assembler
import kotlin.math.*

/**
 * @author Mochizuki Haruka
 * 使用 Shigure 的主要接口
 */
object Shigure {
    @JvmStatic
    val defaultAssembler = Assembler()
    private val exprMap = hashMapOf<String, Expression>()

    @JvmStatic
    inline fun eval(source: String) = eval(source, Context.empty())

    @JvmStatic
    fun eval(source: String, ctx: Context): Double = exprMap.getOrPut(source) { Expression.of(source) }.invoke(ctx)

    @JvmStatic
    fun createExpr(source: String, compileNow: Boolean = false) = Expression.of(source).also { if(compileNow) it.compile() }
}
