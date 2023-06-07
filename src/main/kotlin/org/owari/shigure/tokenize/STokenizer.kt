package org.owari.shigure.tokenize

/**
 * @author Mochizuki Haruka
 * tokenizer 的默认实现.
 *
 * 与其他 tokenizer 不同的是, -number 不会被 tokenize 为两个 token,
 * 而是直接 tokenize 为 SToken(-number)
 */
object STokenizer {
    fun tokenize(source: String): STokenStream {
        val result = arrayListOf<SToken>()
        val src = source.toCharArray()
        val len = src.size
        var offset = 0
        while (offset < src.size) when (src[offset]) {
            ' ', '\t', '\r', '\n' -> offset++
            '(' -> {
                result.add(SToken.LPAREN)
                offset++
            }
            ')' -> {
                result.add(SToken.RPAREN)
                offset++
            }
            '+' -> {
                result.add(SToken.ADD)
                offset++
            }
            '-' -> {
                if (offset + 1 < len && src[offset + 1].isNumber()) {
                    val start = offset
                    do offset++ while (offset < len && src[offset].isNumber())
                    result.add(SToken.number(source.substring(start, offset)))
                } else {
                    result.add(SToken.SUB)
                    offset++
                }
            }
            '*' -> {
                result.add(SToken.MUL)
                offset++
            }
            '/' -> if(offset + 1 < len && src[offset + 1] == '/') {
                result.add(SToken.DIVFLOOR)
                offset += 2
            } else {
                result.add(SToken.DIV)
                offset++
            }
            '%' -> {
                result.add(SToken.MOD)
                offset++
            }
            '^' -> {
                result.add(SToken.POW)
                offset++
            }
            ',' -> {
                result.add(SToken.COMMA)
                offset++
            }
            else -> if (src[offset].isIdStart()) {
                val start = offset
                do offset++ while (offset < len && src[offset].isIdPart())
                result.add(SToken.id(source.substring(start, offset)))
            } else throw IllegalArgumentException("Unexpected character: ${src[offset]}")
        }
        return STokenStream(result)
    }

    @JvmStatic
    private fun Char.isIdStart() =
        this in 'a' .. 'z' || this in 'A' .. 'Z' || this == '_' || this == '$' || this in '\u4e00' .. '\u9fff'

    @JvmStatic
    private fun Char.isIdPart() = this.isIdStart() || this in '0' .. '9'

    @JvmStatic
    private fun Char.isNumber() = this in '0' .. '9'
}