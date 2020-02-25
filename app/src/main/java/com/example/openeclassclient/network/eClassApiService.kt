package com.example.openeclassclient.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://openeclass.uom.gr/"

private  val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface  eClassApiService {
    @FormUrlEncoded
    @POST("modules/mobile/mcourses.php")
    fun getInfo(@Field("token")token: String):
            Call<String>
}

object eClassApi {
    val retrofitService: eClassApiService by lazy {
        retrofit.create(eClassApiService::class.java)
    }
}