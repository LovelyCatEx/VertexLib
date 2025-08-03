package com.lovelycatv.vertex.aspect

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.lang.*
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType
import com.lovelycatv.vertex.reflect.ReflectUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import java.util.*


/**
 * @author lovelycat
 * @since 2025-08-01 02:04
 * @version 1.0
 */
object VertexAspect {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> createJavaProxy(target: T, aspect: AbstractAspect): T {
        val clazz: Class<*> = target::class.java
        val loader = clazz.classLoader
        val interfaces = clazz.interfaces

        require(interfaces.isNotEmpty()) { "Object must implement at least one interface" }

        return Proxy.newProxyInstance(
            loader,
            interfaces
        ) { proxy, method, args ->
            aspect.before(target, method, args ?: emptyArray())
            val result = aspect.invocation(target, method, args ?: arrayOf())
            aspect.after(target, method, args ?: emptyArray())
            result
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> createProxy(target: T, aspect: AbstractAspect): T {
        val targetClazz: Class<*> = target::class.java
        val targetClassType = TypeDeclaration.fromClass(targetClazz)
        val aspectClassType = TypeDeclaration.fromClass(AbstractAspect::class.java)
        val targetClazzConstructor = targetClazz.constructors.find { it.parameterCount == 0 }
            ?: throw IllegalArgumentException("Class ${targetClazz.canonicalName} should has a NoArgsConstructor")

        val proxyClassName = targetClazz.simpleName + "\$\$ByVertex\$\$${randomHex(8)}"
        val proxyClassDeclaration = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = proxyClassName,
            superClass = targetClassType,
            interfaces = null
        )

        proxyClassDeclaration.addField(
            FieldDeclaration(
                modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.FINAL),
                name = "target",
                type = targetClassType
            )
        )

        proxyClassDeclaration.addField(
            FieldDeclaration(
                modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.FINAL),
                name = "aspect",
                type = aspectClassType
            )
        )

        proxyClassDeclaration.addMethod(
            MethodDeclaration.constructor(
                parentClass = proxyClassDeclaration,
                modifier = JavaModifier.PUBLIC,
                parameters = arrayOf(
                    *targetClazzConstructor.parameters.mapIndexed { index, parameter ->
                        ParameterDeclaration.fromType("_var${index + 1}", TypeDeclaration.fromClass(parameter.type))
                    }.toTypedArray(),
                    ParameterDeclaration.fromType("target", targetClassType),
                    ParameterDeclaration.fromType("aspect", aspectClassType)
                ),
                superParameters = targetClazzConstructor.parameters.map { parameter ->
                    TypeDeclaration.fromClass(parameter.type)
                }.toTypedArray(),
                superArgs = {
                    for (i in 0..<targetClazzConstructor.parameterCount) {
                        loadMethodParameter(i)
                    }
                }
            ) {
                val superParametersCount = targetClazzConstructor.parameterCount

                storeField(null, "target", targetClassType, false) {
                    loadMethodParameter(superParametersCount)
                }

                storeField(null, "aspect", aspectClassType, false) {
                    loadMethodParameter(superParametersCount + 1)
                }
            }
        )

