package com.geomat.openeclassclient.network.dataTransferObjects

import nl.adaptivity.xmlutil.serialization.XmlSerialName

@kotlinx.serialization.Serializable
@XmlSerialName("identity","","")
data class ServerInfoResponse (
    val institute: Institute,
    val platform: Platform,
    val administrator: Administrator,
    val authTypeList: List<AuthType> = mutableListOf()
)

@kotlinx.serialization.Serializable
@XmlSerialName("institute","","")
data class Institute (
    var name: String,
    var url: String
)

@kotlinx.serialization.Serializable
@XmlSerialName("platform","","")
data class Platform (
    var name: String,
    var version: String
)

@kotlinx.serialization.Serializable
@XmlSerialName("administrator","","")
data class Administrator (
    val name: String
)

@kotlinx.serialization.Serializable
@XmlSerialName("auth","","")
data class AuthType (
    var title: String = "",
    var url: String= ""
)