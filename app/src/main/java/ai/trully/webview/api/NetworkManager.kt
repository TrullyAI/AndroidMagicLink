package ai.trully.webview.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object NetworkManager {
    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()


    fun buildRetrofit(): Retrofit {
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://webhook.site/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit
    }
}