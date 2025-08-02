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

    GETFIELD(Opcodes.GETFIELD),
    PUTFIELD(Opcodes.PUTFIELD),
    PUTSTATIC(Opcodes.PUTSTATIC),
    GETSTATIC(Opcodes.GETSTATIC),

    INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL),
    INVOKESPECIAL(Opcodes.INVOKESPECIAL),
    INVOKEINTERFACE(Opcodes.INVOKEINTERFACE),
    INVOKESTATIC(Opcodes.INVOKESTATIC)
}