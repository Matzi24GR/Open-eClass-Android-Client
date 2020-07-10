package com.geomat.openeclassclient.screens.Login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.network.interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

data class Server(var name: String, var url: String)

class ServerListAdapter(val ServerData: ArrayList<Server>, private val itemClick: (Server) -> Unit): ListAdapter<Server, ServerListAdapter.ViewHolder>(ServerDiffCallback()), Filterable {

    var serverFilterList = ArrayList<Server>()

    init {
        submitList(ServerData)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, itemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        private val urlTextView: TextView = itemView.findViewById(R.id.url_text_view)

        fun bind(server: Server, itemClick: (Server) -> Unit) {
            nameTextView.text = server.name
            urlTextView.text = server.url
            itemView.setOnClickListener{itemClick(server)}
            interceptor.setHost("")
            eClassApi.PlainTextApi.getApiEnabled("https://${server.url}/modules/mobile/mlogin.php","","")
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

                return ViewHolder(
                    view
                )
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    serverFilterList = ServerData
                } else {
                    val resultList = ArrayList<Server>()
                    for (item in ServerData) {
                        if (item.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) || item.url.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(item)
                        }
                    }
                    serverFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = serverFilterList
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                serverFilterList = results?.values as ArrayList<Server>
                submitList(serverFilterList)
            }

        }
    }

    class ServerDiffCallback: DiffUtil.ItemCallback<Server>() {
        override fun areItemsTheSame(oldItem: Server, newItem: Server): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Server, newItem: Server): Boolean {
            return oldItem == newItem
        }
    }
}