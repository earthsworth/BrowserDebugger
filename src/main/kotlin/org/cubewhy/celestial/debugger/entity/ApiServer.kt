package org.cubewhy.celestial.debugger.entity

data class ApiServer(
    val encrypted: Boolean,
    val address: String
) {
    val wsBase = (if (encrypted) "wss" else "ws") + "://$address"
    val httpBase = (if (encrypted) "https" else "http") + "://$address"
}