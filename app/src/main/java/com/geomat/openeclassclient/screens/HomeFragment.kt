package com.geomat.openeclassclient.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.databinding.FragmentHomeBinding
import com.geomat.openeclassclient.repository.UserInfoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)
        val url = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("url",null)
        val username = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("username",null)

        val database = EClassDatabase.getInstance(requireContext())
        val repo = UserInfoRepository(database.userInfoDao)

        GlobalScope.launch { repo.refreshData(token!!) }
        repo.getUserWithUsername(username!!).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.usernameText.text = it.username
                binding.fullNameText.text = it.fullName
                binding.categoryText.text = it.category.replace(" » ","\n")
                val imageUrl = "https://$url${it.imageUrl}".toUri()
                Glide.with(binding.imageView.context).load(imageUrl).into(binding.imageView)
            }
        })

    }

}