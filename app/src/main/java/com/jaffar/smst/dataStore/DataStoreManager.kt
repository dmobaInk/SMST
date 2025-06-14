package com.jaffar.smst.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.flow.first

object DataStoreManager {

    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    private val KEY_NUMBERS = stringPreferencesKey("phone_numbers")
    private val KEY_MESSAGES = stringPreferencesKey("messages")
    private val KEY_AUTH_CODE = stringPreferencesKey("auth_code")

    suspend fun saveNumbers(context: Context, numbers: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NUMBERS] = JSONObject.toJSONString(numbers)
        }
    }

    suspend fun loadNumbers(context: Context): List<String> {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_NUMBERS]?.let { JSONArray.parseArray(it, String::class.java) } ?: emptyList()
    }

    suspend fun saveMessages(context: Context, messages: List<String>){
        context.dataStore.edit { prefs ->
            prefs[KEY_MESSAGES] = JSONObject.toJSONString(messages)
        }
    }

    suspend fun loadMessages(context: Context): List<String> {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_MESSAGES]?.let { JSONArray.parseArray(it, String::class.java) } ?: emptyList()
    }

    suspend fun saveAuthCode(context: Context, authCode: String){
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTH_CODE] = authCode
        }
    }

    suspend fun loadAuthCode(context: Context): String{
        val prefs = context.dataStore.data.first()
        return prefs[KEY_AUTH_CODE] ?: ""
    }

}