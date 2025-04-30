package ai.trully.webview.api.services

import ai.trully.webview.model.response.WebhookSiteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val token = "YOUR_WEBHOOK_TOKEN"

internal interface WebhookService {
    @GET("/token/$token/requests?sorting=newest")
    suspend fun getData(): Response<WebhookSiteResponse>
}