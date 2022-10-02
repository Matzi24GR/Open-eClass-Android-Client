package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.*
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.OpenEclassService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class AnnouncementRepository @Inject constructor(private val courseDao: CoursesDao, private val announcementDao: AnnouncementDao, private val openEclassService: OpenEclassService) {

    val allAnnouncements: LiveData<List<Announcement>> = Transformations.map(announcementDao.getAllAnnouncementsWithCourseNames()){
        it.asDomainModel()
    }

    val unreadCount = announcementDao.getUnreadCount()

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            try {
                //Get Announcements
                val announcements = getAllAnnouncements()
                //Insert Announcements
                val result = announcementDao.insertAll(announcements)
                //Update announcements that failed to insert
                val toUpdate = mutableListOf<DatabaseAnnouncement>()
                for (i in result.indices) {
                    if (result[i] == -1L) {
                        toUpdate.add(announcements[i])
                    }
                }
                announcementDao.updateAll(toUpdate)
                //Remove Deleted Announcements
                val toRetain = announcements.map {it.id}
                announcementDao.clearNotInList(toRetain)
                //Initialize Read Table
                val readList = toRetain.map {
                    return@map DatabaseAnnouncementReadStatus(it,false)
                }
                announcementDao.insertAllReadStatus(readList)
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
    }

    private suspend fun getAllAnnouncements(): List<DatabaseAnnouncement> {
        // -- Get Feeds --
        val feeds = mutableListOf<String>()
        //System Announcement Feed
        feeds.add("/rss.php")
        //Course Announcement Feeds
        feeds.addAll(courseDao.getAllFeedUrls())

        // -- Get Announcements From Feeds --
        val announcements = mutableListOf<DatabaseAnnouncement>()
        feeds.forEach{
            val announcementsResponse = openEclassService.getRssFeed(it).await()
            announcements.addAll(announcementsResponse.asDatabaseModel())
        }
        return announcements
    }

    suspend fun setRead(announcement: Announcement) {
        withContext(Dispatchers.IO) {
            announcementDao.updateReadStatus(DatabaseAnnouncementReadStatus(announcement.id,true))
        }
    }

    suspend fun setAllRead() {
        withContext(Dispatchers.IO) {
            announcementDao.setAllRead()
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            announcementDao.clear()
        }
    }

}