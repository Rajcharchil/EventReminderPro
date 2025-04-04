package com.charchil.reminderpro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.presentation.ui.MainViewModel
import com.charchil.reminderpro.presentation.ui.setUpAlarm
import com.charchil.reminderpro.presentation.ui.setUpPeriodicAlarm
import com.charchil.reminderpro.presentation.ui.theme.ReminderProTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReminderProTheme {

                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = hiltViewModel<MainViewModel>()
                    MainScreen(viewModel)
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

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Form(
                time = format.format(timeInMillis.value),
                onTimeClick = { isTimePickerVisible.value = true }
            ) { name, dosage, checked ->
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
        }
    ) { paddingValues ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Event Reminder") },
                    actions = {
                        IconButton(onClick = {
                            scope.launch { sheetState.bottomSheetState.expand() }
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null,modifier = Modifier.size(38.dp))
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (isTimePickerVisible.value) {
                Dialog(onDismissRequest = { isTimePickerVisible.value = false }) {
                    Column {
                        TimePicker(state = timePickerState)
                        Row {
                            Button(onClick = { isTimePickerVisible.value = false }) {
                                Text(text = "Cancel")
                            }
                            Button(onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour) // ✅ 24-hour format (correct)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                    set(Calendar.SECOND, 0) // Ensuring precise time
                                    set(Calendar.MILLISECOND, 0)
                                }
                                timeInMillis.value = calendar.timeInMillis
                                isTimePickerVisible.value = false
                            }) {
                                Text(text = "Confirm")
                            }
                        }
                    }
                }
            }

            if (uiState.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Data")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(uiState.data) {
                        Card(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = it.name)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = it.dosage)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = format.format(it.timeInmillis))
                                }
                                if (it.isRepeat) {
                                    IconButton(onClick = {
                                        viewModel.update(it.copy(isTaken = true, isRepeat = false))
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_schedule),
                                            contentDescription = null
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.delete(it) }) {
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
        }
    }
}






@Composable
fun Form(
    time: String,
    onTimeClick: () -> Unit,
    onClick: (String, String, Boolean) -> Unit
) {
    val name = remember { mutableStateOf("") }
    val dosage = remember { mutableStateOf("") }
    val isChecked = remember { mutableStateOf(false) }
    val context = LocalContext.current // ✅ Toast ke liye

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Create Task",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.task_icon),
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // 🔹 **Name Field**
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(text = "Enter Name", color = Color.Gray) }
        )

        Spacer(Modifier.height(10.dp))

        // 🔹 **Dosage Field**
        OutlinedTextField(
            value = dosage.value,
            onValueChange = { dosage.value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(text = "Enter Event Description", color = Color.Gray) }
        )

        Spacer(Modifier.height(10.dp))

        // 🔹 **Time Picker**
        OutlinedTextField(
            value = time,
            onValueChange = {},
            modifier = Modifier
                .clickable { onTimeClick.invoke() }
                .fillMaxWidth(),
            enabled = false
        )

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Schedule")
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 **Button with Validation**
        Button(onClick = {
            if (name.value.isBlank() || dosage.value.isBlank()) {
                // ✅ Empty fields ke liye Toast message
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // ✅ Data Add karna
                onClick.invoke(name.value, dosage.value, isChecked.value)

                // 🧹 **Clear fields after adding**
                name.value = ""
                dosage.value = ""
                isChecked.value = false
            }
        }) {
            Text(text = "Add")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
