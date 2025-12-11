package com.pinwormmy.midoritarot.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pinwormmy.midoritarot.ui.state.AppLanguage
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
    }

    @After
    fun tearDown() {
        context.getSharedPreferences("tarot_settings", Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Test
    fun saveAndLoad_persistsJapaneseLanguage() {
        val initial = repository.load()
        val updated = initial.copy(language = AppLanguage.Japanese)

        repository.save(updated)
        val loaded = repository.load()

        assertEquals(AppLanguage.Japanese, loaded.language)
    }
}
