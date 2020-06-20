package com.example.openeclassclient.network

import android.content.Context
import android.content.SharedPreferences
import com.example.openeclassclient.MainActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://localhost/"

var interceptor = HostSelectionInterceptor()

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
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
            Call<String>

    @FormUrlEncoded
    @POST("/modules/mobile/mlogin.php")
    fun getToken(@Field("uname")username: String,
                 @Field("pass")password: String):
            Call<String>

}

interface JsonApiService {

    //Hidden Open EClass Api

    @Headers("X-Requested-With: xmlhttprequest")
    @GET("/modules/announcements/myannouncements.php")
    fun getAnnouncements(@Header("Cookie")token: String):
            Call<AnnouncementResponse>

    @Headers("X-Requested-With: xmlhttprequest")
    @GET("/main/calendar_data.php?from=0&to=1683013600000")
    fun getCalendar(@Header("Cookie")token: String):
            Call<CalendarResponse>

}

object eClassApi {
    val MobileApi: MobileApiService by lazy {
        retrofit.create(MobileApiService::class.java)
    }
    val JsonApi: JsonApiService by lazy {
        retrofitJson.create(JsonApiService::class.java)
    }
}
