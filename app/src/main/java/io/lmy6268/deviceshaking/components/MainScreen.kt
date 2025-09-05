package io.lmy6268.deviceshaking.components

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import io.lmy6268.deviceshaking.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val mainViewModel: MainViewModel = hiltViewModel()

    val context = LocalContext.current
    LifecycleResumeEffect(mainViewModel) {
        mainViewModel.startListening()
        onPauseOrDispose {
            mainViewModel.stopListening()
        }
    }


    LaunchedEffect(Unit) {
        mainViewModel.shakeStoppedEvents.collectLatest {
            Toast.makeText(context, "흔들림 멈춤 감지됨!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Sensor Example") }) }) { padding ->
        Text(
            text = "흔들고 나서 멈추면 이벤트 발생", modifier = Modifier.padding(padding)
        )
    }
}