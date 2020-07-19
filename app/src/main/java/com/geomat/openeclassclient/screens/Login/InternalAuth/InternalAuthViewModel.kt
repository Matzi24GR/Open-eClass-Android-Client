package com.geomat.openeclassclient.screens.Login.InternalAuth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.network.eClassApi
import com.geomat.openeclassclient.network.interceptor
import com.geomat.openeclassclient.screens.Login.ServerSelect.Server
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class InternalAuthViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    // Selected Server
    private val _selectedServer =  MutableLiveData<Server>(
        Server("", "")
    )
    val selectedServer: LiveData<Server>
        get() = _selectedServer

    // Login Success Event
    private val _loginSuccessful = MutableLiveData<Boolean>(false)
    val loginSuccessful: LiveData<Boolean>
        get() = _loginSuccessful
    fun resetLoginSuccessful(){
        _loginSuccessful.value = false
    }

    // Show SnackBar Event
    private var _showSnackbarString = MutableLiveData<String?>()
    val showSnackBarString: LiveData<String?>
        get() = _showSnackbarString
    fun resetSnackbarString() {
        _showSnackbarString.value = null
    }

    fun updateSelectedServer(server: Server) {
        _selectedServer.value = server
    }

    fun Login(username: String, password: String) {

        if (_selectedServer.value!!.url.isNotBlank()) {

            eClassApi.MobileApi.getToken(username, password)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        _showSnackbarString.value = "Check your Connection"
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val token = response.body()
                        when (token) {
                            "FAILED" -> _showSnackbarString.value = "Wrong username/password"
                            "NOTENABLED" -> _showSnackbarString.value = "Απενεργοποιημένος από τους διαχειριστές"
                            else -> {
                                Timber.i("Login Response: ${response.body()}")
                                context.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                                    .putString("username", username).apply()
                                context.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                                    .putBoolean("hasLoggedIn", true).apply()
                                context.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                                    .putString("token", token).apply()
                                _loginSuccessful.value = true
                            }
                        }
                    }
                })
        }
    }
}