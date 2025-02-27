package ai.trully.webview.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("data")
    val data: List<Data>
)

data class Data(
    @SerializedName("content")
    val content: String
)



