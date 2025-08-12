package com.lovelycatv.vertex.aspect.proxy

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.lang.*
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType
import com.lovelycatv.vertex.reflect.MethodSignature
import com.lovelycatv.vertex.reflect.ReflectUtils
import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.reflect.enhanced.EnhancedClass
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-11 01:55
 * @version 1.0
 */
class VertexProxyFactory<T>(superClass: Class<T>) : AbstractProxyFactory<T, T>(superClass) {
    @Suppress("UNCHECKED_CAST")
    override fun internalCreate(proxyClassName: String, constructorParameterTypes: Array<out Class<*>>, vararg constructorArgs: Any?): T {
        val targetClass: Class<T> = super.superClass
        val targetClassType = TypeDeclaration.fromClass(targetClass)
        val targetClassConstructors = targetClass.constructors
        val targetClassMethods = targetClass.declaredMethods.filter {
            !Modifier.isFinal(it.modifiers) && !Modifier.isPrivate(it.modifiers) && !Modifier.isStatic(it.modifiers)
                && this.methodProxyPolicyProvider.getPolicy(it) != MethodProxyPolicy.NO_OPERATION
        }.groupBy { it.name }
        val interceptorClassType = TypeDeclaration.fromClass(MethodInterceptor::class.java)

        val proxyClassDeclaration = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = proxyClassName,
            superClass = targetClassType,
            interfaces = null
        )

