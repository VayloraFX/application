package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TradeDirection
import com.example.data.model.TradeOrder
import com.example.data.model.TradeStatus
import com.example.ui.theme.GsquadOnYellow
import com.example.ui.theme.GsquadYellow
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
fun ActiveTradeCard(
  trade: TradeOrder?,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = trade != null,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = modifier
  ) {
    if (trade == null) return@AnimatedVisibility

    val isCall = trade.direction == TradeDirection.CALL
    val dirColor = if (isCall) TradeGreen else TradeRed

    val cardBg = when (trade.status) {
      TradeStatus.EXECUTING -> Color(0xFFFCFDFE)
      TradeStatus.WON -> TradeGreenBg
      TradeStatus.LOST -> TradeRedBg
    }

    val borderColor = when (trade.status) {
      TradeStatus.EXECUTING -> GsquadYellow
      TradeStatus.WON -> TradeGreen
      TradeStatus.LOST -> TradeRed
    }

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
        .testTag("active_trade_card"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = cardBg),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp)
      ) {
        // Top row: Deriv Master pooled banner + live status
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Hub,
              contentDescription = null,
              tint = GsquadYellow,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "DERIV POOLED TRADE",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF2C323B)
            )
          }

          // Status badge
          when (trade.status) {
            TradeStatus.EXECUTING -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFFFF3D4))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.HourglassTop,
                  contentDescription = null,
                  tint = Color(0xFFC78500),
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${trade.remainingSeconds}s remaining",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF7A4F00)
                )
              }
            }
            TradeStatus.WON -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(TradeGreen)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "WON +$${String.format(Locale.US, "%.2f", trade.userProfit)}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
            TradeStatus.LOST -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(TradeRed)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Error,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "LOST -$${String.format(Locale.US, "%.2f", trade.userStake)}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main trade info: Symbol & Direction
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = trade.symbol,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = TextPrimary
            )
            Text(
              text = "Ref: ${trade.adminMasterOrderRef}",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = TextMuted
            )
          }

          // Direction Pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isCall) TradeGreenBg else TradeRedBg)
              .border(1.dp, dirColor, RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = if (isCall) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
              contentDescription = null,
              tint = dirColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = trade.direction.badge,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp,
              color = dirColor
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pooled calculation breakdown
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1F4F9))
            .padding(10.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Total Master Pooled Stake:",
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = "$${String.format(Locale.US, "%.2f", trade.pooledStake)} USD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Your Bot Share (${trade.squadTradersCount} active squad traders):",
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = "$${String.format(Locale.US, "%.2f", trade.userStake)} USD",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GsquadYellow
              )
            }
          }
        }

        if (trade.status == TradeStatus.EXECUTING) {
          Spacer(modifier = Modifier.height(10.dp))
          val progress = (trade.durationSeconds - trade.remainingSeconds).toFloat() / trade.durationSeconds
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = GsquadYellow,
            trackColor = LightBorder
          )
        }
      }
    }
  }
}
