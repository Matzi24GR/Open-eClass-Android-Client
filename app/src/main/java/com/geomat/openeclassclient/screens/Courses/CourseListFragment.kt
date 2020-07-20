package com.geomat.openeclassclient.screens.Courses

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.FragmentCourseListBinding
import com.geomat.openeclassclient.repository.CoursesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CourseListFragment : Fragment() {

    private lateinit var binding: FragmentCourseListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = CoursesRepository(EClassDatabase.getInstance(requireContext()).coursesDao)

        val adapter = CourseListAdapter()
        binding.courseRecyclerView.adapter = adapter

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val data = repo.allCourses

        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                adapter.submitList(data.value)
            }
        })

        GlobalScope.launch { repo.refreshData(token!!) }
    }
}
