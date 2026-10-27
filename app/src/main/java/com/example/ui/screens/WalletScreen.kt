package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.data.model.UserAccount
import com.example.data.model.WalletTransaction
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
fun WalletScreen(
  user: UserAccount?,
  transactions: List<WalletTransaction>,
  onDeposit: (amount: Double, method: String) -> Unit,
  onWithdraw: (amount: Double, destination: String) -> Boolean,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showDepositDialog by remember { mutableStateOf(false) }
  var showWithdrawDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item { Spacer(modifier = Modifier.height(2.dp)) }

    // Big Internal Wallet Balance Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("wallet_balance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFF7DB)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AccountBalanceWallet,
                  contentDescription = null,
                  tint = GsquadYellow,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Internal Trading Wallet",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = "Deriv Automated Pooling Balance",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE8F8F0))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "ACTIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TradeGreen
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "TOTAL BALANCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
          )
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "$${String.format(Locale.US, "%.2f", user?.walletBalance ?: 0.0)}",
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "USD",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondary,
              modifier = Modifier.padding(bottom = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Account ID Chip inside wallet
          user?.accountId?.let { id ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .border(0.8.dp, LightBorder, RoundedCornerShape(8.dp))
                .clickable {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("GSQ Account ID", id)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Copied Account ID: $id", Toast.LENGTH_SHORT).show()
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Account ID: ",
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = id,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Deposit & Withdraw Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Deposit (Primary Yellow)
            Button(
              onClick = { showDepositDialog = true },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("deposit_btn"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = GsquadYellow,
                contentColor = GsquadOnYellow
              )
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.FileDownload,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Deposit",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp
                )
              }
            }

            // Withdraw (Secondary Outlined/Light)
            OutlinedButton(
              onClick = { showWithdrawDialog = true },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("withdraw_btn"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextPrimary
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, LightBorder)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.FileUpload,
                  contentDescription = null,
                  tint = TextSecondary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Withdraw",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
            }
          }
        }
      }
    }

    // Transaction History Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            tint = GsquadYellow,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Wallet Ledger",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
          )
        }

        Text(
          text = "${transactions.size} Records",
          fontSize = 11.sp,
          color = TextSecondary
        )
      }
    }

    if (transactions.isEmpty()) {
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
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No wallet transactions recorded yet.",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
        }
      }
    } else {
      items(transactions) { tx ->
        val isCredit = tx.type.isCredit
        val amtColor = if (isCredit) TradeGreen else TradeRed
        val amtBg = if (isCredit) TradeGreenBg else TradeRedBg
        val sign = if (isCredit) "+" else "-"

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LightSurface),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_item_${tx.id}")
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
                  .background(amtBg),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (tx.type) {
                    TransactionType.DEPOSIT -> Icons.Default.FileDownload
                    TransactionType.WITHDRAWAL -> Icons.Default.FileUpload
                    TransactionType.TRADE_WIN -> Icons.Default.ArrowUpward
                    TransactionType.TRADE_LOSS -> Icons.Default.ArrowDownward
                  },
                  contentDescription = null,
                  tint = amtColor,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = tx.type.label,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = TextPrimary
                )
                Text(
                  text = tx.description,
                  fontSize = 11.sp,
                  color = TextSecondary,
                  maxLines = 1
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "$sign$${String.format(Locale.US, "%.2f", tx.amount)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = amtColor
              )
              Text(
                text = "Bal: $${String.format(Locale.US, "%.2f", tx.balanceAfter)}",
                fontSize = 10.sp,
                color = TextMuted
              )
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }

  // Deposit Dialog
  if (showDepositDialog) {
    DepositDialog(
      onDismiss = { showDepositDialog = false },
      onConfirm = { amount, method ->
        onDeposit(amount, method)
        showDepositDialog = false
      }
    )
  }

  // Withdraw Dialog
  if (showWithdrawDialog) {
    WithdrawDialog(
      availableBalance = user?.walletBalance ?: 0.0,
      onDismiss = { showWithdrawDialog = false },
      onConfirm = { amount, dest ->
        val success = onWithdraw(amount, dest)
        if (success) showWithdrawDialog = false
      }
    )
  }
}

@Composable
fun DepositDialog(
  onDismiss: () -> Unit,
  onConfirm: (amount: Double, method: String) -> Unit
) {
  var selectedAmount by remember { mutableStateOf("100") }
  var selectedMethod by remember { mutableStateOf("Deriv P2P") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Deposit to Internal Wallet",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextPrimary
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Choose amount to fund your bot stake balance:",
          fontSize = 12.sp,
          color = TextSecondary
        )

        // Preset Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("20", "50", "100", "250", "500").forEach { amt ->
            val isSelected = selectedAmount == amt
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) GsquadYellow else Color(0xFFEFF2F6))
                .clickable { selectedAmount = amt }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$$amt",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) GsquadOnYellow else TextPrimary
              )
            }
          }
        }

        OutlinedTextField(
          value = selectedAmount,
          onValueChange = { selectedAmount = it },
          prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("deposit_input_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GsquadYellow,
            unfocusedBorderColor = LightBorder
          ),
          shape = RoundedCornerShape(10.dp)
        )

        Text(
          text = "Payment Method:",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondary
        )

        listOf("Deriv P2P", "USDT TRC-20", "Credit / Debit Card", "Bank Transfer").forEach { method ->
          val isSel = selectedMethod == method
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSel) Color(0xFFFFF9E6) else Color(0xFFF7F8FA))
              .border(1.dp, if (isSel) GsquadYellow else LightBorder, RoundedCornerShape(8.dp))
              .clickable { selectedMethod = method }
              .padding(horizontal = 10.dp, vertical = 8.dp)
          ) {
            Icon(
              imageVector = when (method) {
                "Deriv P2P" -> Icons.Default.CurrencyExchange
                "USDT TRC-20" -> Icons.Default.AccountBalanceWallet
                "Credit / Debit Card" -> Icons.Default.CreditCard
                else -> Icons.Default.AccountBalance
              },
              contentDescription = null,
              tint = if (isSel) GsquadYellow else TextSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = method,
              fontSize = 12.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
              color = TextPrimary
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = selectedAmount.toDoubleOrNull() ?: 50.0
          onConfirm(amt, selectedMethod)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = GsquadYellow,
          contentColor = GsquadOnYellow
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("confirm_deposit_btn")
      ) {
        Text("Confirm Deposit", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}

@Composable
fun WithdrawDialog(
  availableBalance: Double,
  onDismiss: () -> Unit,
  onConfirm: (amount: Double, destination: String) -> Unit
) {
  var withdrawAmount by remember { mutableStateOf("50") }
  var destinationAccount by remember { mutableStateOf("CR-9847291") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Withdraw Funds",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextPrimary
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Available Balance: $${String.format(Locale.US, "%.2f", availableBalance)} USD",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TradeGreen
        )

        OutlinedTextField(
          value = withdrawAmount,
          onValueChange = { withdrawAmount = it },
          label = { Text("Amount (USD)") },
          prefix = { Text("$ ", fontWeight = FontWeight.Bold) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("withdraw_input_amount"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GsquadYellow,
            unfocusedBorderColor = LightBorder
          ),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = destinationAccount,
          onValueChange = { destinationAccount = it },
          label = { Text("Destination (Deriv CR ID or USDT Address)") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("withdraw_input_destination"),
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
          val amt = withdrawAmount.toDoubleOrNull() ?: 10.0
          onConfirm(amt, destinationAccount)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = GsquadYellow,
          contentColor = GsquadOnYellow
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("confirm_withdraw_btn")
      ) {
        Text("Request Withdrawal", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
