package com.geomat.openeclassclient.screens.Calendar

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.CalendarEvent
import com.geomat.openeclassclient.database.CalendarEventDao
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.network.CalendarResponse
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.repository.CalendarEventRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val repository = CalendarEventRepository(EClassDatabase.getInstance(requireContext()).calendarEventDao)
        val recyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        val data = repository.allEvents

        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                val adapter = CalendarEventAdapter(data.value!!)
                recyclerView.adapter = adapter
            }
        })

        eClassApi.JsonApi.getCalendar("PHPSESSID=" + token).enqueue(
            object: Callback<CalendarResponse> {
            override fun onFailure(call: Call<CalendarResponse>, t: Throwable) {
                //CalendarTextView.text = "Failure: " + t.message
            }

            override fun onResponse(call: Call<CalendarResponse>, response: Response<CalendarResponse>) {
                val responseList = response.body()?.result
                var DBlist = mutableListOf<CalendarEvent>()

                for (i in responseList!!.indices) {
                    with(responseList[i]){
                        DBlist.add(CalendarEvent(
                            this.id.toLong(),
                            this.title,
                            this.start.toLong(),
                            this.end.toLong(),
                            this.content,
                            this.event_group,
                            this.Class,
                            this.event_type,
                            this.course,
                            this.url
                        ))
                    }
                }

                GlobalScope.launch { repository.insertAll(DBlist) }
            }

            })

        return view
    }

}
