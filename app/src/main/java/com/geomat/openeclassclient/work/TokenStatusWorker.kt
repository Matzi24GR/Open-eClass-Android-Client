package com.geomat.openeclassclient.work

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.network.OpenEclassService
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

class TokenStatusWorker @Inject constructor(appContext: Context, params: WorkerParameters, private val openEclassService: OpenEclassService): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "TokenStatusWorker"
    }

    override suspend fun doWork(): Result {

        try {
            val result = openEclassService.checkTokenStatus().awaitResponse()

            return if (result.isSuccessful) {
                val builder = NotificationCompat.Builder(applicationContext, "status_channel")
                    .setSmallIcon(R.drawable.ic_baseline_announcement_24)
                    .setContentTitle("Open Eclass")
                    .setContentText(result.body().toString())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(applicationContext)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(0, builder.build())
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.i(e)
            return Result.retry()
        }
    }

}