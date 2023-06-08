package org.owari.shigure

import org.owari.shigure.ast.SExprNode
import org.owari.shigure.ast.SRootNode
import org.owari.shigure.codegen.SCodeGenerator
import org.owari.shigure.parse.SParser
import org.owari.shigure.tokenize.STokenizer

class SExpression(
    val source: String,
) : (SContext) -> Double {
    private val tokens by lazy { STokenizer(source).result }
    private val ast: SRootNode by lazy { SParser(tokens).result }
    private val impl: SExprImpl by lazy { SCodeGenerator(ast).result }

    fun eval(ctx: SContext): Double = impl.eval(ctx)
    override operator fun invoke(p1: SContext) = eval(p1)
    fun eval() = impl.eval(SContext())
    fun evalWith(vararg vars: Pair<String, Double>): Double = impl.eval(SContext.of(*vars))

    fun compileNow() { this.impl }
}

abstract class SExprImpl {
    abstract fun eval(ctx: SContext): Double
}
