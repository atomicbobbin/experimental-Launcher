package org.fossify.home.data

import android.content.Context
import android.content.SharedPreferences
import org.fossify.home.core.DeviceCapabilities
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsRepositoryTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    @Mock
    private lateinit var deviceCapabilities: DeviceCapabilities

    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setUp() {
        `when`(context.applicationContext).thenReturn(context)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(editor.apply()).thenReturn(Unit)
        
        settingsRepository = SettingsRepository(context)
    }

    @Test
    fun `homeColumnCount getter returns correct value`() {
        // Given
        `when`(sharedPreferences.getInt("home_column_count", 3)).thenReturn(5)
        
        // When
        val result = settingsRepository.homeColumnCount
        
        // Then
        assertEquals(5, result)
    }

    @Test
    fun `homeColumnCount setter saves value`() {
        // When
        settingsRepository.homeColumnCount = 7
        
        // Then
        verify(editor).putInt("home_column_count", 7)
        verify(editor).apply()
    }

    @Test
    fun `homeRowCount getter returns correct value`() {
        // Given
        `when`(sharedPreferences.getInt("home_row_count", 4)).thenReturn(6)
        
        // When
        val result = settingsRepository.homeRowCount
        
        // Then
        assertEquals(6, result)
    }

    @Test
    fun `homeRowCount setter saves value`() {
        // When
        settingsRepository.homeRowCount = 8
        
        // Then
        verify(editor).putInt("home_row_count", 8)
        verify(editor).apply()
    }

    @Test
    fun `showSearchBar getter returns correct value`() {
        // Given
        `when`(sharedPreferences.getBoolean("show_search_bar", true)).thenReturn(false)
        
        // When
        val result = settingsRepository.showSearchBar
        
        // Then
        assertEquals(false, result)
    }

    @Test
    fun `showSearchBar setter saves value`() {
        // When
        settingsRepository.showSearchBar = false
        
        // Then
        verify(editor).putBoolean("show_search_bar", false)
        verify(editor).apply()
    }

    @Test
    fun `applyCapabilityGating updates feature flags correctly`() {
        // Given
        `when`(deviceCapabilities.supportsDynamicColor).thenReturn(true)
        `when`(deviceCapabilities.supportsRenderEffectBlur).thenReturn(false)
        
        // When
        settingsRepository.applyCapabilityGating(deviceCapabilities)
        
        // Then
        val featureFlags = settingsRepository.featureFlags
        assertTrue(featureFlags.enableDynamicColor)
        assertFalse(featureFlags.enableBlur)
        assertFalse(featureFlags.enableThemedIcons) // Always false as per implementation
    }

    @Test
    fun `applyCapabilityGating handles exceptions gracefully`() {
        // Given
        `when`(deviceCapabilities.supportsDynamicColor).thenThrow(RuntimeException("Test exception"))
        
        // When
        settingsRepository.applyCapabilityGating(deviceCapabilities)
        
        // Then
        // Should not throw exception and should maintain default feature flags
        val featureFlags = settingsRepository.featureFlags
        assertFalse(featureFlags.enableDynamicColor)
        assertTrue(featureFlags.enableBlur)
        assertFalse(featureFlags.enableThemedIcons)
    }

    @Test
    fun `feature flags have correct default values`() {
        // When
        val featureFlags = settingsRepository.featureFlags
        
        // Then
        assertTrue(featureFlags.enableBlur)
        assertFalse(featureFlags.enableThemedIcons)
        assertFalse(featureFlags.enableDynamicColor)
    }
}
