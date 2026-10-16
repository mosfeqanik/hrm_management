package com.hrm.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hrm.mobile.data.Session
import com.hrm.mobile.ui.EmployeeDetailScreen
import com.hrm.mobile.ui.EmployeesScreen
import com.hrm.mobile.ui.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Session.init(this)

        setContent {
            MaterialTheme {
                var loggedIn by rememberSaveable { mutableStateOf(Session.token != null) }
                var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
                Session.onUnauthorized = { runOnUiThread { selectedId = null; loggedIn = false } }

                val id = selectedId
                when {
                    !loggedIn -> LoginScreen(onLoggedIn = { loggedIn = true })
                    id != null -> {
                        BackHandler { selectedId = null }
                        EmployeeDetailScreen(id = id, onBack = { selectedId = null })
                    }
                    else -> EmployeesScreen(
                        onOpen = { selectedId = it },
                        onLogout = { Session.clear(); loggedIn = false },
                    )
                }
            }
        }
    }
}
