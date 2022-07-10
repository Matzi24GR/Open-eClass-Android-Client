package com.geomat.openeclassclient.network.DataTransferObjects

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml
class ToolsResponse {

    @Element(name = "toolgroup")
    lateinit var toolGroupList: List<ToolGroup>

    @Element(name = "status")
    lateinit var status: Status
}

@Xml(name = "toolgroup")
class ToolGroup {

    @Attribute(name="name")
    var name: String = ""

    @Element(name="tool")
    lateinit var toolList: List<Tool>
}

@Xml(name = "tool")
class Tool {
    @Attribute(name = "name")
    var name: String = ""
    @Attribute(name = "link")
    var link: String = ""
    @Attribute(name = "redirect")
    var redirect: String = ""
    @Attribute(name = "type")
    var type: String = ""
    @Attribute(name = "active")
    var active: String = ""
}

@Xml(name = "status")
class Status {
    @Attribute(name = "name")
    var name: String = ""
}

fun ToolsResponse.toSingleSeparatedString(): String {
    return toolGroupList.last().toolList.joinToString(";") { it.type }
}