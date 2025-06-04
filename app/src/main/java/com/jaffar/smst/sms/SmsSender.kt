package com.jaffar.smst.sms

import android.telephony.SmsManager
import android.widget.Toast
import kotlinx.coroutines.delay


suspend fun sendSmsSequentially(
    context: android.content.Context,
    numbers: List<String>,
    message: List<String>,
    onProgress: (Float) -> Unit,
    onDone: () -> Unit
) {
    val smsManager = SmsManager.getDefault()
    try {
        for ((index, number) in numbers.withIndex()) {
            smsManager.sendTextMessage(
                number,
                null,
                message.random(),
                null,
                null
            )
            Toast.makeText(context, "已发送给 $number", Toast.LENGTH_SHORT).show()

            val progress = (index + 1).toFloat() / numbers.size
            onProgress(progress)

            if (index != numbers.lastIndex) {
                delay(10_000) // 每 10 秒一条
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "发送失败: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        onDone()
    }
}