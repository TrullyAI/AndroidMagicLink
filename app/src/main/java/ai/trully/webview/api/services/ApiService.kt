package ai.trully.webview.api.services

import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.model.response.MagicLinkResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ApiService {
    @POST("/v1/magic-link")
    suspend fun generateMagicLink(@Body request: MagicLinkRequest): Response<MagicLinkResponse>

    @GET("/v2/history/request?")
    suspend fun requestResponse(
        @Query("user_id") token: String
    ): JsonObject
}