package com.lovelycatv.vertex.asm

import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-02 12:07
 * @version 1.0
 */
enum class JVMInstruction(val code: Int) {
    ILOAD(Opcodes.ILOAD),
    LLOAD(Opcodes.LLOAD),
    FLOAD(Opcodes.FLOAD),
    DLOAD(Opcodes.DLOAD),
    ALOAD(Opcodes.ALOAD),
    ACONST_NULL(Opcodes.ACONST_NULL),

    ISTORE(Opcodes.ISTORE),
    LSTORE(Opcodes.LSTORE),
    FSTORE(Opcodes.FSTORE),
    DSTORE(Opcodes.DSTORE),
    ASTORE(Opcodes.ASTORE),

    RETURN(Opcodes.RETURN),
    IRETURN(Opcodes.IRETURN),
    LRETURN(Opcodes.LRETURN),
    FRETURN(Opcodes.FRETURN),
    DRETURN(Opcodes.DRETURN),
    ARETURN(Opcodes.ARETURN),

    NEW(Opcodes.NEW),
    DUP(Opcodes.DUP),
    POP(Opcodes.POP),
    POP2(Opcodes.POP2),
    NEWARRAY(Opcodes.NEWARRAY),
    ANEWARRAY(Opcodes.ANEWARRAY),
    MULTIANEWARRAY(Opcodes.MULTIANEWARRAY),

    GETFIELD(Opcodes.GETFIELD),
    PUTFIELD(Opcodes.PUTFIELD),
    PUTSTATIC(Opcodes.PUTSTATIC),
    GETSTATIC(Opcodes.GETSTATIC),

    INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL),
    INVOKESPECIAL(Opcodes.INVOKESPECIAL),
    INVOKEINTERFACE(Opcodes.INVOKEINTERFACE),
    INVOKESTATIC(Opcodes.INVOKESTATIC),

    ICONST_0(Opcodes.ICONST_0),
    ICONST_1(Opcodes.ICONST_1),
    ICONST_2(Opcodes.ICONST_2),
    ICONST_3(Opcodes.ICONST_3),
    ICONST_4(Opcodes.ICONST_4),
    ICONST_5(Opcodes.ICONST_5),
    BIPUSH(Opcodes.BIPUSH),

    T_INT(Opcodes.T_INT),
    T_SHORT(Opcodes.T_SHORT),
    T_LONG(Opcodes.T_LONG),
    T_FLOAT(Opcodes.T_FLOAT),
    T_DOUBLE(Opcodes.T_DOUBLE),
    T_CHAR(Opcodes.T_CHAR),
    T_BYTE(Opcodes.T_BYTE),
    T_BOOLEAN(Opcodes.T_BOOLEAN),
}