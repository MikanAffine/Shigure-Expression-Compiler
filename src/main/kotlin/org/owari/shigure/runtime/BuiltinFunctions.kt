package org.owari.shigure.runtime

import kotlin.math.*
import kotlin.random.Random

object BuiltinFunctions {
    private val RANDOM_GENERATOR = Random(System.currentTimeMillis() xor System.nanoTime())

    @JvmStatic
    val ABS = ArithmeticFunction.of { abs(it[0]) }
    @JvmStatic
    val SIGN = ArithmeticFunction.of { sign(it[0]) }

    @JvmStatic
    val MAX = ArithmeticFunction.of { it.max() }
    @JvmStatic
    val MIN = ArithmeticFunction.of { it.min() }
    @JvmStatic
    val AVG = ArithmeticFunction.of { it.average() }
    @JvmStatic
    val SUM = ArithmeticFunction.of { it.sum() }
    // 求所有参数的乘积
    @JvmStatic
    val PROD = ArithmeticFunction.of { it.reduce { acc, d -> acc * d } }
    // 平方和
    @JvmStatic
    val SQSUM = ArithmeticFunction.of { it.sumOf { d -> d * d } }

    // 注意, Shigure 中三角函数默认使用角度制

    @JvmStatic
    val SIN = ArithmeticFunction.of { sin(Math.toRadians(it[0])) }
    @JvmStatic
    val COS = ArithmeticFunction.of { cos(Math.toRadians(it[0])) }
    @JvmStatic
    val TAN = ArithmeticFunction.of { tan(Math.toRadians(it[0])) }
    @JvmStatic
    val ASIN = ArithmeticFunction.of { Math.toDegrees(asin(it[0])) }
    @JvmStatic
    val ACOS = ArithmeticFunction.of { Math.toDegrees(acos(it[0])) }
    @JvmStatic
    val ATAN = ArithmeticFunction.of { Math.toDegrees(atan(it[0])) }
    @JvmStatic
    val ATAN2 = ArithmeticFunction.of { Math.toDegrees(atan2(it[0], it[1])) }
    @JvmStatic
    val SINH = ArithmeticFunction.of { sinh(it[0]) }
    @JvmStatic
    val COSH = ArithmeticFunction.of { cosh(it[0]) }
    @JvmStatic
    val TANH = ArithmeticFunction.of { tanh(it[0]) }
    @JvmStatic
    val ASINH = ArithmeticFunction.of { asinh(it[0]) }
    @JvmStatic
    val ACOSH = ArithmeticFunction.of { acosh(it[0]) }
    @JvmStatic
    val ATANH = ArithmeticFunction.of { atanh(it[0]) }

    // 角度转弧度
    @JvmStatic
    val RAD = ArithmeticFunction.of { Math.toRadians(it[0]) }
    // 弧度转角度
    @JvmStatic
    val DEG = ArithmeticFunction.of { Math.toDegrees(it[0]) }
    // 求斜边长
    val HYPOT = ArithmeticFunction.of { hypot(it[0], it[1]) }

    // 幂函数, pow(base, exponent) = base ^ exponent
    @Deprecated("已经内置了 power 运算符...", ReplaceWith("a ^ b"), DeprecationLevel.WARNING)
    @JvmStatic
    val POW = ArithmeticFunction.of { it[0].pow(it[1]) }
    // 标准库的 sqrt 是 native 实现, 更快
    @JvmStatic
    val SQRT = ArithmeticFunction.of { sqrt(it[0]) }
    // 理论上, 使用标准库的 cbrt 是更快的
    @JvmStatic
    val CBRT = ArithmeticFunction.of { cbrt(it[0]) }
    // 开方函数, root(base, exponent) = value ^ (1 / exponent)
    @Deprecated("已经内置了 power 运算符...", ReplaceWith("a ^ (1 / b)"), DeprecationLevel.WARNING)
    @JvmStatic
    val ROOT = ArithmeticFunction.of { it[0].pow(1 / it[1]) }

    @JvmStatic
    val EXP = ArithmeticFunction.of { exp(it[0]) }
    @JvmStatic
    val LN = ArithmeticFunction.of { ln(it[0]) }
    @JvmStatic
    val LOG10 = ArithmeticFunction.of { log10(it[0]) }
    @JvmStatic
    val LOG2 = ArithmeticFunction.of { log2(it[0]) }
    // log(value, base)
    @JvmStatic
    val LOG = ArithmeticFunction.of { log(it[0], it[1]) }

    @JvmStatic
    val CEIL = ArithmeticFunction.of { ceil(it[0]) }
    @JvmStatic
    val FLOOR = ArithmeticFunction.of { floor(it[0]) }
    @JvmStatic
    val ROUND = ArithmeticFunction.of { round(it[0]) }
    // 去除小数部分, 保留整数部分
    @JvmStatic
    val TRUNCATE = ArithmeticFunction.of { truncate(it[0]) }

    @JvmStatic
    val RANDOM = ArithmeticFunction.of { RANDOM_GENERATOR.nextDouble() }
    @JvmStatic
    val RANDINT = ArithmeticFunction.of { RANDOM_GENERATOR.nextLong().toDouble() }

    @JvmStatic
    val vars = hashMapOf(
        "pi" to Math.PI,
        "π" to Math.PI,
        "e" to Math.E,
        "φ" to (1 + sqrt(5.0)) / 2,
        "rad" to Math.toRadians(1.0),
    )
    @JvmStatic
    val fns = hashMapOf(
        "abs" to ABS,
        "sign" to SIGN,

        "max" to MAX,
        "min" to MIN,
        "avg" to AVG,
        "sum" to SUM,
        "prod" to PROD,
        "sqsum" to SQSUM,

        "sin" to SIN,
        "cos" to COS,
        "tan" to TAN,
        "asin" to ASIN,
        "acos" to ACOS,
        "atan" to ATAN,
        "atan2" to ATAN2,
        "sinh" to SINH,
        "cosh" to COSH,
        "tanh" to TANH,
        "asinh" to ASINH,
        "acosh" to ACOSH,
        "atanh" to ATANH,

        "rad" to RAD,
        "deg" to DEG,
        "hypot" to HYPOT,

        "pow" to POW,
        "sqrt" to SQRT,
        "cbrt" to CBRT,
        "root" to ROOT,

        "exp" to EXP,
        "ln" to LN,
        "log10" to LOG10,
        "log2" to LOG2,
        "log" to LOG,

        "ceil" to CEIL,
        "floor" to FLOOR,
        "round" to ROUND,
        "trunc" to TRUNCATE,

        "random" to RANDOM,
        "randint" to RANDINT,
    )
}
