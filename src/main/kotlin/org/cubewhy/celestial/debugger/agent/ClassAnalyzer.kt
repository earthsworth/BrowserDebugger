package org.cubewhy.celestial.debugger.agent

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

class ClassAnalyzer : ClassVisitor(Opcodes.ASM9) {
    private var staticFieldCount = 0
    private var hasBooleanField = false
    private var stringFieldCount = 0
    var booleanFieldName: String? = null
    private var hasTargetEnum = false

    override fun visitField(access: Int, name: String, descriptor: String, signature: String?, value: Any?): FieldVisitor? {
        if (access and Opcodes.ACC_STATIC != 0) {
            staticFieldCount++
            when (descriptor) {
                "Z" -> { // boolean 类型
                    hasBooleanField = true
                    booleanFieldName = name
                }
                "Ljava/lang/String;" -> stringFieldCount++ // String 类型
            }
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
        if (access and Opcodes.ACC_ENUM != 0) {
            hasTargetEnum = true
        }
    }

    fun matchesCriteria(): Boolean {
        return staticFieldCount == 6 && hasBooleanField && stringFieldCount == 5 && hasTargetEnum
    }
}
