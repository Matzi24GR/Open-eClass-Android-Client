package com.geomat.openeclassclient.ui.screens.login.serverSelect

import android.app.Application
import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.network.DataTransferObjects.AuthType
import com.geomat.openeclassclient.network.EclassApi
import com.geomat.openeclassclient.network.interceptor
import com.geomat.openeclassclient.ui.screens.destinations.ExternalAuthScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.InternalAuthScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.ServerSelectScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.parcelize.Parcelize
import nl.adaptivity.xmlutil.serialization.XmlSerialException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import timber.log.Timber
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

@Parcelize
data class Server(var name: String = "", var url: String) : Parcelable
enum class ServerStatus {
    ENABLED, DISABLED, UNKNOWN, CHECKING
}

@Parcelize
data class AuthTypeParcel(val name: String, val url: String) : Parcelable

class ServerSelectViewModel(application: Application) : AndroidViewModel(application) {

    // TODO possible split with a server repository

    private val context = application.applicationContext
    private var data: Array<String> = context.resources.getStringArray(R.array.server_list)

    val serverList: List<State<Server>>

    val currentDirection: MutableState<Direction> = mutableStateOf(ServerSelectScreenDestination())

    // Selected Server
    val selectedServer = mutableStateOf(Server("", ""))

    val serverStatusMap = hashMapOf<Server, MutableState<ServerStatus>>()

    fun resetSelectedServer() {
        selectedServer.value = Server("", "")
    }

    init {
        resetSelectedServer()
        val arrayList = arrayListOf<State<Server>>()
        data.forEach {
            val server = mutableStateOf(splitServerString(it))
            arrayList.add(server)
        }
        serverList = arrayList.toList()
        serverList.forEach {
            serverStatusMap[it.value] = mutableStateOf(ServerStatus.UNKNOWN)
        }
    }


    fun checkServerStatus(server: Server) {
        if (server.url != "demo.openeclass.org") {
            serverStatusMap[server]?.value = ServerStatus.CHECKING
            EclassApi.MobileApi.getApiEnabled("https://${server.url}/modules/mobile/mlogin.php")
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
                val response = EclassApi.MobileApi.getServerInfo().awaitResponse()

                if (selectedServer.value.name.isBlank()) {
                    selectedServer.value.name = response.body()?.institute?.name.toString()
                }
                response.body()?.authTypeList?.let {
                    return it
                }
            } catch (e: XmlSerialException) {
                Timber.e(e)
            } catch (e: SSLHandshakeException) {
                Timber.e(e)
            } catch (e: UnknownHostException) {
                Timber.e(e)
            }

        }
        return null
    }

    fun activateSelectedServer(server: Server) {
        interceptor.setHost(server.url)
        selectedServer.value = server
        Timber.i("Set Url: ${server.url}")
        context.getSharedPreferences("login", Context.MODE_PRIVATE).edit()
            .putString("url", server.url).apply()
    }

    fun splitServerString(string: String): Server {
        val split = string.split("||")
        return Server(split[0], split[1])
    }

    fun getFilteredServerList(searchText: String): List<State<Server>> {
        if (searchText.isBlank()) return serverList
        val result = serverList.filter {
            it.value.name.lowercase().contains(searchText.lowercase()) || it.value.url.lowercase()
                .contains(searchText.lowercase())
        }
        Timber.i(result.toString())
        return result
    }
}