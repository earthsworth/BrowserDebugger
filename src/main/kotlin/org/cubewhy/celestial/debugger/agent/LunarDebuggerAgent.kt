package org.cubewhy.celestial.debugger.agent

import org.cubewhy.celestial.debugger.entity.ApiServer
import org.cubewhy.celestial.debugger.utils.compareVersions
import java.lang.instrument.Instrumentation
import javax.swing.JOptionPane
import kotlin.system.exitProcess

@Suppress("UNUSED")
class LunarDebuggerAgent {
    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            println("[LunarDebugger] Welcome to LunarCN! https://lunarclient.top")
            println("[LunarDebugger] Enjoy the FOSS power!")
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

            println("[LunarDebugger] This software is complete open source, you can view the backend source at https://codeberg.org/earthsworth/lunar-api")
            println("[LunarDebugger] And the frontend stuff at https://github.com/earthsworth/LunarDebugger")

            if (agentArgs != "proprietary") {
                val apiAddress = System.getProperty("celestialApiAddress", "ws.lunarclient.top")
                val useEncryption = System.getProperty("celestialApiUseEncryption", "true").toBoolean()

                // set serviceOverride
                ApiServer(useEncryption, apiAddress).let { dto ->
                    println("[LunarDebugger] Lunar-API Server info: ${dto.address} (${if (dto.encrypted) "encrypted" else "unencrypted"})")
                    updateOverride("Authenticator", "${dto.wsBase}/ws")
                    updateOverride("AssetServer", "${dto.wsBase}/ws")
                    updateOverride("Api", "${dto.httpBase}/api/lunar")
                    updateOverride("Styngr", "${dto.httpBase}/api/styngr")
                }
            } else {
                println("[LunarDebugger] Using the proprietary backend")
            }

            println("[LunarDebugger] Patching...")
            // apply transformer
            inst.addTransformer(ClassTransformer(), true)
        }
    }
}

private fun updateOverride(overrideName: String, defaultValue: String?) {
    if (!System.getProperties().containsKey(overrideName)) {
        defaultValue?.let {
            println("[LunarDebugger] Apply default value of service override: ${overrideName}=${defaultValue}")
            System.setProperty("serviceOverride${overrideName}", it)
        }
    } else {
        val value = System.getProperty(overrideName)
        println("[LunarDebugger] Use value of service override from user-defined properties: ${overrideName}=${value}")
    }
}