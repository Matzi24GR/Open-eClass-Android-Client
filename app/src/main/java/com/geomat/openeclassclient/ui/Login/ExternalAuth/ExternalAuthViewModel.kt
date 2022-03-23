package com.geomat.openeclassclient.ui.Login.ExternalAuth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.repository.CredentialsRepository
import com.geomat.openeclassclient.ui.Login.ServerSelect.AuthTypeParcel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExternalAuthViewModel @Inject constructor(val repo: CredentialsRepository) : ViewModel() {

    var authType = AuthTypeParcel("","")

    fun setCredentials(url: String) {

        // Ex eclass-token:a961162%7C28207656262415267923679599
        //    eclass-token:[username]%7C[        token        ]

        val usernameAndToken = url.replace("eclass-token:","").split("%7C")

        val username = usernameAndToken[0]
        val token = usernameAndToken[1]

        val credentials = Credentials(
            username = username,
            token = token,
            isLoggedIn = true,
            selectedAuthName = authType.name,
            selectedAuthUrl = authType.url
        )
        viewModelScope.launch {
            repo.updateFullCredentials(credentials)
        }

    }

}