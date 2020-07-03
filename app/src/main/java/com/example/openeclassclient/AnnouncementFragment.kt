package com.example.openeclassclient

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.openeclassclient.network.AnnouncementResponse
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
        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        eClassApi.JsonApi.getAnnouncements("PHPSESSID=" + token).enqueue(
            object: Callback<AnnouncementResponse> {
            override fun onFailure(call: Call<AnnouncementResponse>, t: Throwable) {
                announcementTextView.text = "Failure: " + t.message
            }

            override fun onResponse(call: Call<AnnouncementResponse>, response: Response<AnnouncementResponse>) {
                announcementTextView.text = response.body()!!.aaData[9][1]
            }

        })

        return view
    }
}
