package com.example.openeclassclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.openeclassclient.network.HostSelectionInterceptor
import com.example.openeclassclient.network.interceptor
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url = getSharedPreferences("login", Context.MODE_PRIVATE).getString("url","localhost")
        interceptor.setHost(url!!)

        // Finding the Navigation Controller
        val navController = findNavController(R.id.fragment)

        // Setting Navigation Controller with the BottomNavigationView
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bttm_nav)
        bottomNavView.setupWithNavController(navController)

        val hasLoggedIn: Boolean = getPreferences(Context.MODE_PRIVATE).getBoolean("hasLoggedIn", false)

        if ( !hasLoggedIn ) {
            bottomNavView.visibility = View.GONE
            supportActionBar!!.hide()
            navController.navigate(R.id.loginFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOutButton -> {
                getPreferences(Context.MODE_PRIVATE).edit().putBoolean("hasLoggedIn", false).apply()
                findNavController(R.id.fragment).navigate(R.id.mainActivity)
            }
        }
        return true
    }
}
