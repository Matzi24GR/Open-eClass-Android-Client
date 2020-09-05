package com.geomat.openeclassclient.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.work.ListenableWorker
import com.geomat.openeclassclient.BuildConfig
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.ActivityMainBinding
import com.geomat.openeclassclient.network.EclassApi
import com.geomat.openeclassclient.network.interceptor
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.repository.CalendarEventRepository
import com.geomat.openeclassclient.repository.CoursesRepository
import com.geomat.openeclassclient.repository.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = getSharedPreferences("login", Context.MODE_PRIVATE).getString("url","localhost")
        if (url != null ) {
            Timber.i("Got Url: $url")
            interceptor.setHost(url)
        }

        // Finding the Navigation Controller
        val navController = findNavController(R.id.fragment)

        // Setting Navigation Controller with the BottomNavigationView
        binding.bottomNav.setupWithNavController(navController)
        val topLevelDestinations = setOf(
            R.id.serverSelectFragment,
            R.id.homeFragment,
            R.id.announcementFragment,
            R.id.courseListFragment,
            R.id.calendarFragment
        )
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)



        val hasLoggedIn: Boolean = getSharedPreferences("login", Context.MODE_PRIVATE).getBoolean("hasLoggedIn", false)

        if ( !hasLoggedIn ) {
            binding.bottomNav.visibility = View.GONE
            navController.navigate(MainActivityDirections.actionGlobalServerSelectFragment())
        }

        val token = applicationContext.getSharedPreferences("login", Context.MODE_PRIVATE).getString("token", null)


        GlobalScope.launch{
            try {
                val result = EclassApi.MobileApi.checkTokenStatus(token!!).awaitResponse()
                if (result.body() == "EXPIRED") {
                    binding.TokenExpiredBanner.visibility = View.VISIBLE
                } else {
                    binding.TokenExpiredBanner.visibility = View.GONE
                }
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
        binding.DismissBanner.setOnClickListener {
            binding.TokenExpiredBanner.visibility = View.GONE
        }
        binding.ReLogIn.setOnClickListener {
            binding.TokenExpiredBanner.visibility = View.GONE
            navController.navigate(MainActivityDirections.actionGlobalServerSelectFragment())
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        if (BuildConfig.DEBUG) {
            menu.findItem(R.id.deleteButton).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logOutButton -> {
                getSharedPreferences("login", Context.MODE_PRIVATE).edit().putBoolean("hasLoggedIn", false).apply()
                findNavController(R.id.fragment).navigate(
                    R.id.mainActivity
                )
                val database = EClassDatabase.getInstance(this)
                val userInfoRepo = UserInfoRepository(database.userInfoDao)
                val courseRepo = CoursesRepository(database.coursesDao)
                val announcementRepo = AnnouncementRepository(database)
                GlobalScope.launch {
                    userInfoRepo.clear()
                    courseRepo.clear()
                    announcementRepo.clear()
                }
                true
            }
            R.id.deleteButton-> {
                val database = EClassDatabase.getInstance(this)
                val userInfoRepo = UserInfoRepository(database.userInfoDao)
                val courseRepo = CoursesRepository(database.coursesDao)
                val announcementRepo = AnnouncementRepository(database)
                GlobalScope.launch {
                    userInfoRepo.clear()
                    courseRepo.clear()
                    announcementRepo.clear()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
