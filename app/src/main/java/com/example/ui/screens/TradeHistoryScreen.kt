package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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

enum class TradeFilter { ALL, WINS, LOSSES }

@Composable
fun TradeHistoryScreen(
  trades: List<TradeOrder>,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf(TradeFilter.ALL) }

  val filteredTrades = remember(trades, selectedFilter) {
    when (selectedFilter) {
      TradeFilter.ALL -> trades
      TradeFilter.WINS -> trades.filter { it.status == TradeStatus.WON }
      TradeFilter.LOSSES -> trades.filter { it.status == TradeStatus.LOST }
    }
  }

  val totalPnL = trades.sumOf { it.userProfit }
  val totalWins = trades.count { it.status == TradeStatus.WON }
  val winRate = if (trades.isNotEmpty()) ((totalWins.toDouble() / trades.size) * 100).toInt() else 0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item { Spacer(modifier = Modifier.height(2.dp)) }

    // Summary Header Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("trades_summary_card"),
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
            text = "Total Bot PnL",
            fontSize = 12.sp,
            color = TextSecondary
          )
          Text(
            text = "$${String.format(Locale.US, "%+.2f", totalPnL)} USD",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (totalPnL >= 0) TradeGreen else TradeRed
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(8.dp)
            ) {
              Column {
                Text(text = "Total Trades", fontSize = 11.sp, color = TextSecondary)
                Text(text = "${trades.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              }
            }
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(8.dp)
            ) {
              Column {
                Text(text = "Win Rate", fontSize = 11.sp, color = TextSecondary)
                Text(text = "$winRate%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GsquadYellow)
              }
            }
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(8.dp)
            ) {
              Column {
                Text(text = "Won / Lost", fontSize = 11.sp, color = TextSecondary)
                Text(text = "$totalWins / ${trades.size - totalWins}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              }
            }
          }
        }
      }
    }

    // Filter Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TradeFilter.values().forEach { filter ->
          val isSelected = filter == selectedFilter
          val label = when (filter) {
            TradeFilter.ALL -> "All (${trades.size})"
            TradeFilter.WINS -> "Wins ($totalWins)"
            TradeFilter.LOSSES -> "Losses (${trades.size - totalWins})"
          }
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) GsquadYellow else LightSurface)
              .border(1.dp, if (isSelected) GsquadYellow else LightBorder, RoundedCornerShape(10.dp))
              .clickable { selectedFilter = filter }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) GsquadOnYellow else TextPrimary
            )
          }
        }
      }
    }

    // Trade List
    if (filteredTrades.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LightSurface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No trades in this filter yet.",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
              )
              Text(
                text = "When the bot executes pooled trades, they will appear here.",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }
    } else {
      items(filteredTrades) { trd ->
        val isWon = trd.status == TradeStatus.WON
        val resColor = if (isWon) TradeGreen else TradeRed
        val resBg = if (isWon) TradeGreenBg else TradeRedBg
        val isCall = trd.direction == TradeDirection.CALL

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LightSurface),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("trade_item_${trd.tradeId}")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isCall) TradeGreenBg else TradeRedBg),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isCall) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isCall) TradeGreen else TradeRed,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = trd.symbol,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                  )
                  Text(
                    text = "Ref: ${trd.adminMasterOrderRef}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                  )
                }
              }

              // Result Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(resBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (isWon) "+$${String.format(Locale.US, "%.2f", trd.userProfit)}" else "-$${String.format(Locale.US, "%.2f", trd.userStake)}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = resColor
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pooled details line
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Hub,
                  contentDescription = null,
                  tint = GsquadYellow,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Pooled: $${String.format(Locale.US, "%.2f", trd.pooledStake)} (${trd.squadTradersCount} traders)",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
              Text(
                text = "Your Stake: $${String.format(Locale.US, "%.2f", trd.userStake)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}
