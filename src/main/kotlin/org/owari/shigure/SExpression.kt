package org.owari.shigure

import org.owari.shigure.ast.SExprNode
import org.owari.shigure.ast.SRootNode
import org.owari.shigure.codegen.SCodeGenerator
import org.owari.shigure.parse.SParser
import org.owari.shigure.runtime.SASTEvaluator
import org.owari.shigure.tokenize.STokenizer
import java.util.concurrent.atomic.AtomicLong

class SExpression(
    private val source: String,
) : (SContext) -> Double {
    companion object {
        // benchmarked
        const val JITThreshold = 16_000
    }

    private var count = 0
    private var isJIT = false

    private val tokens by lazy { STokenizer(source).result }
    private val ast: SRootNode by lazy { SParser(tokens).result }
    private val astEvaluator: SASTEvaluator by lazy { SASTEvaluator(ast) }
    private val impl: SExprImpl by lazy { SCodeGenerator(ast).result }

    fun eval(ctx: SContext): Double {
        if(isJIT) return impl.eval(ctx)
        count++
        if(count > JITThreshold) {
            isJIT = true
            return impl.eval(ctx)
        }
        return astEvaluator.eval(ctx)
    }

    override operator fun invoke(p1: SContext) = eval(p1)

    inline fun eval() = eval(SContext())

    fun evalWith(vararg vars: Pair<String, Double>): Double = impl.eval(SContext.of(*vars))

    fun compileNow() {
        this.impl
        isJIT = true
    }
}

abstract class SExprImpl {
    abstract fun eval(ctx: SContext): Double
}