        targetClazz.declaredMethods.filter {
            Modifier.isPublic(it.modifiers) && !Modifier.isFinal(it.modifiers) && !Modifier.isStatic(it.modifiers)
        }.forEach {
            val parameters = it.parameters.map {
                ParameterDeclaration.fromType(it.name, TypeDeclaration.fromClass(it.type))
            }.toTypedArray()
            val returnType = TypeDeclaration.fromClass(it.returnType)

            val method =  MethodDeclaration(
                modifiers = arrayOf(JavaModifier.PUBLIC),
                methodName = it.name,
                parameters = parameters,
                returnType = returnType,
                throws = it.exceptionTypes.map { TypeDeclaration.fromClass(it) }.toTypedArray()
            )

            method.writeCode {
                val objArrayType = TypeDeclaration(ASMUtils.OBJECT_CLASS, true, 1)
                // Class[] argClasses = new Class[]{};
                defineFinalVariable("argClasses", TypeDeclaration(Class::class.java, true, 1))
                loadArray(TypeDeclaration.fromClass(Class::class.java), 1, arrayOf(method.parameters?.size ?: 0))
                storeVariable("argClasses")

                parameters.forEachIndexed { index, parameter ->
                    loadVariable("argClasses")
                    storeArrayValue(TypeDeclaration.fromClass(Class::class.java), { loadConstant(index) }) {
                        if (parameter.isPrimitiveType()) {
                            loadStaticField(
                                targetClass = ReflectUtils.getPackagedPrimitiveType(parameter.type),
                                fieldName = "TYPE",
                                fieldType = Class::class.java
                            )
                        } else if (parameter.isArray) {
                            loadMethodParameter(index)
                            invokeMethod(
                                type = FunctionInvocationType.NORMAL,
                                owner = ASMUtils.OBJECT_CLASS,
                                methodName = "getClass",
                                parameters = arrayOf(),
                                returnType = Class::class.java
                            )
                        } else {
                            loadMethodParameter(index)
                            invokeMethod(
                                type = FunctionInvocationType.NORMAL,
                                owner = parameter.originalClass,
                                methodName = "getClass",
                                parameters = arrayOf(),
                                returnType = Class::class.java
                            )
                        }
                    }
                }

                // Object[] args = new Object[]{};
                defineFinalVariable("args", objArrayType)
                loadArray(TypeDeclaration.OBJECT, 1, arrayOf(method.parameters?.size ?: 0))
                storeVariable("args")

                parameters.forEachIndexed { index, parameter ->
                    loadVariable("args")
                    storeArrayValue(TypeDeclaration.OBJECT, index = { loadConstant(index) }) {
                        if (parameter.isPrimitiveType()) {
                            // Transform to packaged type
                            val packagedType = ReflectUtils.getPackagedPrimitiveType(parameter.type)
                            invokeMethod(
                                type = FunctionInvocationType.STATIC,
                                owner = packagedType,
                                methodName = "valueOf",
                                parameters = arrayOf(TypeDeclaration.fromClass(parameter.type)),
                                returnType = packagedType
                            ) {
                                loadMethodParameter(index)
                            }
                        } else {
                            loadMethodParameter(index)
                        }
                    }

                }

                // Class<*> clazz = this.target.getClass();
                defineFinalVariable("clazz", Class::class.java)
                loadField(null, "target", targetClassType.type)
                invokeMethod(
                    type = FunctionInvocationType.NORMAL,
                    owner = targetClazz,
                    methodName = "getClass",
                    parameters = arrayOf(),
                    returnType = Class::class.java
                )
                storeVariable("clazz")

                // Method method = getDeclaredMethod(method.methodName, new Class[0]);
                defineFinalVariable("method", Method::class.java)
                loadVariable("clazz")
                invokeMethod(
                    type = FunctionInvocationType.NORMAL,
                    owner = Class::class.java,
                    methodName = "getDeclaredMethod",
                    parameters = arrayOf(TypeDeclaration.STRING, TypeDeclaration(Class::class.java, true, 1)),
                    returnType = Method::class.java
                ) {
                    loadConstant(method.methodName)
                    loadVariable("argClasses")
                }
                storeVariable("method")

                // this.aspect.before(this.target, method, args);
                loadField(null, "aspect", aspectClassType.type)
                invokeMethod(
                    type = FunctionInvocationType.NORMAL,
                    owner = aspectClassType.type,
                    methodName = "before",
                    parameters = arrayOf(
                        TypeDeclaration.OBJECT,
                        TypeDeclaration.fromClass(Method::class.java),
                        TypeDeclaration(ASMUtils.OBJECT_CLASS, true, 1)
                    ),
                    returnType = TypeDeclaration.VOID
                ) {
                    loadField(null, "target", targetClassType.type)
                    loadVariable("method")
                    loadVariable("args")
                }

                // Object result = this.aspect.invocation(this.target, method, args);
                defineFinalVariable("result", ASMUtils.OBJECT_CLASS)
                loadField(null, "aspect", aspectClassType.type)
                invokeMethod(
                    type = FunctionInvocationType.NORMAL,
                    owner = aspectClassType.type,
                    methodName = "invocation",
                    parameters = arrayOf(
                        TypeDeclaration.OBJECT,
                        TypeDeclaration.fromClass(Method::class.java),
                        TypeDeclaration(ASMUtils.OBJECT_CLASS, true, 1)
                    ),
                    returnType = TypeDeclaration.OBJECT
                ) {
                    loadField(null, "target", targetClassType.type)
                    loadVariable("method")
                    loadVariable("args")
                }
                storeVariable("result")

                // this.aspect.after(this.target, method, args);
                loadField(null, "aspect", aspectClassType.type)
                invokeMethod(
                    type = FunctionInvocationType.NORMAL,
                    owner = aspectClassType.type,
                    methodName = "after",
                    parameters = arrayOf(
                        TypeDeclaration.OBJECT,
                        TypeDeclaration.fromClass(Method::class.java),
                        TypeDeclaration(ASMUtils.OBJECT_CLASS, true, 1)
                    ),
                    returnType = TypeDeclaration.VOID
                ) {
                    loadField(null, "target", targetClassType.type)
                    loadVariable("method")
                    loadVariable("args")
                }

                if (!method.isReturnVoid()) {
                    val tReturnType = method.returnType!!

                    loadVariable("result")
                    if (tReturnType.isPrimitiveType()) {
                        // Cast to packaged type
                        val packagedClass = ReflectUtils.getPackagedPrimitiveType(tReturnType.originalClass)
                        typeCast(TypeDeclaration.fromClass(packagedClass))
                        invokeMethod(
                            owner = packagedClass,
                            methodName = "${tReturnType.originalClass.canonicalName}Value",
                            parameters = arrayOf(),
                            returnType = tReturnType.originalClass
                        )
                    } else {
                        typeCast(method.returnType!!)
                    }
                }
                returnFunc()
            }

            proxyClassDeclaration.addMethod(method)
        }

        val bytecode = VertexASM.writeClassToByteArray(proxyClassDeclaration)
        val proxyClass = VertexASM.loadClassFromByteArray(proxyClassName, bytecode)
        return proxyClass.constructors[0].newInstance(target, aspect) as T
    }

    fun randomHex(length: Int): String {
        val sb = StringBuilder()
        for (i in 0..<length) {
            sb.append(Integer.toHexString((0..15).random()))
        }
        return sb.toString()
    }
}