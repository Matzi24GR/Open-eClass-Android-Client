package com.geomat.openeclassclient.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CalendarEvent::class,Course::class,UserInfo::class,Announcement::class], version = 7,  exportSchema = false)
abstract class EClassDatabase: RoomDatabase () {

    abstract val calendarEventDao: CalendarEventDao
    abstract val coursesDao: CoursesDao
    abstract val userInfoDao: UserInfoDao
    abstract val announcementDao: AnnouncementDao

    companion object {

        @Volatile
        private var INSTANCE: EClassDatabase? = null

        fun getInstance(context: Context): EClassDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance ==null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EClassDatabase::class.java,
                        "eClass_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}