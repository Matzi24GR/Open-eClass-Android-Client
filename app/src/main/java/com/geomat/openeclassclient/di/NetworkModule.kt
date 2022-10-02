package com.geomat.openeclassclient.di

import com.geomat.openeclassclient.network.ConverterFactorySelector
import com.geomat.openeclassclient.network.HostSelectionInterceptor
import com.geomat.openeclassclient.network.OpenEclassService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private const val BASE_URL = "https://localhost/"

    @Singleton
    @Provides
    fun provideHostSelectionInterceptor(): HostSelectionInterceptor = HostSelectionInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(hostSelectionInterceptor: HostSelectionInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(hostSelectionInterceptor)
        .writeTimeout(0, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ConverterFactorySelector())
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): OpenEclassService = retrofit.create(OpenEclassService::class.java)
}