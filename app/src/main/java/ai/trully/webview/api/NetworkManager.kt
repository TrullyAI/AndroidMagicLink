package ai.trully.webview.api

import ai.trully.webview.api.interceptor.TrullyInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object NetworkManager {
    private fun client(apiKey: String?): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                apiKey?.let { key ->
                    addInterceptor(TrullyInterceptor(key))
                }
            }
            .build()


    fun buildRetrofit(baseUrl: String, apiKey: String? = null): Retrofit {
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client(apiKey))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit
    }
}