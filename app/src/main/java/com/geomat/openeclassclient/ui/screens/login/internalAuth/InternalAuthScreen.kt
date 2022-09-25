package com.geomat.openeclassclient.ui.screens.login.internalAuth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.PopUpToBuilder
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.login.serverSelect.Server
import com.geomat.openeclassclient.ui.screens.main.LoginNavGraph
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Destination()
@LoginNavGraph
@Composable
fun InternalAuthScreen(
    server: Server,
    authName: String = "",
    viewModel: InternalAuthViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            OpenEclassTopBar(
                title = authName.ifBlank { server.name },
                navigator = navigator,
                showMoreButtons = false
            )
        },
        scaffoldState = scaffoldState
    ) {

        viewModel.updateSelectedServer(server)
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 200.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val username = remember {
                mutableStateOf("")
            }
            val password = remember {
                mutableStateOf("")
            }

            val success = viewModel.success
            val wrongCredentials = viewModel.wrongCredentials
            val notEnabled = viewModel.notEnabled
            val connectionError = viewModel.connectionError
            val loading = remember { mutableStateOf(false) }

            UsernameText(username = username, wrongCredentials = wrongCredentials)
            PasswordText(password = password, wrongCredentials = wrongCredentials)

            Button(
                onClick = {
                    loading.value = true
                    connectionError.value = false
                    viewModel.login(username.value, password.value)
                },
                Modifier.padding(vertical = 64.dp).height(50.dp).fillMaxWidth(0.8f)
            ) {
                Text(text = stringResource(id = R.string.login))
            }

            if (loading.value) {
                CircularProgressIndicator()
            }

            wrongCredentials.run {
                if (wrongCredentials.value) {
                    val message = stringResource(id = R.string.wrong_username_password)
                    scope.launch {
                        loading.value = false
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            notEnabled.run {
                if (notEnabled.value) {
                    loading.value = false
                    val message = stringResource(id = R.string.server_not_enabled)
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            connectionError.run {
                loading.value = false
                if (connectionError.value) {
                    val message = stringResource(id = R.string.connection_error)
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            if (success.value) {
                loading.value = false
                navigator.navigate(NavGraphs.root) {
                    popUpTo(NavGraphs.root) {
                        PopUpToBuilder()
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UsernameText(username: MutableState<String>, wrongCredentials: MutableState<Boolean>) {
    val usernameNode = AutofillNode(
        autofillTypes = listOf(AutofillType.Username),
        onFill = { username.value = it }
    )
    val usernameAutoFill = LocalAutofill.current
    LocalAutofillTree.current += usernameNode
    OutlinedTextField(
        value = username.value,
        onValueChange = { username.value = it; wrongCredentials.value = false },
        label = { Text(text = stringResource(id = R.string.username)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        isError = wrongCredentials.value,
        modifier = Modifier
            .onGloballyPositioned {
                usernameNode.boundingBox = it.boundsInWindow()
            }
            .onFocusChanged { focusState ->
                usernameAutoFill?.run {
                    if (focusState.isFocused) {
                        requestAutofillForNode(usernameNode)
                    } else {
                        cancelAutofillForNode(usernameNode)
                    }
                }
            }
            .padding(vertical = 8.dp).fillMaxWidth(0.8f)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordText(password: MutableState<String>, wrongCredentials: MutableState<Boolean>) {
    val passwordNode = AutofillNode(
        autofillTypes = listOf(AutofillType.Password),
        onFill = { password.value = it }
    )
    val passwordAutoFill = LocalAutofill.current
    LocalAutofillTree.current += passwordNode
    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it; wrongCredentials.value = false },
        label = { Text(text = stringResource(id = R.string.password)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        isError = wrongCredentials.value,
        modifier = Modifier
            .onGloballyPositioned {
                passwordNode.boundingBox = it.boundsInWindow()
            }
            .onFocusChanged { focusState ->
                passwordAutoFill?.run {
                    if (focusState.isFocused) {
                        requestAutofillForNode(passwordNode)
                    } else {
                        cancelAutofillForNode(passwordNode)
                    }
                }
            }
            .fillMaxWidth(0.8f)
    )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    InternalAuthScreen(
        server = Server("Server", "openeclass.com"),
        navigator = EmptyDestinationsNavigator
    )
}