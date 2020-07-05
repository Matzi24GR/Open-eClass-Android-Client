package com.geomat.openeclassclient

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.geomat.openeclassclient.databinding.FragmentLoginBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class LoginFragment : Fragment() {

    //TODO hide bottom sheet on back button press

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        //Disable Nav Drawer
        requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        //ServerList on Click
        val adapter =
            ServerListAdapter(viewModel.serverArray) {
                viewModel.updateSelectedServer(it)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        viewModel.selectedServer.observe(viewLifecycleOwner, Observer {
            selectedServer -> if (selectedServer.url.isNotBlank()) binding.serverButton.text = selectedServer.url
        })

        viewModel.loginSuccessful.observe(viewLifecycleOwner, Observer { loginSuccessful ->
            if (loginSuccessful) {
                findNavController().navigate(R.id.action_loginFragment_to_MainActivity)
                viewModel.resetLoginSuccessful()
            }
        })

        //Setup recyclerview
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.serverButton.setOnClickListener {
            binding.recyclerView.adapter?.notifyDataSetChanged()
            binding.searchview.requestFocus()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.loginButton.setOnClickListener {
            val username = binding.userText.editText?.text.toString()
            val password = binding.passText.editText?.text.toString()
            if (username.contains("007")) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/vaKHTJuOuyE?t=6")))
            }
            viewModel.Login(username,password)

        }

        binding.searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                bottomSheetBehavior.state= BottomSheetBehavior.STATE_EXPANDED
                adapter.filter.filter(newText)
                return false
            }

        })

    }
}