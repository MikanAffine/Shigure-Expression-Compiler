package org.owari.shigure.tokenize

/**
 * @author Mochizuki Haruka
 * tokenizer 的默认实现.
 *
 * 与其他 tokenizer 不同的是, -number 不会被 tokenize 为两个 token,
 * 而是直接 tokenize 为 SToken(-number)
 */
class STokenizer(private val source: String) {
    val result by lazy(this::tokenizeAll)

    private val src = source.toCharArray()
    private val len = src.size

    fun tokenizeAll(): STokenStream {
        val result = arrayListOf<SToken>()
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
                if (offset + 1 < len && isNumber(src[offset + 1])) {
                    val start = offset
                    do offset++ while (offset < len && isNumber(src[offset]))
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

            '/' -> if (offset + 1 < len && src[offset + 1] == '/') {
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

            else -> if (isNumber(src[offset])) {
                val start = offset
                do offset++ while (offset < len && isNumber(src[offset]))
                if (offset < len && src[offset] == '.') {
                    offset++
                    while (offset < len && isNumber(src[offset])) offset++
                }
                result.add(SToken.number(source.substring(start, offset)))
            } else if (isIdStart(src[offset])) {
                val start = offset
                do offset++ while (offset < len && isIdPart(src[offset]))
                if (offset < len && src[offset] == '(') result.add(SToken.fnCall(source.substring(start, offset)))
                else result.add(SToken.id(source.substring(start, offset)))
            } else throw IllegalArgumentException("Unexpected character: ${src[offset]}")
        }
        return STokenStream(result)
    }

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