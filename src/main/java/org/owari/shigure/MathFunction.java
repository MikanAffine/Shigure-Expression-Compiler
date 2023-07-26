package org.owari.shigure;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MathFunction {
    double invoke(@NotNull double[] p1);
}
