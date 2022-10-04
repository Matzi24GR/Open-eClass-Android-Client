package com.geomat.openeclassclient.network.dataTransferObjects

import com.geomat.openeclassclient.ui.screens.documents.Document
import org.jsoup.Jsoup

fun parseDocumentPageResponse(page: String): List<Document> {
    val document = Jsoup.parse(page)
    val fileList = document.select("td:eq(1) a")
    return fileList.map {
        Document(
            isDirectory = it.attr("href").contains("openDir="),
            name = it.text(),
            id = it.attr("href").substringAfter("openDir=").replace("openDir=",""),
            link = it.attr("href")
        )
    }
}