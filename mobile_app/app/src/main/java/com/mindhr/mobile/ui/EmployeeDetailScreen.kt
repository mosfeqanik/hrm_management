package com.mindhr.mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindhr.mobile.data.Employee
import com.mindhr.mobile.data.Session

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(id: Long, onBack: () -> Unit) {
    var employee by remember { mutableStateOf<Employee?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        runCatching { Session.api.employee(id) }
            .onSuccess { employee = it }
            .onFailure { error = Session.errorMessage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(employee?.fullName ?: "Employee") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            val e = employee
            val err = error
            when {
                err != null -> Text(err, color = MaterialTheme.colorScheme.error)
                e == null -> CircularProgressIndicator()
                else -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Field("Code", e.employeeCode)
                    Field("Position", e.position)
                    Field("Email", e.email)
                    Field("Phone", e.phone)
                    Field("Employment type", e.employmentType)
                    Field("Joined", e.joinedAt)
                    Field("Address", e.address)
                    Field(
                        "Emergency contact",
                        listOfNotNull(e.emergencyContactName, e.emergencyContactRelation, e.emergencyContactPhone)
                            .joinToString(" · "),
                    )
                    Field("Status", if (e.active) "Active" else "Inactive")
                }
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String?) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(if (value.isNullOrBlank()) "-" else value, style = MaterialTheme.typography.bodyLarge)
    }
}
