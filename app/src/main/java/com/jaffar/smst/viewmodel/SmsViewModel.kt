package com.jaffar.smst.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaffar.smst.dataStore.DataStoreManager
import com.jaffar.smst.sms.sendSmsSequentially
import kotlinx.coroutines.launch

class SmsViewModel(application: Application): AndroidViewModel(application) {

    var progress by mutableFloatStateOf(0f)
        private set
    var isSending by mutableStateOf(false)
        private set
    var numbers = mutableStateListOf<String>()
        private set
    var messages = mutableStateListOf<String>()
        private set

    fun startSending(context: Context) {
        if (isSending) return
        progress = 0f
        isSending = true
        if (messages.isEmpty()){
            Toast.makeText(context, "消息模板为空", Toast.LENGTH_SHORT).show()
            return
        }
        if (numbers.isEmpty()){
            Toast.makeText(context, "号码清单为空", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            sendSmsSequentially(
                context,
                numbers,
                messages,
                onProgress = { progress = it },
                onDone = { isSending = false }
            )
        }
    }

    fun saveNumbers(numbersStr: String){
        val newNumber: List<String> = numbersStr
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        numbers.clear()
        numbers.addAll(newNumber)
        saveNumbers2Storage()
    }

    fun numbers2Str(): String{
        return numbers.joinToString(separator = "\n")
    }

    fun saveMessage(index: Int? = null, message: String){
        index?.let { messages[index] = message} ?: messages.add(message)
        saveMessage2Storage()
    }

    private fun saveNumbers2Storage(){
        viewModelScope.launch {
            DataStoreManager.saveNumbers(getApplication(), numbers)
        }
    }

    fun loadNumbersFromStorage(){
        viewModelScope.launch {
            val saved = DataStoreManager.loadNumbers(getApplication())
            numbers.clear()
            numbers.addAll(saved)
        }
    }

    private fun saveMessage2Storage(){
        viewModelScope.launch {
            DataStoreManager.saveMessages(getApplication(), messages)
        }
    }

    fun loadMessagesFromStorage(){
        viewModelScope.launch {
            val saved = DataStoreManager.loadMessages(getApplication())
            messages.clear()
            messages.addAll(saved)
        }
    }

}