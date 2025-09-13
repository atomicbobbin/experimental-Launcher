package org.fossify.home.search

import android.content.Context

interface SearchProvider {
    val id: String
    fun isEnabled(): Boolean
    fun search(query: String): List<SearchResult>
}

data class SearchResult(val title: String, val subtitle: String?, val action: () -> Unit)

class SearchRegistry(private val context: Context) {
    private val providers = linkedMapOf<String, SearchProvider>()

    fun register(provider: SearchProvider) {
        providers[provider.id] = provider
    }

    fun queryAll(query: String): List<SearchResult> {
        return providers.values.filter { it.isEnabled() }.flatMap { it.search(query) }
    }
}


