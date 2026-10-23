package com.mindhr.mobile.data

import android.content.Context
import com.mindhr.mobile.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.IOException

interface MindHrApi {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/employees")
    suspend fun employees(): List<Employee>

    @GET("api/employees/{id}")
    suspend fun employee(@Path("id") id: Long): Employee
}

/** Holds the JWT (SharedPreferences) and the Retrofit client that attaches it. */
object Session {
    private const val PREFS = "mindhr"
    private const val TOKEN = "token"
    private const val NAME = "name"

    private lateinit var prefs: android.content.SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }

    val token: String? get() = prefs.getString(TOKEN, null)
    val fullName: String get() = prefs.getString(NAME, "") ?: ""

    /** Called on a 401 from a protected endpoint so the UI can return to login. */
    var onUnauthorized: (() -> Unit)? = null

    val api: MindHrApi by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                token?.let { req.header("Authorization", "Bearer $it") }
                val res = chain.proceed(req.build())
                if (res.code == 401 && !res.request.url.encodedPath.endsWith("/auth/login")) {
                    clear()
                    onUnauthorized?.invoke()
                }
                res
            }
            .build()
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MindHrApi::class.java)
    }

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun save(res: LoginResponse) {
        prefs.edit().putString(TOKEN, res.accessToken).putString(NAME, res.fullName).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    /** Best-effort message from the backend error body or the network error. */
    fun errorMessage(e: Throwable): String = when (e) {
        is HttpException -> {
            val raw = runCatching { e.response()?.errorBody()?.string().orEmpty() }.getOrDefault("")
            val body = runCatching { json.decodeFromString<ErrorBody>(raw) }.getOrNull()
            body?.message ?: body?.error ?: when (e.code()) {
                401 -> "Invalid credentials"
                403 -> "You do not have permission"
                else -> "Request failed (${e.code()})"
            }
        }
        is IOException -> "Cannot reach the server"
        else -> e.message ?: "Unexpected error"
    }
}
