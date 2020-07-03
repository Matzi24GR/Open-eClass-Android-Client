package com.example.openeclassclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.openeclassclient.databinding.ActivityMainBinding
import com.example.openeclassclient.network.interceptor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val url = getSharedPreferences("login", Context.MODE_PRIVATE).getString("url","localhost")
        interceptor.setHost(url!!)

        // Finding the Navigation Controller
        val navController = findNavController(R.id.fragment)

        // Setting Navigation Controller with the BottomNavigationView
        binding.bottomNav.setupWithNavController(navController)

        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(bottom_nav.menu, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)



        val hasLoggedIn: Boolean = getSharedPreferences("login", Context.MODE_PRIVATE).getBoolean("hasLoggedIn", false)

        if ( !hasLoggedIn ) {
            binding.bottomNav.visibility = View.GONE
            supportActionBar!!.hide()
            navController.navigate(R.id.tologinFragment)
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
                findNavController(R.id.fragment).navigate(R.id.mainActivity)
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
