package com.example.openeclassclient

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.openeclassclient.network.eClassApi
import kotlinx.android.synthetic.main.fragment_login.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO Add Search

data class Server(var name: String, var url: String)

class ServerListAdapter(val ServerData: Array<Server>,val itemClick: (Server) -> Unit): RecyclerView.Adapter<ServerListAdapter.ViewHolder>() {

    override fun getItemCount() = ServerData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ServerData[position]
        holder.bind(item, itemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nameTextView = itemView.findViewById<TextView>(R.id.name_text_view)
        var urlTextView = itemView.findViewById<TextView>(R.id.url_text_view)

        fun bind(server: Server, itemClick: (Server) -> Unit) {
            nameTextView.text = server.name
            urlTextView.text = server.url
            itemView.setOnClickListener{itemClick(server)}
            eClassApi.MobileApi.getToken("","")
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {}
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.body() == "FAILED") urlTextView.text = "✓ "+server.url
                        else urlTextView.text = "✗ "+server.url
                    }
                })
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.server_list_item, parent, false)

                return ViewHolder(view)
            }
        }
    }
}