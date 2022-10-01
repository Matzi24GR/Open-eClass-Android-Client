package com.geomat.openeclassclient.network.DataTransferObjects

import nl.adaptivity.xmlutil.serialization.XmlSerialName


@kotlinx.serialization.Serializable
@XmlSerialName("tools","","")
data class ToolsResponse (
    val toolGroupList: List<ToolGroup>,
    val status: Status
)

@kotlinx.serialization.Serializable
@XmlSerialName("toolgroup","","")
data class ToolGroup (
    val name: String,
    val toolList: List<Tool>
)

@kotlinx.serialization.Serializable
@XmlSerialName("tool","","")
data class Tool (
    val name: String,
    val link: String,
    val redirect: String,
    val type: String,
    val active: String
)

@kotlinx.serialization.Serializable
@XmlSerialName("status","","")
data class Status (
    val name: String
)

fun ToolsResponse.toSingleSeparatedString(): String {
    return toolGroupList.last().toolList.joinToString(";") { it.type }
}