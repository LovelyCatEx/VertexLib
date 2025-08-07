package com.lovelycatv.vertex.asm.misc

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.exception.IllegalValueAccessException
import com.lovelycatv.vertex.asm.lang.MethodDeclaration
import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-02 12:32
 * @version 1.0
 */
class MethodLocalVariableMap(
    method: MethodDeclaration,
    private val map: MutableMap<Int, Record> = mutableMapOf()
) : Map<Int, MethodLocalVariableMap.Record> by map {
    init {
        this.map.clear()

        method.parameters.forEach {
            this.add(it.parameterName, it)
        }
    }

    fun getByIndex(index: Int): Record? {
        val target = this[index]
        return if (target == null && index - 1 in (0..<this.size) ) {
            val lastOne = this[index - 1]
                ?: throw IllegalStateException("Skipping too many slots.")
            if (ASMUtils.countSlots(lastOne.type.originalClass) == 2) {
                this[index + 1]
            } else {
                throw IllegalStateException("Skipping too many slots.")
            }
        } else {
            target
        }
    }

    fun add(name: String, type: TypeDeclaration): Record {
        if (getByName(name) != null) {
            throw IllegalValueAccessException("Variable named $name already exists")
        }

        val slot = requireNextSlot()

        val record = Record(slot, name, type)
        this.map[slot] = record

        return record
    }

    fun getByName(name: String): Record? {
        return this.values.find { it.name == name }
    }

    private fun requireNextSlot(): Int {
        if (this.isEmpty()) {
            return 1
        }

        val lastVariable = this.values.last()
        return lastVariable.slotIndex + ASMUtils.countSlots(lastVariable.type.originalClass)
    }

    class Record(val slotIndex: Int, val name: String, val type: TypeDeclaration)
}