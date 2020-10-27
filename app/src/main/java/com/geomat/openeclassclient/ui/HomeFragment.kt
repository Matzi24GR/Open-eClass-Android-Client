package com.geomat.openeclassclient.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.FragmentAnnouncementBinding
import com.geomat.openeclassclient.databinding.FragmentHomeBinding
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.repository.UserInfoRepository
import com.geomat.openeclassclient.ui.Announcements.AnnouncementAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var userRepo: UserInfoRepository
    @Inject lateinit var announcementRepo: AnnouncementRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)
        val url = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("url",null)
        val username = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("username",null)

        GlobalScope.launch { userRepo.refreshData(token!!) }
        userRepo.getUserWithUsername(username!!).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.usernameText.text = it.username
                binding.fullNameText.text = it.fullName
                binding.categoryText.text = it.category.replace(" » ","\n")
                val imageUrl = "https://$url${it.imageUrl}".toUri()
                Glide.with(binding.imageView.context).load(imageUrl).into(binding.imageView)
            }
        })
        val announcements = announcementRepo.allAnnouncements
        val adapter = AnnouncementAdapter({}, {})
        binding.homeAnnouncementRecyclerView.adapter = adapter
        announcements.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it.take(2))
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}