package com.mindhr.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun EmployeesScreen(onOpen: (Long) -> Unit, onLogout: () -> Unit) {
    var employees by remember { mutableStateOf<List<Employee>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }

    LaunchedEffect(reload) {
        employees = null
        error = null
        runCatching { Session.api.employees() }
            .onSuccess { employees = it }
            .onFailure { error = Session.errorMessage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employees") },
                actions = { TextButton(onClick = onLogout) { Text("Sign out") } },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            val list = employees
            val err = error
            when {
                err != null -> Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(err, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { reload++ }) { Text("Retry") }
                }
                list == null -> CircularProgressIndicator()
                list.isEmpty() -> Text("No employees yet")
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(list, key = { it.id }) { e ->
                        ListItem(
                            modifier = Modifier.fillMaxWidth().clickable { onOpen(e.id) },
                            headlineContent = { Text(e.fullName) },
                            supportingContent = { Text(listOfNotNull(e.employeeCode, e.position).joinToString(" · ")) },
                            trailingContent = { if (!e.active) Text("Inactive") },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
