package ai.trully.webview.api

import ai.trully.webview.model.ResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val token = "YOUR_WEBHOOK_TOKEN"

internal interface WebService {
    @GET("/token/$token/requests?sorting=newest")
    suspend fun getData(@Query("query") query: String): Response<ResponseModel>
}