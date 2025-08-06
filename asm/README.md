# Vertex ASM

Vertex ASM is a JVM bytecode manipulation library based on `org.objectweb.asm`. 

Compared to the original complex operations, Vertex ASM provides semantic support, making it easy to generate corresponding bytecode.

In addition, Vertex ASM further extends some methods from the `org.objectweb.asm` library. For details, see `ASMExtensions.kt`.

## 0x00 Import

Maven:
```xml
<dependency>
    <groupId>com.lovelycatv.vertex</groupId>
    <artifactId>asm</artifactId>
    <version>${LATEST-VERSION}</version>
</dependency>
```

Gradle:
```groovy
implementation "com.lovelycatv.vertex:asm:${LATEST-VERSION}"
```

Gradle (Kotlin):
```kotlin
implementation("com.lovelycatv.vertex:asm:${LATEST-VERSION}")
```

## 0x01 Declarations

To better represent code structures (such as classes, fields, methods, etc.), the concept of `Declaration` is introduced. 

Vertex ASM can generate corresponding bytecode from Declarations, while allowing developers to write a class in this simpler way.

Here are the Declarations:

| ClassName            | Description                                                                 |
|----------------------|-----------------------------------------------------------------------------|
| ClassDeclaration     | Describing a class, including all fields, methods, etc.                     |
| FieldDeclaration     | Describing a field of class.                                                |
| MethodDeclaration    | Describing a method and contains the code within its body.                  |
| ParameterDeclaration | The sub-class of TypeDeclaration used for describing a parameter of method. |
| TypeDeclaration      | Describing a Class<?> object, also describing a ClassType in Vertex ASM.    |

## 0x02 Simple Example

404 Not Found

## 0x03 MileStones

Vertex ASM aims to support all bytecode instructions. 

Below is a table showing the currently implemented and unimplemented bytecodes. 

