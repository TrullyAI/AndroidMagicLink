package ai.trully.webview.model.response

import com.google.gson.annotations.SerializedName

data class MagicLinkResponse(
    @SerializedName("data")
    val data: Res
)

data class Res(
    @SerializedName("magic_link_url")
    val magic_link_url: String
)
