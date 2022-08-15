package com.geomat.openeclassclient.ui.screens.login.serverSelect

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.network.DataTransferObjects.AuthType
import com.geomat.openeclassclient.ui.screens.destinations.ServerSelectScreenDestination
import com.geomat.openeclassclient.ui.screens.main.LoginNavGraph
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException

@OptIn(ExperimentalMaterialApi::class)
@Destination()
@LoginNavGraph(start = true)
@Composable
fun ServerSelectScreen(
    navigator: DestinationsNavigator,
    viewModel: ServerSelectViewModel = hiltViewModel()
) {
    navigator.clearBackStack(ServerSelectScreenDestination)
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    Scaffold(topBar = {
        OpenEclassTopBar(
            title = "Open Eclass Client",
            navigator = navigator,
            navigateBack = false,
            showMoreButtons = false
        )
    }) {
        ModalBottomSheetLayout(
            sheetContent = { BottomSheet(bottomSheetState = modalBottomSheetState) },
            sheetState = modalBottomSheetState,
        ) {
            Column {
                BuiltInServerCard(
                    modalBottomSheetState = modalBottomSheetState,
                    elevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
                ManualServerCard(
                    elevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
        with(viewModel.currentDirection.value) {
            if (this != ServerSelectScreenDestination) {
                viewModel.currentDirection.value = ServerSelectScreenDestination()
                val scope = rememberCoroutineScope()
                scope.launch { modalBottomSheetState.hide() }
                navigator.navigate(direction = this)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BuiltInServerCard(
    modalBottomSheetState: ModalBottomSheetState,
    elevation: Dp,
    modifier: Modifier,
    viewModel: ServerSelectViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val authTypes: MutableState<List<AuthType>> = remember { mutableStateOf(listOf()) }
    val schString = stringResource(id = R.string.schServer)
    val schServer = remember { mutableStateOf(viewModel.splitServerString(schString)) }
    Card(elevation = elevation, modifier = modifier) {
        Column(
            Modifier
                .padding(12.dp)
        ) {
            Text(text = stringResource(R.string.select_greek_school_network))
            Button(
                onClick = {
                    scope.launch {
                        val types = viewModel.getAuthTypes(schServer)
                        if (types.isEmpty()) viewModel.setDestination()
                        if (types.size == 1) viewModel.setDestination(types[0])
                        if (types.size > 1) authTypes.value = types
                    }
                },
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_e_taxi_logo1),
                    contentDescription = ""
                )
            }
            Text(text = stringResource(R.string.select_other_institutions))
            Button(
                onClick = { scope.launch { modalBottomSheetState.show() } },
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = stringResource(id = R.string.server_list))
            }
        }
    }
}

@Composable
private fun ManualServerCard(
    elevation: Dp,
    modifier: Modifier,
    viewModel: ServerSelectViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var address by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val authTypes: MutableState<List<AuthType>> = remember { mutableStateOf(listOf()) }
    val server = remember { mutableStateOf(Server("", address)) }
    val errored = remember { mutableStateOf(false) }
    Card(elevation = elevation, modifier = modifier.animateContentSize()) {
        Column(
            Modifier
                .padding(12.dp)
        ) {
            Text(text = stringResource(id = R.string.or_manually_insert_an_url))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.server_url)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                singleLine = true,
            )
            Button(
                onClick = {
                    loading = true
                    errored.value = false
                    scope.launch {
                        server.value = Server("", address)
                        val types: List<AuthType>
                        try {
                            types = viewModel.getAuthTypes(server)
                        } catch (e: UnknownHostException) {
                            Timber.e(e)
                            loading = false
                            errored.value = true
                            return@launch
                        }
                        if (types.isEmpty()) viewModel.setDestination()
                        if (types.size == 1) viewModel.setDestination(types[0])
                        if (types.size > 1) authTypes.value = types
                        loading = false
                    }
                },
                Modifier.fillMaxWidth(),
                enabled = authTypes.value.isEmpty()
            ) {
                Text(text = stringResource(id = R.string.connect))
            }
            if (loading) {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
            }
            if (authTypes.value.isNotEmpty()) {
                Column(modifier = Modifier.padding(4.dp)) {
                    authTypes.value.forEach {
                        Button(onClick = {
                            viewModel.setDestination(authType = it)
                        }) {
                            Text(text = it.title)
                        }
                    }
                }
            }
            if (errored.value) {
                Text(text = stringResource(id = R.string.wrong_url))
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheet(
    viewModel: ServerSelectViewModel = hiltViewModel(),
    bottomSheetState: ModalBottomSheetState
) {
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        label = { Text(text = stringResource(id = R.string.search_server)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_server)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) scope.launch {
                    bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                }
            }
    )
    viewModel.getFilteredServerList(searchText).let {
        if (it.isEmpty()) {
            Icon(
                imageVector = Icons.Default.SentimentDissatisfied,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
            Text(
                text = stringResource(R.string.no_results_found), modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), fontSize = 24.sp, textAlign = TextAlign.Center
            )
        }
        LazyColumn(Modifier.fillMaxHeight()) {
            items(
                items = it,
                key = { server ->
                    server.value.url
                }
            ) { server ->
                ServerRow(server = server, viewModel = viewModel)
                viewModel.checkServerStatus(server.value)
            }
        }
    }
}

@Composable
private fun ServerRow(server: State<Server>, viewModel: ServerSelectViewModel) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    val authTypes: MutableState<List<AuthType>> = remember { mutableStateOf(listOf()) }
    val serverStatus = remember { viewModel.serverStatusMap[server.value] }

    Column(
        Modifier
            .animateContentSize()
            .clickable {
                loading = true
                scope.launch {
                    val types = viewModel.getAuthTypes(server)
                    if (types.isEmpty()) viewModel.setDestination()
                    if (types.size == 1) viewModel.setDestination(types[0])
                    if (types.size > 1) authTypes.value = types
                    loading = false
                }
            }
            .padding(8.dp + if (authTypes.value.isEmpty()) 0.dp else 14.dp)
            .fillMaxWidth()
    ) {
        Text(text = server.value.name)
        Row {
            when (serverStatus!!.value) {
                ServerStatus.CHECKING -> Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = "Checking"
                )
                ServerStatus.UNKNOWN -> Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Unknown"
                )
                ServerStatus.ENABLED -> Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Enabled"
                )
                ServerStatus.DISABLED -> Icon(
                    imageVector = Icons.Default.DisabledByDefault,
                    contentDescription = "Disabled"
                )
            }
            Text(text = server.value.url)
        }

        if (loading) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )
        }
        if (authTypes.value.isNotEmpty()) {
            Column(modifier = Modifier.padding(4.dp)) {
                authTypes.value.forEach {
                    Button(onClick = {
                        viewModel.setDestination(authType = it)
                    },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = it.title)
                    }
                }
            }
        }
    }
}