package com.lovelycatv.vertex.asm

import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-02 12:07
 * @version 1.0
 */
enum class JVMInstruction(val code: Int, val instructionOnly: Boolean) {
    ACONST_NULL(Opcodes.ACONST_NULL, true),
    ICONST_M1(Opcodes.ICONST_M1, true),
    ICONST_0(Opcodes.ICONST_0, true),
    ICONST_1(Opcodes.ICONST_1, true),
    ICONST_2(Opcodes.ICONST_2, true),
    ICONST_3(Opcodes.ICONST_3, true),
    ICONST_4(Opcodes.ICONST_4, true),
    ICONST_5(Opcodes.ICONST_5, true),
    LCONST_0(Opcodes.LCONST_0, true),
    LCONST_1(Opcodes.LCONST_1, true),
    FCONST_0(Opcodes.FCONST_0, true),
    FCONST_1(Opcodes.FCONST_1, true),
    FCONST_2(Opcodes.FCONST_2, true),
    DCONST_0(Opcodes.DCONST_0, true),
    DCONST_1(Opcodes.DCONST_1, true),
    BIPUSH(Opcodes.BIPUSH, false),
    SIPUSH(Opcodes.SIPUSH, false),
    LDC(Opcodes.LDC, false),
    LDC_W(Opcodes.LDC, false),
    LDC2_W(Opcodes.LDC, false),

    ILOAD(Opcodes.ILOAD, false),
    LLOAD(Opcodes.LLOAD, false),
    FLOAD(Opcodes.FLOAD, false),
    DLOAD(Opcodes.DLOAD, false),
    ALOAD(Opcodes.ALOAD, false),

    ILOAD_0(0x1a, true),
    LLOAD_0(0x1e, true),
    FLOAD_0(0x22, true),
    DLOAD_0(0x26, true),
    ALOAD_0(0x2a, true),

    ILOAD_1(0x1b, true),
    LLOAD_1(0x1f, true),
    FLOAD_1(0x23, true),
    DLOAD_1(0x27, true),
    ALOAD_1(0x2b, true),

    ILOAD_2(0X1c, true),
    LLOAD_2(0x20, true),
    FLOAD_2(0x24, true),
    DLOAD_2(0x28, true),
    ALOAD_2(0x2c, true),

    ILOAD_3(0X1d, true),
    LLOAD_3(0x21, true),
    FLOAD_3(0x25, true),
    DLOAD_3(0x29, true),
    ALOAD_3(0x2d, true),

    ISTORE(Opcodes.ISTORE, false),
    LSTORE(Opcodes.LSTORE, false),
    FSTORE(Opcodes.FSTORE, false),
    DSTORE(Opcodes.DSTORE, false),
    ASTORE(Opcodes.ASTORE, false),

    RETURN(Opcodes.RETURN, true),
    IRETURN(Opcodes.IRETURN, true),
    LRETURN(Opcodes.LRETURN, true),
    FRETURN(Opcodes.FRETURN, true),
    DRETURN(Opcodes.DRETURN, true),
    ARETURN(Opcodes.ARETURN, true),

    IALOAD(Opcodes.IALOAD, false),
    LALOAD(Opcodes.LALOAD, false),
    FALOAD(Opcodes.FALOAD, false),
    DALOAD(Opcodes.DALOAD, false),
    AALOAD(Opcodes.AALOAD, false),
    BALOAD(Opcodes.BALOAD, false),
    CALOAD(Opcodes.CALOAD, false),
    SALOAD(Opcodes.SALOAD, false),

    AASTORE(Opcodes.AASTORE, true),
    IASTORE(Opcodes.IASTORE, true),
    BASTORE(Opcodes.BASTORE, true),
    CASTORE(Opcodes.CASTORE, true),
    DASTORE(Opcodes.DASTORE, true),
    FASTORE(Opcodes.FASTORE, true),
    LASTORE(Opcodes.LASTORE, true),
    SASTORE(Opcodes.SASTORE, true),

    I2L(Opcodes.I2L, true),
    I2F(Opcodes.I2F, true),
    I2D(Opcodes.I2D, true),
    I2B(Opcodes.I2B, true),
    I2C(Opcodes.I2C, true),
    I2S(Opcodes.I2S, true),
    L2I(Opcodes.L2I,  true),
    L2F(Opcodes.L2F,  true),
    L2D(Opcodes.L2D,  true),
    F2I(Opcodes.F2I, true),
    F2L(Opcodes.F2L, true),
    F2D(Opcodes.F2D, true),
    D2I(Opcodes.D2I, true),
    D2L(Opcodes.D2L, true),
    D2F(Opcodes.D2F, true),

