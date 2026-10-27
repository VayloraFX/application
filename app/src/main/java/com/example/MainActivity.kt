package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.manager.TradingBotManager
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MobileContainer
import com.example.ui.components.NavTab
import com.example.ui.components.TopHeader
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BotDashboardScreen
import com.example.ui.screens.RiskManagementScreen
import com.example.ui.screens.SquadProfileScreen
import com.example.ui.screens.TradeHistoryScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.GsquadOnYellow
import com.example.ui.theme.GsquadYellow
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

  private lateinit var botManager: TradingBotManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    botManager = TradingBotManager(applicationContext)

    setContent {
      MyApplicationTheme {
        MobileContainer {
          var showSplash by remember { mutableStateOf(true) }

          if (showSplash) {
            com.example.ui.screens.SplashScreen(
              onSplashFinished = { showSplash = false }
            )
          } else {
            GsquadApp(botManager = botManager)
          }
        }
      }
    }
  }
}

@Composable
fun GsquadApp(botManager: TradingBotManager) {
  val currentUser by botManager.currentUser.collectAsStateWithLifecycle()
  val allUsers by botManager.registeredUsers.collectAsStateWithLifecycle()
  val squadMembers by botManager.squadMembers.collectAsStateWithLifecycle()
  val activeTrade by botManager.activeTrade.collectAsStateWithLifecycle()
  val recentTrades by botManager.recentTrades.collectAsStateWithLifecycle()
  val walletTransactions by botManager.walletTransactions.collectAsStateWithLifecycle()
  val latestSignals by botManager.latestSignals.collectAsStateWithLifecycle()
  val marketPrices by botManager.marketPrices.collectAsStateWithLifecycle()
  val priceHistory by botManager.priceHistory.collectAsStateWithLifecycle()
  val alertMessage by botManager.alertEvent.collectAsStateWithLifecycle()
  val botLogs by botManager.botLogs.collectAsStateWithLifecycle()

  var currentTab by remember { mutableStateOf(NavTab.BOT) }
  var showNewUserDialog by remember { mutableStateOf(false) }

  // Back handling
  BackHandler(enabled = currentTab != NavTab.BOT || showNewUserDialog) {
    if (showNewUserDialog) {
      showNewUserDialog = false
    } else {
      currentTab = NavTab.BOT
    }
  }

  if (currentUser == null) {
    AuthScreen(
      onLogin = { email, pass -> botManager.login(email, pass) },
      onRegister = { name, email, pass -> botManager.register(name, email, pass) },
      onQuickDemoLogin = {
        val first = allUsers.firstOrNull()
        if (first != null) {
          botManager.login(first.accountId, "")
        } else {
          botManager.register("G-Squad Trader", "demo@gsquad.io", "")
        }
      }
    )
  } else {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .background(LightBackground),
      containerColor = LightBackground,
      topBar = {
        TopHeader(
          user = currentUser,
          isBotRunning = currentUser?.isBotRunning == true,
          onWalletClick = { currentTab = NavTab.WALLET }
        )
      },
      bottomBar = {
        BottomNavBar(
          currentTab = currentTab,
          onTabSelected = { currentTab = it }
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentTab) {
          NavTab.BOT -> {
            BotDashboardScreen(
              user = currentUser,
              squadMembers = squadMembers,
              activeTrade = activeTrade,
              alertMessage = alertMessage,
              botLogs = botLogs,
              onToggleBot = { botManager.toggleBot() },
              onConfigureRisk = { currentTab = NavTab.RISK },
              onDismissAlert = { botManager.dismissAlert() },
              onGoToWallet = { currentTab = NavTab.WALLET }
            )
          }

          NavTab.RISK -> {
            RiskManagementScreen(
              user = currentUser,
              onSaveRisk = { stake, tp, sl ->
                botManager.updateRiskManagement(stake, tp, sl)
              }
            )
          }

          NavTab.TRADES -> {
            TradeHistoryScreen(
              trades = recentTrades
            )
          }

          NavTab.WALLET -> {
            WalletScreen(
              user = currentUser,
              transactions = walletTransactions,
              onDeposit = { amount, method -> botManager.depositFunds(amount, method) },
              onWithdraw = { amount, dest -> botManager.withdrawFunds(amount, dest) }
            )
          }

          NavTab.SQUAD -> {
            SquadProfileScreen(
              user = currentUser,
              allUsers = allUsers,
              squadMembers = squadMembers,
              onSwitchUser = { accountId -> botManager.switchUser(accountId) },
              onOpenRegisterNew = { showNewUserDialog = true },
              onLogout = { botManager.logout() }
            )
          }
        }
      }
    }
  }

  // Create New User Account Dialog (for testing multiple users and pooling)
  if (showNewUserDialog) {
    CreateNewUserDialog(
      onDismiss = { showNewUserDialog = false },
      onCreate = { name, email ->
        botManager.register(name, email, "")
        showNewUserDialog = false
      }
    )
  }
}

@Composable
fun CreateNewUserDialog(
  onDismiss: () -> Unit,
  onCreate: (name: String, email: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Register New Squad Trader",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextPrimary
      )
    },
    text = {
      Column {
        Text(
          text = "A new unique GSQ Account ID (e.g. GSQ-583737735) and internal wallet will be generated automatically for this user.",
          fontSize = 12.sp,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Trader Name") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("new_user_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GsquadYellow,
            unfocusedBorderColor = LightBorder
          ),
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("new_user_email_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GsquadYellow,
            unfocusedBorderColor = LightBorder
          ),
          shape = RoundedCornerShape(10.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (email.isNotBlank()) {
            onCreate(name.ifBlank { "Squad Member" }, email)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = GsquadYellow,
          contentColor = GsquadOnYellow
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("confirm_create_user_btn")
      ) {
        Text("Create & Switch", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
