package com.geomat.openeclassclient.screens.Calendar

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.FragmentCalendarBinding
import com.geomat.openeclassclient.repository.CalendarEventRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val repository = CalendarEventRepository(EClassDatabase.getInstance(requireContext()).calendarEventDao)

        val data = repository.allEvents
        val adapter = CalendarEventAdapter()
        binding.calendarRecyclerView.adapter = adapter

        GlobalScope.launch { repository.refreshData(token!!) }
        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                adapter.submitList(data.value)
            }
        })

    }
}
