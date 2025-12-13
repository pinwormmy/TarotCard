package com.pinwormmy.midoritarot.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TarotDataSchemaTest {

    @Test
    fun tarotData_hasExpectedShapeAndLocalizedFields_enJa() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val json = context.assets.open("tarot_data.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)

        assertEquals(78, array.length())

        val ids = mutableSetOf<String>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val id = item.getString("id")
            assertTrue("Duplicate id: $id", ids.add(id))

            assertNotBlank(item, "name", id)
            assertNotBlank(item, "arcana", id)
            assertNotBlank(item, "uprightMeaning", id)
            assertNotBlank(item, "reversedMeaning", id)
            assertNotBlank(item, "description", id)
            assertNonEmptyStringArray(item, "keywords", id)

            for (lang in listOf("en", "ja")) {
                assertNotBlank(item, "name_$lang", id)
                assertNotBlank(item, "arcana_$lang", id)
                assertNotBlank(item, "uprightMeaning_$lang", id)
                assertNotBlank(item, "reversedMeaning_$lang", id)
                assertNotBlank(item, "description_$lang", id)
                assertNonEmptyStringArray(item, "keywords_$lang", id)
            }
        }
    }

    private fun assertNotBlank(item: JSONObject, key: String, id: String) {
        val value = item.optString(key)
        assertTrue("Missing or blank '$key' for id=$id", value.isNotBlank())
    }

    private fun assertNonEmptyStringArray(item: JSONObject, key: String, id: String) {
        val array = item.optJSONArray(key)
        assertNotNull("Missing array '$key' for id=$id", array)
        requireNotNull(array)
        assertTrue("Empty array '$key' for id=$id", array.length() > 0)
        for (index in 0 until array.length()) {
            val value = array.optString(index)
            assertTrue(
                "Blank value in '$key' for id=$id (index=$index)",
                value.isNotBlank()
            )
        }
    }
}

