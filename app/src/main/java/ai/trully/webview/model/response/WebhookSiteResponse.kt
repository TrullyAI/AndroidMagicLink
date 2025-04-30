package ai.trully.webview.model.response

import com.google.gson.annotations.SerializedName

data class WebhookSiteResponse(
    @SerializedName("data")
    val data: List<Data>
)

data class Data(
    @SerializedName("content")
    val content: String
)



