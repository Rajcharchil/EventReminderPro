package com.charchil.reminderpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.presentation.ui.MainViewModel
import com.charchil.reminderpro.presentation.ui.setUpAlarm
import com.charchil.reminderpro.presentation.ui.setUpPeriodicAlarm
import com.charchil.reminderpro.presentation.ui.theme.ReminderProTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.charchil.reminderpro.presentation.ui.BottomNavItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import com.charchil.reminderpro.presentation.ui.Form

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReminderProTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = hiltViewModel<MainViewModel>()
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isTimePickerVisible = remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val format = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeInMillis = remember { mutableStateOf(0L) }
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        BottomNavItem.Home,
        BottomNavItem.Reminder,
        BottomNavItem.Settings
    )

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Form(
                time = format.format(timeInMillis.value),
                onTimeClick = { isTimePickerVisible.value = true },
                onSubmit = { name, dosage, checked ->
                    val reminder = Reminder(
                        name, dosage, timeInMillis.value, isTaken = false,
                        isRepeat = checked
                    )
                    viewModel.insert(reminder)
                    if (checked) {
                        setUpPeriodicAlarm(context, reminder)
                    } else {
                        setUpAlarm(context, reminder)
                    }
                }
            )

        }
    ) { paddingValues ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Event Reminder") },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                sheetState.bottomSheetState.expand()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(text = item.title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            if (isTimePickerVisible.value) {
                Dialog(onDismissRequest = { isTimePickerVisible.value = false }) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.White)
                    ) {
                        TimePicker(state = timePickerState)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = {
                                isTimePickerVisible.value = false
                            }) {
                                Text("Cancel")
                            }
                            TextButton(onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                timeInMillis.value = calendar.timeInMillis
                                isTimePickerVisible.value = false
                            }) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                if (uiState.data.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_no_data),
                                contentDescription = "No Data Icon",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Data Found!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        items(uiState.data) { reminder ->
                            Card(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = reminder.name)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = reminder.dosage)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = format.format(reminder.timeInmillis))
                                    }
                                    if (reminder.isRepeat) {
                                        IconButton(onClick = {
                                            viewModel.update(reminder.copy(isTaken = true, isRepeat = false))
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_schedule),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                    IconButton(onClick = {
                                        viewModel.delete(reminder)
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_delete),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${tabs[selectedTab].title} screen coming soon...", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ReminderForm(
    time: String,
    onTimeClick: () -> Unit,
    onSubmit: (String, String, Boolean) -> Unit
) {
    // Dummy form UI for example. Replace with your real form UI.
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Selected Time: $time")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onTimeClick) {
            Text("Pick Time")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onSubmit("Medicine Name", "1 Dose", false)
        }) {
            Text("Save Reminder")
        }
    }
}
