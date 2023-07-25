package org.owari.shigure.impl

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
 * FnCall ::= FnName '(' AddSub (',' AddSub)*  ')'
 */
class Parser(private val tkz: PassiveTokenizer) {
    val result by lazy(this::parse)

    private val usedVars = mutableSetOf<String>()

    fun parse(): SyntaxTree {
        tkz.reset()
        return SyntaxTree(parseAddSub(), usedVars.toList(), usedFns.toList())
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
    private fun parseAddSub(): Node {
        val lhs = parseMulDivMod()
        var result = lhs
        while(tks.test(TokenType.ADD) || tks.test(TokenType.SUB)) {
            val op = when {
                tks.testAndSkip(TokenType.ADD) -> Operators.ADD
                tks.testAndSkip(TokenType.SUB) -> Operators.SUB
                else -> throw RuntimeException("unreachable")
            }
            val rhs = parseMulDivMod()
            result = SBinaryExprNode(op, result, rhs)
        }
        return result
    }

    private fun parseMulDivMod(): Node {
        val lhs = parsePower()
        var result = lhs
        while(tks.test(TokenType.MUL) || tks.test(TokenType.DIV) || tks.test(TokenType.DIVFLOOR) || tks.test(
                TokenType.MOD)) {
            val op = when {
                tks.testAndSkip(TokenType.MUL) -> Operators.MUL
                tks.testAndSkip(TokenType.DIV) -> Operators.DIV
                tks.testAndSkip(TokenType.DIVFLOOR) -> Operators.FLOORDIV
                tks.testAndSkip(TokenType.MOD) -> Operators.MOD
                else -> throw RuntimeException("unreachable")
            }
            val rhs = parsePower()
            result = SBinaryExprNode(op, result, rhs)
        }
        return result
    }

    private fun parsePower(): Node {
        val lhs = parseUnary()
        return if (tks.testAndSkip(TokenType.POW)) SBinaryExprNode(Operators.POW, lhs, parsePower())
        else lhs
    }

    /**
     * 解析一元运算
     * 对任何一个值, 只能用一次一元加减, 因为多次是不必要的.
     */
    private fun parseUnary(): Node {
        return when {
            // tks.testAndSkip(STokenType.ADD) -> SUnaryExprNode(SExprOperator.ADD, parsePrimary())
            tks.testAndSkip(TokenType.ADD) -> parsePrimary() // 一元加是没有意义的, 直接跳过
            tks.testAndSkip(TokenType.SUB) -> SUnaryExprNode(Operators.SUB, parsePrimary())
            else -> parsePrimary()
        }
    }

    private fun parsePrimary(): Node {
        return when {
            tks.testAndSkip(TokenType.LPAREN) -> {
                val expr = parseAddSub()
                tks.expect(TokenType.RPAREN)
                expr
            }
            tks.test(TokenType.NUMBER) -> SConstNumNode(tks.expect(TokenType.NUMBER).text)
            tks.test(TokenType.ID) -> {
                usedVars.add(tks.peek().text)
                SVarAccessNode(tks.expect(TokenType.ID).text)
            }
            tks.test(TokenType.FNCALL) -> parseFnCall()
            else -> throw IllegalStateException("Unexpected Token: ${tks.peek()}")
        }
    }

    private fun parseFnCall(): SFnCallNode {
        val name = tks.expect(TokenType.FNCALL).text
        val args = arrayListOf<Node>()
        tks.expect(TokenType.LPAREN)
        if (tks.test(TokenType.RPAREN)) return SFnCallNode(name, args)
        do args.add(parseAddSub()) while (tks.testAndSkip(TokenType.COMMA))
        tks.expect(TokenType.RPAREN)
        usedFns.add(name)
        return SFnCallNode(name, args)
    }
}
