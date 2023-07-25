package org.owari.shigure.impl

/**
 * @author Mochizuki Haruka
 * 经过tokenizer分割的单词
 */
class Token (
    val type: Int,
    val text: String
) {
    override operator fun equals(other: Any?): Boolean =
        other is Token && other.type == type && (if (type == TokenType.ID || type == TokenType.FNCALL) other.text == text else true)

    override fun hashCode(): Int {
        return (31 * type) shl 10 xor text.hashCode()
    }

    override fun toString(): String {
        return "[$type : $text]"
    }

    /*
     * 以下为一些常量, 比如 Token(+), 并不需要有多个实例
     */
    companion object {
        @JvmStatic
        val UNKNOWN = Token(TokenType.UNKNOWN, "UNKNOWN")
        @JvmStatic val ADD = Token(TokenType.ADD, "+")
        @JvmStatic val SUB = Token(TokenType.SUB, "-")
        @JvmStatic val MUL = Token(TokenType.MUL, "*")
        @JvmStatic val DIV = Token(TokenType.DIV, "/")
        @JvmStatic val MOD = Token(TokenType.MOD, "%")
        @JvmStatic val POW = Token(TokenType.POW, "^")
        @JvmStatic val FLOORDIV = Token(TokenType.DIVFLOOR, "//")
        @JvmStatic val COMMA = Token(TokenType.COMMA, ",")
        @JvmStatic val LPAREN = Token(TokenType.LPAREN, "(")
        @JvmStatic val RPAREN = Token(TokenType.RPAREN, ")")

        @JvmStatic fun number(n: String) = Token(TokenType.NUMBER, n)
        @JvmStatic fun id(n: String) = Token(TokenType.ID, n)
        @JvmStatic fun fnCall(n: String) = Token(TokenType.FNCALL, n)
    }
}

/**
 * UNKNOWN: 未知种类的单词
 * NUMBER: 数字(均作为浮点数)
 * ID: 变量名
 * FNCALL: 函数调用
 * COMMA: , 逗号
 * LPAREN: ( 左括号
 * RPAREN: ) 右括号
 * ADD: + 加号
 * SUB: - 减号
 * MUL: * 乘号
 * DIV: / 除号
 * MOD: % 取模
 * POW: ^ 指数
 * FLOORDIV: // 取整除号(向下取整)
 */
object TokenType {
    const val UNKNOWN: Int = -1

    const val NUMBER: Int = 0
    const val ID: Int = 1
    const val FNCALL: Int = 2

    const val COMMA: Int = 3

    const val LPAREN: Int = 4
    const val RPAREN: Int = 5

    const val ADD: Int = 6
    const val SUB: Int = 7
    const val MUL: Int = 8
    const val DIV: Int = 9
    const val MOD: Int = 10
    const val POW: Int = 11
    const val DIVFLOOR: Int = 12
}

interface Tokenizer {
    // 工具函数

    /**
     * 判断一个字符是否是合法的标识符的开头
     * 合法的标识符开头可以是 英文大小写字母, 下划线, 美元符号, 中文字符, 日语平假名, 片假名 和 韩文字符
     * 中文的范围是 4e00 - 9fff
     * 日语平假名和片假名的范围是 3040 - 30ff
     * 韩文的范围是 ac00 - d7af
     */
    private inline fun isIdStart(c: Char): Boolean =
        c in 'a' .. 'z' || c in 'A' .. 'Z' || c == '_' || c == '$' || c in '\u4e00' .. '\u9fff' || c in '\u3040' .. '\u30ff' || c in '\uac00' .. '\ud7af'

    /**
     * 判断一个字符是否是合法的标识符的一部分
     * 合法的标识符的一部分可以是 英文大小写字母, 下划线, 美元符号, 中文字符, 日语平假名, 片假名, 韩文字符 和 数字
     */
    private inline fun isIdPart(c: Char): Boolean = isIdStart(c) || isNumber(c)

    /**
     * 判断一个字符是否是数字
     * 不使用标准库的 [Character.isDigit] 是因为它会判断一些非 ASCII 字符为数字
     * 在实际使用中没有必要把它们判断为数字...
     */
    private inline fun isNumber(c: Char): Boolean = c in '0' .. '9'
}

/**
 * @author Mochizuki Haruka
 * @since 1.0 - release
 * 由 Parser 主动调用的 被动式 Tokenizer
 * 被动式 Tokenize 可以省去一遍扫描, 达到 one-pass 的效果
 */
class PassiveTokenizer(private val source: String) : Tokenizer {
    private val src = source.toCharArray()
    private val len = src.size
    private var index = 0

    fun hasMore() = index < len

    fun skip() { index++ }
    fun skip(n: Int = 1) { index += n }

    fun reset() { index = 0 }

    fun expect(type: Int): Token = when(type) {

        else -> throw IllegalArgumentException("Unexpected token type: $type")
    }
    fun expectNumber(): Token {
        val start = index
        return Token.number(source.substring(start, index))
    }
}


/*

已弃用

class ActiveTokenizer(private val source: String) : Tokenizer {
    private val src = source.toCharArray()
    private val len = src.size

    fun tokenizeAll(): TokenStream {
        val result = arrayListOf<Token>()
        var offset = 0
        while (offset < src.size) when (src[offset]) {
            ' ', '\t', '\r', '\n' -> offset++

            '(' -> {
                result.add(Token.LPAREN)
                offset++
            }

            ')' -> {
                result.add(Token.RPAREN)
                offset++
            }

            '+' -> {
                result.add(Token.ADD)
                offset++
            }

            '-' -> {
                if (offset + 1 < len && isNumber(src[offset + 1])) {
                    val start = offset
                    do offset++ while (offset < len && isNumber(src[offset]))
                    result.add(Token.number(source.substring(start, offset)))
                } else {
                    result.add(Token.SUB)
                    offset++
                }
            }

            '*' -> {
                result.add(Token.MUL)
                offset++
            }

            '/' -> if (offset + 1 < len && src[offset + 1] == '/') {
                result.add(Token.FLOORDIV)
                offset += 2
            } else {
                result.add(Token.DIV)
                offset++
            }

            '%' -> {
                result.add(Token.MOD)
                offset++
            }

            '^' -> {
                result.add(Token.POW)
                offset++
            }

            ',' -> {
                result.add(Token.COMMA)
                offset++
            }

            else -> if (isNumber(src[offset])) {
                val start = offset
                do offset++ while (offset < len && isNumber(src[offset]))
                if (offset < len && src[offset] == '.') {
                    offset++
                    while (offset < len && isNumber(src[offset])) offset++
                }
                result.add(Token.number(source.substring(start, offset)))
            } else if (isIdStart(src[offset])) {
                val start = offset
                do offset++ while (offset < len && isIdPart(src[offset]))
                if (offset < len && src[offset] == '(') result.add(Token.fnCall(source.substring(start, offset)))
                else result.add(Token.id(source.substring(start, offset)))
            } else throw IllegalArgumentException("Unexpected character: ${src[offset]}")
        }
        return TokenStream(result)
    }
}
 */