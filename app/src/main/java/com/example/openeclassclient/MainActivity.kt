package com.example.openeclassclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.openeclassclient.network.eClassApi
import com.example.openeclassclient.network.eClassApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        eClassApi.retrofitService.getInfo("4ktuc6hrcgbvpa06dd9hnesuaa").enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                textView.text = "Failure: " + t.message
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                textView.text = response.body()
            }

        })
    }
}
