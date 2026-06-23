package com.stdy4u.study4u.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

data class GitHubRelease(
    val tag_name: String?,
    val name: String?,
    val body: String?,
    val assets: List<GitHubAsset>?,
    val prerelease: Boolean = false
)

data class GitHubAsset(
    val name: String?,
    val browser_download_url: String?,
    val size: Long = 0
)

interface GitHubApiService {

    @GET("repos/MoHamed-B-M/study4u/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease
}

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gitHubApiService: GitHubApiService = retrofit.create(GitHubApiService::class.java)
}
