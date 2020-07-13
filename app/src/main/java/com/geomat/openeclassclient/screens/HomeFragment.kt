package com.geomat.openeclassclient.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.repository.UserInfoRepository
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val UserText = view.findViewById<TextView>(R.id.usernameText)
        val NameText = view.findViewById<TextView>(R.id.FullNameText)
        val CategoryText = view.findViewById<TextView>(R.id.CategoryText)
        val imageView = view.findViewById<ImageView>(R.id.imageView2)

        val token = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("token",null)
        val url = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("url",null)
        val username = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).getString("username",null)

        val database = EClassDatabase.getInstance(requireContext())
        val repo = UserInfoRepository(database.userInfoDao)

        GlobalScope.launch { repo.refreshData(token!!) }
        repo.getUserWithUsername(username!!).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Timber.i("here2")
                UserText.text = it.username
                FullNameText.text = it.fullName
                CategoryText.text = it.category
                val imageUrl = "https://$url${it.imageUrl}".toUri()
                Glide.with(imageView.context).load(imageUrl).into(imageView)
            }
        })

    }

}