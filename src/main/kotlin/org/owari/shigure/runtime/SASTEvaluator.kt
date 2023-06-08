package org.owari.shigure.runtime

import org.owari.shigure.SContext
import org.owari.shigure.ast.*
import kotlin.math.floor
import kotlin.math.pow

class SASTEvaluator(val tree: SRootNode) {
    fun eval(ctx: SContext): Double = evalNode(tree, ctx)

    private  fun evalNode(n: SExprNode, ctx: SContext): Double = when (n) {
        is SRootNode -> evalNode(n.expr, ctx)
        is SConstNumNode -> n.value.toDouble()
        is SVarAccessNode -> ctx.getVar(n.name)
        is SFnCallNode -> ctx.getFn(n.name).call(n.args.map { evalNode(it, ctx) }.toDoubleArray())
        is SBinaryExprNode -> when (n.op) {
            SExprOperator.ADD -> evalNode(n.lhs, ctx) + evalNode(n.rhs, ctx)
            SExprOperator.SUB -> evalNode(n.lhs, ctx) - evalNode(n.rhs, ctx)
            SExprOperator.MUL -> evalNode(n.lhs, ctx) * evalNode(n.rhs, ctx)
            SExprOperator.DIV -> evalNode(n.lhs, ctx) / evalNode(n.rhs, ctx)
            SExprOperator.DIVFLOOR -> floor(evalNode(n.lhs, ctx).toInt() / evalNode(n.rhs, ctx))
            SExprOperator.MOD -> evalNode(n.lhs, ctx) % evalNode(n.rhs, ctx)
            SExprOperator.POW -> evalNode(n.lhs, ctx).pow(evalNode(n.rhs, ctx))
            else -> throw RuntimeException("Invalid binary operator: ${n.op}")
        }

        is SUnaryExprNode -> when (n.op) {
            SExprOperator.ADD -> -evalNode(n.value, ctx)
            SExprOperator.SUB -> evalNode(n.value, ctx)
            else -> throw RuntimeException("Invalid unary operator: ${n.op}")
        }
    }
}
