package com.geomat.openeclassclient.network

import com.geomat.openeclassclient.network.DataTransferObjects.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface OpenEclassService {

    /*
    *   Official Mobile Api
    *   https://dev.openeclass.org/projects/openeclass/wiki/%CE%A7%CF%81%CE%AE%CF%83%CE%B7_%CF%84%CE%BF%CF%85_Mobile_API
    */

    @FormUrlEncoded
    @Xml
    @POST("/modules/mobile/mcourses.php")
    fun getCourses(@Field("token")token: String):
            Call<CourseResponse>

    @FormUrlEncoded
    @Xml
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

    @POST
    @FormUrlEncoded
    fun getApiEnabled(@Url url: String,
                      @Field("uname")username: String = "",
                      @Field("pass")password: String = ""):
            Call<String>

    @Xml
    @GET("/modules/mobile/midentity.php")
    fun getServerInfo():
            Call<ServerInfoResponse>

    @GET
    @Xml
    fun getRssFeed(@Url url: String):
            Call<RssResponse>

    @FormUrlEncoded
    @POST("/modules/mobile/mlogin.php?logout")
    fun logout(@Field("token")token: String):
            Call<String>

    /*
    *   Exposed Json and Rss Feeds
    */

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

    /*
    *   Web Scraping
    */

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