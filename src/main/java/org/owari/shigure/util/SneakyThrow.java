package org.owari.shigure.util;

@SuppressWarnings("unchecked")
public final class SneakyThrow {
    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) throw new NullPointerException("t");
        return SneakyThrow.sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}
