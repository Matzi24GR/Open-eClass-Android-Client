package com.geomat.openeclassclient.screens.Login.ServerSelect

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.databinding.ServerListItemBinding
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.network.interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

data class Server(var name: String, var url: String)

class ServerListAdapter(val ServerData: ArrayList<Server>, private val itemClick: (Server) -> Unit): ListAdapter<Server, ServerListAdapter.ViewHolder>(
    ServerDiffCallback()
), Filterable {

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

    class ViewHolder private constructor(private val binding: ServerListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(server: Server, itemClick: (Server) -> Unit) {
            with(binding) {
                nameTextView.text = server.name
                urlTextView.text = server.url
                itemView.setOnClickListener{itemClick(server)}
                eClassApi.MobileApi.getApiEnabled("https://${server.url}/modules/mobile/mlogin.php")
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

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val itemBinding = ServerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(
                    itemBinding
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