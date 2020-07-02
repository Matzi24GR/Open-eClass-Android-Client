package com.example.openeclassclient

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.openeclassclient.network.interceptor

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private var data: Array<String> = context.resources.getStringArray(R.array.server_list)

    val serverArray = arrayListOf<Server>()
    var selectedServer =  MutableLiveData<Server>(Server("",""))

    init {
        selectedServer.value = Server("","")
        for (i in data.indices) {
            val str = data[i].split("||")
            serverArray.add(Server(str[0],str[1]))
        }
    }

    fun updateSelectedServer(server: Server) {
        selectedServer.value = server
        interceptor.setHost(server.url)
        context.getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("url",server.url).apply()
    }

}