package com.geomat.openeclassclient.network

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "identity")
class ServerInfoResponse {

    @Element(name = "institute")
    lateinit var institute: Institute

    @Element
    lateinit var platform: Platform

    @Element
    lateinit var administrator: Administrator

    @Element(name = "auth")
    var AuthTypeList: List<AuthType> = mutableListOf()

}

@Xml(name = "institute")
class Institute {


    @Attribute
    var name: String = ""

    @Attribute
    var url: String = ""

}

@Xml(name = "platform")
class Platform {

    @Attribute
    var name: String = ""

    @Attribute
    var version: String = ""

}

@Xml(name = "administrator")
class Administrator {

    @Attribute
    var name: String = ""
}

@Xml(name = "auth")
class AuthType {

    @Attribute
    var title: String = ""

    @Attribute
    var url: String = ""
}