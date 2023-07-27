package org.owari.shigure.impl

import org.owari.shigure.impl.TokenizeUtil.isIdPart
import org.owari.shigure.impl.TokenizeUtil.isIdStart
import org.owari.shigure.impl.TokenizeUtil.isNumber
import org.owari.shigure.impl.TokenizeUtil.isSpace

/**
 * @author Mochizuki Haruka
 * 经过tokenizer分割的单词
 */
class Token (
    val type: Int,
    val text: String = ""
) {
    override operator fun equals(other: Any?): Boolean = other is Token && other.type == type && (if (type == TokenType.ID) other.text == text else true)

    override fun hashCode(): Int = (31 * type) shl 10 xor text.hashCode()

    override fun toString() = "[$type : $text]"


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
        @JvmStatic val FLOORDIV = Token(TokenType.FLOORDIV, "//")
        @JvmStatic val COMMA = Token(TokenType.COMMA, ",")
        @JvmStatic val LPAREN = Token(TokenType.LPAREN, "(")
        @JvmStatic val RPAREN = Token(TokenType.RPAREN, ")")

        @JvmStatic fun number(n: String) = Token(TokenType.NUMBER, n)
        @JvmStatic fun id(n: String) = Token(TokenType.ID, n)
    }
}

/**
 * UNKNOWN: 未知种类的单词
 * NUMBER: 数字(均作为浮点数)
 * ID: 标识符
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
    const val UNKNOWN: Int = 0

    const val NUMBER: Int = 1
    const val ID: Int = 2

    const val COMMA: Int = 3

    const val LPAREN: Int = 4
    const val RPAREN: Int = 5

    const val ADD: Int = 6
    const val SUB: Int = 7
    const val MUL: Int = 8
    const val DIV: Int = 9
    const val MOD: Int = 10
    const val POW: Int = 11
    const val FLOORDIV: Int = 12
}

object TokenizeUtil {
    // 工具函数

    /**
     * 判断一个字符是否是合法的标识符的开头
     * 合法的标识符开头可以是 英文大小写字母, 下划线, 美元符号, 中文字符, 日语平假名, 片假名 和 韩文字符
     * 中文的范围是 4e00 - 9fff
     * 日语平假名和片假名的范围是 3040 - 30ff
     * 韩文的范围是 ac00 - d7af
     */
    @JvmStatic inline fun Char.isIdStart(): Boolean =
        this in 'a' .. 'z' || this in 'A' .. 'Z' || this == '_' || this == '$'
                || this in '\u4e00' .. '\u9fff' || this in '\u3040' .. '\u30ff' || this in '\uac00' .. '\ud7af'

    /**
     * 判断一个字符是否是合法的标识符的一部分
     * 合法的标识符的一部分可以是 英文大小写字母, 下划线, 美元符号, 中文字符, 日语平假名, 片假名, 韩文字符 和 数字
     */
    @JvmStatic inline fun Char.isIdPart(): Boolean = this.isIdStart() || this.isNumber()

    /**
     * 判断一个字符是否是数字
     * 不使用标准库的 [Character.isDigit] 是因为它会判断一些非 ASCII 字符为数字
     * 在实际使用中没有必要把它们判断为数字...
     */
    @JvmStatic inline fun Char.isNumber(): Boolean = this in '0' .. '9'

    @JvmStatic inline fun Char.isSpace(): Boolean = this == ' ' || this == '\n' || this == '\r' || this == '\t'
}

/**
 * @author Mochizuki Haruka
 * @since 1.0 - release
 *
 * Passive Tokenizer
 */
class Tokenizer(source: String) {
    private val src: CharArray = source.toCharArray()
    private val len: Int = src.size
    private var offset = 0

    private inline fun hasMore() = offset < len
    private inline fun skip() { offset++ }
    private inline fun skip(n: Int) { offset += n }
    private inline fun peek() = src[offset]
    private inline fun peek(next: Int = 0) = src[offset + next]

    private inline fun skipSpace() {
        while(hasMore() && src[offset].isSpace()) skip()
    }

    fun reset() { offset = 0 }
    fun hasNext(): Boolean {
        skipSpace()
        return hasMore()
    }
    fun next(): Token {
        skipSpace()
        val c = src[offset]
        when (c) {
            ',' -> {
                skip()
                return Token.COMMA
            }
            '(' -> {
                skip()
                return Token.LPAREN
            }
            ')' -> {
                skip()
                return Token.RPAREN
            }
            '+' -> {
                skip()
                return Token.ADD
            }
            '-' -> {
                skip()
                return Token.SUB
            }
            '*' -> {
                skip()
                return if(hasMore() && peek() == '*') {
                    skip()
                    Token.POW
                }
                else Token.MUL
            }
            '/' -> {
                skip()
                return if(hasMore() && peek() == '/') {
                    skip()
                    Token.FLOORDIV
                }
                else Token.DIV
            }
            '%' -> {
                skip()
                return Token.MOD
            }
            '^' -> {
                skip()
                return Token.POW
            }

        }
        when {
            c.isNumber() -> {
                val start = offset
                do skip() while (hasMore() && peek().isNumber())
                if (hasMore() && peek() == '.') {
                    skip()
                    do skip() while (hasMore() && peek().isNumber())
                }
                return Token.number(String(src, start, offset - start))
            }
            c.isIdStart() -> {
                val start = offset
                do skip() while (hasMore() && peek().isIdPart())
                return Token.id(String(src, start, offset - start))
            }
        }
        throw IllegalArgumentException("Unexpected char: $c")
    }

    fun expect(type: Int): Token = next().also { if(it.type != type) throw IllegalArgumentException("Unexpected token: $it") }
    fun test(type: Int): Boolean {
        skipSpace()
        return hasMore() && when (type) {
            TokenType.NUMBER -> peek().isNumber()
            TokenType.ID -> peek().isIdStart()
            TokenType.COMMA -> peek() == ','
            TokenType.ADD -> peek() == '+'
            TokenType.SUB -> peek() == '-'
            TokenType.MUL -> peek() == '*' && peek(1) != '*'
            TokenType.DIV -> peek() == '/' && peek(1) != '/'
            TokenType.MOD -> peek() == '%'
            TokenType.POW -> peek() == '^' || (peek() == '*' && peek(1) == '*')
            TokenType.FLOORDIV -> peek() == '/' && peek(1) == '/'
            TokenType.LPAREN -> peek() == '('
            TokenType.RPAREN -> peek() == ')'
            else -> throw IllegalArgumentException("Unexpected token type: $type")
        }
    }
    fun testAndSkip(type: Int): Boolean = test(type).also { if(it) next() }
}