    IADD(Opcodes.IADD, true),
    LADD(Opcodes.LADD, true),
    FADD(Opcodes.FADD, true),
    DADD(Opcodes.DADD, true),

    IS(Opcodes.ISUB, true),
    LS(Opcodes.LSUB, true),
    FS(Opcodes.FSUB, true),
    DS(Opcodes.DSUB, true),

    IMUL(Opcodes.IMUL, true),
    LMUL(Opcodes.LMUL, true),
    FMUL(Opcodes.FMUL, true),
    DMUL(Opcodes.DMUL, true),

    IDIV(Opcodes.IDIV, true),
    LDIV(Opcodes.LDIV, true),
    FDIV(Opcodes.FDIV, true),
    DDIV(Opcodes.DDIV, true),

    IREM(Opcodes.IREM, true),
    LREM(Opcodes.LREM, true),
    FREM(Opcodes.FREM, true),
    DREM(Opcodes.DREM, true),

    INEG(Opcodes.INEG, true),
    LNEG(Opcodes.LNEG, true),
    FNEG(Opcodes.FNEG, true),
    DNEG(Opcodes.DNEG, true),

    ISHL(Opcodes.ISHL, true),
    LSHL(Opcodes.LSHL, true),

    ISHR(Opcodes.ISHR, true),
    LSHR(Opcodes.LSHR, true),

    IUSHR(Opcodes.IUSHR, true),
    LUSHR(Opcodes.LUSHR, true),

    IAND(Opcodes.IAND, true),
    LAND(Opcodes.LAND, true),

    IOR(Opcodes.IOR, true),
    LOR(Opcodes.LOR, true),

    IXOR(Opcodes.IXOR, true),
    LXOR(Opcodes.LXOR, true),

    IINC(Opcodes.IINC, false),

    NEW(Opcodes.NEW, false),
    DUP(Opcodes.DUP, true),
    DUP_X1(Opcodes.DUP_X1, true),
    DUP_X2(Opcodes.DUP_X2, true),
    DUP2(Opcodes.DUP2, true),
    DUP2_X1(Opcodes.DUP2_X1, true),
    DUP2_X2(Opcodes.DUP2_X2, true),
    NOP(Opcodes.NOP, true),
    SWAP(Opcodes.SWAP, true),
    POP(Opcodes.POP, true),
    POP2(Opcodes.POP2, true),
    NEWARRAY(Opcodes.NEWARRAY, false),
    ANEWARRAY(Opcodes.ANEWARRAY, false),
    MULTIANEWARRAY(Opcodes.MULTIANEWARRAY, false),

    LCMP(Opcodes.LCMP, true),
    FCMPL(Opcodes.FCMPL, true),
    FCMPG(Opcodes.FCMPG, true),
    DCMPL(Opcodes.DCMPL, true),
    DCMPG(Opcodes.DCMPG, true),

    GETFIELD(Opcodes.GETFIELD, false),
    PUTFIELD(Opcodes.PUTFIELD, false),
    PUTSTATIC(Opcodes.PUTSTATIC, false),
    GETSTATIC(Opcodes.GETSTATIC, false),

    INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL, false),
    INVOKESPECIAL(Opcodes.INVOKESPECIAL, false),
    INVOKEINTERFACE(Opcodes.INVOKEINTERFACE, false),
    INVOKESTATIC(Opcodes.INVOKESTATIC, false),

    // Only used in creating primitive type array.
    T_INT(Opcodes.T_INT, true),
    T_SHORT(Opcodes.T_SHORT, true),
    T_LONG(Opcodes.T_LONG, true),
    T_FLOAT(Opcodes.T_FLOAT, true),
    T_DOUBLE(Opcodes.T_DOUBLE, true),
    T_CHAR(Opcodes.T_CHAR, true),
    T_BYTE(Opcodes.T_BYTE, true),
    T_BOOLEAN(Opcodes.T_BOOLEAN, true),

    CHECKCAST(Opcodes.CHECKCAST, false),
    ATHROW(Opcodes.ATHROW, true),
    INSTANCEOF(Opcodes.INSTANCEOF, false)
}