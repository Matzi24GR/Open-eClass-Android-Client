package com.example.openeclassclient

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.openeclassclient.network.eClassApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnnouncementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_announcement, container, false)

        val announcementTextView = view.findViewById<TextView>(R.id.announcementTextView)
        val token = activity?.getPreferences(Context.MODE_PRIVATE)?.getString("token",null)

        eClassApi.JsonApi.getAnnouncements("PHPSESSID=" + token).enqueue(object :
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                announcementTextView.text = "Failure: " + t.message
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                announcementTextView.text = response.body()
            }

        })

        return view
    }
}
