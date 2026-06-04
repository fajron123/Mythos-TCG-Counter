package com.fajron.mythoslicznik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // chowanie paskow
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            // Blokada wygaszania ekranu w trakcie gry
            val currentView = LocalView.current
            DisposableEffect(currentView) {
                currentView.keepScreenOn = true
                onDispose { currentView.keepScreenOn = false }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A0A)
                ) {
                    GameCounterScreen()
                }
            }
        }
    }
}

@Composable
fun GameCounterScreen() {
    // chakra i misje graczy
    var chakra1 by remember { mutableStateOf(5) }
    var mission1 by remember { mutableStateOf(0) }
    var chakra2 by remember { mutableStateOf(5) }
    var mission2 by remember { mutableStateOf(0) }

    // Kto ma edge
    var edgePlayer by remember { mutableStateOf(1) }

    // Zmienne od timera
    var timeLeft by remember { mutableStateOf(30 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Odliczanie czasu
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        if (timeLeft == 0) isTimerRunning = false
    }

    // Zamiana sekund na minuty i sekundy
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    // zmiana kolorow jak sie czas konczy
    val (themeCardColor, themeAlertColor) = when {
        timeLeft <= 60 -> {
            // minuta do konca
            Pair(Color(0xFF3D1616), Color(0xFFEF5350))
        }
        timeLeft <= 300 -> {
            // 5 min do konca
            Pair(Color(0xFF2B1B10), Color(0xFFB05C1D))
        }
        else -> {
            Pair(Color(0xFF1E1E1E), Color.Transparent)
        }
    }

    // Kolor samego tekstu zegara
    val timerTextColor = when {
        timeLeft <= 60 -> Color(0xFFFF8A80)
        timeLeft <= 300 -> Color(0xFFFFCC80)
        else -> Color.White
    }

    // Funkcja czyszcząca stan gry
    fun resetGame() {
        chakra1 = 5
        mission1 = 0
        chakra2 = 5
        mission2 = 0
        edgePlayer = 1
        timeLeft = 30 * 60
        isTimerRunning = false
        showResetDialog = false
    }

    // Pop-up z zapytaniem o restart
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset game") },
            text = { Text("Are you sure you want to reset the game?") },
            confirmButton = {
                TextButton(onClick = { resetGame() }) {
                    Text("Yes", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gracz 2
        PlayerSection(
            playerName = "Player 2",
            chakra = chakra1,
            missionPoints = mission1,
            hasEdge = edgePlayer == 1,
            cardColor = themeCardColor,
            alertColor = themeAlertColor,
            onChakraChange = { chakra1 = it },
            onChakraReset = { chakra1 = 5 },
            onMissionChange = { mission1 = it },
            onEdgeClick = { edgePlayer = 1 },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .graphicsLayer(rotationZ = 180f)
        )

        // Reset i czas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.weight(1f)
            ) {
                Text("RESET", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier.weight(1.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = timeString,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = timerTextColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Button(
                    onClick = { isTimerRunning = !isTimerRunning },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isTimerRunning) Color(0xFFE57373) else Color(0xFF81C784)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(if (isTimerRunning) "Pause" else "Start", color = Color.Black, fontSize = 12.sp)
                }
            }
        }

        // Gracz 1
        PlayerSection(
            playerName = "Player 1",
            chakra = chakra2,
            missionPoints = mission2,
            hasEdge = edgePlayer == 2,
            cardColor = themeCardColor,
            alertColor = themeAlertColor,
            onChakraChange = { chakra2 = it },
            onChakraReset = { chakra2 = 5 },
            onMissionChange = { mission2 = it },
            onEdgeClick = { edgePlayer = 2 },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun PlayerSection(
    playerName: String,
    chakra: Int,
    missionPoints: Int,
    hasEdge: Boolean,
    cardColor: Color,    // Przyjmujemy dynamiczny kolor tła panelu
    alertColor: Color,   // Przyjmujemy dynamiczny kolor ostrzeżenia czasu
    onChakraChange: (Int) -> Unit,
    onChakraReset: () -> Unit,
    onMissionChange: (Int) -> Unit,
    onEdgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Jeśli gracz ma Edge, ramka jest żółta. Jeśli nie, ale trwa alarm czasowy, ramka przyjmuje kolor alarmu.
    val borderColor = when {
        hasEdge -> Color.Yellow
        alertColor != Color.Transparent -> alertColor
        else -> Color.Transparent
    }

    val borderWidth = if (hasEdge || alertColor != Color.Transparent) 3.dp else 0.dp

    Column(
        modifier = modifier
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .background(cardColor, RoundedCornerShape(12.dp)) // Dynamiczne tło panelu
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Nazwa gracza i przycisk Edge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = playerName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Box(
                modifier = Modifier
                    .background(if (hasEdge) Color.Yellow else Color(0xFF333333), RoundedCornerShape(6.dp))
                    .clickable { onEdgeClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (hasEdge) "EDGE" else "TAKE EDGE",
                    color = if (hasEdge) Color.Black else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Liczniki
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Licznik Chakry
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "CHAKRA", color = Color(0xFF4DD0E1), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onChakraChange(chakra - 1) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) { Text("-", fontSize = 20.sp) }

                    Text(
                        text = "$chakra",
                        fontSize = 64.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(
                        onClick = { onChakraChange(chakra + 1) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) { Text("+", fontSize = 20.sp) }
                }

                TextButton(
                    onClick = onChakraReset,
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Reset (5)", color = Color.Gray, fontSize = 12.sp)
                }
            }

            // Licznik punktów misji
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "MISSION POINTS", color = Color(0xFFBA68C8), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onMissionChange(missionPoints - 1) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) { Text("-", fontSize = 20.sp) }

                    Text(
                        text = "$missionPoints",
                        fontSize = 64.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(
                        onClick = { onMissionChange(missionPoints + 1) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) { Text("+", fontSize = 20.sp) }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}