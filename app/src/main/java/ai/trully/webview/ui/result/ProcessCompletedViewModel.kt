package ai.trully.webview.ui.result

import ai.trully.webview.api.NetworkManager
import ai.trully.webview.api.services.WebhookService
import ai.trully.webview.model.response.SDKResponse
import ai.trully.webview.ui.launcher.stateflow.MagicLinkUrlState
import ai.trully.webview.ui.result.stateflow.WebhookDataState
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProcessCompletedViewModel : ViewModel() {

    private val _webhookData = MutableStateFlow<WebhookDataState>(WebhookDataState.Idle)
    val webhookData: StateFlow<WebhookDataState> = _webhookData

    fun getWebhookData(userID: String) {
        viewModelScope.launch {
            val response = NetworkManager.buildRetrofit("https://webhook.site/").create(
                WebhookService::class.java)
                .getData("content:$userID")
            val body = response.body()
            val listSize = body?.data?.size ?: 0

            if (listSize > 0) {
                val content = body?.data?.get(1)?.content

                if (content != null) {
                    val sdkResponse: SDKResponse = Gson().fromJson(content, SDKResponse::class.java)
                    _webhookData.value = WebhookDataState.Success(sdkResponse)
                } else {
                    _webhookData.value = WebhookDataState.Error("Debes terminar el proceso")
                }
            } else {
                _webhookData.value = WebhookDataState.Error("Debes terminar el proceso")
            }
        }
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}