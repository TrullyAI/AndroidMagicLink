package ai.trully.webview.api.services

import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.model.response.HistoryResponse
import ai.trully.webview.model.response.MagicLinkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ApiService {
    @POST("/v1/magic-link")
    suspend fun generateMagicLink(@Body request: MagicLinkRequest): Response<MagicLinkResponse>

    @POST("/v2/history/request?")
    suspend fun requestHistory(
        @Query("magic_link_token") token: String
    ): Response<HistoryResponse>
}