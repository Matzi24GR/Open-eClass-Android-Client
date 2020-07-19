package com.geomat.openeclassclient.screens.Login.ServerSelect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.databinding.AuthMethodBottomSheetBinding
import com.geomat.openeclassclient.databinding.FragmentServerSelectBinding
import com.geomat.openeclassclient.network.ServerInfoResponse
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.network.interceptor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.auth_method_bottom_sheet.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServerSelectFragment : Fragment() {

    private lateinit var binding: FragmentServerSelectBinding
    private lateinit var viewModel: ServerSelectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ServerSelectViewModel::class.java)
        binding = FragmentServerSelectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        //Auth Method Bottom Sheet Modal
        val dialogBinding = AuthMethodBottomSheetBinding.inflate(layoutInflater, binding.root,false)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        //ServerList on Click
        val adapter =
            ServerListAdapter(
                viewModel.serverArray
            ) {
                viewModel.updateSelectedServer(it)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        //Setup recyclerview
        binding.recyclerView.adapter = adapter

        binding.serverListButton.setOnClickListener {
            binding.searchview.requestFocus()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.SchEclassCard.setOnClickListener {
            viewModel.setSelectedServerToSch()
        }

        binding.connectButton.setOnClickListener{
            val url = binding.urlText.editText?.text.toString()
            viewModel.updateSelectedServer( Server("", url) )
        }

        //TODO fix first click not working sometimes
        // Selected Server Observer
        viewModel.selectedServer.observe(viewLifecycleOwner, Observer { selectedServer ->

            if (selectedServer.url.isNotBlank()) {
                eClassApi.MobileApi.getServerInfo().enqueue(object: Callback<ServerInfoResponse> {
                    override fun onFailure(call: Call<ServerInfoResponse>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<ServerInfoResponse>, response: Response<ServerInfoResponse>) {

                        if (selectedServer.name.isBlank()) {
                            selectedServer.name = response.body()?.institute?.name.toString()
                        }

                        val list = response.body()!!.AuthTypeList

                        when (list.size) {
                            0-> {
                                //Internal Auth
                                val action = ServerSelectFragmentDirections.actionServerSelectFragmentToInternalAuthFragment(selectedServer.url, selectedServer.name, "")
                                findNavController().navigate(action)
                            }
                            1 -> {
                                val action = if (list[0].url.isBlank()) {
                                    //Internal Auth
                                    ServerSelectFragmentDirections
                                        .actionServerSelectFragmentToInternalAuthFragment(selectedServer.url, selectedServer.name, list[0].title)
                                } else {
                                    //External Auth
                                    ServerSelectFragmentDirections
                                        .actionServerSelectFragmentToExternalAuthFragment(list[0].url, selectedServer.name, list[0].title)
                                }
                                findNavController().navigate(action)
                            }
                            else -> {
                                val array = arrayListOf<String>()
                                list.forEach {
                                    array.add(it.title)
                                }
                                //TODO change this to a better recycler view
                                dialogBinding.authMethodListView.adapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item , array)
                                dialogBinding.authMethodListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val item = list[position]

                                    val action = if (item.url.isBlank()) {
                                        //Internal Auth
                                        ServerSelectFragmentDirections
                                            .actionServerSelectFragmentToInternalAuthFragment(selectedServer.url, selectedServer.name, item.title)
                                    } else {
                                        //External Auth
                                        ServerSelectFragmentDirections
                                            .actionServerSelectFragmentToExternalAuthFragment(item.url, selectedServer.name, item.title)
                                    }
                                    bottomSheetDialog.dismiss()
                                    findNavController().navigate(action)

                                }
                                bottomSheetDialog.show()
                            }
                        }
                    }
                })
            }
        })


        // Show SnackBar Event Observer
        viewModel.showSnackBarString.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    it,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.resetSnackbarString()
            }
        })

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


    override fun onStart() {
        super.onStart()
        viewModel.resetSelectedServer()
        interceptor.setHost("")
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}