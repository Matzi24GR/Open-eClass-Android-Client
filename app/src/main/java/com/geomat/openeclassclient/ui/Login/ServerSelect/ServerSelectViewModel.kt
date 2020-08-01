package com.geomat.openeclassclient.ui.Login.ServerSelect

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.network.DataTransferObjects.AuthType
import com.geomat.openeclassclient.network.interceptor
import timber.log.Timber

class ServerSelectViewModel(application: Application): AndroidViewModel(application) {

    private val context = application.applicationContext
    private var data: Array<String> = context.resources.getStringArray(R.array.server_list)

    val serverArray = arrayListOf<Server>()

    // Show SnackBar Event
    private var _showSnackbarString = MutableLiveData<String?>()
    val showSnackBarString: LiveData<String?>
        get() = _showSnackbarString
    fun resetSnackbarString() {
        _showSnackbarString.value = null
    }

    // Selected Server
    private val _selectedServer =  MutableLiveData<Server>(
        Server("", "")
    )
    val selectedServer: LiveData<Server>
        get() = _selectedServer
    fun setSelectedServerToSch() {
        val schString = context.resources.getString(R.string.schServer)
        updateSelectedServer(splitServerString(schString))
    }
    fun resetSelectedServer() {
        _selectedServer.value = Server("","")
    }

    init {
        _selectedServer.value = Server("","")
        data.forEach {
            serverArray.add(splitServerString(it))
        }
    }

    fun updateSelectedServer(server: Server) {
        interceptor.setHost(server.url)
        _selectedServer.value = server
        Timber.i("Set Url: ${server.url}")
        context.getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("url",server.url).apply()
    }

    fun splitServerString(string: String): Server {
        val split = string.split("||")
        return Server(split[0],split[1])
    }

    fun decideAction(item: AuthType?): NavDirections {
        with(ServerSelectFragmentDirections) {
            return when {
                // Internal Auth
                item == null -> actionServerSelectFragmentToInternalAuthFragment(selectedServer.value!!.url, selectedServer.value!!.name, "")
                // Internal Auth
                item.url.isBlank() -> actionServerSelectFragmentToInternalAuthFragment(selectedServer.value!!.url, selectedServer.value!!.name, item.title)
                // External Auth
                else -> { actionServerSelectFragmentToExternalAuthFragment(item.url, selectedServer.value!!.name, item.title)
                }
            }
        }
    }
}