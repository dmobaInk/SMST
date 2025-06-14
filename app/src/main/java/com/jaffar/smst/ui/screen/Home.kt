package com.jaffar.smst.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.jaffar.smst.R
import com.jaffar.smst.viewmodel.SmsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun Home(onNavigate: (String) -> Unit, smsViewModel: SmsViewModel) {

    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    var showAuthDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.g_logo), // 替换为你的图片名
                contentDescription = "群发助手Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)) // 如果你想有圆角
            )

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = "群发助手",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5) // 浅灰色背景
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp // 阴影
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            onClick = {
                onNavigate("Numbers")
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(65.dp) // 大图标
                        .padding(end = 20.dp),
                    tint = Color.Black,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "手机号",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "已识别${smsViewModel.numbers.size}个手机号",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5) // 浅灰色背景
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp // 阴影
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            onClick = {
                onNavigate("Messages")
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(65.dp) // 大图标
                        .padding(end = 20.dp),
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "配置",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "已应用${smsViewModel.messages.size}个配置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val verified = smsViewModel.verifyAuthCode()
                    withContext(Dispatchers.Main) {
                        if (verified){
                            if (!hasPermission) {
                                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                            } else {
                                smsViewModel.startSending(context)
                            }
                        }else{
                            showAuthDialog = true
                        }
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "发送",
                fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 5.sp)
        }

        if (smsViewModel.isSending) {
            LinearProgressIndicator(
                progress = { smsViewModel.progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "发送中 ${(smsViewModel.progress * 100).toInt()}%")
        }


        if (showAuthDialog) {
            AlertDialog(
                onDismissRequest = { showAuthDialog = false },
                title = { Text("请输入授权码") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = smsViewModel.authCode,
                            onValueChange = { smsViewModel.saveAuthCode(it) },
                            label = { Text("授权码") }
                        )
                        if (errorMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMessage, color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val verified = smsViewModel.verifyAuthCode()
                            if (verified) {
                                showAuthDialog = false
                                errorMessage = ""
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    errorMessage = "授权码无效"
                                }
                            }
                        }
                    }) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAuthDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}