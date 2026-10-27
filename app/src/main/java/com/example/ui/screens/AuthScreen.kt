package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GsquadOnYellow
import com.example.ui.theme.GsquadYellow
import com.example.ui.theme.GsquadYellowContainer
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TradeGreen

@Composable
fun AuthScreen(
  onLogin: (email: String, pass: String) -> Boolean,
  onRegister: (name: String, email: String, pass: String) -> Unit,
  onQuickDemoLogin: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isRegisterMode by remember { mutableStateOf(false) }
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var showPassword by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LightBackground)
      .statusBarsPadding()
      .padding(horizontal = 20.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Logo + App Name
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(GsquadYellowContainer)
          .border(2.dp, GsquadYellow, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_gsquad_logo),
          contentDescription = "G-squad Logo",
          modifier = Modifier.size(60.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "G-squad",
          fontSize = 26.sp,
          fontWeight = FontWeight.ExtraBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GsquadYellow)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "DERIV BOT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = GsquadOnYellow
          )
        }
      }

      Text(
        text = "Automated Pooled Trading for Deriv Synthetics",
        fontSize = 12.sp,
        color = TextSecondary,
        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
      )

      // Main Auth Card
      Card(
        modifier = Modifier.fillMaxWidth(),
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
          // Tab Switcher
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF1F3F7))
              .padding(4.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (!isRegisterMode) LightSurface else Color.Transparent)
                .clickable {
                  isRegisterMode = false
                  errorMessage = null
                }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Log In",
                fontSize = 13.sp,
                fontWeight = if (!isRegisterMode) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (!isRegisterMode) TextPrimary else TextSecondary
              )
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isRegisterMode) LightSurface else Color.Transparent)
                .clickable {
                  isRegisterMode = true
                  errorMessage = null
                }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Register",
                fontSize = 13.sp,
                fontWeight = if (isRegisterMode) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isRegisterMode) TextPrimary else TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Register: Full Name
          if (isRegisterMode) {
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Full Name / Trader Handle") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = GsquadYellow
                )
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_name_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GsquadYellow,
                unfocusedBorderColor = LightBorder
              ),
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Email / Account ID
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(if (isRegisterMode) "Email Address" else "Email or GSQ Account ID") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = GsquadYellow
              )
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GsquadYellow,
              unfocusedBorderColor = LightBorder
            ),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = GsquadYellow
              )
            },
            trailingIcon = {
              IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                  imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password",
                  tint = TextSecondary
                )
              }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_pass_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GsquadYellow,
              unfocusedBorderColor = LightBorder
            ),
            shape = RoundedCornerShape(10.dp)
          )

          if (isRegisterMode) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF7DB))
                .padding(8.dp)
            ) {
              Text(
                text = "Auto-generates your unique GSQ Account ID (e.g. GSQ-583737735) and creates your internal trading wallet with starting credit.",
                fontSize = 11.sp,
                color = Color(0xFF6E4D00),
                lineHeight = 15.sp
              )
            }
          }

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = errorMessage ?: "",
              fontSize = 12.sp,
              color = Color(0xFFE53935),
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Primary Submit Button (Yellow Accent!)
          Button(
            onClick = {
              if (isRegisterMode) {
                if (email.isBlank()) {
                  errorMessage = "Please enter your email."
                  return@Button
                }
                onRegister(name, email, password)
              } else {
                if (email.isBlank()) {
                  errorMessage = "Please enter your email or Account ID."
                  return@Button
                }
                val success = onLogin(email, password)
                if (!success) {
                  errorMessage = "Account not found. Please register or use Quick Demo Sign-In."
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("auth_submit_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = GsquadYellow,
              contentColor = GsquadOnYellow
            )
          ) {
            Text(
              text = if (isRegisterMode) "CREATE G-SQUAD ACCOUNT" else "LOG IN TO BOT",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Demo Sign-In
          OutlinedButton(
            onClick = onQuickDemoLogin,
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("auth_quick_demo_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = TextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, LightBorder)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = GsquadYellow,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Instant Demo Login (GSQ-583737735)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
