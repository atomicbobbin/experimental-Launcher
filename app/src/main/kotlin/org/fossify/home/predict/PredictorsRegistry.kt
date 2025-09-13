package org.fossify.home.predict

import android.content.Context
import org.fossify.home.models.AppLauncher

interface Predictor {
    val id: String
    fun suggest(apps: List<AppLauncher>, maxCount: Int): List<AppLauncher>
}

class PredictorsRegistry(context: Context, private val tracker: UsageTracker) {
    private val predictors = linkedMapOf<String, Predictor>()

    init {
        register(RecencyPredictor(tracker))
        register(FrequencyPredictor(tracker))
    }

    fun register(predictor: Predictor) {
        predictors[predictor.id] = predictor
    }

    fun getSuggestions(apps: List<AppLauncher>, maxCount: Int): List<AppLauncher> {
        // Simple ensembling: interleave predictors results for diversity
        val lists = predictors.values.map { it.suggest(apps, maxCount) }
        val out = ArrayList<AppLauncher>()
        var i = 0
        while (out.size < maxCount && lists.any { it.size > i }) {
            lists.forEach { l ->
                if (i < l.size && !out.any { it.packageName == l[i].packageName && it.activityName == l[i].activityName }) {
                    out.add(l[i])
                }
            }
            i++
        }
        return out
    }
}

class RecencyPredictor(private val tracker: UsageTracker) : Predictor {
    override val id: String = "recency"
    override fun suggest(apps: List<AppLauncher>, maxCount: Int): List<AppLauncher> {
        val recent = tracker.getRecentLaunches()
        return recent.mapNotNull { id -> apps.firstOrNull { it.getLauncherIdentifier() == id } }
            .take(maxCount)
    }
}

class FrequencyPredictor(private val tracker: UsageTracker) : Predictor {
    override val id: String = "frequency"
    override fun suggest(apps: List<AppLauncher>, maxCount: Int): List<AppLauncher> {
        val freq = tracker.getMostFrequent()
        return freq.mapNotNull { id -> apps.firstOrNull { it.getLauncherIdentifier() == id } }
            .take(maxCount)
    }
}


