package org.owari.shigure;

import org.owari.shigure.impl.*;
import org.owari.shigure.util.SneakyThrow;

import java.util.function.Function;

public final class Expression implements Function<Context, Double> {
    public static Expression of(String source) {
        return new Expression(source);
    }
    private static final int JITThreshold = 16_000;

    private final String source;
    private int count = 0;
    private boolean isInit = false;
    private boolean isJIT = false;
    private OpcodeSet os;
    private CalcFunc impl;

    private Expression(String source) {
        this.source = source;
    }

    private void init() {
        Tokenizer t = new Tokenizer(source);
        Parser p = new Parser(t);
        os = p.parse();
    }

    public double invoke(Context ctx) {
        if(isJIT) return impl.invoke(ctx);
        if(!isInit) {
            init();
            isInit = true;
        }
        count++;
        if(count >= JITThreshold) {
            compile();
            return impl.invoke(ctx);
        }
        return Interpreter.eval(os, ctx);
    }

    public void compile() {
        if(isJIT) return;
        if(!isInit) {
            init();
            isInit = true;
        }
        Assembler as = Shigure.getDefaultAssembler();
        String name = as.newClassName();
        CodeGenerator cg = new CodeGenerator(os, name);
        try {
            impl = (CalcFunc) as.assemble(name, cg.generate()).newInstance();
        } catch (Throwable t) {
            throw SneakyThrow.sneakyThrow(t);
        }
        isJIT = true;
    }

    @Override
    public Double apply(Context ctx) {
        return invoke(ctx);
    }
}
