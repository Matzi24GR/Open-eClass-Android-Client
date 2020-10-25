package com.geomat.openeclassclient.ui.Calendar

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.DatabaseCalendarSyncId
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.databinding.CalendarSelectBottomSheetBinding
import com.geomat.openeclassclient.databinding.FragmentCalendarBinding
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.repository.CalendarEventRepository
import com.geomat.openeclassclient.util.asSyncAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    //TODO Add a notice when no events returned
    //TODO Ask for calendar permissions
    //TODO Courses must be refreshed for calendar to refresh

    private lateinit var binding: FragmentCalendarBinding
    private val viewModel: CalendarViewModel by viewModels()
    @Inject
    lateinit var repo: CalendarEventRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
            .getString("token", null)

        val data = repo.allEvents
        val adapter = CalendarEventAdapter()
        binding.calendarRecyclerView.adapter = adapter

        GlobalScope.launch { repo.refreshData(token!!) }
        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                adapter.submitList(data.value)
            }
        })

        //Delete Button
        binding.button2.setOnClickListener {
            viewModel.deleteAllEvents(requireContext().contentResolver)
        }
        //Sync Button
        binding.button.setOnClickListener {


            //Setup Bottom Sheet
            val dialogBinding =
                CalendarSelectBottomSheetBinding.inflate(layoutInflater, binding.root, false)
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)

            //Setup ListView
            val calendarList = viewModel.getCalendars(requireContext().contentResolver)
            dialogBinding.authMethodListView.adapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                calendarList.map { it.displayName })
            bottomSheetDialog.show()

            //On Item Click
            dialogBinding.authMethodListView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->

                    bottomSheetDialog.dismiss()

                    val selectedCal = calendarList[position].id

                    GlobalScope.launch {
                        val toSync = repo.getNotSyncedEvents()
                        toSync.forEach {
                            viewModel.insertEventToCalendar(
                                it,
                                selectedCal,
                                requireContext().contentResolver
                            )
                        }
                    }

                }
        }

    }

}
