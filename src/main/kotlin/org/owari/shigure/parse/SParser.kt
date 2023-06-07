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
 * AddSub ::= MulDiv (('+' | '-') AddSub)?
 * MulDivMod ::= Unary (('*' | '//' | '/' | '%') MulDivMod)?
 * // Power 运算是右结合的, 最终 codegen 之后会先执行最右边的 Power 运算, 然后再向左执行.
 * Power ::= Unary ('^' Power)?
 * Unary ::= ('+' | '-')? Primary
 * Primary ::= '(' AddSub ')' | Number | Var | FnCall
 * FnCall ::= FnName AddSub (',' AddSub)*  ')'
 */
object SParser {
    @JvmStatic
    fun parse(tks: STokenStream): SExprNode {
        return parseAddSub(tks)
    }

    @JvmStatic
    private fun parseAddSub(tks: STokenStream) : SExprNode {
        val lhs = parseMulDivMod(tks)
        return when {
            tks.testAndSkip(STokenType.ADD) -> SBinaryExprNode(SExprOperator.ADD, lhs, parseAddSub(tks))
            tks.testAndSkip(STokenType.SUB) -> SBinaryExprNode(SExprOperator.SUB, lhs, parseAddSub(tks))
            else -> lhs
        }
    }

    @JvmStatic
    private fun parseMulDivMod(tks: STokenStream) : SExprNode {
        val lhs = parsePower(tks)
        return when {
            tks.testAndSkip(STokenType.MUL) -> SBinaryExprNode(SExprOperator.MUL, lhs, parseMulDivMod(tks))
            tks.testAndSkip(STokenType.DIV) -> SBinaryExprNode(SExprOperator.DIV, lhs, parseMulDivMod(tks))
            tks.testAndSkip(STokenType.MOD) -> SBinaryExprNode(SExprOperator.MOD, lhs, parseMulDivMod(tks))
            tks.testAndSkip(STokenType.DIVFLOOR) -> SBinaryExprNode(SExprOperator.DIVFLOOR, lhs, parseMulDivMod(tks))
            else -> lhs
        }
    }

    @JvmStatic
    private fun parsePower(tks: STokenStream) : SExprNode {
        val lhs = parseUnary(tks)
        return if (tks.testAndSkip(STokenType.POW)) SBinaryExprNode(SExprOperator.POW, lhs, parsePower(tks))
        else lhs
    }

    @JvmStatic
    private fun parseUnary(tks: STokenStream) : SExprNode {
        return when {
            tks.testAndSkip(STokenType.ADD) -> SUnaryExprNode(SExprOperator.ADD, parseUnary(tks))
            tks.testAndSkip(STokenType.SUB) -> SUnaryExprNode(SExprOperator.SUB, parseUnary(tks))
            else -> parsePrimary(tks)
        }
    }

    @JvmStatic
    private fun parsePrimary(tks: STokenStream) : SExprNode {
        return when {
            tks.testAndSkip(STokenType.LPAREN) -> {
                val expr = parseAddSub(tks)
                tks.expect(STokenType.RPAREN)
                expr
            }
            tks.test(STokenType.NUMBER) -> SConstNumNode(tks.expect(STokenType.NUMBER).text)
            tks.test(STokenType.ID) -> SVarAccessNode(tks.expect(STokenType.ID).text)
            tks.test(STokenType.FNCALL) -> parseFnCall(tks)
            else -> throw IllegalStateException("Unexpected Token: ${tks.peek()}")
        }
    }

    @JvmStatic
    private fun parseFnCall(tks: STokenStream) : SFnCallNode {
        val name = tks.expect(STokenType.FNCALL).text
        val args = arrayListOf<SExprNode>()
        tks.expect(STokenType.LPAREN)
        if(tks.test(STokenType.RPAREN)) return SFnCallNode(name, args)
        do args.add(parseAddSub(tks)) while (tks.testAndSkip(STokenType.COMMA))
        tks.expect(STokenType.RPAREN)
        return SFnCallNode(name, args)
    }
}
