package com.geomat.openeclassclient.di

import android.content.Context
import androidx.room.Room
import com.geomat.openeclassclient.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): EClassDatabase {
        return Room.databaseBuilder(
            appContext,
            EClassDatabase::class.java,
            "eClass_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAnnouncementDao(database: EClassDatabase): AnnouncementDao {
        return database.announcementDao
    }
    @Provides
    fun provideCalendarEventDao(database: EClassDatabase): CalendarEventDao {
        return database.calendarEventDao
    }
    @Provides
    fun provideCourseDao(database: EClassDatabase): CoursesDao {
        return database.coursesDao
    }

    @Provides
    fun provideUserInfoDao(database: EClassDatabase): UserInfoDao {
        return database.userInfoDao
    }


}