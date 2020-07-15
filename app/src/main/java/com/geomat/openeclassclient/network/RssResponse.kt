package com.geomat.openeclassclient.network

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "rss")
class RssResponse {

    @Attribute(name="version")
    var version: String = ""

    @Element(name = "channel")
    lateinit var Channel: Channel
}

@Xml(name = "channel")
class Channel {

    @Element(name = "atom:link")
    lateinit var atomLink: AtomLink

    @PropertyElement
    var title: String = ""

    @PropertyElement
    var link: String = ""

    @PropertyElement
    var description: String = ""

    @PropertyElement
    var lastBuildDate: String = ""

    @PropertyElement
    var language: String = ""

    @Element(name="item")
    lateinit var announcementList: List<Announcement>
}

@Xml(name = "atom:link")
class AtomLink {
    @Attribute
    var href: String = ""

    @Attribute
    var rel: String = ""

    @Attribute
    var type: String = ""
}

@Xml(name = "item")
class Announcement {

    @PropertyElement
    var title: String = ""

    @PropertyElement
    var link: String = ""

    @PropertyElement
    var description: String = ""

    @PropertyElement
    var pubDate: String = ""

}