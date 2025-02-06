package com.jetbrains.kmpapp.repositories

import com.jetbrains.kmpapp.modules.MuseumObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

interface MuseumRepository {
    fun initialize()
    fun getObjects(): Flow<List<MuseumObject>>
    fun getObjectById(objectId: Int): Flow<MuseumObject?>
}

class MuseumRepositoryImpl(private val client: HttpClient) : MuseumRepository {
    companion object {
        private const val API_URL =
            "https://raw.githubusercontent.com/Kotlin/KMP-App-Template/main/list.json"
    }

    private val scope = CoroutineScope(SupervisorJob())
    private val storedObjects = MutableStateFlow(emptyList<MuseumObject>())

    override fun initialize() {
        scope.launch {
            refresh()
        }
    }

    private suspend fun getData(): List<MuseumObject> {
        return try {
            client.get(API_URL).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }

    suspend fun refresh() {
        storedObjects.value = getData()
    }

    override fun getObjects(): Flow<List<MuseumObject>> = storedObjects

    override fun getObjectById(objectId: Int): Flow<MuseumObject?> {
        return storedObjects.map { objects ->
            objects.find { it.objectID == objectId }
        }
    }
}
