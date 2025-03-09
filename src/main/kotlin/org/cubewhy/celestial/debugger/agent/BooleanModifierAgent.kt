package org.cubewhy.celestial.debugger.agent

import java.lang.instrument.*
import org.objectweb.asm.*
import java.security.ProtectionDomain

@Suppress("UNUSED")
class BooleanModifierAgent {
    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            // set serviceOverride
            if (agentArgs != "useCustom") {
                println("[LunarDebugger] Enable LunarCN servers...")
                System.setProperty("serviceOverrideAuthenticator", "wss://ws.lunarclient.top/ws")
                System.setProperty("serviceOverrideAssetServer", "wss://ws.lunarclient.top/ws")
            } else {
                println("[LunarDebugger] The default websockets were disabled, please manual config it in JVM args")
            }
            println("[LunarDebugger] Patch LunarClient...")
            inst.addTransformer(ClassTransformer(), true)
        }
    }
}

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
