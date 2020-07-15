package com.geomat.openeclassclient.screens.Announcements

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.repository.AnnouncementRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AnnouncementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_announcement, container, false)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val repo = AnnouncementRepository(EClassDatabase.getInstance(requireContext()))

        val recyclerView = view.findViewById<RecyclerView>(R.id.announcementRecyclerView)
        val data = repo.allAnnouncements
        val adapter = AnnouncementAdapter()
        recyclerView.adapter = adapter

        data.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
        GlobalScope.launch {
            repo.fillInFeedUrls(token!!)
            repo.updateAllAnnouncements()
        }

        return view
    }
}
