package com.paraooo.circularprogressimage

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paraooo.circular_progress_image.AnimatedCircularProgressImage
import com.paraooo.circular_progress_image.CircularProgressImage
import com.paraooo.circular_progress_image.rememberCircularProgressState
import com.paraooo.circularprogressimage.ui.theme.CircularProgressImageTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CircularProgressImageTheme {
                ExampleScreen()
            }
        }
    }
}
@Composable
fun ExampleScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            Example1(
                Modifier.weight(1F)
            )
            Example2(
                Modifier.weight(1F)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            Example3(
                Modifier.weight(1F)
            )
            Example4(
                Modifier.weight(1F)
            )
        }
    }
}

@Composable
fun Example1(
    modifier: Modifier = Modifier
) {

    var example1Progress by remember { mutableFloatStateOf(0.5f) }

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Static Example")

        CircularProgressImage(
            painter = painterResource(id = R.drawable.bg_todolist),
            progress = example1Progress,
            modifier = Modifier.size(160.dp),
        )

        Slider(value = example1Progress, colors = SliderDefaults.colors().copy(activeTrackColor = Color(0xFF15B392), thumbColor = Color(0xFF15B392)), onValueChange = { example1Progress = it })
    }
}

@Composable
fun Example2(
    modifier: Modifier = Modifier
) {
    val example2State = rememberCircularProgressState(initialProgress = 0.25f)

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Animated Example")

        AnimatedCircularProgressImage(
            state = example2State,
            painter = painterResource(id = R.drawable.bg_todolist),
            modifier = Modifier.size(160.dp),
            color = Color.Blue,
            backgroundColor = Color.LightGray
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(modifier = Modifier.weight(1F), colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Blue), onClick = { example2State.moveTo(0F) }) { }
            Button(modifier = Modifier.weight(1F), colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Blue), onClick = { example2State.moveTo(0.25F) }) {}
            Button(modifier = Modifier.weight(1F), colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Blue), onClick = { example2State.moveTo(0.5F) }) {  }
            Button(modifier = Modifier.weight(1F), colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Blue), onClick = { example2State.moveTo(0.75F) }) {  }
            Button(modifier = Modifier.weight(1F), colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Blue), onClick = { example2State.moveTo(1F) }) {  }
        }
    }
}

@Composable
fun Example3(
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infinite-rotation")

    val example3Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Infinite Rotation")

        CircularProgressImage(
            painter = painterResource(id = R.drawable.bg_todolist),
            color = Color.Magenta,
            backgroundColor = Color.LightGray,
            progress = example3Progress,
            modifier = Modifier.size(160.dp),
        )
    }
}

@Composable
fun Example4(
    modifier: Modifier = Modifier
) {
    var example4Progress by remember { mutableFloatStateOf(0f) }
    var isPressed by remember { mutableStateOf(false) }
    var direction by remember { mutableStateOf(1) }

    LaunchedEffect(isPressed, direction) {
        if (isPressed) {
            while (true) {
                example4Progress = (example4Progress + (0.01f * direction)).coerceIn(0f, 1f)

                if (example4Progress == 1f || example4Progress == 0f) {
                    break
                }
                delay(16L)
            }
        }
    }

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Press & Hold Example")

        CircularProgressImage(
            painter = painterResource(id = R.drawable.bg_todolist),
            backgroundColor = Color.LightGray,
            progress = example4Progress,
            modifier = Modifier.size(160.dp).pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                            if (example4Progress >= 1f) {
                                direction = -1
                            } else if (example4Progress <= 0f) {
                                direction = 1
                            }
                        }
                    }
                )
            },
        )
    }
}