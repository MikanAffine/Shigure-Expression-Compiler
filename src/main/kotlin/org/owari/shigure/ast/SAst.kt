package org.owari.shigure.ast

sealed class SExprNode

class SRootNode(
    val expr: SExprNode,
    val usedVars: List<String>,
    val usedFns: List<String>,
) : SExprNode()

object SExprOperator {
    const val ADD = 0
    const val SUB = 1
    const val MUL = 2
    const val DIV = 3
    const val MOD = 4
    const val POW = 5
    const val DIVFLOOR = 6
}

class SBinaryExprNode(
    val op: Int,
    val lhs: SExprNode,
    val rhs: SExprNode,
) : SExprNode()

class SUnaryExprNode(
    val op: Int,
    val value: SExprNode,
) : SExprNode()

class SVarAccessNode(
    val name: String,
) : SExprNode()

class SConstNumNode(
    val value: String,
) : SExprNode()

class SFnCallNode(
    val name: String,
    val args: List<SExprNode>,
) : SExprNode()
