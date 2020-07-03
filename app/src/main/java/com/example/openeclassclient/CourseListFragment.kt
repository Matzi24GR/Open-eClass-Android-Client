package com.example.openeclassclient

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.openeclassclient.network.eClassApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_list, container, false)

        val CoursesTextView = view.findViewById<TextView>(R.id.coursesTextView)
        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)
        if (token != null) {
            eClassApi.MobileApi.getCourses(token).enqueue(object :
                Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    CoursesTextView.text = "Failure: " + t.message
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    CoursesTextView.text = response.body()
                }

            })
        }

        return view
    }
}
