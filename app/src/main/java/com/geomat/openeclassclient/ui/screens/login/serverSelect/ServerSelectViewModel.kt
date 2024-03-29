package com.geomat.openeclassclient.ui.screens.login.serverSelect

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.geomat.openeclassclient.network.AuthInterceptor
import com.geomat.openeclassclient.network.OpenEclassService
import com.geomat.openeclassclient.network.dataTransferObjects.AuthType
import com.geomat.openeclassclient.ui.screens.destinations.ExternalAuthScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.InternalAuthScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.ServerSelectScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

@Parcelize
data class Server(var name: String = "", var url: String) : Parcelable
enum class ServerStatus {
    ENABLED, DISABLED, UNKNOWN, CHECKING
}

@Parcelize
data class AuthTypeParcel(val name: String, val url: String) : Parcelable

@HiltViewModel
class ServerSelectViewModel @Inject constructor(private val openEclassService: OpenEclassService, private val authInterceptor: AuthInterceptor) : ViewModel() {

    //TODO possible split with a server repository
    private var serverList: List<State<Server>>? = null

    val currentDirection: MutableState<Direction> = mutableStateOf(ServerSelectScreenDestination())

    // Selected Server
    private val selectedServer = mutableStateOf(Server("", ""))

    val serverStatusMap = hashMapOf<Server, MutableState<ServerStatus>>()

    private fun resetSelectedServer() {
        selectedServer.value = Server("", "")
    }

    fun setData(data: Array<String>) {
        resetSelectedServer()
        val arrayList = arrayListOf<State<Server>>()
        data.forEach {
            val server = mutableStateOf(splitServerString(it))
            arrayList.add(server)
        }
        serverList = arrayList.toList()
        serverList?.forEach {
            serverStatusMap[it.value] = mutableStateOf(ServerStatus.UNKNOWN)
        }
    }


    fun checkServerStatus(server: Server) {
        if (server.url != "demo.openeclass.org") {
            serverStatusMap[server]?.value = ServerStatus.CHECKING
            openEclassService.getApiEnabled("https://${server.url}/modules/mobile/mlogin.php")
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {}
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        when (response.body()) {
                            "FAILED" -> serverStatusMap[server]?.value = ServerStatus.ENABLED
                            "NOTENABLED" -> serverStatusMap[server]?.value = ServerStatus.DISABLED
                            else -> serverStatusMap[server]?.value = ServerStatus.UNKNOWN
                        }
                    }
                })
        }
    }

    fun setDestination(authType: AuthType = AuthType("","")) {
        if (authType.title.isBlank() && authType.url.isBlank()) currentDirection.value =
            InternalAuthScreenDestination(server = selectedServer.value)
        else if (authType.url.isBlank()) currentDirection.value =
            InternalAuthScreenDestination(server = selectedServer.value, authName = authType.title)
        else currentDirection.value =
            ExternalAuthScreenDestination(authType = AuthTypeParcel(authType.title, authType.url))
    }

    suspend fun getAuthTypes(server: State<Server>): List<AuthType>? {
        activateSelectedServer(server.value)
        if (selectedServer.value.url.isNotBlank()) {

            try {
                val response = openEclassService.getServerInfo().awaitResponse()

                if (selectedServer.value.name.isBlank()) {
                    selectedServer.value.name = response.body()?.institute?.name.toString()
                }
                response.body()?.authTypeList?.let {
                    return it
                }
            } catch (e: Exception) {
                Timber.e(e)
            }

        }
        return null
    }

    private fun activateSelectedServer(server: Server) {
        authInterceptor.setHost(server.url)
        selectedServer.value = server
        Timber.i("Set Url: ${server.url}")
    }

    fun splitServerString(string: String): Server {
        val split = string.split("||")
        return Server(split[0], split[1])
    }

    fun getFilteredServerList(searchText: String): List<State<Server>> {
        if (searchText.isBlank()) return serverList!!
        val result = serverList!!.filter {
            it.value.name.lowercase().contains(searchText.lowercase()) || it.value.url.lowercase()
                .contains(searchText.lowercase())
        }
        Timber.i(result.toString())
        return result
    }
}