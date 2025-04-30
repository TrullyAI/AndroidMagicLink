package ai.trully.webview.api.services

import ai.trully.webview.model.response.WebhookSiteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val token = "a963bce2-ab11-4204-8128-eca3b3908120"

internal interface WebhookService {
    @GET("/token/$token/requests?sorting=newest")
    suspend fun getData(@Query("query") query: String): Response<WebhookSiteResponse>
}