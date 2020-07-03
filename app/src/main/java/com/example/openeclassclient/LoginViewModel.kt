package com.example.openeclassclient

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.openeclassclient.network.eClassApi
import com.example.openeclassclient.network.interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private var data: Array<String> = context.resources.getStringArray(R.array.server_list)

    val serverArray = arrayListOf<Server>()

    private val _selectedServer =  MutableLiveData<Server>(Server("",""))
    val selectedServer: LiveData<Server>
        get() = _selectedServer

    private val _loginSuccessful = MutableLiveData<Boolean>(false)
    val loginSuccessful: LiveData<Boolean>
        get() = _loginSuccessful

    fun resetLoginSuccessful(){
        _loginSuccessful.value = false
    }

    init {
        for (i in data.indices) {
            val str = data[i].split("||")
            serverArray.add(Server(str[0],str[1]))
        }
    }

    fun updateSelectedServer(server: Server) {
        _selectedServer.value = server
        interceptor.setHost(server.url)
        context.getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("url",server.url).apply()
    }

    fun Login(username: String, password: String) {

        if (_selectedServer.value!!.url.isNotBlank()) {

            eClassApi.MobileApi.getToken(username, password)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(context, "Check your Connection", Toast.LENGTH_SHORT)
                            .show()
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val token = response.body()
                        if (token == "FAILED") {
                            Toast.makeText(context, "Wrong username/password", Toast.LENGTH_SHORT)
                                .show()
                        } else if(token == "NOTENABLED") {
                            Toast.makeText(context, "Απενεργοποιημένος από τους διαχειριστές", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Timber.i("Login Response: ${response.body()}")
                            context.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                                .putBoolean("hasLoggedIn", true).apply()
                            context.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                                .putString("token", token).apply()
                            _loginSuccessful.value = true
                        }
                    }
                })
        }
    }
}