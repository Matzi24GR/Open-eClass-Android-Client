package com.geomat.openeclassclient.ui.Announcements

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.BottomSheetAnnouncementFullBinding
import com.geomat.openeclassclient.databinding.FragmentAnnouncementBinding
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_announcement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.AssertionError
import javax.inject.Inject

@AndroidEntryPoint
class AnnouncementFragment : Fragment() {

    //TODO Add a notice when no announcements returned

    private lateinit var binding: FragmentAnnouncementBinding
    @Inject lateinit var repo: AnnouncementRepository

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

        val data = repo.allAnnouncements
        val adapter = AnnouncementAdapter() {
            val dialogBinding = BottomSheetAnnouncementFullBinding.inflate(layoutInflater, binding.root, false)
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)
            dialogBinding.announcementContentText.text = it.description.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT).trim()
            bottomSheetDialog.show()
        }
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
            try {
                repo.fillInFeedUrls(token!!)
                repo.updateAllAnnouncements()
            } catch (e: AssertionError) {
                Timber.i(e)
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }
}
