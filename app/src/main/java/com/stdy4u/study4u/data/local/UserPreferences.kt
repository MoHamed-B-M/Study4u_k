package com.stdy4u.study4u.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.store: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val isFirstLaunch: Flow<Boolean> = context.store.data.map { preferences ->
        preferences[Keys.IS_FIRST_LAUNCH] ?: true
    }

    val userName: Flow<String> = context.store.data.map { preferences ->
        preferences[Keys.USER_NAME] ?: ""
    }

    suspend fun setOnboardingComplete(name: String) {
        context.store.edit { preferences ->
            preferences[Keys.IS_FIRST_LAUNCH] = false
            preferences[Keys.USER_NAME] = name
        }
    }
}
