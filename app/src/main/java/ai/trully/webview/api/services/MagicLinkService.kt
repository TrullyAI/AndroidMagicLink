package ai.trully.webview.api.services

import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.model.response.MagicLinkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface MagicLinkService {
    @POST("/v1/magic-link")
    suspend fun generateMagicLink(@Body request: MagicLinkRequest): Response<MagicLinkResponse>
}