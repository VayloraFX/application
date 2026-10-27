package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SquadMember
import com.example.data.model.TradeOrder
import com.example.data.model.UserAccount
import androidx.compose.material.icons.filled.Shield
import com.example.ui.components.ActiveTradeCard
import com.example.ui.theme.GsquadOnYellow
import com.example.ui.theme.GsquadYellow
import com.example.ui.theme.GsquadYellowContainer
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradeGreenBg
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradeRedBg
import java.util.Locale

@Composable
fun BotDashboardScreen(
  user: UserAccount?,
  squadMembers: List<SquadMember>,
  activeTrade: TradeOrder?,
  alertMessage: String?,
  botLogs: List<String>,
  onToggleBot: () -> Unit,
  onConfigureRisk: () -> Unit = {},
  onDismissAlert: () -> Unit,
  onGoToWallet: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isRunning = user?.isBotRunning == true

  // Pulsing animation for the Big Start/Stop button
  val infiniteTransition = rememberInfiniteTransition(label = "btn_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "btn_scale"
  )

  val activeSquadCount = squadMembers.count { it.isBotRunning } + (if (isRunning) 1 else 0)
  val squadStakeTotal = squadMembers.filter { it.isBotRunning }.sumOf { it.stakeAmount } + (if (isRunning) (user?.stakeAmount ?: 0.0) else 0.0)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item { Spacer(modifier = Modifier.height(2.dp)) }

    // Alert Banner if risk management triggered or error
    if (alertMessage != null) {
      item {
        val isSuccess = alertMessage.startsWith("TARGET_PROFIT")
        val alertBg = if (isSuccess) TradeGreenBg else TradeRedBg
        val alertBorder = if (isSuccess) TradeGreen else TradeRed
        val alertIcon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = alertBg),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, alertBorder, RoundedCornerShape(12.dp))
            .testTag("alert_banner")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = alertIcon,
                contentDescription = null,
                tint = alertBorder,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = alertMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = alertBorder
              )
            }
            IconButton(
              onClick = onDismissAlert,
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = alertBorder,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }

    // Active trade card (executing / won / lost)
    if (activeTrade != null) {
      item {
        ActiveTradeCard(trade = activeTrade)
      }
    }

    // Hero Bot Control Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_bot_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Status tag
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isRunning) TradeGreenBg else Color(0xFFF1F3F6))
              .padding(horizontal = 12.dp, vertical = 5.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isRunning) TradeGreen else Color(0xFF8E99A8))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isRunning) "SCANNING DERIV SYNTHETICS" else "BOT READY TO LAUNCH",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isRunning) TradeGreen else TextSecondary
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Big Glowing Robot Mascot Avatar
          Box(
            modifier = Modifier
              .size(84.dp)
              .clip(CircleShape)
              .background(if (isRunning) GsquadYellowContainer else Color(0xFFF3F5F8))
              .border(
                2.dp,
                if (isRunning) GsquadYellow else LightBorder,
                CircleShape
              )
              .then(if (isRunning) Modifier.scale(pulseScale) else Modifier),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_gsquad_logo),
              contentDescription = "Bot Avatar",
              modifier = Modifier.size(62.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = if (isRunning) "G-squad Bot is Active" else "G-squad Bot is Idle",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
          )

          Text(
            text = if (isRunning)
              "Aggregating stakes with $activeSquadCount squad traders on Deriv Master Account"
            else
              "Configure risk limits below and launch to start automated pooled trading",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          // Prominent Primary Action Button (Yellow Accent!)
          Button(
            onClick = onToggleBot,
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("toggle_bot_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isRunning) TradeRed else GsquadYellow,
              contentColor = if (isRunning) Color.White else GsquadOnYellow
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isRunning) "STOP TRADING BOT" else "START AUTOMATED BOT",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }
        }
      }
    }

    // Squad Pool Live Syndicate Banner
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFC)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
          .testTag("squad_pool_banner")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(GsquadYellowContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = Color(0xFF9E6E00),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Live Squad Syndicate",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "$activeSquadCount traders running | Pooled: $${String.format(Locale.US, "%.2f", squadStakeTotal)}",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(GsquadYellow)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "DERIV MASTER",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = GsquadOnYellow
            )
          }
        }
      }
    }

    // Session Performance Metrics Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("session_metrics_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.QueryStats,
                contentDescription = null,
                tint = GsquadYellow,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Session Performance",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
              )
            }

            val pnl = user?.sessionNetPnL ?: 0.0
            val pnlColor = if (pnl >= 0) TradeGreen else TradeRed
            Text(
              text = "$${String.format(Locale.US, "%+.2f", pnl)} USD",
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold,
              color = pnlColor
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4 Grid stats
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricPill(
              title = "Target Profit",
              value = "$${String.format(Locale.US, "%.0f", user?.targetProfit ?: 50.0)}",
              color = TradeGreen,
              modifier = Modifier.weight(1f)
            )
            MetricPill(
              title = "Stop Loss",
              value = "$${String.format(Locale.US, "%.0f", user?.targetLoss ?: 25.0)}",
              color = TradeRed,
              modifier = Modifier.weight(1f)
            )
            MetricPill(
              title = "Win Rate",
              value = "${user?.winRatePercentage ?: 0}%",
              color = GsquadYellow,
              modifier = Modifier.weight(1f)
            )
            MetricPill(
              title = "W / L",
              value = "${user?.totalWins ?: 0}/${user?.totalLosses ?: 0}",
              color = TextPrimary,
              modifier = Modifier.weight(1f)
            )
          }

          // Target profit progress bar
          val targetProfit = (user?.targetProfit ?: 50.0).coerceAtLeast(1.0)
          val currentProfit = (user?.sessionProfit ?: 0.0).coerceAtLeast(0.0)
          val tpProgress = (currentProfit / targetProfit).toFloat().coerceIn(0f, 1f)

          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Progress toward Target Profit:",
              fontSize = 11.sp,
              color = TextSecondary
            )
            Text(
              text = "${(tpProgress * 100).toInt()}%",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TradeGreen
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
            progress = { tpProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = TradeGreen,
            trackColor = Color(0xFFE5E9F0)
          )
        }
      }
    }

    // Quick Risk Profile Shortcut Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onConfigureRisk() }
          .testTag("bot_risk_shortcut_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GsquadYellowContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = GsquadYellow,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Stake $${String.format(Locale.US, "%.0f", user?.stakeAmount ?: 5.0)} • TP $${String.format(Locale.US, "%.0f", user?.targetProfit ?: 50.0)} • SL $${String.format(Locale.US, "%.0f", user?.targetLoss ?: 25.0)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "Manage risk limits on Risk page",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(GsquadYellow)
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = "Configure",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = GsquadOnYellow
            )
          }
        }
      }
    }

    // Live Execution Activity Feed
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("bot_logs_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.ListAlt,
              contentDescription = null,
              tint = GsquadYellow,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Live Execution Feed",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = TextPrimary
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF6F8FB))
              .border(0.8.dp, LightBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            if (botLogs.isEmpty()) {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                  text = "Bot idle. Start bot to see real-time signals & pooled executions.",
                  fontSize = 11.sp,
                  color = TextMuted,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                items(botLogs) { log ->
                  val isWin = log.contains("WON")
                  val isLoss = log.contains("LOSS")
                  val logColor = when {
                    isWin -> TradeGreen
                    isLoss -> TradeRed
                    log.contains("Signal found") -> Color(0xFF0066CC)
                    else -> TextSecondary
                  }
                  Text(
                    text = log,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = logColor,
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}

@Composable
fun MetricPill(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFFF7F8FA))
      .border(0.8.dp, LightBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 6.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = title,
        fontSize = 10.sp,
        color = TextSecondary,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
  }
}
