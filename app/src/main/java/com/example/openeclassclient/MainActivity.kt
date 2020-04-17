package com.example.openeclassclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.openeclassclient.network.eClassApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Finding the Navigation Controller
        val navController = findNavController(R.id.fragment)

        // Setting Navigation Controller with the BottomNavigationView
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bttm_nav)
        bottomNavView.setupWithNavController(navController)

        val userText = findViewById<EditText>(R.id.editText)
        val passText = findViewById<EditText>(R.id.editText2)
        val button = findViewById<Button>(R.id.button)
        var token: String? = null

        button.setOnClickListener() {
            val username = userText.text.toString()
            val password = passText.text.toString()
            eClassApi.MobileApi.getToken(username, password)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {}
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        token = response.body().toString()
                        button.text = token
                        this@MainActivity.getPreferences(Context.MODE_PRIVATE).edit().putString("token",token).apply()
                    }
                })
        }

    }

}
