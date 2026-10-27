package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GsquadOnYellow
import com.example.ui.theme.GsquadYellow
import com.example.ui.theme.GsquadYellowContainer
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit,
  modifier: Modifier = Modifier
) {
  val totalDurationMs = 10000L // 10 seconds as requested
  val progressAnimatable = remember { Animatable(0f) }
  var currentStepText by remember { mutableStateOf("Initializing G-squad Engine...") }
  var secondsRemaining by remember { mutableStateOf(10) }

  // Pulse animation for robot logo
  val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "logo_scale"
  )

  // Glow wave animation for progress bar (left to right sweep)
  val sweepOffset by infiniteTransition.animateFloat(
    initialValue = -0.5f,
    targetValue = 1.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "sweep_anim"
  )

  // 10-second timer and progress step handler
  LaunchedEffect(Unit) {
    kotlinx.coroutines.coroutineScope {
      // Animate progress smoothly from 0f to 1f over 10 seconds
      val animJob = launch {
        progressAnimatable.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = totalDurationMs.toInt(), easing = LinearEasing)
        )
      }

      // Step descriptions & countdown across 10 seconds
      for (sec in 10 downTo 1) {
        secondsRemaining = sec
        currentStepText = when (sec) {
          10, 9 -> "Connecting to Deriv Synapse API..."
          8, 7 -> "Aggregating G-Squad Trading Syndicate..."
          6, 5 -> "Calibrating Volatility 75 (1s) Algorithm..."
          4, 3 -> "Synchronizing Pooled Internal Wallets..."
          2, 1 -> "Finalizing Risk Management Engine..."
          else -> "Ready! Launching G-squad Terminal..."
        }
        delay(1000)
      }

      currentStepText = "Welcome to G-squad"
      delay(300)
      animJob.join()
      onSplashFinished()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LightBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("splash_screen_container")
  ) {
    // Skip chip on top-right for convenience
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(16.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFEFF2F6))
        .clickable { onSplashFinished() }
        .padding(horizontal = 10.dp, vertical = 5.dp)
        .testTag("skip_splash_btn")
    ) {
      Text(
        text = "Skip (${secondsRemaining}s)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary
      )
    }

    // Center Content: Logo & Branding
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Glow Ring + Mascot Logo
      Box(
        modifier = Modifier
          .size(120.dp)
          .scale(pulseScale)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(
                GsquadYellowContainer,
                Color(0xFFFFF9E8),
                LightBackground
              )
            )
          )
          .border(2.5.dp, GsquadYellow, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_gsquad_logo),
          contentDescription = "G-squad App Logo",
          modifier = Modifier.size(90.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Title & Badge
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "G-squad",
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          color = TextPrimary,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GsquadYellow)
            .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
          Text(
            text = "BOT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = GsquadOnYellow
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Automated Deriv Pooled Trading",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(10.dp))

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFFEFF3F8))
          .border(0.8.dp, LightBorder, RoundedCornerShape(20.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Multi-User Syndicate • Synthetics Volatility 75",
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Bottom Footer: Small Progress Bar that loads Left to Right across 10s
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Dynamic status label above the progress bar
      Text(
        text = currentStepText,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(10.dp))

      // The small progress bar that loads left side to right side
      val progress = progressAnimatable.value
      Box(
        modifier = Modifier
          .width(220.dp)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(Color(0xFFE5E9F0))
          .testTag("splash_progress_bar")
      ) {
        // Filled track from left to right
        Box(
          modifier = Modifier
            .fillMaxWidth(fraction = progress.coerceIn(0.01f, 1f))
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  GsquadYellow,
                  Color(0xFFFFCE38),
                  GsquadYellow
                )
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Percentage and countdown display
      Row(
        modifier = Modifier.width(220.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${(progress * 100).toInt()}%",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = GsquadYellow
        )
        Text(
          text = "${secondsRemaining}s remaining",
          fontSize = 10.sp,
          color = TextMuted
        )
      }
    }
  }
}
