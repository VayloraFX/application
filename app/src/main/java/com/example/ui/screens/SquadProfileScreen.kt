package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SquadMember
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
fun SquadProfileScreen(
  user: UserAccount?,
  allUsers: List<UserAccount>,
  squadMembers: List<SquadMember>,
  onSwitchUser: (String) -> Unit,
  onOpenRegisterNew: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showUserSwitcher by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item { Spacer(modifier = Modifier.height(2.dp)) }

    // User Profile Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("profile_card"),
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
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(GsquadYellowContainer)
                .border(1.5.dp, GsquadYellow, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.ic_gsquad_logo),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = user?.name ?: "G-Squad Trader",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
              Text(
                text = user?.email ?: "trader@gsquad.io",
                fontSize = 12.sp,
                color = TextSecondary
              )

              Spacer(modifier = Modifier.height(4.dp))

              // Account ID copy chip
              user?.accountId?.let { id ->
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF3F5F9))
                    .clickable {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("GSQ Account ID", id)
                      clipboard.setPrimaryClip(clip)
                      Toast.makeText(context, "Copied Account ID: $id", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = id,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF1F242E)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(11.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Deriv Connection Card
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
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = TradeGreen,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Deriv Connection Status",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TradeGreenBg)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = "ONLINE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TradeGreen
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Connected to Deriv WebSocket Tick Stream. Master pooling orders are executed with zero latency on the Deriv synthetic market.",
            fontSize = 11.sp,
            color = TextSecondary,
            lineHeight = 16.sp
          )
        }
      }
    }

    // Multi-User Switcher & Account Management
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
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.SwitchAccount,
                contentDescription = null,
                tint = GsquadYellow,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Multi-User Accounts (${allUsers.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
              )
            }

            // Create new user button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(GsquadYellow)
                .clickable { onOpenRegisterNew() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("add_account_btn")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = null,
                  tint = GsquadOnYellow,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "New User",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GsquadOnYellow
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // List of registered accounts
          allUsers.forEach { acct ->
            val isCurrent = acct.accountId == user?.accountId
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isCurrent) Color(0xFFFFF7DB) else Color(0xFFF7F8FA))
                .border(
                  1.dp,
                  if (isCurrent) GsquadYellow else LightBorder,
                  RoundedCornerShape(10.dp)
                )
                .clickable { onSwitchUser(acct.accountId) }
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = acct.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = TextPrimary
                )
                Text(
                  text = "${acct.accountId} • Bal: $${String.format(Locale.US, "%.2f", acct.walletBalance)}",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = TextSecondary
                )
              }

              if (isCurrent) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GsquadYellow)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GsquadOnYellow
                  )
                }
              } else {
                Text(
                  text = "Switch",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0066CC)
                )
              }
            }
          }
        }
      }
    }

    // Squad Pool Traders Roster
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = null,
            tint = GsquadYellow,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Active Squad Pool Syndicate",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextPrimary
          )
        }

        Text(
          text = "${squadMembers.count { it.isBotRunning }} Active Bots",
          fontSize = 11.sp,
          color = TradeGreen,
          fontWeight = FontWeight.Bold
        )
      }
    }

    items(squadMembers) { member ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, LightBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF2F6)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = member.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
              )
              Text(
                text = "${member.accountId} • Stake $${String.format(Locale.US, "%.0f", member.stakeAmount)}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (member.isBotRunning) TradeGreenBg else Color(0xFFF1F3F6))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (member.isBotRunning) "RUNNING" else "IDLE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (member.isBotRunning) TradeGreen else TextMuted
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "+$${String.format(Locale.US, "%.0f", member.totalProfit)} (${member.winRate}%)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TradeGreen
            )
          }
        }
      }
    }

    // Logout Action
    item {
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedButton(
        onClick = onLogout,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("logout_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = TradeRed
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, TradeRed.copy(alpha = 0.5f))
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Log Out of Account",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}
