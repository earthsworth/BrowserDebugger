package org.cubewhy.celestial.debugger.agent

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class ClassTransformer : ClassFileTransformer {
    override fun transform(
        loader: ClassLoader?, className: String?, classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?, classfileBuffer: ByteArray?
    ): ByteArray? {
        if (className == null || classBeingRedefined != null || classfileBuffer == null) return null

        return try {
            val cr = ClassReader(classfileBuffer)
            val analyzer = ClassAnalyzer()
            cr.accept(analyzer, 0)

            if (analyzer.matchesCriteria()) {
                println("[LunarDebugger] Find class: $className")
                val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
                val modifier = ClassModifier(cw, analyzer.booleanFieldName, className)
                cr.accept(modifier, 0)
                return cw.toByteArray()
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}