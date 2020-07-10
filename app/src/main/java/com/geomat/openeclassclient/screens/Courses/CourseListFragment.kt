package com.geomat.openeclassclient.screens.Courses

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.network.CourseResponse
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.repository.CoursesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_list, container, false)

        val repo = CoursesRepository(EClassDatabase.getInstance(requireContext()).coursesDao)

        val recyclerView = view.findViewById<RecyclerView>(R.id.course_recycler_view)
        val adapter = CourseListAdapter()
        recyclerView.adapter = adapter

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val data = repo.allCourses

        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                adapter.submitList(data.value)
            }
        })

        GlobalScope.launch { repo.refreshData(token!!) }

        return view
    }
}
