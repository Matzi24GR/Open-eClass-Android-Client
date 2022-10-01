package com.geomat.openeclassclient.network

import com.geomat.openeclassclient.network.DataTransferObjects.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.ExperimentalSerializationApi
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import timber.log.Timber
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://localhost/"

var interceptor = HostSelectionInterceptor()

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .writeTimeout(0,TimeUnit.SECONDS)
    .build()

annotation class Json
annotation class Xml


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

private  val retrofit = Retrofit.Builder()
    .addConverterFactory(ConverterFactorySelector())
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()


interface  MobileApiService {

    //Provided Api For Mobile App

    @FormUrlEncoded @Xml
    @POST("/modules/mobile/mcourses.php")
    fun getCourses(@Field("token")token: String):
            Call<CourseResponse>

    @FormUrlEncoded @Xml
    @POST("modules/mobile/mtools.php")
    fun getTools(@Field("token")token: String, @Query("course")courseId: String):
            Call<ToolsResponse>

    @FormUrlEncoded
    @POST("/modules/mobile/mlogin.php")
    fun checkTokenStatus(@Field("token")token: String):
            Call<String>

    @FormUrlEncoded
    @POST("/modules/mobile/mlogin.php")
    fun getToken(@Field("uname")username: String,
                 @Field("pass")password: String):
            Call<String>

    @POST @FormUrlEncoded
    fun getApiEnabled(@Url url: String,
                      @Field("uname")username: String = "",
                      @Field("pass")password: String = ""):
            Call<String>

    @Xml
    @GET("/modules/mobile/midentity.php")
    fun getServerInfo():
            Call<ServerInfoResponse>

    @GET @Xml
    fun getRssFeed(@Url url: String):
            Call<RssResponse>

    @FormUrlEncoded
    @POST("/modules/mobile/mlogin.php?logout")
    fun logout(@Field("token")token: String):
            Call<String>

    //Undocumented Json Open EClass Api

    @Json
    @Headers("X-Requested-With: xmlhttprequest")
    @GET("/modules/announcements/myannouncements.php")
    fun getAnnouncements(@Header("Cookie")token: String):
            Call<MyAnnouncementResponse>

    @Json
    @Headers("X-Requested-With: xmlhttprequest")
    @GET("/main/calendar_data.php?from=0&to=1683013600000")
    fun getCalendar(@Header("Cookie")token: String):
            Call<CalendarResponse>

    // Web Scraping
    @GET("/main/portfolio.php")
    fun getMainPage(@Header("Cookie")token: String): Call<String>

    @GET("/modules/announcements/")
    fun getAnnouncementPage(@Header("Cookie")token: String,
                            @Query("course")courseId: String
    ): Call<String>

    @GET("/courses/{id}/")
    fun getCoursePage(@Header("Cookie")token: String, @Path("id") courseId: String): Call<String>

    @GET("/modules/document/")
    fun getDocumentsPage(
        @Header("Cookie")token: String,
        @Query("course")courseId:String,
        @Query("openDir")openDir:String?
    ): Call<String>

    @Streaming
    @GET
    suspend fun downloadFile(@Header("Cookie")token: String, @Url fileUrl: String?): ResponseBody
}

sealed class Download {
    data class Progress(val percent: Int) : Download()
    data class Finished(val file: File) : Download()
    data class Cancelled(val msg: String = ""): Download()
}

fun ResponseBody.downloadToFileWithProgress(directory: File, folder: String, filename: String): Flow<Download> =
    flow {
        emit(Download.Progress(0))

        val file = File(directory, "/$folder/${filename}.${contentType()?.subtype()}")

        Timber.i("Cached File: ${file.length()}, ToDownloadFile: ${contentLength()}")

        if (file.length() == contentLength()) {
            emit(Download.Finished(file))
            close()
        } else {
            byteStream().use { inputStream ->
                file.outputStream().use { outputStream ->
                    val totalBytes = contentLength()
                    val data = ByteArray(8_192)
                    var progressBytes = 0L

                    while (true) {
                        val bytes = inputStream.read(data)
                        if (bytes == -1) {
                            break
                        }
                        outputStream.write(data, 0, bytes)
                        progressBytes += bytes

                        emit(Download.Progress(percent = ((progressBytes * 100) / totalBytes).toInt()))
                    }
                }
            }
            emit(Download.Finished(file))
        }

    }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

object EclassApi {
    val MobileApi: MobileApiService by lazy {
        retrofit.create(MobileApiService::class.java)
    }
}
