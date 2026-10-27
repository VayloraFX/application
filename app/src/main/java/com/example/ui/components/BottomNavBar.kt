package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GsquadOnYellow
import com.example.ui.theme.GsquadYellow
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.Shield

enum class NavTab(val title: String, val icon: ImageVector, val tag: String) {
  BOT("Bot", Icons.Default.SmartToy, "nav_bot"),
  RISK("Risk", Icons.Default.Shield, "nav_risk"),
  TRADES("Trades", Icons.Default.History, "nav_trades"),
  WALLET("Wallet", Icons.Default.AccountBalanceWallet, "nav_wallet"),
  SQUAD("Squad", Icons.Default.Group, "nav_squad")
}

@Composable
fun BottomNavBar(
  currentTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = LightSurface,
    shadowElevation = 8.dp
  ) {
    Column(modifier = Modifier.navigationBarsPadding()) {
      HorizontalDivider(color = LightBorder, thickness = 0.8.dp)

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        NavTab.values().forEach { tab ->
          val isSelected = currentTab == tab

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = GsquadYellow)
              ) { onTabSelected(tab) }
              .padding(vertical = 6.dp)
              .testTag(tab.tag),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isSelected) GsquadYellow else Color.Transparent)
                  .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = tab.icon,
                  contentDescription = tab.title,
                  tint = if (isSelected) GsquadOnYellow else TextMuted,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.height(3.dp))

              Text(
                text = tab.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF1E232A) else TextSecondary
              )
            }
          }
        }
      }
    }
  }
}
