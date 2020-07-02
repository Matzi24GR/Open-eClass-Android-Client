package com.example.openeclassclient

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.openeclassclient.network.CalendarResponse
import com.example.openeclassclient.network.eClassApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val token = activity?.getPreferences(Context.MODE_PRIVATE)?.getString("token",null)

        eClassApi.JsonApi.getCalendar("PHPSESSID=" + token).enqueue(
            object: Callback<CalendarResponse> {
            override fun onFailure(call: Call<CalendarResponse>, t: Throwable) {
                //CalendarTextView.text = "Failure: " + t.message
            }

            override fun onResponse(call: Call<CalendarResponse>, response: Response<CalendarResponse>) {
                //CalendarTextView.text = response.body().toString()
            }

            })

        return view
    }

}
