package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import java.util.Locale

@Composable
fun TopHeader(
  user: UserAccount?,
  isBotRunning: Boolean,
  onWalletClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Pulsing animation for running bot
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = LightSurface,
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App Logo & Title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { /* Brand click */ }
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(GsquadYellowContainer)
              .border(1.5.dp, GsquadYellow, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_gsquad_logo),
              contentDescription = "G-squad Logo",
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "G-squad",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(GsquadYellow)
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "BOT",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  color = GsquadOnYellow
                )
              }
            }
            Text(
              text = "Deriv Pooled Bot",
              fontSize = 11.sp,
              color = TextSecondary,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Wallet Balance Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF4F6F9))
            .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
            .clickable { onWalletClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("header_wallet_chip")
        ) {
          Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = "Wallet",
            tint = GsquadYellow,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$${String.format(Locale.US, "%.2f", user?.walletBalance ?: 0.0)}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Secondary Bar: Account ID + Live Bot Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Account ID with quick copy
        user?.accountId?.let { accountId ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFF7F8FA))
              .border(0.8.dp, LightBorder, RoundedCornerShape(6.dp))
              .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("GSQ Account ID", accountId)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Account ID copied: $accountId", Toast.LENGTH_SHORT).show()
              }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("copy_account_id_chip")
          ) {
            Text(
              text = "ID: ",
              fontSize = 11.sp,
              color = TextMuted,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = accountId,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Account ID",
              tint = TextSecondary,
              modifier = Modifier.size(12.dp)
            )
          }
        }

        // Live Bot Running Indicator
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isBotRunning) Color(0xFFE6F8F0) else Color(0xFFF1F3F6))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.FiberManualRecord,
            contentDescription = null,
            tint = if (isBotRunning) TradeGreen else Color(0xFF9EA7B4),
            modifier = Modifier
              .size(10.dp)
              .then(if (isBotRunning) Modifier.scale(pulseScale) else Modifier)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isBotRunning) "BOT RUNNING" else "BOT STANDBY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isBotRunning) TradeGreen else TextSecondary
          )
        }
      }
    }
  }
}
