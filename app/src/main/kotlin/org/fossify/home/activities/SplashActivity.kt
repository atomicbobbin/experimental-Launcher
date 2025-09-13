package org.fossify.home.activities

import android.content.Intent
import org.fossify.commons.activities.BaseSplashActivity
import org.fossify.home.core.ServiceLocator

class SplashActivity : BaseSplashActivity() {
    override fun initActivity() {
        ServiceLocator.initialize(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
