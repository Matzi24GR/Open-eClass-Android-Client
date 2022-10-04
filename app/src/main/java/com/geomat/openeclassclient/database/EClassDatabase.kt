package com.geomat.openeclassclient.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DatabaseCalendarEvent::class,DatabaseCalendarSyncId::class,DatabaseCourse::class,DatabaseFeedUrl::class,DatabaseUserInfo::class,DatabaseAnnouncement::class,DatabaseAnnouncementReadStatus::class], version = 24,  exportSchema = true)
abstract class EClassDatabase: RoomDatabase () {

    abstract val calendarEventDao: CalendarEventDao
    abstract val coursesDao: CoursesDao
    abstract val userInfoDao: UserInfoDao
    abstract val announcementDao: AnnouncementDao

}