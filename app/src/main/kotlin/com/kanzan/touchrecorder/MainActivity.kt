package com.kanzan.touchrecorder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kanzan.core.designsystem.theme.KanzanTheme
import com.kanzan.touchrecorder.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if launched from floating overlay
        val shouldNavigateToMacroList = intent?.getBooleanExtra("ACTION_PLAY_MACRO", false) ?: false
        val shouldShowSaveDialog = intent?.getBooleanExtra("ACTION_SAVE_RECORDING", false) ?: false
        
        setContent {
            KanzanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    LaunchedEffect(shouldNavigateToMacroList, shouldShowSaveDialog) {
                        if (shouldNavigateToMacroList || shouldShowSaveDialog) {
                            // Navigate to macro list if coming from floating overlay
                            navController.navigate("macro_list") {
                                popUpTo("macro_list") { inclusive = true }
                            }
                        }
                    }
                    
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Handle new intents when activity already running
        val shouldNavigateToMacroList = intent.getBooleanExtra("ACTION_PLAY_MACRO", false)
        if (shouldNavigateToMacroList) {
            // Activity already open, user can select macro
        }
    }
}
