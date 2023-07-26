package org.owari.shigure.impl

import org.owari.shigure.Context

/**
 * 用于计算的函数
 * 为了 asm 生成代码方便, 设置了插桩父类
 */
abstract class CalcFunc {
    abstract fun invoke(ctx: Context): Double
}
