package ai.trully.webview.model

import com.google.gson.annotations.SerializedName

data class SDKResponse(
    @SerializedName("document_image")
    val doc: String? = null,
    @SerializedName("image")
    val selfie: String? = null,
    @SerializedName("label")
    val label: String? = null
)
