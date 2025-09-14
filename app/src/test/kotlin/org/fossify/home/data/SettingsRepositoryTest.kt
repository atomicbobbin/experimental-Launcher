package org.fossify.home.data

import android.content.Context
import android.content.SharedPreferences
import org.fossify.home.core.DeviceCapabilities
import org.fossify.home.helpers.Config
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var deviceCapabilities: DeviceCapabilities

    @Before
    fun setUp() {
        // Use Robolectric's context - this will use real SharedPreferences in test environment
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        
        // Create a mock for DeviceCapabilities for the capability gating tests
        deviceCapabilities = mock(DeviceCapabilities::class.java)
    }

    @Test
    fun `homeColumnCount getter returns correct value`() {
        // Given - set a specific value
        settingsRepository.homeColumnCount = 5

        // When
        val result = settingsRepository.homeColumnCount

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `homeColumnCount setter saves value`() {
        // When
        settingsRepository.homeColumnCount = 7

        // Then - verify the value was actually saved
        assertEquals(7, settingsRepository.homeColumnCount)
    }

    @Test
    fun `homeRowCount getter returns correct value`() {
        // Given - set a specific value
        settingsRepository.homeRowCount = 6

        // When
        val result = settingsRepository.homeRowCount

        // Then
        assertEquals(6, result)
    }

    @Test
    fun `homeRowCount setter saves value`() {
        // When
        settingsRepository.homeRowCount = 8

        // Then - verify the value was actually saved
        assertEquals(8, settingsRepository.homeRowCount)
    }

    @Test
    fun `showSearchBar getter returns correct value`() {
        // Given - set a specific value
        settingsRepository.showSearchBar = false
        
        // When
        val result = settingsRepository.showSearchBar
        
        // Then
        assertFalse(result)
    }

    @Test
    fun `showSearchBar setter saves value`() {
        // When
        settingsRepository.showSearchBar = false
        
        // Then - verify the value was actually saved
        assertFalse(settingsRepository.showSearchBar)
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
