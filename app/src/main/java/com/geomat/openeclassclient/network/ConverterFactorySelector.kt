package com.geomat.openeclassclient.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.moshi.Moshi
import kotlinx.serialization.ExperimentalSerializationApi
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type

@OptIn(ExperimentalSerializationApi::class)
class ConverterFactorySelector : Converter.Factory() {

    val xml: Converter.Factory = XML.asConverterFactory(MediaType.get("application/xml"))
    val json: Converter.Factory = MoshiConverterFactory.create(Moshi.Builder().build())

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            return when(annotation.annotationClass) {
                Json::class -> json.responseBodyConverter(type, annotations, retrofit)
                Xml::class -> xml.responseBodyConverter(type, annotations, retrofit)
                else -> null
            }
        }
        // There is no annotation so we cannot handle it
        return null
    }
}