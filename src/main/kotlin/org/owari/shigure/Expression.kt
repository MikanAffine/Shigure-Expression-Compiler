package org.owari.shigure

import org.owari.shigure.impl.*

@FunctionalInterface
interface Expression : (Context) -> Double {
    override operator fun invoke(ctx: Context): Double

    companion object {
        inline fun of(s: String) = ExpressionImpl(s)
    }
}

interface JITExpression : Expression {
    fun compile()
}

class ExpressionImpl(private val source: String) : JITExpression {
    companion object {
        // benchmarked
        const val JITThreshold = 16_000
    }

    private var count = 0
    private var isJIT = false

    private val tokenizer = PassiveTokenizer(source)
    private val parser = Parser(tokenizer)
    private val ast by lazy { parser.parse() }
    private val astEvaluator: ASTEvaluator by lazy { ASTEvaluator(ast) }
    private val impl: CalculateFunc by lazy { TODO() }

    override operator fun invoke(ctx: Context): Double  {
        if(isJIT) return impl.eval(ctx)
        count++
        if(count > JITThreshold) {
            isJIT = true
            return impl.eval(ctx)
        }
        return astEvaluator.eval(ctx)
    }

    override fun compile() {
        this.impl
        isJIT = true
    }
}

/**
 * 用于计算的函数
 * 为了 asm 生成代码方便, 设置了插桩父类
 */
abstract class CalculateFunc {
    abstract fun eval(ctx: Context): Double
}
