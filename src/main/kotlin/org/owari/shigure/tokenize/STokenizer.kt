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
            else -> if(src[offset].isNumber()){
                    val start = offset
                    do offset++ while (offset < len && src[offset].isNumber())
                    if (offset < len && src[offset] == '.') {
                        offset++
                        while (offset < len && src[offset].isNumber()) offset++
                    }
                    result.add(SToken.number(source.substring(start, offset)))
                } else if (src[offset].isIdStart()) {
                    val start = offset
                    do offset++ while (offset < len && src[offset].isIdPart())
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
    @JvmStatic
    private inline fun Char.isIdStart() =
        this in 'a' .. 'z' || this in 'A' .. 'Z' || this == '_' || this == '$'
                || this in '\u4e00' .. '\u9fff' || this in '\u3040' .. '\u30ff' || this in '\uac00' .. '\ud7af'

    /**
     * 判断一个字符是否是合法的标识符的一部分
     * 合法的标识符的一部分可以是 英文大小写字母, 下划线, 美元符号, 中文字符, 日语平假名, 片假名, 韩文字符 和 数字
     */
    @JvmStatic
    private inline fun Char.isIdPart() = this.isIdStart() || this.isNumber()

    /**
     * 判断一个字符是否是数字
     * 不使用标准库的 [Character.isDigit] 是因为它会判断一些非 ASCII 字符为数字
     * 在实际使用中没有必要把它们判断为数字...
     */
    @JvmStatic
    private inline fun Char.isNumber() = this in '0' .. '9'
}