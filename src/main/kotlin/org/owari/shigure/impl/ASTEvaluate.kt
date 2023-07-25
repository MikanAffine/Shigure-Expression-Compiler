package org.owari.shigure.impl

import org.owari.shigure.Context
import kotlin.math.floor
import kotlin.math.pow

class ASTEvaluator(private val tree: SyntaxTree) {
    fun eval(ctx: Context): Double = evalNode(tree, ctx)

    private fun evalNode(n: Node, ctx: Context): Double = when (n) {
        is SyntaxTree -> evalNode(n.expr, ctx)
        is SConstNumNode -> n.value.toDouble()
        is SVarAccessNode -> ctx[n.name]
        is SFnCallNode -> ctx.call(n.name, n.args.map { evalNode(it, ctx) }.toDoubleArray())
        is SBinaryExprNode -> when (n.op) {
            Operators.ADD -> evalNode(n.lhs, ctx) + evalNode(n.rhs, ctx)
            Operators.SUB -> evalNode(n.lhs, ctx) - evalNode(n.rhs, ctx)
            Operators.MUL -> evalNode(n.lhs, ctx) * evalNode(n.rhs, ctx)
            Operators.DIV -> evalNode(n.lhs, ctx) / evalNode(n.rhs, ctx)
            Operators.FLOORDIV -> floor(evalNode(n.lhs, ctx).toInt() / evalNode(n.rhs, ctx))
            Operators.MOD -> evalNode(n.lhs, ctx) % evalNode(n.rhs, ctx)
            Operators.POW -> evalNode(n.lhs, ctx).pow(evalNode(n.rhs, ctx))
            else -> throw RuntimeException("Invalid binary operator: ${n.op}")
        }
        is SUnaryExprNode -> when (n.op) {
            Operators.ADD -> -evalNode(n.value, ctx)
            Operators.SUB -> evalNode(n.value, ctx)
            else -> throw RuntimeException("Invalid unary operator: ${n.op}")
        }
    }
}
