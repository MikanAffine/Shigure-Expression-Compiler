package org.owari.shigure.parse

import org.owari.shigure.ast.*
import org.owari.shigure.tokenize.STokenStream
import org.owari.shigure.tokenize.STokenType

/**
 * @author Mochizuki Haruka
 * Parser 的默认实现
 *
 * shigure的文法:
 *
 * // lexer rules (start with uppercase, as to identify Terminals):
 * // Number token 本身就包含了符号部分, 因为我们已经知道 pos/neg 运算对单纯的数字有什么作用了.
 * Number ::= ('+' | '-')? [0-9]+ ('.' [0-9]*)?
 * FnName ::= [\u4e00-\u9fff$_a-zA-Z] [\u4e00-\u9fff$_a-zA-Z0-9]* '('
 * Var ::= [\u4e00-\u9fff$_a-zA-Z] [\u4e00-\u9fff$_a-zA-Z0-9]*
 *
 * parser rules (start with lowercase, as to identify Non-Terminals):
 * AddSub ::= MulDivMod | AddSub ('+' | '-') MulDivMod
 * MulDivMod ::= Power | MulDivMod ('*' | '//' | '/' | '%') Power
 * // Power 运算是右结合的, 最终 codegen 之后会先执行最右边的 Power 运算, 然后再向左执行.
 * Power ::= Unary | Unary '^' Power // <right-assoc>
 * Unary ::= ('+' | '-')? Primary
 * Primary ::= '(' AddSub ')' | Number | Var | FnCall
 * FnCall ::= FnName AddSub (',' AddSub)*  ')'
 */
class SParser(private val tks: STokenStream) {
    val result by lazy(this::parse)

    private val usedVars = mutableSetOf<String>()
    private val usedFns = mutableSetOf<String>()

    fun parse(): SRootNode {
        tks.reset()
        return SRootNode(parseAddSub(), usedVars.toList(), usedFns.toList())
    }

    /**
     * 解析加减运算
     * 作为左结合的运算符, 1 + 2 - 3 解析后应当为
     * sub(add(1, 2), 3)
     * 形象化表示为
     *     -
     *    /\
     *   + 3
     *  /\
     * 1 2
     */
    private fun parseAddSub(): SExprNode {
        val lhs = parseMulDivMod()
        var result = lhs
        while(tks.test(STokenType.ADD) || tks.test(STokenType.SUB)) {
            val op = when {
                tks.testAndSkip(STokenType.ADD) -> SExprOperator.ADD
                tks.testAndSkip(STokenType.SUB) -> SExprOperator.SUB
                else -> throw RuntimeException("unreachable")
            }
            val rhs = parseMulDivMod()
            result = SBinaryExprNode(op, result, rhs)
        }
        return result
    }

    private fun parseMulDivMod(): SExprNode {
        val lhs = parsePower()
        var result = lhs
        while(tks.test(STokenType.MUL) || tks.test(STokenType.DIV) || tks.test(STokenType.DIVFLOOR) || tks.test(STokenType.MOD)) {
            val op = when {
                tks.testAndSkip(STokenType.MUL) -> SExprOperator.MUL
                tks.testAndSkip(STokenType.DIV) -> SExprOperator.DIV
                tks.testAndSkip(STokenType.DIVFLOOR) -> SExprOperator.DIVFLOOR
                tks.testAndSkip(STokenType.MOD) -> SExprOperator.MOD
                else -> throw RuntimeException("unreachable")
            }
            val rhs = parsePower()
            result = SBinaryExprNode(op, result, rhs)
        }
        return result
    }

    private fun parsePower(): SExprNode {
        val lhs = parseUnary()
        return if (tks.testAndSkip(STokenType.POW)) SBinaryExprNode(SExprOperator.POW, lhs, parsePower())
        else lhs
    }

    /**
     * 解析一元运算
     * 对任何一个值, 只能用一次一元加减, 因为多次是不必要的.
     */
    private fun parseUnary(): SExprNode {
        return when {
            // tks.testAndSkip(STokenType.ADD) -> SUnaryExprNode(SExprOperator.ADD, parsePrimary())
            tks.testAndSkip(STokenType.ADD) -> parsePrimary() // 一元加是没有意义的, 直接跳过
            tks.testAndSkip(STokenType.SUB) -> SUnaryExprNode(SExprOperator.SUB, parsePrimary())
            else -> parsePrimary()
        }
    }

    private fun parsePrimary(): SExprNode {
        return when {
            tks.testAndSkip(STokenType.LPAREN) -> {
                val expr = parseAddSub()
                tks.expect(STokenType.RPAREN)
                expr
            }
            tks.test(STokenType.NUMBER) -> SConstNumNode(tks.expect(STokenType.NUMBER).text)
            tks.test(STokenType.ID) -> {
                usedVars.add(tks.peek().text)
                SVarAccessNode(tks.expect(STokenType.ID).text)
            }
            tks.test(STokenType.FNCALL) -> parseFnCall()
            else -> throw IllegalStateException("Unexpected Token: ${tks.peek()}")
        }
    }

    private fun parseFnCall(): SFnCallNode {
        val name = tks.expect(STokenType.FNCALL).text
        val args = arrayListOf<SExprNode>()
        tks.expect(STokenType.LPAREN)
        if (tks.test(STokenType.RPAREN)) return SFnCallNode(name, args)
        do args.add(parseAddSub()) while (tks.testAndSkip(STokenType.COMMA))
        tks.expect(STokenType.RPAREN)
        usedFns.add(name)
        return SFnCallNode(name, args)
    }
}
