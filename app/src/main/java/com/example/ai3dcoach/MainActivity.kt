package com.example.ai3dcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(12.dp)
    ) {
        Text(
            text = "AI 3D COACH - $activeMuscle",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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

            // दायां पैनल: वर्कआउट एक्शन
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EXERCISE ACTION",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Form & Motion Guide",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
            errorMessage = "एरर: ${e.javaClass.simpleName} - ${e.message ?: "File format issue"}"
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
