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

    NEW(Opcodes.NEW, false),
    DUP(Opcodes.DUP, true),
    POP(Opcodes.POP, true),
    POP2(Opcodes.POP2, true),
    NEWARRAY(Opcodes.NEWARRAY, false),
    ANEWARRAY(Opcodes.ANEWARRAY, false),
    MULTIANEWARRAY(Opcodes.MULTIANEWARRAY, false),

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

    CHECKCAST(Opcodes.CHECKCAST, false)
}