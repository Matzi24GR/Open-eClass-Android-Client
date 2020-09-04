package com.geomat.openeclassclient.ui.Announcements

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.FragmentAnnouncementBinding
import com.geomat.openeclassclient.repository.AnnouncementRepository
import kotlinx.android.synthetic.main.fragment_announcement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AnnouncementFragment : Fragment() {

    private lateinit var binding: FragmentAnnouncementBinding
    private lateinit var repo: AnnouncementRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentAnnouncementBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repo = AnnouncementRepository(EClassDatabase.getInstance(requireContext()))

        val data = repo.allAnnouncements
        val adapter = AnnouncementAdapter()
        binding.announcementRecyclerView.adapter = adapter

        data.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })

        binding.swipeRefresh.isRefreshing = true
        refreshData()

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.refreshButton).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refreshButton -> {
                binding.swipeRefresh.isRefreshing = true
                refreshData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun refreshData() {
        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)
        GlobalScope.launch {
            repo.fillInFeedUrls(token!!)
            repo.updateAllAnnouncements()
            binding.swipeRefresh.isRefreshing = false
        }
    }
}
