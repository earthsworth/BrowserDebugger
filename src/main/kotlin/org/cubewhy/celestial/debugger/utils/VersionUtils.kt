package org.cubewhy.celestial.debugger.utils

import org.apache.maven.artifact.versioning.ComparableVersion

fun compareVersions(v1: String, v2: String): Int {
    val version1 = ComparableVersion(v1)
    val version2 = ComparableVersion(v2)
    return version1.compareTo(version2)
}