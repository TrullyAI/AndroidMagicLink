package ai.trully.webview.model.request

import com.google.gson.annotations.SerializedName

data class MagicLinkRequest(
    @SerializedName("one_time_only")
    val one_time_only: Boolean,
    @SerializedName("external_id")
    val user_id: String,
    @SerializedName("metadata")
    val metadata: Metadata
)

data class Metadata(
    @SerializedName("logo")
    val logo: String,
    @SerializedName("redirect_url")
    val redirect_url: String
)
