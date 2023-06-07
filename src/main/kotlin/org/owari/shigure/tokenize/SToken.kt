package org.owari.shigure.tokenize

/**
 * @author Mochizuki Haruka
 * 经过tokenizer分割的单词
 */
class SToken(
    val type: Int,
    val text: String,
) {
    override operator fun equals(other: Any?): Boolean =
        other is SToken && other.type == type && (if (type == STokenType.ID || type == STokenType.FNCALL) other.text == text else true)

    override fun hashCode(): Int {
        return (31 * type) shl 10 xor text.hashCode()
    }

    override fun toString(): String {
        return "SToken(type=$type, text='$text')"
    }

    /*
     * 以下为一些常量, 比如 SToken(+), 并不需要有多个实例
     */
    companion object {
        @JvmStatic
        val UNKNOWN = SToken(STokenType.UNKNOWN, "UNKNOWN")
        @JvmStatic val ADD = SToken(STokenType.ADD, "+")
        @JvmStatic val SUB = SToken(STokenType.SUB, "-")
        @JvmStatic val MUL = SToken(STokenType.MUL, "*")
        @JvmStatic val DIV = SToken(STokenType.DIV, "/")
        @JvmStatic val MOD = SToken(STokenType.MOD, "%")
        @JvmStatic val POW = SToken(STokenType.POW, "^")
        @JvmStatic val DIVFLOOR = SToken(STokenType.DIVFLOOR, "//")
        @JvmStatic val COMMA = SToken(STokenType.COMMA, ",")
        @JvmStatic val LPAREN = SToken(STokenType.LPAREN, "(")
        @JvmStatic val RPAREN = SToken(STokenType.RPAREN, ")")

        @JvmStatic fun number(n: String) = SToken(STokenType.NUMBER, n)
        @JvmStatic fun id(n: String) = SToken(STokenType.ID, n)
        @JvmStatic fun fnCall(n: String) = SToken(STokenType.FNCALL, n)
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
 * DIVFLOOR: // 取整除号(向下取整)
 */
object STokenType {
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