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
import androidx.lifecycle.Observer
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.DatabaseCalendarSyncId
import com.geomat.openeclassclient.database.EClassDatabase
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
    @Inject lateinit var repo: CalendarEventRepository

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

        val data = repo.allEvents
        val adapter = CalendarEventAdapter()
        binding.calendarRecyclerView.adapter = adapter

        GlobalScope.launch { repo.refreshData(token!!) }
        data.observe(viewLifecycleOwner, Observer {
            if (data.value != null) {
                adapter.submitList(data.value)
            }
        })

        binding.button2.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val syncedIds = repo.syncedIDs.value
                syncedIds?.forEach {
                    Timber.i(it.toString())
                    val deleteUri: Uri =
                        ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it)
                    val rows: Int = requireContext().contentResolver.delete(deleteUri, null, null)
                    Timber.i("Rows deleted: $rows")
                }
            }
        }

        binding.button.setOnClickListener {

            val EVENT_PROJECTION: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,                     // 0
                CalendarContract.Calendars.ACCOUNT_NAME,            // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
                CalendarContract.Calendars.OWNER_ACCOUNT,           // 3
                CalendarContract.Calendars.ACCOUNT_TYPE             // 4

            )

            // The indices for the projection array above.
            val PROJECTION_ID_INDEX: Int = 0
            val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
            val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
            val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3
            val PROJECTION_ACCOUNT_TYPE_INDEX: Int = 4

            // Run query
            val uri: Uri = CalendarContract.Calendars.CONTENT_URI
            val cursor: Cursor? = requireContext().contentResolver.query(uri, EVENT_PROJECTION, null, null, null)


            if (cursor != null) {
                data class Calendar(
                    val id: Long,
                    val displayName: String,
                    val accountName: String,
                    val ownerName: String
                )

                val array = arrayListOf<Calendar>()
                while (cursor.moveToNext()) {
                    // Get the field values
                    val calID: Long = cursor.getLong(PROJECTION_ID_INDEX)
                    val displayName: String = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
                    val accountName: String = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                    val accountType: String = cursor.getString(PROJECTION_ACCOUNT_TYPE_INDEX)
                    // Do something with the values...
                    array.add(Calendar(calID, displayName, accountName, accountType))
                    Timber.i(
                        "ID:%s    Name:%s    AccountName:%s    AccountType:%s",
                        calID.toString(),
                        displayName,
                        accountName,
                        accountType
                    )
                }
                if (cursor.count == 0) Timber.i("No Calendars Found")
                cursor.close()

                val dialogBinding =
                    CalendarSelectBottomSheetBinding.inflate(layoutInflater, binding.root, false)
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(dialogBinding.root)

                dialogBinding.authMethodListView.adapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    array.map { it.displayName })
                bottomSheetDialog.show()

                dialogBinding.authMethodListView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->

                        bottomSheetDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            array[position].id.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        val selectedCal = array[position].id
                        GlobalScope.launch(Dispatchers.IO) {
                            val EventsThatNeedSyncing = repo.notSyncedEvents.value
                            EventsThatNeedSyncing?.forEach {
                                val values = ContentValues().apply {
                                    put(CalendarContract.Events.DTSTART, it.start)
                                    put(CalendarContract.Events.DTEND, it.end)
                                    put(CalendarContract.Events.TITLE, it.title)
                                    put(CalendarContract.Events.DESCRIPTION, it.content)
                                    put(CalendarContract.Events.CALENDAR_ID, selectedCal)
                                    put("eventTimezone", TimeZone.getDefault().id)
                                }
                                val EventUri: Uri = requireContext().contentResolver.insert(
                                    CalendarContract.Events.CONTENT_URI,
                                    values
                                )!!

                                val eventID: Long? = EventUri.lastPathSegment?.toLong()
                                if (eventID != null) {
                                    repo.insertSyncedEvent(eventID, it.id)
                                }

                            }
                        }

                    }
            }

        }

    }
}
