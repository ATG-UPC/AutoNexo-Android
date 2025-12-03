package com.atg.autonexo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.core.navigation.AppNavigation
import com.atg.autonexo.core.navigation.Route
import com.atg.autonexo.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferences: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }
                
                // Determinar la ruta inicial basándose en el estado de autenticación
                LaunchedEffect(Unit) {
                    startDestination = if (userPreferences.isLoggedIn()) {
                        Route.Home.route
                    } else {
                        Route.Auth.Login.route
                    }
                }
                
                // Esperar a determinar la ruta inicial antes de mostrar navegación
                startDestination?.let { destination ->
                    AppNavigation(
                        navController = navController,
                        startDestination = destination
                    )
                }
            }
        }
    }
}
