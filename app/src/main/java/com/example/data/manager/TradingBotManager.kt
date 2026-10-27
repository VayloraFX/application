package com.example.data.manager

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.MarketSignal
import com.example.data.model.SquadMember
import com.example.data.model.TradeDirection
import com.example.data.model.TradeOrder
import com.example.data.model.TradeStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserAccount
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

class TradingBotManager(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("gsquad_trading_prefs", Context.MODE_PRIVATE)

  private val scope = CoroutineScope(Dispatchers.Default)
  private var botJob: Job? = null
  private var marketTicksJob: Job? = null

  // State flows
  private val _currentUser = MutableStateFlow<UserAccount?>(null)
  val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

  private val _registeredUsers = MutableStateFlow<List<UserAccount>>(emptyList())
  val registeredUsers: StateFlow<List<UserAccount>> = _registeredUsers.asStateFlow()

  private val _squadMembers = MutableStateFlow<List<SquadMember>>(emptyList())
  val squadMembers: StateFlow<List<SquadMember>> = _squadMembers.asStateFlow()

  private val _activeTrade = MutableStateFlow<TradeOrder?>(null)
  val activeTrade: StateFlow<TradeOrder?> = _activeTrade.asStateFlow()

  private val _recentTrades = MutableStateFlow<List<TradeOrder>>(emptyList())
  val recentTrades: StateFlow<List<TradeOrder>> = _recentTrades.asStateFlow()

  private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
  val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

  private val _latestSignals = MutableStateFlow<List<MarketSignal>>(emptyList())
  val latestSignals: StateFlow<List<MarketSignal>> = _latestSignals.asStateFlow()

  // Live price per symbol for charts/market feed
  private val _marketPrices = MutableStateFlow<Map<String, Double>>(
    mapOf(
      "Volatility 75 (1s)" to 243120.50,
      "Crash 500 Index" to 4215.30,
      "Boom 1000 Index" to 8940.80,
      "Step Index" to 6120.40,
      "Volatility 100" to 1482.20
    )
  )
  val marketPrices: StateFlow<Map<String, Double>> = _marketPrices.asStateFlow()

  // Price history for chart rendering
  private val _priceHistory = MutableStateFlow<Map<String, List<Double>>>(emptyMap())
  val priceHistory: StateFlow<Map<String, List<Double>>> = _priceHistory.asStateFlow()

  // System notification event (e.g. Target Profit Reached, Stop Loss Reached)
  private val _alertEvent = MutableStateFlow<String?>(null)
  val alertEvent: StateFlow<String?> = _alertEvent.asStateFlow()

  // Live bot activity log feed
  private val _botLogs = MutableStateFlow<List<String>>(emptyList())
  val botLogs: StateFlow<List<String>> = _botLogs.asStateFlow()

  init {
    initDefaultSquadMembers()
    loadSavedState()
    startMarketPriceStream()
  }

  private fun initDefaultSquadMembers() {
    _squadMembers.value = listOf(
      SquadMember("GSQ-882194812", "Alex_Pro", isBotRunning = true, stakeAmount = 10.0, totalProfit = 142.50, winRate = 78),
      SquadMember("GSQ-392019482", "Sarah_Fx", isBotRunning = true, stakeAmount = 15.0, totalProfit = 210.00, winRate = 82),
      SquadMember("GSQ-772910384", "David_Quant", isBotRunning = true, stakeAmount = 25.0, totalProfit = 385.00, winRate = 75),
      SquadMember("GSQ-510293849", "Ken_Algo", isBotRunning = true, stakeAmount = 10.0, totalProfit = 115.00, winRate = 80),
      SquadMember("GSQ-991823746", "Elena_Scalp", isBotRunning = true, stakeAmount = 5.0, totalProfit = 84.00, winRate = 76)
    )
  }

  // Generate unique GSQ-583737735 format account ID
  fun generateAccountId(): String {
    val digits = (100000000L..999999999L).random()
    return "GSQ-$digits"
  }

  fun register(name: String, email: String, password: String): UserAccount {
    val newId = generateAccountId()
    val initialBalance = 250.0 // Starting internal wallet credit for user demo
    val newUser = UserAccount(
      accountId = newId,
      name = name.ifBlank { "Squad Trader" },
      email = email.trim(),
      passwordHash = password,
      walletBalance = initialBalance,
      currency = "USD",
      stakeAmount = 5.0,
      targetProfit = 50.0,
      targetLoss = 25.0,
      isBotRunning = false
    )

    val currentList = _registeredUsers.value.toMutableList()
    currentList.add(newUser)
    _registeredUsers.value = currentList
    _currentUser.value = newUser

    // Initial welcome credit transaction
    val welcomeTx = WalletTransaction(
      id = "TX-${System.currentTimeMillis() % 100000}",
      type = TransactionType.DEPOSIT,
      amount = initialBalance,
      balanceAfter = initialBalance,
      description = "Welcome Bonus Deposit to $newId"
    )
    _walletTransactions.value = listOf(welcomeTx)

    addLog("Created new account: $newId for ${newUser.name}. Internal wallet funded with $250.00 USD.")
    saveState()
    return newUser
  }

  fun login(emailOrId: String, password: String): Boolean {
    val clean = emailOrId.trim()
    val user = _registeredUsers.value.find {
      it.email.equals(clean, ignoreCase = true) || it.accountId.equals(clean, ignoreCase = true)
    }
    return if (user != null) {
      _currentUser.value = user
      addLog("User logged in: ${user.accountId} (${user.name})")
      saveState()
      true
    } else {
      false
    }
  }

  fun logout() {
    stopBot("User Logged Out")
    _currentUser.value = null
    saveState()
  }

  fun switchUser(accountId: String) {
    val target = _registeredUsers.value.find { it.accountId == accountId }
    if (target != null) {
      stopBot("User Switched")
      _currentUser.value = target
      addLog("Switched active profile to ${target.accountId}")
      saveState()
    }
  }

  fun updateRiskManagement(stake: Double, targetProfit: Double, targetLoss: Double) {
    val user = _currentUser.value ?: return
    val sanitizedStake = stake.coerceAtLeast(1.0)
    val sanitizedTp = targetProfit.coerceAtLeast(2.0)
    val sanitizedSl = targetLoss.coerceAtLeast(2.0)

    val updated = user.copy(
      stakeAmount = sanitizedStake,
      targetProfit = sanitizedTp,
      targetLoss = sanitizedSl
    )
    _currentUser.value = updated
    updateUserInList(updated)
    addLog("Risk parameters updated: Stake $${String.format(Locale.US, "%.2f", sanitizedStake)}, Target Profit $${String.format(Locale.US, "%.2f", sanitizedTp)}, Stop Loss $${String.format(Locale.US, "%.2f", sanitizedSl)}")
    saveState()
  }

  fun toggleBot() {
    val user = _currentUser.value ?: return
    if (user.isBotRunning) {
      stopBot("Manually stopped by user")
    } else {
      startBot()
    }
  }

  fun startBot(): Boolean {
    val user = _currentUser.value ?: return false

    if (user.walletBalance < user.stakeAmount) {
      _alertEvent.value = "INSUFFICIENT_BALANCE: Wallet balance ($${String.format(Locale.US, "%.2f", user.walletBalance)}) is lower than your stake ($${String.format(Locale.US, "%.2f", user.stakeAmount)}). Please deposit to run the bot."
      return false
    }

    // Reset session PnL on fresh manual start
    val updated = user.copy(
      isBotRunning = true,
      sessionProfit = 0.0,
      sessionLoss = 0.0
    )
    _currentUser.value = updated
    updateUserInList(updated)

    addLog("Bot STARTED for ${user.accountId}. Stake: $${user.stakeAmount}. Listening for Deriv market signals...")

    launchBotTradingLoop()
    saveState()
    return true
  }

  fun stopBot(reason: String) {
    val user = _currentUser.value ?: return
    botJob?.cancel()
    botJob = null
    _activeTrade.value = null

    val updated = user.copy(isBotRunning = false)
    _currentUser.value = updated
    updateUserInList(updated)

    addLog("Bot STOPPED for ${user.accountId}. Reason: $reason. Final Session PnL: $${String.format(Locale.US, "%+.2f", updated.sessionNetPnL)}")
    saveState()
  }

  fun dismissAlert() {
    _alertEvent.value = null
  }

  fun depositFunds(amount: Double, paymentMethod: String) {
    val user = _currentUser.value ?: return
    val newBalance = user.walletBalance + amount
    val updated = user.copy(walletBalance = newBalance)
    _currentUser.value = updated
    updateUserInList(updated)

    val tx = WalletTransaction(
      id = "TX-${System.currentTimeMillis() % 100000}",
      type = TransactionType.DEPOSIT,
      amount = amount,
      balanceAfter = newBalance,
      description = "Deposit via $paymentMethod"
    )
    _walletTransactions.value = listOf(tx) + _walletTransactions.value
    addLog("Deposited $${String.format(Locale.US, "%.2f", amount)} via $paymentMethod. New Wallet Balance: $${String.format(Locale.US, "%.2f", newBalance)}")
    saveState()
  }

  fun withdrawFunds(amount: Double, destination: String): Boolean {
    val user = _currentUser.value ?: return false
    if (user.walletBalance < amount) {
      _alertEvent.value = "WITHDRAW_ERROR: Insufficient wallet balance."
      return false
    }

    val newBalance = user.walletBalance - amount
    val updated = user.copy(walletBalance = newBalance)
    _currentUser.value = updated
    updateUserInList(updated)

    val tx = WalletTransaction(
      id = "TX-${System.currentTimeMillis() % 100000}",
      type = TransactionType.WITHDRAWAL,
      amount = amount,
      balanceAfter = newBalance,
      description = "Withdrawal to $destination"
    )
    _walletTransactions.value = listOf(tx) + _walletTransactions.value
    addLog("Withdrew $${String.format(Locale.US, "%.2f", amount)} to $destination. New Wallet Balance: $${String.format(Locale.US, "%.2f", newBalance)}")
    saveState()
    return true
  }

  // Pooled automated trading engine
  private fun launchBotTradingLoop() {
    botJob?.cancel()
    botJob = scope.launch {
      while (isActive) {
        val user = _currentUser.value
        if (user == null || !user.isBotRunning) break

        // Scan delay between trade executions (8 to 14 seconds)
        delay(Random.nextLong(8000, 14000))

        if (!isActive) break
        val currentUserNow = _currentUser.value ?: break
        if (!currentUserNow.isBotRunning) break

        // 1. Bot identifies signal on a Deriv synthetic symbol
        val symbols = listOf("Volatility 75 (1s)", "Crash 500 Index", "Boom 1000 Index", "Step Index", "Volatility 100")
        val chosenSymbol = symbols.random()
        val currentSpot = _marketPrices.value[chosenSymbol] ?: 243120.0
        val direction = if (Random.nextBoolean()) TradeDirection.CALL else TradeDirection.PUT
        val confidence = Random.nextInt(86, 98)
        val strategies = listOf(
          "Deriv RSI Dynamic Reversal",
          "Volatility Momentum Breakout",
          "EMA 9/21 Trend Surge",
          "Deriv Algorithmic Scalper"
        )
        val strategy = strategies.random()

        val signal = MarketSignal(
          id = "SIG-${System.currentTimeMillis() % 10000}",
          symbol = chosenSymbol,
          direction = direction,
          confidence = confidence,
          strategy = strategy,
          price = currentSpot
        )
        _latestSignals.value = (listOf(signal) + _latestSignals.value).take(15)

        addLog("Signal found: $chosenSymbol ${direction.badge} (${confidence}% confidence via $strategy)")

        // 2. Check all users whose bot is running (current user + active squad members)
        val activeSquad = _squadMembers.value.filter { it.isBotRunning }
        val squadCount = activeSquad.size + 1 // +1 for current user
        val totalSquadStake = activeSquad.sumOf { it.stakeAmount }
        val totalPooledStake = totalSquadStake + currentUserNow.stakeAmount

        val masterOrderRef = "DRV-MASTER-${Random.nextInt(100000, 999999)}"
        addLog("Aggregated $squadCount active squad traders. Master Deriv Pooled Stake: $${String.format(Locale.US, "%.2f", totalPooledStake)} ($${currentUserNow.stakeAmount} from you). Placing order...")

        // 3. Initiate active trade countdown
        val duration = 5
        var trade = TradeOrder(
          tradeId = "TRD-${System.currentTimeMillis() % 1000000}",
          symbol = chosenSymbol,
          direction = direction,
          userStake = currentUserNow.stakeAmount,
          pooledStake = totalPooledStake,
          squadTradersCount = squadCount,
          entrySpot = currentSpot,
          exitSpot = currentSpot,
          durationSeconds = duration,
          remainingSeconds = duration,
          status = TradeStatus.EXECUTING,
          payoutPercentage = 95.0,
          adminMasterOrderRef = masterOrderRef
        )
        _activeTrade.value = trade

        // Countdown ticks simulation
        for (sec in (duration - 1) downTo 0) {
          delay(1000)
          if (!isActive) break
          // Small tick movement
          val tickShift = (Random.nextDouble() - 0.48) * (currentSpot * 0.0003)
          val livePrice = currentSpot + tickShift
          _activeTrade.value = _activeTrade.value?.copy(
            remainingSeconds = sec,
            exitSpot = livePrice
          )
        }

        if (!isActive) break

        // 4. Trade resolution
        // Algorithmic tuned win rate (around 72%)
        val isWin = Random.nextInt(100) < 72
        val exitSpot = if (isWin) {
          if (direction == TradeDirection.CALL) currentSpot + (currentSpot * 0.0004)
          else currentSpot - (currentSpot * 0.0004)
        } else {
          if (direction == TradeDirection.CALL) currentSpot - (currentSpot * 0.0004)
          else currentSpot + (currentSpot * 0.0004)
        }

        val finalizedUser = _currentUser.value ?: break
        if (isWin) {
          val profitEarned = finalizedUser.stakeAmount * 0.95
          val updatedBalance = finalizedUser.walletBalance + profitEarned
          val newSessionProfit = finalizedUser.sessionProfit + profitEarned

          val wonTrade = trade.copy(
            status = TradeStatus.WON,
            exitSpot = exitSpot,
            userProfit = profitEarned,
            remainingSeconds = 0
          )
          _activeTrade.value = wonTrade
          _recentTrades.value = (listOf(wonTrade) + _recentTrades.value).take(30)

          val updatedUser = finalizedUser.copy(
            walletBalance = updatedBalance,
            sessionProfit = newSessionProfit,
            totalTrades = finalizedUser.totalTrades + 1,
            totalWins = finalizedUser.totalWins + 1
          )
          _currentUser.value = updatedUser
          updateUserInList(updatedUser)

          val tx = WalletTransaction(
            id = "TX-${System.currentTimeMillis() % 100000}",
            type = TransactionType.TRADE_WIN,
            amount = profitEarned,
            balanceAfter = updatedBalance,
            description = "Profit Credit: ${wonTrade.symbol} ${wonTrade.direction.badge} (Stake $${wonTrade.userStake})"
          )
          _walletTransactions.value = listOf(tx) + _walletTransactions.value

          addLog("TRADE WON! Master Pool returned profit. Credited +$${String.format(Locale.US, "%.2f", profitEarned)} to your internal wallet. Balance: $${String.format(Locale.US, "%.2f", updatedBalance)}")

          // Check Target Profit
          if (updatedUser.sessionNetPnL >= updatedUser.targetProfit) {
            val msg = "TARGET_PROFIT_REACHED: Congratulations! Target Profit of $${String.format(Locale.US, "%.2f", updatedUser.targetProfit)} achieved (Net: $${String.format(Locale.US, "%.2f", updatedUser.sessionNetPnL)}). Bot stopped safely."
            _alertEvent.value = msg
            stopBot("Target Profit reached ($${updatedUser.targetProfit})")
            break
          }
        } else {
          val lostAmount = finalizedUser.stakeAmount
          val updatedBalance = (finalizedUser.walletBalance - lostAmount).coerceAtLeast(0.0)
          val newSessionLoss = finalizedUser.sessionLoss + lostAmount

          val lostTrade = trade.copy(
            status = TradeStatus.LOST,
            exitSpot = exitSpot,
            userProfit = -lostAmount,
            remainingSeconds = 0
          )
          _activeTrade.value = lostTrade
          _recentTrades.value = (listOf(lostTrade) + _recentTrades.value).take(30)

          val updatedUser = finalizedUser.copy(
            walletBalance = updatedBalance,
            sessionLoss = newSessionLoss,
            totalTrades = finalizedUser.totalTrades + 1,
            totalLosses = finalizedUser.totalLosses + 1
          )
          _currentUser.value = updatedUser
          updateUserInList(updatedUser)

          val tx = WalletTransaction(
            id = "TX-${System.currentTimeMillis() % 100000}",
            type = TransactionType.TRADE_LOSS,
            amount = lostAmount,
            balanceAfter = updatedBalance,
            description = "Trade Loss: ${lostTrade.symbol} ${lostTrade.direction.badge} (Stake $${lostTrade.userStake})"
          )
          _walletTransactions.value = listOf(tx) + _walletTransactions.value

          addLog("TRADE LOSS. Deducted -$${String.format(Locale.US, "%.2f", lostAmount)} stake from wallet. Balance: $${String.format(Locale.US, "%.2f", updatedBalance)}")

          // Check Stop Loss (Target Loss)
          if (updatedUser.sessionLoss >= updatedUser.targetLoss) {
            val msg = "STOP_LOSS_REACHED: Stop Loss of $${String.format(Locale.US, "%.2f", updatedUser.targetLoss)} reached (Loss: $${String.format(Locale.US, "%.2f", updatedUser.sessionLoss)}). Bot stopped automatically to protect capital."
            _alertEvent.value = msg
            stopBot("Target Loss reached ($${updatedUser.targetLoss})")
            break
          }

          // Check Insufficient balance for next trade
          if (updatedUser.walletBalance < updatedUser.stakeAmount) {
            val msg = "INSUFFICIENT_BALANCE: Wallet balance ($${String.format(Locale.US, "%.2f", updatedUser.walletBalance)}) is lower than your stake ($${String.format(Locale.US, "%.2f", updatedUser.stakeAmount)}). Bot stopped."
            _alertEvent.value = msg
            stopBot("Low wallet balance")
            break
          }
        }

        saveState()
        // Wait 3 seconds showing outcome before clearing active trade
        delay(3000)
        _activeTrade.value = null
      }
    }
  }

  // Real-time market tick stream for synthetic indices
  private fun startMarketPriceStream() {
    marketTicksJob?.cancel()
    marketTicksJob = scope.launch {
      // Initialize price history map
      val history = mutableMapOf<String, MutableList<Double>>()
      _marketPrices.value.forEach { (sym, price) ->
        val list = mutableListOf<Double>()
        var curr = price
        for (i in 0 until 20) {
          curr += (Random.nextDouble() - 0.49) * (price * 0.0004)
          list.add(curr)
        }
        history[sym] = list
      }
      _priceHistory.value = history

      while (isActive) {
        delay(1200)
        val updatedMap = _marketPrices.value.toMutableMap()
        val currentHistory = _priceHistory.value.toMutableMap()

        updatedMap.forEach { (sym, price) ->
          val pct = when (sym) {
            "Volatility 75 (1s)" -> 0.0005
            "Crash 500 Index" -> 0.0003
            "Boom 1000 Index" -> 0.0003
            else -> 0.0002
          }
          val delta = (Random.nextDouble() - 0.495) * (price * pct)
          val newPrice = price + delta
          updatedMap[sym] = newPrice

          val oldList = currentHistory[sym]?.toMutableList() ?: mutableListOf()
          oldList.add(newPrice)
          if (oldList.size > 25) oldList.removeAt(0)
          currentHistory[sym] = oldList
        }
        _marketPrices.value = updatedMap
        _priceHistory.value = currentHistory
      }
    }
  }

  private fun updateUserInList(user: UserAccount) {
    val list = _registeredUsers.value.toMutableList()
    val idx = list.indexOfFirst { it.accountId == user.accountId }
    if (idx >= 0) {
      list[idx] = user
    } else {
      list.add(user)
    }
    _registeredUsers.value = list
  }

  private fun addLog(msg: String) {
    val time = String.format(Locale.US, "%tT", System.currentTimeMillis())
    _botLogs.value = (listOf("[$time] $msg") + _botLogs.value).take(40)
  }

  private fun saveState() {
    try {
      val usersArray = JSONArray()
      _registeredUsers.value.forEach { u ->
        val obj = JSONObject().apply {
          put("accountId", u.accountId)
          put("name", u.name)
          put("email", u.email)
          put("walletBalance", u.walletBalance)
          put("stakeAmount", u.stakeAmount)
          put("targetProfit", u.targetProfit)
          put("targetLoss", u.targetLoss)
          put("totalTrades", u.totalTrades)
          put("totalWins", u.totalWins)
          put("totalLosses", u.totalLosses)
        }
        usersArray.put(obj)
      }

      prefs.edit()
        .putString("users_json", usersArray.toString())
        .putString("current_user_id", _currentUser.value?.accountId)
        .apply()
    } catch (_: Exception) {}
  }

  private fun loadSavedState() {
    try {
      val usersStr = prefs.getString("users_json", null)
      val currentId = prefs.getString("current_user_id", null)

      if (!usersStr.isNullOrBlank()) {
        val jsonArray = JSONArray(usersStr)
        val loadedUsers = mutableListOf<UserAccount>()
        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          loadedUsers.add(
            UserAccount(
              accountId = obj.optString("accountId", "GSQ-583737735"),
              name = obj.optString("name", "G-Squad Trader"),
              email = obj.optString("email", "trader@gsquad.io"),
              walletBalance = obj.optDouble("walletBalance", 250.0),
              stakeAmount = obj.optDouble("stakeAmount", 5.0),
              targetProfit = obj.optDouble("targetProfit", 50.0),
              targetLoss = obj.optDouble("targetLoss", 25.0),
              totalTrades = obj.optInt("totalTrades", 0),
              totalWins = obj.optInt("totalWins", 0),
              totalLosses = obj.optInt("totalLosses", 0)
            )
          )
        }
        _registeredUsers.value = loadedUsers
        _currentUser.value = loadedUsers.find { it.accountId == currentId } ?: loadedUsers.firstOrNull()
      }

      // If no users exist yet, create default demo account
      if (_registeredUsers.value.isEmpty()) {
        val defaultUser = UserAccount(
          accountId = "GSQ-583737735",
          name = "G-Squad Member",
          email = "demo@gsquad.io",
          walletBalance = 350.0,
          stakeAmount = 5.0,
          targetProfit = 50.0,
          targetLoss = 25.0,
          isBotRunning = false
        )
        _registeredUsers.value = listOf(defaultUser)
        _currentUser.value = defaultUser

        _walletTransactions.value = listOf(
          WalletTransaction(
            id = "TX-89214",
            type = TransactionType.DEPOSIT,
            amount = 350.0,
            balanceAfter = 350.0,
            description = "Initial Internal Wallet Balance for GSQ-583737735"
          )
        )
      }
    } catch (_: Exception) {
      if (_currentUser.value == null) {
        val defaultUser = UserAccount(
          accountId = "GSQ-583737735",
          name = "G-Squad Member",
          email = "demo@gsquad.io",
          walletBalance = 350.0,
          stakeAmount = 5.0,
          targetProfit = 50.0,
          targetLoss = 25.0,
          isBotRunning = false
        )
        _registeredUsers.value = listOf(defaultUser)
        _currentUser.value = defaultUser
      }
    }
  }
}
