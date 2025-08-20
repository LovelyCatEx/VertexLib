package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.lang.code.CodeWriter
import com.lovelycatv.vertex.util.StringUtils

/**
 * @author lovelycat
 * @since 2025-08-01 18:45
 * @version 1.0
 */
class ClassDeclaration(
    val modifiers: Array<JavaModifier>,
    val className: String,
    superClass: TypeDeclaration? = null,
    interfaces: Array<TypeDeclaration>? = null,
    fields: List<FieldDeclaration>? = null,
    methods: List<MethodDeclaration>? = null
) : CodeContainer() {
    val superClass: TypeDeclaration = superClass ?: TypeDeclaration.OBJECT

    val interfaces: Array<TypeDeclaration> = interfaces ?: emptyArray()

    private val _fields: MutableList<FieldDeclaration> = fields?.toMutableList() ?: mutableListOf()
    val fields: List<FieldDeclaration> get() = this._fields

    private val _methods: MutableList<MethodDeclaration> = methods?.toMutableList() ?: mutableListOf()
    val methods: List<MethodDeclaration> get() = this._methods

    val constructors: List<MethodDeclaration> get() = this.methods.filter { it.methodName == ASMUtils.CONSTRUCTOR_NAME }

    var staticInitMethod: MethodDeclaration? = null
        private set

    fun isNoSuperClass() = this.superClass.originalClass == TypeDeclaration.OBJECT.originalClass

    fun addField(field: FieldDeclaration) {
        this._fields.add(field)
    }

    fun addFields(vararg field: FieldDeclaration) {
        this._fields.addAll(field)
    }

    fun addFields(fields: Iterable<FieldDeclaration>) {
        this._fields.addAll(fields)
    }

    fun addMethod(method: MethodDeclaration) {
        this._methods.add(method)
    }

    fun addMethods(vararg method: MethodDeclaration) {
        this._methods.addAll(method)
    }

    fun addMethods(methods: Iterable<MethodDeclaration>) {
        this._methods.addAll(methods)
    }

    fun setStaticInitMethod(method: MethodDeclaration?) {
        this.staticInitMethod = method
    }

    companion object {
        /**
         * Create ClassDeclaration by java code expression.
         *
         * @param expression Java class declare expression,
         *                   eg: expression: "public class TestClass extends $T implements $T, $T"
         *                       args      : Object.class, Serializable.class, Comparable.class
         * @return ClassDeclaration
         */
        @JvmStatic
        fun fromExpression(expression: String, vararg placeholders: Class<*>, classBuilder: (Builder.() -> Unit)? = null): ClassDeclaration {
            val tExpression = StringUtils.orderReplace(expression, arrayOf("\$T"), *placeholders.map { it.canonicalName }.toTypedArray())

            val indexOfClass = tExpression.indexOf("class")
            val endIndexOfClass = indexOfClass + 5

            val modifiers = tExpression.substring(0..<indexOfClass)
                .split(" ")
                .filter { it.isNotBlank() }
                .map { JavaModifier.valueOf(it.uppercase()) }
                .toTypedArray()

            val className: String

            var superClass: TypeDeclaration? = null
            var interfaces: List<TypeDeclaration>? = null

            if (tExpression.contains("extends") || tExpression.contains("implements")) {
                if (tExpression.contains("extends")) {
                    val indexOfExtends = tExpression.indexOf("extends")
                    val endIndexOfExtends = tExpression.indexOf("extends") + 7

                    className = tExpression.substring(endIndexOfClass + 1..<indexOfExtends - 1)

                    // left index +1 to skip whitespace
                    val superClassName = if (tExpression.contains("implements")) {
                        tExpression.substring(endIndexOfExtends + 1..<tExpression.indexOf("implements") - 1)
                    } else {
                        // No implements
                        tExpression.substring(endIndexOfExtends + 1..<tExpression.length)
                    }

                    superClass = TypeDeclaration.fromName(superClassName.trim())
                } else {
                    // expression.contains("implements") == true
                    className = tExpression.substring(endIndexOfClass + 1..<tExpression.indexOf("implements") - 1)
                }

                if (tExpression.contains("implements")) {
                    // Read implements
                    val endIndexOfImplements = tExpression.indexOf("implements") + 10
                    interfaces = tExpression.substring(endIndexOfImplements + 1..<tExpression.length)
                        .split(",")
                        .filter { it.isNotBlank() }
                        .map { TypeDeclaration.fromName(it.trim()) }
                }
            } else {
                className = tExpression.substring(endIndexOfClass + 1..<tExpression.length)
            }

            val builder = Builder(className)
                .addModifiers(*modifiers)

            superClass?.let {
                builder.superClass(it)
            }

            interfaces?.let {
                builder.addInterfaces(*it.toTypedArray())
            }

            classBuilder?.invoke(builder)

            return builder.build()
        }
    }

    class Builder(private var className: String) {
        private val modifiers: MutableList<JavaModifier> = mutableListOf()
        private var superClass: TypeDeclaration? = null
        private val interfaces: MutableList<TypeDeclaration> = mutableListOf()
        private val fields: MutableList<FieldDeclaration> = mutableListOf()
        private val methods: MutableList<MethodDeclaration> = mutableListOf()

        fun addModifier(modifier: JavaModifier): Builder {
            this.modifiers.add(modifier)
            return this
        }

        fun addModifiers(vararg modifier: JavaModifier): Builder {
            this.modifiers.addAll(modifier)
            return this
        }

        fun className(name: String): Builder {
            this.className = name
            return this
        }

        fun noSuperClass(): Builder {
            this.superClass = null
            return this
        }

        fun superClass(superClass: TypeDeclaration): Builder {
            this.superClass = superClass
            return this
        }

        fun superClass(clazz: Class<*>): Builder {
            this.superClass = TypeDeclaration.fromClass(clazz)
            return this
        }

        fun noInterfaces(): Builder {
            this.interfaces.clear()
            return this
        }

        fun addInterface(interfaceType: TypeDeclaration): Builder {
            this.interfaces.add(interfaceType)
            return this
        }

        fun addInterface(interfaceType: Class<*>): Builder {
            this.interfaces.add(TypeDeclaration.fromClass(interfaceType))
            return this
        }

        fun addInterfaces(vararg interfaceType: TypeDeclaration): Builder {
            this.interfaces.addAll(interfaceType)
            return this
        }

        fun addInterfaces(vararg interfaceType: Class<*>): Builder {
            this.interfaces.addAll(interfaceType.map { TypeDeclaration.fromClass(it) })
            return this
        }

        fun addMethod(method: MethodDeclaration): Builder {
            this.methods.add(method)
            return this
        }

        fun addMethods(vararg method: MethodDeclaration): Builder {
            this.methods.addAll(method)
            return this
        }

        fun addField(field: FieldDeclaration): Builder {
            this.fields.add(field)
            return this
        }

        fun addFields(vararg field: FieldDeclaration): Builder {
            this.fields.addAll(field)
            return this
        }

        fun addMethodFromExpression(
            expression: String,
            vararg placeholders: Class<*>,
            codeWriter: (CodeWriter.() -> Unit)? = null
        ): Builder {
            this.addMethod(MethodDeclaration.fromExpression(expression, *placeholders, codeWriter = codeWriter))
            return this
        }

        fun String.toMethod(vararg placeholders: Class<*>, codeWriter: (CodeWriter.() -> Unit)? = null): Builder {
            this@Builder.addMethodFromExpression(this, *placeholders, codeWriter = codeWriter)
            return this@Builder
        }

        fun build() = ClassDeclaration(
            modifiers = modifiers.toTypedArray(),
            className = className,
            superClass = superClass,
            interfaces = interfaces.toTypedArray()
        ).apply {
            this.addFields(this@Builder.fields)
            this.addMethods(this@Builder.methods)
        }
    }
}