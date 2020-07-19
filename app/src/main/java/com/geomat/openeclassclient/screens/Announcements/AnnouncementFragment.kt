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
import com.geomat.openeclassclient.databinding.FragmentAnnouncementBinding
import com.geomat.openeclassclient.repository.AnnouncementRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AnnouncementFragment : Fragment() {

    private lateinit var binding: FragmentAnnouncementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnnouncementBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)

        val repo = AnnouncementRepository(EClassDatabase.getInstance(requireContext()))

        val data = repo.allAnnouncements
        val adapter = AnnouncementAdapter()
        binding.announcementRecyclerView.adapter = adapter

        data.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
        GlobalScope.launch {
            repo.fillInFeedUrls(token!!)
            repo.updateAllAnnouncements()
        }
    }
}
