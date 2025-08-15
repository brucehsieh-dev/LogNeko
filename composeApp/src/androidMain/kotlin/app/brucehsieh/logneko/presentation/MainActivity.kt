package app.brucehsieh.logneko.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.brucehsieh.logneko.App
import app.brucehsieh.logneko.core.initializer.ActivityInitializer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ActivityInitializer(this)

        setContent {
            App()
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}