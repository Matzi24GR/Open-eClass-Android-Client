package com.example.openeclassclient

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openeclassclient.network.eClassApi
import com.example.openeclassclient.network.interceptor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.recycler_view
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    //TODO Prevent user going back to previous screens
    //TODO hide bottom sheet on back button press

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.bottom_sheet)

        val data = resources.getStringArray(R.array.server_list)
        val serverArray = arrayListOf<Server>()
        for (i in data.indices) {
            val str = data[i].split("||")
            serverArray.add(Server(str[0],str[1]))
        }
        var selectedServer: Server = Server("","")
        val adapter = ServerListAdapter(serverArray){
            selectedServer = it
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            interceptor.setHost(selectedServer.url)
            view.server_button.text = it.url
            requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("url",selectedServer.url).apply()
        }
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(this.context)
        var token: String? = null

        view.server_button.setOnClickListener() {
            recycler_view.adapter?.notifyDataSetChanged()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        view.login_button.setOnClickListener() {
            val username = view.user_text.text.toString()
            val password = view.pass_text.text.toString()
            Log.i("loginFragment","button pressed")
            interceptor.setHost(selectedServer.url)
            eClassApi.MobileApi.getToken(username, password)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {}
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val token = response.body().toString()
                        Log.i("loginFragment",response.body())
                        view.server_button.text = token
                        activity!!.getPreferences(Context.MODE_PRIVATE).edit().putBoolean("hasLoggedIn", true).apply()
                        activity!!.getPreferences(Context.MODE_PRIVATE).edit().putString("token",token).apply()
                        findNavController().navigate(R.id.mainActivity)
                    }
                })
        }

        view.searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

    }
}