package org.cubewhy.celestial.debugger.agent

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ClassModifier(cv: ClassVisitor, private val booleanFieldName: String?, private val className: String) :
    ClassVisitor(Opcodes.ASM9, cv) {

    override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "<clinit>" && descriptor == "()V") {
            return ClinitModifier(mv, booleanFieldName, className)
        }
        return mv
    }
}

class ClinitModifier(mv: MethodVisitor, private val booleanFieldName: String?, private val className: String) :
    MethodVisitor(Opcodes.ASM9, mv) {

    override fun visitInsn(opcode: Int) {
        if (opcode == Opcodes.RETURN) {
            println("Modify <clinit> to enable debug mode ($booleanFieldName = false)")
            mv.visitInsn(Opcodes.ICONST_0) // 压入 false (0)
            mv.visitFieldInsn(Opcodes.PUTSTATIC, className, booleanFieldName, "Z") // 赋值给静态字段
        }
        super.visitInsn(opcode)
    }
}
