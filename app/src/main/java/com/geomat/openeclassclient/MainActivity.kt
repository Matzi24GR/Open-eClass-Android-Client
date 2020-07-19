package com.geomat.openeclassclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.ActivityMainBinding
import com.geomat.openeclassclient.network.interceptor
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.repository.CalendarEventRepository
import com.geomat.openeclassclient.repository.CoursesRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
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

        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)



        val hasLoggedIn: Boolean = getSharedPreferences("login", Context.MODE_PRIVATE).getBoolean("hasLoggedIn", false)

        if ( !hasLoggedIn ) {
            binding.bottomNav.visibility = View.GONE
            navController.navigate(MainActivityDirections.actionGlobalServerSelectFragment())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logOutButton -> {
                getSharedPreferences("login", Context.MODE_PRIVATE).edit().putBoolean("hasLoggedIn", false).apply()
                findNavController(R.id.fragment).navigate(
                    R.id.mainActivity
                )
                val calendarRepo = CalendarEventRepository(EClassDatabase.getInstance(this).calendarEventDao)
                val courseRepo = CoursesRepository(EClassDatabase.getInstance(this).coursesDao)
                val announcementRepo = AnnouncementRepository(EClassDatabase.getInstance(this))
                GlobalScope.launch {
                    calendarRepo.clear()
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