Reference: [https://docs.oracle.com/javase/specs/jvms/se17/html/index.html]()

| Impl | ByteCode | Mnemonic        | Ref                        | Description                                                                                                                                                |
|------|----------|-----------------|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ❌    | 0x00     | nop             |                            | Nothing to do.                                                                                                                                             |
| ✅    | 0x01     | aconst_null     | `LoadNull`                 | Push null onto stack.                                                                                                                                      |
| ✅    | 0x02     | iconst_m1       | `LoadConstantValue`        | Push int -1 onto stack.                                                                                                                                    |
| ✅    | 0x03     | iconst_0        | `LoadConstantValue`        | Push int 0 onto stack.                                                                                                                                     |
| ✅    | 0x04     | iconst_1        | `LoadConstantValue`        | Push int 1 onto stack.                                                                                                                                     |
| ✅    | 0x05     | iconst_2        | `LoadConstantValue`        | Push int 2 onto stack.                                                                                                                                     |
| ✅    | 0x06     | iconst_3        | `LoadConstantValue`        | Push int 3 onto stack.                                                                                                                                     |
| ✅    | 0x07     | iconst_4        | `LoadConstantValue`        | Push int 4 onto stack.                                                                                                                                     |
| ✅    | 0x08     | iconst_5        | `LoadConstantValue`        | Push int 5 onto stack.                                                                                                                                     |
| ✅    | 0x09     | lconst_0        | `LoadConstantValue`        | Push long 0 onto stack.                                                                                                                                    |
| ✅    | 0x0a     | lconst_1        | `LoadConstantValue`        | Push long 1 onto stack.                                                                                                                                    |
| ✅    | 0x0b     | fconst_0        | `LoadConstantValue`        | Push float 0 onto stack.                                                                                                                                   |
| ✅    | 0x0c     | fconst_1        | `LoadConstantValue`        | Push float 1 onto stack.                                                                                                                                   |
| ✅    | 0x0d     | fconst_2        | `LoadConstantValue`        | Push float 2 onto stack.                                                                                                                                   |
| ✅    | 0x0e     | dconst_0        | `LoadConstantValue`        | Push double 0 onto stack.                                                                                                                                  |
| ✅    | 0x0f     | dconst_1        | `LoadConstantValue`        | Push double 0 onto stack.                                                                                                                                  |
| ✅    | 0x10     | bipush          | `LoadConstantValue`        | Push a byte value [-128, 127] onto stack.                                                                                                                  |
| ✅    | 0x11     | sipush          | `LoadConstantValue`        | Push a short int value [-32768, 32767] onto stack.                                                                                                         |
| ✅    | 0x12     | ldc             | `LoadConstantValue`        | Push a int, float or String value onto stack.                                                                                                              |
| ❌    | 0x13     | ldc_w           | `LoadConstantValue`        | Push a int, float or String value onto stack. (wide index)                                                                                                 |
| ❌    | 0x14     | ldc2_w          | `LoadConstantValue`        | Push a long, double value onto stack. (wide index)                                                                                                         |
| ✅    | 0x15     | iload           | `LoadLocalVariable`        | Load a int local variable onto stack.                                                                                                                      |
| ✅    | 0x16     | lload           | `LoadLocalVariable`        | Load a long local variable onto stack.                                                                                                                     |
| ✅    | 0x17     | fload           | `LoadLocalVariable`        | Load a float local variable onto stack.                                                                                                                    |
| ✅    | 0x18     | dload           | `LoadLocalVariable`        | Load a double local variable onto stack.                                                                                                                   |
| ✅    | 0x19     | aload           | `LoadLocalVariable`        | Load a reference type local variable onto stack.                                                                                                           |
| ❌    | 0x1a     | iload_0         | `LoadLocalVariable`        | Load and push first int local variable onto stack.                                                                                                         |
| ❌    | 0x1b     | iload_1         | `LoadLocalVariable`        | Load second int local variable onto stack.                                                                                                                 |
| ❌    | 0x1c     | iload_2         | `LoadLocalVariable`        | Load third int local variable onto stack.                                                                                                                  |
| ❌    | 0x1d     | iload_3         | `LoadLocalVariable`        | Load fourth int local variable onto stack.                                                                                                                 |
| ❌    | 0x1e     | lload_0         | `LoadLocalVariable`        | Load first long local variable onto stack.                                                                                                                 |
| ❌    | 0x1f     | lload_1         | `LoadLocalVariable`        | Load second long local variable onto stack.                                                                                                                |
| ❌    | 0x20     | lload_2         | `LoadLocalVariable`        | Load third long local variable onto stack.                                                                                                                 |
| ❌    | 0x21     | lload_3         | `LoadLocalVariable`        | Load fourth long local variable onto stack.                                                                                                                |
| ❌    | 0x22     | fload_0         | `LoadLocalVariable`        | Load first float local variable onto stack.                                                                                                                |
| ❌    | 0x23     | fload_1         | `LoadLocalVariable`        | Load second float local variable onto stack.                                                                                                               |
| ❌    | 0x24     | fload_2         | `LoadLocalVariable`        | Load third float local variable onto stack.                                                                                                                |
| ❌    | 0x25     | fload_3         | `LoadLocalVariable`        | Load fourth float local variable onto stack.                                                                                                               |
| ❌    | 0x26     | dload_0         | `LoadLocalVariable`        | Load first double local variable onto stack.                                                                                                               |
| ❌    | 0x27     | dload_1         | `LoadLocalVariable`        | Load second double local variable onto stack.                                                                                                              |
| ❌    | 0x28     | dload_2         | `LoadLocalVariable`        | Load third double local variable onto stack.                                                                                                               |
| ❌    | 0x29     | dload_3         | `LoadLocalVariable`        | Load fourth double local variable onto stack.                                                                                                              |
| ❌    | 0x2a     | aload_0         | `LoadLocalVariable`        | Load first reference type local variable onto stack.                                                                                                       |
| ❌    | 0x2b     | aload_1         | `LoadLocalVariable`        | Load second reference type local variable onto stack.                                                                                                      |
| ❌    | 0x2c     | aload_2         | `LoadLocalVariable`        | Load third reference type local variable onto stack.                                                                                                       |
| ❌    | 0x2d     | aload_3         | `LoadLocalVariable`        | Load fourth reference type local variable onto stack.                                                                                                      |
| ✅    | 0x2e     | iaload          | `LoadArrayValue`           | Load a value of the specified index of int array onto stack.                                                                                               |
| ✅    | 0x2f     | laload          | `LoadArrayValue`           | Load a value of the specified index of long array onto stack.                                                                                              |                                                                                           
| ✅    | 0x30     | faload          | `LoadArrayValue`           | Load a value of the specified index of float array onto stack.                                                                                             |                                                                                           
| ✅    | 0x31     | daload          | `LoadArrayValue`           | Load a value of the specified index of double array onto stack.                                                                                            |                                                                                           
| ✅    | 0x32     | aaload          | `LoadArrayValue`           | Load a value of the specified index of reference type array onto stack.                                                                                    |                                                                                           
| ✅    | 0x33     | baload          | `LoadArrayValue`           | Load a value of the specified index of byte or boolean array onto stack.                                                                                   |                                                                                           
| ✅    | 0x34     | caload          | `LoadArrayValue`           | Load a value of the specified index of char array onto stack.                                                                                              |                                                                                           
| ✅    | 0x35     | saload          | `LoadArrayValue`           | Load a value of the specified index of short array onto stack.                                                                                             |                                                                                           
| ✅    | 0x36     | istore          | `StoreLocalVariable`       | Store a int value from top of stack into a specified local variable.                                                                                       |
| ✅    | 0x37     | lstore          | `StoreLocalVariable`       | Store a long value from top of stack into a specified local variable.                                                                                      |
| ✅    | 0x38     | fstore          | `StoreLocalVariable`       | Store a float value from top of stack into a specified local variable.                                                                                     |
| ✅    | 0x39     | dstore          | `StoreLocalVariable`       | Store a double value from top of stack into a specified local variable.                                                                                    |
| ✅    | 0x3a     | astore          | `StoreLocalVariable`       | Store a reference type value from top of stack into a specified local variable.                                                                            |
| ❌    | 0x3b     | istore_0        | `StoreLocalVariable`       | Store a int value from top of stack into first local variable.                                                                                             |
| ❌    | 0x3c     | istore_1        | `StoreLocalVariable`       | Store a int value from top of stack into second local variable.                                                                                            |
| ❌    | 0x3d     | istore_2        | `StoreLocalVariable`       | Store a int value from top of stack into third local variable.                                                                                             |
| ❌    | 0x3e     | istore_3        | `StoreLocalVariable`       | Store a int value from top of stack into fourth local variable.                                                                                            |
| ❌    | 0x3f     | lstore_0        | `StoreLocalVariable`       | Store a long value from top of stack into first local variable.                                                                                            |
| ❌    | 0x40     | lstore_1        | `StoreLocalVariable`       | Store a long value from top of stack into second local variable.                                                                                           |
| ❌    | 0x41     | lstore_2        | `StoreLocalVariable`       | Store a long value from top of stack into third local variable.                                                                                            |
| ❌    | 0x42     | lstore_3        | `StoreLocalVariable`       | Store a long value from top of stack into fourth local variable.                                                                                           |
| ❌    | 0x43     | fstore_0        | `StoreLocalVariable`       | Store a float value from top of stack into first local variable.                                                                                           |
| ❌    | 0x44     | fstore_1        | `StoreLocalVariable`       | Store a float value from top of stack into second local variable.                                                                                          |
| ❌    | 0x45     | fstore_2        | `StoreLocalVariable`       | Store a float value from top of stack into third local variable.                                                                                           |
| ❌    | 0x46     | fstore_3        | `StoreLocalVariable`       | Store a float value from top of stack into fourth local variable.                                                                                          |
| ❌    | 0x47     | dstore_0        | `StoreLocalVariable`       | Store a double value from top of stack into first local variable.                                                                                          |
| ❌    | 0x48     | dstore_1        | `StoreLocalVariable`       | Store a double value from top of stack into second local variable.                                                                                         |
| ❌    | 0x49     | dstore_2        | `StoreLocalVariable`       | Store a double value from top of stack into third local variable.                                                                                          |
| ❌    | 0x4a     | dstore_3        | `StoreLocalVariable`       | Store a double value from top of stack into fourth local variable.                                                                                         |
| ❌    | 0x4b     | astore_0        | `StoreLocalVariable`       | Store a reference type value from top of stack into first local variable.                                                                                  |
| ❌    | 0x4c     | astore_1        | `StoreLocalVariable`       | Store a reference type value from top of stack into second local variable.                                                                                 |
| ❌    | 0x4d     | astore_2        | `StoreLocalVariable`       | Store a reference type value from top of stack into third local variable.                                                                                  |
| ❌    | 0x4e     | astore_3        | `StoreLocalVariable`       | Store a reference type value from top of stack into fourth local variable.                                                                                 |
| ✅    | 0x4f     | iastore         | `StoreArrayValue`          | Store a int value from top of stack into specified index of int array.                                                                                     |
| ✅    | 0x50     | lastore         | `StoreArrayValue`          | Store a long value from top of stack into specified index of int array.                                                                                    |
| ✅    | 0x51     | fastore         | `StoreArrayValue`          | Store a float value from top of stack into specified index of float array.                                                                                 |
| ✅    | 0x52     | dastore         | `StoreArrayValue`          | Store a double value from top of stack into specified index of double array.                                                                               |
| ✅    | 0x53     | aastore         | `StoreArrayValue`          | Store a reference type value from top of stack into specified index of reference type array.                                                               |
| ✅    | 0x54     | bastore         | `StoreArrayValue`          | Store a byte or boolean value from top of stack into specified index of byte or boolean array.                                                             |
| ✅    | 0x55     | castore         | `StoreArrayValue`          | Store a char value from top of stack into specified index of char array.                                                                                   |
| ✅    | 0x56     | sastore         | `StoreArrayValue`          | Store a short value from top of stack into specified index of short array.                                                                                 |
| ✅    | 0x57     | pop             | `PopValue`                 | Pop a value from top of stack.                                                                                                                             |
| ✅    | 0x58     | pop2            | `PopValue`                 | Pop a long/double or 2 values from top of stack.                                                                                                           |
| ❌    | 0x59     | dup             |                            | Duplicate the top operand stack value.                                                                                                                     |
| ❌    | 0x5a     | dup_x1          |                            | Duplicate the top operand stack value and insert two values down.                                                                                          |
| ❌    | 0x5b     | dup_x2          |                            | Duplicate the top operand stack value and insert two or three values down.                                                                                 |
| ❌    | 0x5c     | dup2            |                            | Duplicate the top one or two operand stack values.                                                                                                         |
| ❌    | 0x5d     | dup2_x1         |                            | Duplicate the top one or two operand stack values and insert two or three values down.                                                                     |
| ❌    | 0x5e     | dup2_x2         |                            | Duplicate the top one or two operand stack values and insert two, three, or four values down.                                                              |
| ❌    | 0x5f     | swap            |                            | Swap the top two operand stack values                                                                                                                      |
| ❌    | 0x60     | iadd            |                            | Add two int at top-stack and push the result onto stack.                                                                                                   |
| ❌    | 0x61     | ladd            |                            | Add two long at top-stack and push the result onto stack.                                                                                                  |
| ❌    | 0x62     | fadd            |                            | Add two float at top-stack and push the result onto stack.                                                                                                 |
| ❌    | 0x63     | dadd            |                            | Add two doubles at top-stack and push the result onto stack.                                                                                               |
| ❌    | 0x64     | is              |                            | Subtract two int at top-stack and push the result onto stack.                                                                                              |
| ❌    | 0x65     | ls              |                            | Subtract two long at top-stack and push the result onto stack.                                                                                             |
| ❌    | 0x66     | fs              |                            | Subtract two float at top-stack and push the result onto stack.                                                                                            |
| ❌    | 0x67     | ds              |                            | Subtract two doubles at top-stack and push the result onto stack.                                                                                          |
| ❌    | 0x68     | imul            |                            | Multiply two int at top-stack and push the result onto stack.                                                                                              |
| ❌    | 0x69     | lmul            |                            | Multiply two long at top-stack and push the result onto stack.                                                                                             |
| ❌    | 0x6a     | fmul            |                            | Multiply two float at top-stack and push the result onto stack.                                                                                            |
| ❌    | 0x6b     | dmul            |                            | Multiply two doubles at top-stack and push the result onto stack.                                                                                          |
| ❌    | 0x6c     | idiv            |                            | Divide two int at top-stack and push the result onto stack.                                                                                                |
| ❌    | 0x6d     | ldiv            |                            | Divide two long at top-stack and push the result onto stack.                                                                                               |
| ❌    | 0x6e     | fdiv            |                            | Divide two float at top-stack and push the result onto stack.                                                                                              |
| ❌    | 0x6f     | ddiv            |                            | Divide two doubles at top-stack and push the result onto stack.                                                                                            |
| ❌    | 0x70     | irem            |                            | Get remainder between two int at top-stack and push the result onto stack.                                                                                 |
| ❌    | 0x71     | lrem            |                            | Get remainder between two long at top-stack and push the result onto stack.                                                                                |
| ❌    | 0x72     | frem            |                            | Get remainder between two float at top-stack and push the result onto stack.                                                                               |
| ❌    | 0x73     | drem            |                            | Get remainder between two double at top-stack and push the result onto stack.                                                                              |
| ❌    | 0x74     | ineg            |                            | Get negative int value at top-stack and push the result onto stack.                                                                                        |
| ❌    | 0x75     | lneg            |                            | Get negative long value at top-stack and push the result onto stack.                                                                                       |
| ❌    | 0x76     | fneg            |                            | Get negative float value at top-stack and push the result onto stack.                                                                                      |
| ❌    | 0x77     | dneg            |                            | Get negative double value at top-stack and push the result onto stack.                                                                                     |
| ❌    | 0x78     | ishl            |                            | Shift the int at top-stack left by a specified position and push the result onto stack.                                                                    |
| ❌    | 0x79     | lshl            |                            | Shift the long at top-stack left by a specified position and push the result onto stack.                                                                   |
| ❌    | 0x7a     | ishr            |                            | Shift the int at top-stack right by a specified position and push the result onto stack.                                                                   |
| ❌    | 0x7b     | lshr            |                            | Shift the long at top-stack right by a specified position and push the result onto stack.                                                                  |
| ❌    | 0x7c     | iushr           |                            | Shift the int at top-stack right by a specified position ignoring sign and push the result onto stack.                                                     |
| ❌    | 0x7d     | lushr           |                            | Shift the long at top-stack right by a specified position ignoring sign and push the result onto stack.                                                    |
| ❌    | 0x7e     | iand            |                            | Perform bit-AND operation on the two int values at top-stack and push the result onto stack.                                                               |
| ❌    | 0x7f     | land            |                            | Perform bit-AND operation on the two long values at top-stack and push the result onto stack.                                                              |
| ❌    | 0x80     | ior             |                            | Perform bit-OR operation on the two int values at top-stack and push the result onto stack.                                                                |
| ❌    | 0x81     | lor             |                            | Perform bit-OR operation on the two long values at top-stack and push the result onto stack.                                                               |
| ❌    | 0x82     | ixor            |                            | Perform bit-XOR operation on the two int values at top-stack and push the result onto stack.                                                               |
| ❌    | 0x83     | lxor            |                            | Perform bit-XOR operation on the two long values at top-stack and push the result onto stack.                                                              |
| ❌    | 0x84     | iinc            |                            | Increase the specified int variable by a specified value.                                                                                                  |
| ❌    | 0x85     | i2l             |                            | Convert int on top-stack to long.                                                                                                                          |
| ❌    | 0x86     | i2f             |                            | Convert int on top-stack to double.                                                                                                                        |
| ❌    | 0x87     | i2d             |                            | Convert int on top-stack to double.                                                                                                                        |
| ❌    | 0x88     | l2i             |                            | Convert long on top-stack to int.                                                                                                                          |
| ❌    | 0x89     | l2f             |                            | Convert long on top-stack to float.                                                                                                                        |
| ❌    | 0x8a     | l2d             |                            | Convert long on top-stack to double.                                                                                                                       |
| ❌    | 0x8b     | f2i             |                            | Convert float on top-stack to int.                                                                                                                         |
| ❌    | 0x8c     | f2l             |                            | Convert float on top-stack to long.                                                                                                                        |
| ❌    | 0x8d     | f2d             |                            | Convert float on top-stack to double.                                                                                                                      |
| ❌    | 0x8e     | d2i             |                            | Convert double on top-stack to int.                                                                                                                        |
| ❌    | 0x8f     | d2l             |                            | Convert double on top-stack to long.                                                                                                                       |
| ❌    | 0x90     | d2f             |                            | Convert double on top-stack to float.                                                                                                                      |
| ❌    | 0x91     | i2b             |                            | Convert int on top-stack to byte.                                                                                                                          |
| ❌    | 0x92     | i2c             |                            | Convert int on top-stack to char.                                                                                                                          |
| ❌    | 0x93     | i2s             |                            | Convert int on top-stack to short.                                                                                                                         |
| ❌    | 0x94     | lcmp            |                            | Compare two long values at top-stack and push the result (1/0/-1) onto stack.                                                                              |
| ❌    | 0x95     | fcmpl           |                            | Compare two float values at top-stack and push the result (1/0/-1) onto stack. If any is NaN, push -1 onto stack.                                          |
| ❌    | 0x96     | fcmpg           |                            | Compare two float values at top-stack and push the result (1/0/-1) onto stack. If any is NaN, push 1 onto stack.                                           |
| ❌    | 0x97     | dcmpl           |                            | Compare two double values at top-stack and push the result (1/0/-1) onto stack. If any is NaN, push -1 onto stack.                                         |
| ❌    | 0x98     | dcmpg           |                            | Compare two double values at top-stack and push the result (1/0/-1) onto stack. If any is NaN, push 1 onto stack.                                          |
| ❌    | 0x99     | ifeq            |                            | Jump when the int value at top-stack is equal to 0.                                                                                                        |
| ❌    | 0x9a     | ifne            |                            | Jump when the int value at top-stack is not equal to 0.                                                                                                    |
| ❌    | 0x9b     | iflt            |                            | Jump when the int value at top-stack is less than 0.                                                                                                       |
| ❌    | 0x9c     | lfge            |                            | Jump when the int value at top-stack is great than or equal to 0.                                                                                          |
| ❌    | 0x9d     | lfgt            |                            | Jump when the int value at top-stack is great than 0.                                                                                                      |
| ❌    | 0x9e     | ifle            |                            | Jump when the int value at top-stack is less than or equal to 0.                                                                                           |
| ❌    | 0x9f     | if_icmpeq       |                            | Compare two int values at top-stack, and jump when the result is equal to 0.                                                                               |
| ❌    | 0xa0     | if_icmpne       |                            | Compare two int values at top-stack, and jump when the result is not equal to 0.                                                                           |
| ❌    | 0xa1     | if_icmplt       |                            | Compare two int values at top-stack, and jump when the result is less than 0.                                                                              |
| ❌    | 0xa2     | if_icmpge       |                            | Compare two int values at top-stack, and jump when the result is great than or equal to 0.                                                                 |
| ❌    | 0xa3     | if_icmpgt       |                            | Compare two int values at top-stack, and jump when the result is great than 0.                                                                             |
| ❌    | 0xa4     | if_icmple       |                            | Compare two int values at top-stack, and jump when the result is less than or equal to 0.                                                                  |
| ❌    | 0xa5     | if_acmpeq       |                            | Compare two reference type values at the top of the stack, jump when the result is equal.                                                                  |
| ❌    | 0xa6     | if_acmpne       |                            | Compare two reference type values at the top of the stack, jump when the result is not equal.                                                              |
| ❌    | 0xa7     | goto            |                            | A goto instruction is type safe iff its target operand is a valid branch target.                                                                           |
| ❌    | 0xa8     | jsr             |                            | Jump to the specified 16-bit offset position and push the next instruction address of jsr_w to the top of the stack.                                       |
| ❌    | 0xa9     | ret             |                            | Jump to the return address saved in local variable.                                                                                                        |
| ❌    | 0xaa     | tableswitch     |                            | Used for switch conditional jump, with continuous case values.                                                                                             |
| ❌    | 0xab     | lookupswitch    |                            | Used for switch conditional jump, case values are discontinuous.                                                                                           |
| ✅    | 0xac     | ireturn         | `DefineReturn`             | Return a int value.                                                                                                                                        |
| ✅    | 0xad     | lreturn         | `DefineReturn`             | Return a long value.                                                                                                                                       |
| ✅    | 0xae     | freturn         | `DefineReturn`             | Return a float value.                                                                                                                                      |
| ✅    | 0xaf     | dreturn         | `DefineReturn`             | Return a double value.                                                                                                                                     |
| ✅    | 0xb0     | areturn         | `DefineReturn`             | Return a reference type value.                                                                                                                             |
| ✅    | 0xb1     | return          | `DefineReturn`             | Return void.                                                                                                                                               |
| ✅    | 0xb2     | getstatic       | `LoadFieldValue`           | Get value of a static field and push onto stack.                                                                                                           |
| ✅    | 0xb3     | putstatic       | `StoreFieldValue`          | Put value to a static field.                                                                                                                               |
| ✅    | 0xb4     | getfield        | `LoadFieldValue`           | Get value of a non-static field and push onto stack.                                                                                                       |                                                           
| ✅    | 0xb5     | putfield        | `StoreFieldValue`          | Put value to a non-static field.                                                                                                                           |                                                           
| ✅    | 0xb6     | invokevirtual   | `DefineFunctionInvocation` | Invoke a function and push the return value onto stack if exists.                                                                                          |
| ✅    | 0xb7     | invokespecial   | `DefineFunctionInvocation` | Invoke a super or private function and push the return value onto stack if exists.                                                                         |
| ✅    | 0xb8     | invokestatic    | `DefineFunctionInvocation` | Invoke a static function and push the return value onto stack if exists.                                                                                   |
| ✅    | 0xb9     | invokeinterface | `DefineFunctionInvocation` | Invoke a function from interface and push the return value onto stack if exists.                                                                           |
| ✅    | 0xba     | ------------    | ------------               | ------------                                                                                                                                               |
| ✅    | 0xbb     | new             | `DefineNewInstance`        | New instance and push onto stack.                                                                                                                          |
| ✅    | 0xbc     | newarray        | `LoadArray`                | Create a new primitive type array and push onto stack.                                                                                                     |
| ✅    | 0xbd     | anewarray       | `LoadArray`                | Create a new reference type array and push onto stack.                                                                                                     |
| ✅    | 0xbe     | arraylength     |                            | Get length of array on top-stack and push the result onto stack.                                                                                           |
| ❌    | 0xbf     | athrow          |                            | Throw exception or error.                                                                                                                                  |
| ✅    | 0xc0     | checkcast       | `DefineTypeCast`           | Check whether object is of given type.                                                                                                                     |
| ❌    | 0xc1     | instanceof      |                            | Determine if object is of given type and push 1 onto stack if true, otherwise 0.                                                                           |
| ❌    | 0xc2     | monitorenter    |                            | A monitorenter instruction is type safe iff one can validly pop a type matching reference off the incoming operand stack yielding the outgoing type state. |
| ❌    | 0xc3     | monitorexit     |                            | A monitorexit instruction is type safe iff the equivalent monitorenter instruction is type safe.                                                           |
| ❌    | 0xc4     | wide            |                            | Widen 8bits index to 16bits.                                                                                                                               |
| ✅    | 0xc5     | multianewarray  | `LoadArray`                | Create a multi-dimensions array (primitive or reference type) and push onto stack.                                                                         |
| ❌    | 0xc6     | ifnull          |                            | Jump if the value on top-stack is null.                                                                                                                    |
| ❌    | 0xc7     | ifnonnull       |                            | Jump if the value on top-stack is non-null.                                                                                                                |
| ❌    | 0xc8     | goto_w          |                            | Jump without condition. (wide index)                                                                                                                       |
| ❌    | 0xc9     | jsr_            |                            | Jump to the specified 32-bit offset position and push the next instruction address of jsr_w to the top of the stack.                                       |