        /**
         * Field: MethodInterceptor
         */
        val fieldMethodInterceptor = FieldDeclaration(
            modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.FINAL),
            name = "methodInterceptor",
            type = interceptorClassType
        )
        proxyClassDeclaration.addField(fieldMethodInterceptor)

        /**
         * Field: Target Class
         */
        val fieldTargetClass = FieldDeclaration(
            modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.STATIC, JavaModifier.FINAL),
            name = "targetClass",
            type = TypeDeclaration.CLASS
        )
        proxyClassDeclaration.addField(fieldTargetClass)

        val fieldProxyClass = FieldDeclaration(
            modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.STATIC, JavaModifier.FINAL),
            name = "proxyClass",
            type = TypeDeclaration.CLASS
        )
        proxyClassDeclaration.addField(fieldProxyClass)

        /**
         * Fields: OriginalMethods & ProxyMethods
         */
        val fxMethodFieldNaming = fun (methodName: String, index: Int): String {
            return "${proxyClassName}\$\$$methodName\$$index\$Method"
        }
        val fxProxyMethodFieldNaming = fun (methodName: String, index: Int): String {
            return "${proxyClassName}\$\$$methodName\$$index\$ProxyMethod"
        }
        targetClassMethods.forEach { (methodName, methods) ->
            methods.forEachIndexed { index, method ->
                val correspondingMethodField = FieldDeclaration(
                    modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.STATIC, JavaModifier.FINAL),
                    name = fxMethodFieldNaming.invoke(methodName, index),
                    type = TypeDeclaration.METHOD
                )

                val correspondingMethodProxyField = FieldDeclaration(
                    modifiers = arrayOf(JavaModifier.PRIVATE, JavaModifier.STATIC, JavaModifier.FINAL),
                    name = fxProxyMethodFieldNaming.invoke(methodName, index),
                    type = TypeDeclaration.fromClass(MethodProxy::class.java)
                )

                proxyClassDeclaration.addField(correspondingMethodField)
                proxyClassDeclaration.addField(correspondingMethodProxyField)
            }
        }

        /**
         * Static Init
         */
        val fxSuperMethodNaming = fun (methodName: String, index: Int): String {
            return "${proxyClassName}\$\$$methodName\$$index\$Super"
        }
        proxyClassDeclaration.addMethod(MethodDeclaration.staticInitMethod {
            // Target Class
            storeField(
                targetClass = null,
                field = fieldTargetClass,
                isStatic = true
            ) {
                invokeMethod(
                    type = FunctionInvocationType.STATIC,
                    owner = Class::class.java,
                    methodName = "forName",
                    parameters = arrayOf(TypeDeclaration.STRING),
                    returnType = TypeDeclaration.CLASS
                ) {
                    loadConstant(targetClass.canonicalName)
                }
            }

            // Proxy Class
            storeField(
                targetClass = null,
                field = fieldProxyClass,
                isStatic = true
            ) {
                invokeMethod(
                    type = FunctionInvocationType.STATIC,
                    owner = Class::class.java,
                    methodName = "forName",
                    parameters = arrayOf(TypeDeclaration.STRING),
                    returnType = TypeDeclaration.CLASS
                ) {
                    loadConstant(proxyClassName)
                }
            }

            targetClassMethods.forEach { (methodName, methods) ->
                methods.forEachIndexed { index, method ->
                    // Method
                    storeField(
                        targetClass = null,
                        fieldName = fxMethodFieldNaming.invoke(methodName, index),
                        fieldType = TypeDeclaration.METHOD,
                        isStatic = true
                    ) {
                        invokeMethod(
                            type = FunctionInvocationType.STATIC,
                            owner = MethodProxy::class.java,
                            methodName = "getMethod",
                            parameters = TypeDeclaration.fromClasses(Class::class.java, MethodSignature::class.java),
                            returnType = TypeDeclaration.METHOD
                        ) {
                            loadStaticField(null, fieldTargetClass)

                            newInstance(
                                clazz = MethodSignature::class.java,
                                constructorParameters = arrayOf(TypeDeclaration.STRING, TypeDeclaration.STRING)
                            ) {
                                loadConstant(methodName)
                                loadConstant(method.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")") { TypeUtils.getDescriptor(it) })
                            }
                        }
                    }

                    // Method Proxy
                    storeField(
                        targetClass = null,
                        fieldName = fxProxyMethodFieldNaming.invoke(methodName, index),
                        fieldType = TypeDeclaration.fromClass(MethodProxy::class.java),
                        isStatic = true
                    ) {
                        invokeMethod(
                            type = FunctionInvocationType.STATIC,
                            owner = MethodProxy::class.java,
                            methodName = "getProxy",
                            parameters = TypeDeclaration.fromClasses(Class::class.java, MethodSignature::class.java),
                            returnType = TypeDeclaration.fromClass(MethodProxy::class.java)
                        ) {
                            loadStaticField(null, fieldProxyClass)

                            newInstance(
                                clazz = MethodSignature::class.java,
                                constructorParameters = arrayOf(TypeDeclaration.STRING, TypeDeclaration.STRING)
                            ) {
                                loadConstant(fxSuperMethodNaming.invoke(methodName, index))
                                loadConstant(method.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")") { TypeUtils.getDescriptor(it) })
                            }
                        }
                    }
                }
            }
        })

        /**
         * All super constructors
         */
        targetClassConstructors.forEach { constructor ->
            val superConstructorParameters = constructor.parameterTypes.mapIndexed { index, it ->
                ParameterDeclaration.fromType("var$index", TypeDeclaration.fromClass(it))
            }.toTypedArray()

            proxyClassDeclaration.addMethod(MethodDeclaration.constructor(
                parentClass = proxyClassDeclaration,
                modifier = JavaModifier.PUBLIC,
                // <init>(*superConstructorParameters, methodInterceptor)V
                parameters = superConstructorParameters + arrayOf(ParameterDeclaration.fromType("methodInterceptor", interceptorClassType)),
                superParameters = superConstructorParameters as Array<TypeDeclaration>,
                superArgs = {
                    for (i in 0..<constructor.parameterCount) {
                        loadMethodParameter(i)
                    }
                }
            ) {
                storeField(null, fieldMethodInterceptor.name, fieldMethodInterceptor.type, false) {
                    loadMethodParameter(constructor.parameterCount)
                }
            })
        }

        /**
         * All methods
         */
        targetClassMethods.forEach { (methodName, methods) ->
            methods.forEachIndexed { index, method ->
                val parameters = method.parameterTypes.mapIndexed { parameterIndex, it ->
                    ParameterDeclaration.fromType("var$parameterIndex", TypeDeclaration.fromClass(it))
                }.toTypedArray()
                val returnType = TypeDeclaration.fromClass(method.returnType)

                /**
                 * Impl Method
                 */
                proxyClassDeclaration.addMethod(
                    MethodDeclaration(
                        modifiers = arrayOf(if (Modifier.isPublic(method.modifiers)) JavaModifier.PUBLIC else JavaModifier.PROTECTED),
                        methodName = method.name,
                        parameters = parameters,
                        returnType = returnType
                    ) {
                        val objArrayType = TypeDeclaration(ASMUtils.OBJECT_CLASS, true, 1)

                        // Object[] args = new Object[]{};
                        defineFinalVariable("args", objArrayType)
                        loadArray(TypeDeclaration.OBJECT, 1, arrayOf(method.parameters?.size ?: 0))
                        storeVariable("args")

                        parameters.forEachIndexed { index, parameter ->
                            loadVariable("args")
                            storeArrayValue(TypeDeclaration.OBJECT, index = { loadConstant(index) }) {
                                if (parameter.isPrimitiveType()) {
                                    // Transform to packaged type
                                    val packagedType = TypeUtils.getPackagedPrimitiveType(parameter.type)
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

                        defineVariable("result", TypeDeclaration.OBJECT)
                        loadField(null, fieldMethodInterceptor.name, fieldMethodInterceptor.type.originalClass)

                        invokeMethod(function = MethodInterceptor::intercept) {
                            loadThis()
                            loadStaticField(null, fxMethodFieldNaming.invoke(methodName, index), Method::class.java)
                            loadVariable("args")
                            loadStaticField(null, fxProxyMethodFieldNaming.invoke(methodName, index), MethodProxy::class.java)
                        }

                        storeVariable("result")

                        if (!TypeUtils.isVoid(returnType.originalClass)) {
                            loadVariable("result")
                            if (returnType.isPrimitiveType()) {
                                // Cast to packaged type
                                val packagedClass = TypeUtils.getPackagedPrimitiveType(returnType.originalClass)
                                typeCast(TypeDeclaration.fromClass(packagedClass))
                                invokeMethod(
                                    owner = packagedClass,
                                    methodName = "${returnType.originalClass.canonicalName}Value",
                                    parameters = arrayOf(),
                                    returnType = returnType.originalClass
                                )
                            } else {
                                typeCast(returnType)
                            }
                        }
                        returnFunc()
                    }
                )

                /**
                 * Super calling
                 */
                val callSuperFuncName = fxSuperMethodNaming.invoke(methodName, index)
                proxyClassDeclaration.addMethod(
                    MethodDeclaration(
                        modifiers = arrayOf(if (Modifier.isPublic(method.modifiers)) JavaModifier.PUBLIC else JavaModifier.PROTECTED),
                        methodName = callSuperFuncName,
                        parameters = parameters,
                        returnType = returnType
                    ) {
                        invokeMethod(
                            type = FunctionInvocationType.SUPER,
                            owner = targetClass,
                            methodName = methodName,
                            parameters = parameters as Array<TypeDeclaration>,
                            returnType = returnType
                        ) {
                            parameters.forEachIndexed { index, parameter ->
                                loadMethodParameter(index)
                            }
                        }
                        returnFunc()
                    }
                )
            }
        }

        if (isDebuggingEnabled()) {
            FileOutputStream(File("").absolutePath + "/${proxyClassName}.class").use {
                it.write(VertexASM.writeClassToByteArray(proxyClassDeclaration))
            }
        }

        val generatedProxyClass = ReflectUtils.loadClassFromByteArray(
            className = proxyClassName,
            bytes = VertexASM.writeClassToByteArray(proxyClassDeclaration),
            classLoader = VertexProxyFactory::class.java.classLoader
        )

        val enhancedProxyClass = EnhancedClass.createNative(generatedProxyClass, false, generatedProxyClass.classLoader)
        return enhancedProxyClass
            .getConstructor(*constructorParameterTypes, MethodInterceptor::class.java)
            .invokeMethod(*constructorArgs, this.methodInterceptor) as T
    }

}