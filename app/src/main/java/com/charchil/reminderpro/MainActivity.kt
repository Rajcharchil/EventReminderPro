package com.charchil.reminderpro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import com.charchil.reminderpro.presentation.screens.worldclock.WorldClockActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import com.charchil.reminderpro.presentation.ui.Form
import com.charchil.reminderpro.util.StopwatchActivity
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import android.widget.Toast
import androidx.compose.foundation.clickable

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
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf(false) }

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
                onSubmit = { newName, newDosage, newRepeat ->
                    if (editingReminder != null) {
                        // Update existing reminder
                        val updatedReminder = editingReminder!!.copy(
                            name = newName,
                            dosage = newDosage,
                            timeInmillis = timeInMillis.value,
                            isRepeat = newRepeat
                        )
                        viewModel.update(updatedReminder)
                        editingReminder = null
                    } else {
                        // Create new reminder
                        val reminder = Reminder(
                            newName, newDosage, timeInMillis.value, isTaken = false,
                            isRepeat = newRepeat
                        )
                        viewModel.insert(reminder)
                        if (newRepeat) {
                            setUpPeriodicAlarm(context, reminder)
                        } else {
                            setUpAlarm(context, reminder)
                        }
                    }
                    name = ""
                    dosage = ""
                    repeat = false
                    timeInMillis.value = 0L
                },
                initialName = name,
                initialDosage = dosage,
                initialRepeat = repeat
            )
        }
    ) { innerPadding ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Event Reminder") },
                    actions = {
                        // Notification Icon with Count
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    val message = if (uiState.data.isEmpty()) {
                                        "No events scheduled"
                                    } else {
                                        "You have ${uiState.data.size} event${if (uiState.data.size > 1) "s" else ""} scheduled"
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                modifier = Modifier.size(38.dp)
                            )
                            if (uiState.data.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            color = Color.Red,
                                            shape = CircleShape
                                        )
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.data.size.toString(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(2.dp)
                                    )
                                }
                            }
                        }
                        // Add Button
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
                            onClick = {
                                when (item) {
                                    BottomNavItem.Reminder -> {
                                        context.startActivity(Intent(context, WorldClockActivity::class.java))
                                    }
                                    BottomNavItem.Settings -> {
                                        context.startActivity(Intent(context, StopwatchActivity::class.java))
                                    }
                                    else -> {
                                        selectedTab = index
                                    }
                                }
                            },
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
                        items(uiState.data.sortedBy { it.timeInmillis }) { reminder ->
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reminder.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = reminder.dosage,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = format.format(reminder.timeInmillis),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            editingReminder = reminder
                                            name = reminder.name
                                            dosage = reminder.dosage
                                            repeat = reminder.isRepeat
                                            timeInMillis.value = reminder.timeInmillis
                                            scope.launch {
                                                sheetState.bottomSheetState.expand()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        if (reminder.isRepeat) {
                                            IconButton(onClick = {
                                                viewModel.update(reminder.copy(isTaken = true, isRepeat = false))
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_schedule),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        IconButton(onClick = {
                                            viewModel.delete(reminder)
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_delete),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            } else if (selectedTab == 2) {
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

    // TIME PICKER DIALOG
    if (isTimePickerVisible.value) {
        Dialog(onDismissRequest = { isTimePickerVisible.value = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isTimePickerVisible.value = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                            }
                            timeInMillis.value = calendar.timeInMillis
                            isTimePickerVisible.value = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}