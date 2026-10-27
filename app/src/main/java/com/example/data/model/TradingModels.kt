package com.example.data.model

data class UserAccount(
  val accountId: String, // e.g. "GSQ-583737735"
  val name: String,
  val email: String,
  val passwordHash: String = "",
  val walletBalance: Double = 250.0,
  val currency: String = "USD",
  val stakeAmount: Double = 5.0,
  val targetProfit: Double = 50.0,
  val targetLoss: Double = 25.0,
  val isBotRunning: Boolean = false,
  val sessionProfit: Double = 0.0,
  val sessionLoss: Double = 0.0,
  val totalTrades: Int = 0,
  val totalWins: Int = 0,
  val totalLosses: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
) {
  val sessionNetPnL: Double
    get() = sessionProfit - sessionLoss

  val winRatePercentage: Int
    get() = if (totalTrades > 0) ((totalWins.toDouble() / totalTrades) * 100).toInt() else 0
}

enum class TradeDirection(val label: String, val badge: String) {
  CALL("RISE / CALL", "CALL"),
  PUT("FALL / PUT", "PUT")
}

enum class TradeStatus {
  EXECUTING,
  WON,
  LOST
}

data class TradeOrder(
  val tradeId: String,
  val symbol: String,
  val direction: TradeDirection,
  val userStake: Double,
  val pooledStake: Double,
  val squadTradersCount: Int,
  val entrySpot: Double,
  val exitSpot: Double = 0.0,
  val durationSeconds: Int = 6,
  val remainingSeconds: Int = 6,
  val status: TradeStatus = TradeStatus.EXECUTING,
  val userProfit: Double = 0.0,
  val payoutPercentage: Double = 95.0,
  val adminMasterOrderRef: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class SquadMember(
  val accountId: String,
  val name: String,
  val isBotRunning: Boolean,
  val stakeAmount: Double,
  val totalProfit: Double,
  val winRate: Int
)

enum class TransactionType(val label: String, val isCredit: Boolean) {
  DEPOSIT("Deposit Funds", true),
  WITHDRAWAL("Withdrawal", false),
  TRADE_WIN("Bot Trade Profit", true),
  TRADE_LOSS("Bot Trade Loss", false)
}

data class WalletTransaction(
  val id: String,
  val type: TransactionType,
  val amount: Double,
  val balanceAfter: Double,
  val description: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class MarketSignal(
  val id: String,
  val symbol: String,
  val direction: TradeDirection,
  val confidence: Int,
  val strategy: String,
  val price: Double,
  val timestamp: Long = System.currentTimeMillis()
)
