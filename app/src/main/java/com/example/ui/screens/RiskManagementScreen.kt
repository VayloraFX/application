package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
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
import java.util.Locale

@Composable
fun RiskManagementScreen(
  user: UserAccount?,
  onSaveRisk: (stake: Double, tp: Double, sl: Double) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isRunning = user?.isBotRunning == true

  var stakeInput by remember(user?.stakeAmount) {
    mutableStateOf(String.format(Locale.US, "%.0f", user?.stakeAmount ?: 5.0))
  }
  var tpInput by remember(user?.targetProfit) {
    mutableStateOf(String.format(Locale.US, "%.0f", user?.targetProfit ?: 50.0))
  }
  var slInput by remember(user?.targetLoss) {
    mutableStateOf(String.format(Locale.US, "%.0f", user?.targetLoss ?: 25.0))
  }

  val parsedStake = stakeInput.toDoubleOrNull() ?: 5.0
  val parsedTp = tpInput.toDoubleOrNull() ?: 50.0
  val parsedSl = slInput.toDoubleOrNull() ?: 25.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item { Spacer(modifier = Modifier.height(2.dp)) }

    // Page Header
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("risk_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(GsquadYellowContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = GsquadYellow,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Risk Management",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TextPrimary
                )
                Text(
                  text = "Automated Capital Safeguards",
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }
            }

            if (isRunning) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFFF3D4))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF8A5800),
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "LOCKED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A5800)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Active Parameters Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
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
          Text(
            text = "CURRENT ACTIVE RISK PROFILE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF7F8FA))
                .padding(10.dp)
            ) {
              Column {
                Text(text = "Stake / Trade", fontSize = 11.sp, color = TextSecondary)
                Text(
                  text = "$${String.format(Locale.US, "%.2f", user?.stakeAmount ?: 5.0)}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GsquadYellow
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF7F8FA))
                .padding(10.dp)
            ) {
              Column {
                Text(text = "Target Profit", fontSize = 11.sp, color = TextSecondary)
                Text(
                  text = "$${String.format(Locale.US, "%.2f", user?.targetProfit ?: 50.0)}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TradeGreen
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF7F8FA))
                .padding(10.dp)
            ) {
              Column {
                Text(text = "Stop Loss", fontSize = 11.sp, color = TextSecondary)
                Text(
                  text = "$${String.format(Locale.US, "%.2f", user?.targetLoss ?: 25.0)}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TradeRed
                )
              }
            }
          }
        }
      }
    }

    // Input Configuration Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("risk_configuration_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
        ) {
          Text(
            text = "Configure Parameters",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "Specify your trade stake, take-profit milestone, and stop-loss limit.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
          )

          // 1. STAKE AMOUNT
          Text(
            text = "STAKE AMOUNT (USD)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = stakeInput,
            onValueChange = { if (!isRunning) stakeInput = it },
            enabled = !isRunning,
            prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LocalAtm,
                contentDescription = null,
                tint = GsquadYellow
              )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("risk_stake_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GsquadYellow,
              unfocusedBorderColor = LightBorder
            ),
            shape = RoundedCornerShape(10.dp)
          )

          // Quick Preset Stake Chips
          if (!isRunning) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(1, 5, 10, 25, 50, 100).forEach { amt ->
                val isSel = stakeInput == amt.toString()
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) GsquadYellow else Color(0xFFEFF2F6))
                    .clickable { stakeInput = amt.toString() }
                    .padding(vertical = 7.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$$amt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSel) GsquadOnYellow else TextPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 2. TARGET PROFIT
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = null,
              tint = TradeGreen,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "TARGET PROFIT",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TradeGreen
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = tpInput,
            onValueChange = { if (!isRunning) tpInput = it },
            enabled = !isRunning,
            prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("risk_tp_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TradeGreen,
              unfocusedBorderColor = LightBorder
            ),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(18.dp))

          // 3. TARGET LOSS (STOP LOSS)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Flag,
              contentDescription = null,
              tint = TradeRed,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "TARGET LOSS (STOP LOSS)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TradeRed
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = slInput,
            onValueChange = { if (!isRunning) slInput = it },
            enabled = !isRunning,
            prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("risk_sl_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TradeRed,
              unfocusedBorderColor = LightBorder
            ),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(22.dp))

          // Primary "Configure" Button - as explicitly requested by user!
          Button(
            onClick = {
              if (!isRunning) {
                onSaveRisk(parsedStake, parsedTp, parsedSl)
                Toast.makeText(context, "Risk parameters configured: Stake $${parsedStake}, TP $${parsedTp}, SL $${parsedSl}", Toast.LENGTH_SHORT).show()
              }
            },
            enabled = !isRunning,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("configure_risk_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = GsquadYellow,
              contentColor = GsquadOnYellow,
              disabledContainerColor = Color(0xFFE5E9F0),
              disabledContentColor = TextMuted
            )
          ) {
            Text(
              text = "Configure",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp
            )
          }
        }
      }
    }

    // Risk Safeguards Explainer Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = GsquadYellow,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Capital Safeguard Rules",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = TextPrimary
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TradeGreen,
              modifier = Modifier.size(16.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(text = "Automatic Profit Lock-In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text(text = "When session gains reach Target Profit ($${String.format(Locale.US, "%.0f", parsedTp)}), the bot halts to protect profits.", fontSize = 11.sp, color = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TradeGreen,
              modifier = Modifier.size(16.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(text = "Strict Stop-Loss Protection", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text(text = "If session losses hit Stop Loss ($${String.format(Locale.US, "%.0f", parsedSl)}), the bot stops instantly to protect your capital.", fontSize = 11.sp, color = TextSecondary)
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}
