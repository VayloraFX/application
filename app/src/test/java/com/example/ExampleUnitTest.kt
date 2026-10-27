package com.example

import com.example.data.model.UserAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testAccountIdFormat() {
    val digits = (100000000L..999999999L).random()
    val accountId = "GSQ-$digits"
    assertTrue(accountId.startsWith("GSQ-"))
    assertEquals(13, accountId.length)
  }

  @Test
  fun testUserNetPnLCalculation() {
    val user = UserAccount(
      accountId = "GSQ-583737735",
      name = "Test Trader",
      email = "trader@test.com",
      walletBalance = 300.0,
      sessionProfit = 45.0,
      sessionLoss = 15.0,
      totalTrades = 4,
      totalWins = 3,
      totalLosses = 1
    )
    assertEquals(30.0, user.sessionNetPnL, 0.001)
    assertEquals(75, user.winRatePercentage)
  }
}
