package com.example.ai3dcoach

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import io.github.sceneview.Scene
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WorkoutSplitScreen()
            }
        }
    }
}

@Composable
fun WorkoutSplitScreen() {
    var setsCount by remember { mutableStateOf(3) }
    var repsCount by remember { mutableStateOf(15) }
    var activeMuscle by remember { mutableStateOf("Upper Chest") }

    // मसल के आधार पर एक्सरसाइज का नाम और एक्शन वीडियो
    val currentExercise = if (activeMuscle == "Upper Chest") {
        "Incline Dumbbell Press"
    } else {
        "Triceps Overhead Extension"
    }

    val currentVideoUrl = if (activeMuscle == "Upper Chest") {
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    } else {
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(12.dp)
    ) {
        // हेडर
        Text(
            text = "AI 3D COACH - $activeMuscle",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // स्प्लिट स्क्रीन (50% - 50%)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // बायां पैनल: 3D मॉडल
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Safe3DViewer()
            }

            // दायां पैनल: लूपिंग वीडियो प्लेयर (एक्शन गाइड)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                ExerciseVideoPlayer(videoUrl = currentVideoUrl)

                // वीडियो के ऊपर इनफार्मेशन बैज
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = Color(0xCC000000),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "ACTION GUIDE",
                            color = Color(0xFF4CAF50),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = Color(0xCC000000),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentExercise,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // कंट्रोल कार्ड
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242424)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TARGET GOAL", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = "${setsCount}X$repsCount",
                        color = Color.Yellow,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Button(
                    onClick = {
                        activeMuscle = if (activeMuscle == "Upper Chest") "Triceps" else "Upper Chest"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("बदलें मसल")
                }
            }
        }
    }
}

@Composable
fun ExerciseVideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f // म्यूट रखा गया है ताकि सिर्फ मूवमेंट दिखे
        }
    }

    LaunchedEffect(videoUrl) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}

@Composable
fun Safe3DViewer() {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    LaunchedEffect(modelLoader) {
        try {
            val instance = modelLoader.createModelInstance(
                assetFileLocation = "models/human_body.glb"
            )
            if (instance != null) {
                modelNode = ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 1.0f
                )
            } else {
                errorMessage = "3D फ़ाइल लोड नहीं हुई"
            }
        } catch (e: Throwable) {
            errorMessage = "एरर: ${e.javaClass.simpleName}"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFFF5252))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color(0xFFFF8A80),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                childNodes = listOfNotNull(modelNode)
            )
        }
    }
}
