package com.geomat.openeclassclient.network.dataTransferObjects

import org.jsoup.Jsoup
import timber.log.Timber

class CoursePageResponse(page: String) {
    private val document = Jsoup.parse(page)

    var imageUrl = ""
    var desc = ""
    var moreInfo = ""

    init {
        imageUrl = document.select(".banner-image").first()?.attr("src") ?: ""
        desc = document.select("#descr_content div").first()?.toString()?.replace("</p>","</p><br>") ?: ""
        moreInfo = document.select("#collapseDescription").first()?.toString()?.replace("</p>","</p><br>") ?: ""
        Timber.i(desc)
    }

}