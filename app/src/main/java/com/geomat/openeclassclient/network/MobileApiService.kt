package com.geomat.openeclassclient.network

import com.geomat.openeclassclient.network.DataTransferObjects.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://localhost/"

var interceptor = HostSelectionInterceptor()

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .writeTimeout(0,TimeUnit.SECONDS)
    .build()

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val tikXml = TikXml.Builder().exceptionOnUnreadXml(false).build()

private val retrofitHtml = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(TikXmlConverterFactory.create(tikXml))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

private  val retrofitJson = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()


interface  MobileApiService {

    //Provided Api For Mobile App

    @FormUrlEncoded
    @POST("/modules/mobile/mcourses.php")
    fun getCourses(@Field("token")token: String):
            Call<CourseResponse>

    @FormUrlEncoded
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

    @FormUrlEncoded
    @POST
    fun getApiEnabled(@Url url: String,
                      @Field("uname")username: String = "",
                      @Field("pass")password: String = ""):
            Call<String>

    @GET("/modules/mobile/midentity.php")
    fun getServerInfo():
            Call<ServerInfoResponse>

    @GET
    fun getRssFeed(@Url url: String):
            Call<RssResponse>

    @FormUrlEncoded
    @POST("/modules/mobile/mlogin.php?logout")
    fun logout(@Field("token")token: String):
            Call<String>
}

interface JsonApiService {

    //Undocumented Json Open EClass Api

    @Headers("X-Requested-With: xmlhttprequest")
    @GET("/modules/announcements/myannouncements.php")
    fun getAnnouncements(@Header("Cookie")token: String):
            Call<MyAnnouncementResponse>

    @Headers("X-Requested-With: xmlhttprequest")
    @GET("/main/calendar_data.php?from=0&to=1683013600000")
    fun getCalendar(@Header("Cookie")token: String):
            Call<CalendarResponse>

}

interface HtmlParserService {
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
    val JsonApi: JsonApiService by lazy {
        retrofitJson.create(JsonApiService::class.java)
    }
    val HtmlParser: HtmlParserService by lazy {
        retrofitHtml.create(HtmlParserService::class.java)
    }
}
