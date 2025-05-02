package org.cubewhy.celestial.debugger.agent

import org.apache.maven.artifact.versioning.ComparableVersion
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain
import javax.swing.JOptionPane
import kotlin.system.exitProcess

data class ServerDTO(
    val encrypted: Boolean,
    val address: String
) {
    val wsBase = (if (encrypted) "wss" else "ws") + "://$address"
    val httpBase = (if (encrypted) "https" else "http") + "://$address"
}

fun compareVersions(v1: String, v2: String): Int {
    val version1 = ComparableVersion(v1)
    val version2 = ComparableVersion(v2)
    return version1.compareTo(version2)
}

@Suppress("UNUSED")
class BooleanModifierAgent {
    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            val celestialVersion = System.getProperty("celestialVersion")
            if (celestialVersion != null) {
                // compare version
                try {
                    val result = compareVersions(celestialVersion, "3.2.1-SNAPSHOT")
                    if (result < 0) {
                        // unsupported version
                        val msg = "Please update your Celestial version to 3.2.1 or higher!\nhttps://lunarclient.top"
                        JOptionPane.showMessageDialog(null, msg)
                        println("[LunarDebugger] $msg")
                        exitProcess(1)
                    }
                } catch (t: Throwable) {
                    // ignored
                }
            }
            // set serviceOverride
            when (agentArgs) {
                "custom" -> {
                    println("[LunarDebugger] The default websockets were disabled, please manual config it in JVM args")
                    null
                }

                "local", "localhost" -> {
                    ServerDTO(false, "127.0.0.1:8080")
                }

                "backup-prod1" -> {
                    ServerDTO(true, "backup-prod.lunarcn.top")
                }

                "backup-prod2" -> {
                    ServerDTO(true, "lccn-prod.cubewhy.eu.org")
                }

                "backup-prod3" -> {
                    ServerDTO(true, "lccn.howecraft.eu.org")
                }

                "dev" -> {
                    ServerDTO(true, "dev.lunarcn.top")
                }

                "prod", null -> {
                    ServerDTO(true, "ws.lunarclient.top")
                }

                else -> {
                    throw IllegalArgumentException("[LunarDebugger] Unknown agentArgs: $agentArgs\nOnly [custom|local|dev|prod|backup-prod(1-3)|<empty>] supported")
                }
            }?.let { dto ->
                println("[LunarDebugger] Server info: ${dto.address} (${if (dto.encrypted) "encrypted" else "unencrypted"})")
                System.setProperty("serviceOverrideAuthenticator", "${dto.wsBase}/ws")
                System.setProperty("serviceOverrideAssetServer", "${dto.wsBase}/ws")
                System.setProperty("serviceOverrideApi", "${dto.httpBase}/api/lunar")
                System.setProperty("serviceOverrideStyngr", "${dto.httpBase}/api/styngr")
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
