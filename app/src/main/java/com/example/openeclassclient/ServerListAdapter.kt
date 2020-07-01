package com.example.openeclassclient

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.openeclassclient.network.HostSelectionInterceptor
import com.example.openeclassclient.network.eClassApi
import com.example.openeclassclient.network.interceptor
import kotlinx.android.synthetic.main.fragment_login.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

//TODO Add Search

data class Server(var name: String, var url: String)

class ServerListAdapter(val ServerData: ArrayList<Server>,val itemClick: (Server) -> Unit): RecyclerView.Adapter<ServerListAdapter.ViewHolder>(), Filterable {

    var ServerFilterList = ArrayList<Server>()

    init {
        ServerFilterList = ServerData
    }

    override fun getItemCount(): Int {
        return ServerFilterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ServerFilterList[position]
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
            interceptor.setHost(server.url)
            eClassApi.MobileApi.getToken("","")
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {}
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                            when(response.body()){
                                "FAILED"-> urlTextView.text = "✓ "+server.url
                                "NOTENABLED"-> urlTextView.text = "✗ " +server.url +" (Απενεργοποιημένο)"
                                else -> urlTextView.text = "✗ "+server.url
                            }
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    ServerFilterList = ServerData
                } else {
                    val resultList = ArrayList<Server>()
                    for (item in ServerData) {
                        if (item.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(item)
                        }
                        if (item.url.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(item)
                        }
                    }
                    ServerFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = ServerFilterList
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                ServerFilterList = results?.values as ArrayList<Server>
                notifyDataSetChanged()
            }

        }
    }
}