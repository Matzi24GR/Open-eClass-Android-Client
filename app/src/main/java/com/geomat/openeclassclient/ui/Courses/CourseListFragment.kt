package com.geomat.openeclassclient.ui.Courses

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.FragmentAnnouncementBinding
import com.geomat.openeclassclient.databinding.FragmentCourseListBinding
import com.geomat.openeclassclient.repository.CoursesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.AssertionError
import javax.inject.Inject

@AndroidEntryPoint
class CourseListFragment : Fragment() {

    //TODO Add a notice when no courses returned

    private var _binding: FragmentCourseListBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var repo: CoursesRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val repo = CoursesRepository(EClassDatabase.getInstance(requireContext()).coursesDao)

        val adapter = CourseListAdapter()
        binding.courseRecyclerView.adapter = adapter

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val data = repo.allCourses

        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                adapter.submitList(data.value)
            }
        })

        GlobalScope.launch {
            try {
                repo.refreshData(token!!)
            } catch (e: AssertionError){
                Timber.i(e)
            }
        }
        //GlobalScope.launch { repo.refreshData(token!!) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
