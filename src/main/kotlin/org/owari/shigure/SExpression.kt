package org.owari.shigure

import org.owari.shigure.ast.SExprNode
import org.owari.shigure.codegen.SCodeGenerator
import org.owari.shigure.parse.SParser
import org.owari.shigure.tokenize.STokenizer

class SExpression(
    val source: String,
) : (SContext) -> Double {
    private val tokens by lazy { STokenizer.tokenize(source) }
    private val ast: SExprNode by lazy { SParser.parse(tokens) }
    private val impl: SExprImpl by lazy { SCodeGenerator.generate(ast) }

    fun eval(ctx: SContext): Double = impl.eval(ctx)
    override operator fun invoke(p1: SContext) = eval(p1)
}

abstract class SExprImpl {
    abstract fun eval(ctx: SContext): Double
}
