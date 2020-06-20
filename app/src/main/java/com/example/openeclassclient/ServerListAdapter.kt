package com.example.openeclassclient

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

//TODO Add Search

data class Server(var name: String, var url: String)

class ServerListAdapter(val itemClick: (Server) -> Unit): RecyclerView.Adapter<ServerListAdapter.ViewHolder>() {

    //TODO add more servers to list

    val data = arrayOf(
        Server("Demo Open Eclass", "demo.openeclass.org"),
        Server("Αριστοτέλειο Πανεπιστήμιο Θεσσαλονίκης", "eclass.auth.gr"),
        Server("Δημοκρίτειο Πανεπιστήμιο Θράκης", "eclass.duth.gr"),
        Server("Εθνικό και Καποδιστριακό Πανεπιστήμιο Αθηνών", "eclass.uoa.gr"),
        Server("Πανεπιστήμιο Δυτικής Αττικής", "eclass.teiath.gr"),
        Server("Πανεπιστήμιο Μακεδονίας","openeclass.uom.gr"),
        Server("Πανεπιστήμιο Πατρών", "eclass.upatras.gr"),
        Server("Σχολικό Δίκτυο", "eclass.sch.gr"),
        Server("Χαροκόπειο Πανεπιστήμιο", "eclass.hua.gr")
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
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