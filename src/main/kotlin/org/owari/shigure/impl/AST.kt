package org.owari.shigure.impl

sealed class Node

class SyntaxTree(
    val expr: Node,
) : Node()

object Operators {
    const val ADD = 0
    const val SUB = 1
    const val MUL = 2
    const val DIV = 3
    const val MOD = 4
    const val POW = 5
    const val FLOORDIV = 6
}

class SBinaryExprNode(
    val op: Int,
    val lhs: Node,
    val rhs: Node,
) : Node()

class SUnaryExprNode(
    val op: Int,
    val value: Node,
) : Node()

class SVarAccessNode(
    val name: String,
) : Node()

class SConstNumNode(
    val value: String,
) : Node()

class SFnCallNode(
    val name: String,
    val args: List<Node>,
) : Node()
