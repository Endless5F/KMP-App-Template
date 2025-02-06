package com.jetbrains.kmpapp.di

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.host
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

// TODO 替换为真实值
const val HOST = "https://www.wanandroid.com"
const val PATH = ""

@OptIn(ExperimentalSerializationApi::class)
val json = Json(DefaultJson) {
    namingStrategy = JsonNamingStrategy.SnakeCase
    ignoreUnknownKeys = true
    explicitNulls = false
}

val httpModule = module {
    single(named("OkHttpClient")) {
        HttpClient {
            install(UserAgent) {
                agent = ""
            }
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                filter { it.host == HOST }
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            expectSuccess = true
            defaultRequest {
                url {
                    host = HOST
                    protocol = URLProtocol.HTTPS
                    path(PATH)
                }
                header(
                    "MuseumInfo",
                    "terminal=${"#deviceBrand"}; versionCode=${"#versionCode"}"
                ) // TODO deviceBrand and versionCode 替换为真实值
                runBlocking {
                    bearerAuth("#token")
                } // TODO token 替换为真实值
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    if (cause is ResponseException &&
                        cause.response.status == HttpStatusCode.Unauthorized &&
                        request.url.host == HOST &&
                        request.headers.contains(HttpHeaders.Authorization)
                    ) {
                        // TODO 重新登录，执行退登逻辑
                    }
                }
            }
        }
    }
}

fun getHttpClient() = KoinPlatform.getKoin().get<HttpClient>(named("OkHttpClient"))

@Serializable
data class BodyWrapper<T>(
    val status: Int,
    val message: String,
    val data: T,
)

suspend inline fun <reified T> HttpResponse.bodyData(): T {
    val bodyWrapper = body<BodyWrapper<T>>()
    if (bodyWrapper.status != 200) {
        throw Exception(bodyWrapper.message)
    }
    return bodyWrapper.data
}

suspend inline fun HttpResponse.bodyErrorMsg(): String? {
    if (!status.isSuccess()) {
        // 如果错误body是JSON结构，则返回其中的message字段，否则直接返回body
        var errorMsg = bodyAsText()
        try {
            val jsonElement = Json.parseToJsonElement(errorMsg)
            if (jsonElement is JsonObject && jsonElement["message"]?.jsonPrimitive?.isString == true) {
                errorMsg = jsonElement["message"]?.jsonPrimitive?.content.toString()
            }
        } catch (e: Exception) {
        }
        return errorMsg
    }
    return null
}
