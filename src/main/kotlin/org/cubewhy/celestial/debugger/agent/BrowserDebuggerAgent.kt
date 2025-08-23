package org.cubewhy.celestial.debugger.agent

import org.cubewhy.celestial.debugger.entity.ApiServer
import java.lang.instrument.Instrumentation

@Suppress("UNUSED")
class BrowserDebuggerAgent {
    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            println("[BrowserDebugger] Welcome to LunarCN! https://lunarclient.top")
            println("[BrowserDebugger] Enjoy the FOSS power!")
//            val celestialVersion = System.getProperty("celestialVersion")
//            if (celestialVersion != null) {
//                // compare version
//                try {
//                    val result = compareVersions(celestialVersion, "3.2.1-SNAPSHOT")
//                    if (result < 0) {
//                        // unsupported version
//                        val msg = "Please update your Celestial version to 3.2.1 or higher!\nhttps://lunarclient.top"
//                        JOptionPane.showMessageDialog(null, msg)
//                        println("[BrowserDebugger] $msg")
//                        exitProcess(1)
//                    }
//                } catch (t: Throwable) {
//                    // ignored
//                }
//            }

            println("[BrowserDebugger] This software is complete open source, you can view the backend source at https://codeberg.org/earthsworth/lunar-api")
            println("[BrowserDebugger] And the frontend stuff at https://github.com/earthsworth/BrowserDebugger")

            if (agentArgs != "proprietary") {
                val apiAddress = System.getProperty("celestialApiAddress", "ws.lunarclient.top")
                val useEncryption = System.getProperty("celestialApiUseEncryption", "true").toBoolean()

                // set serviceOverride
                ApiServer(useEncryption, apiAddress).let { dto ->
                    println("[BrowserDebugger] Lunar-API Server info: ${dto.address} (${if (dto.encrypted) "encrypted" else "unencrypted"})")
                    updateOverride("Authenticator", "${dto.wsBase}/ws")
                    updateOverride("AssetServer", "${dto.wsBase}/ws")
                    updateOverride("Api", "${dto.httpBase}/api/lunar")
                    updateOverride("Styngr", "${dto.httpBase}/api/styngr")
                }
            } else {
                println("[BrowserDebugger] Using the proprietary backend")
            }

            println("[BrowserDebugger] Patching...")
            // apply transformer
            inst.addTransformer(ClassTransformer(), true)
        }
    }
}

private fun updateOverride(overrideName: String, defaultValue: String?) {
    val propName = "serviceOverride${overrideName}"
    if (!System.getProperties().containsKey(propName)) {
        defaultValue?.let {
            println("[BrowserDebugger] Apply default value of service override: ${overrideName}=${defaultValue}")
            System.setProperty(propName, it)
        }
    } else {
        val value = System.getProperty(propName)
        println("[BrowserDebugger] Use value of service override from user-defined properties: ${overrideName}=${value}")
    }
}