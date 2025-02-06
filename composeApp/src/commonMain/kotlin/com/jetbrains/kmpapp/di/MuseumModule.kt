package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.repositories.MuseumRepository
import com.jetbrains.kmpapp.repositories.MuseumRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val museumModule = module {
    single(named("museumHttpClient")) {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
        }
    }

    single<MuseumRepository> {
        MuseumRepositoryImpl(get(named("museumHttpClient"))).apply {
            initialize()
        }
    }
}
