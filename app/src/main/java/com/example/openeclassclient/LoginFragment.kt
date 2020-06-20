package com.example.openeclassclient

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openeclassclient.network.eClassApi
import com.example.openeclassclient.network.interceptor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    //TODO Prevent user going back to previous screens

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

        val adapter = ServerListAdapter(){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            interceptor.setHost(it.url)
            requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("url",it.url).apply()
        }
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(this.context)

        var token: String? = null

        view.button.setOnClickListener() {

            val username = view.user_text.text.toString()
            val password = view.pass_text.text.toString()
            Log.i("loginFragment","button pressed")
            eClassApi.MobileApi.getToken(username, password)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {}
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        token = response.body().toString()
                        Log.i("loginFragment",response.body())
                        view.button.text = token
                        activity!!.getPreferences(Context.MODE_PRIVATE).edit().putBoolean("hasLoggedIn", true).apply()
                        activity!!.getPreferences(Context.MODE_PRIVATE).edit().putString("token",token).apply()
                        findNavController().navigate(R.id.mainActivity)
                    }
                })
        }

    }
}