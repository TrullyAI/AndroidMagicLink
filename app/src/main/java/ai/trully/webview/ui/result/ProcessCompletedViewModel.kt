package ai.trully.webview.ui.result

import ai.trully.webview.api.NetworkManager
import ai.trully.webview.api.services.ApiService
import ai.trully.webview.model.response.MLResponse
import ai.trully.webview.ui.result.stateflow.DataState
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProcessCompletedViewModel : ViewModel() {

    companion object {
        private const val YOUR_API_KEY = "YOUR_API_KEY"
    }

    private val _data = MutableStateFlow<DataState>(DataState.Idle)
    val data: StateFlow<DataState> = _data

    fun getData(userID: String) {
        viewModelScope.launch {
            val response = NetworkManager.buildRetrofit("https://sandbox.trully.ai/", YOUR_API_KEY).create(
                ApiService::class.java
            ).requestResponse(userID)
            val statusCode = response.get("status_code").asString

            if (statusCode == "200") {
                _data.value = DataState.Success(MLResponse(
                    doc = response.getAsJsonObject("data").getAsJsonObject("images").get("document_image").asString,
                    selfie = response.getAsJsonObject("data").getAsJsonObject("images").get("selfie").asString,
                    label = response.getAsJsonObject("data").getAsJsonObject("response").get("label").asString,
                    user_id = response.getAsJsonObject("data").get("user_id").asString,
                ))
            } else {
                _data.value = DataState.Error("Ha ocurrido un error obteniendo el resultado del análisis")
            }
        }
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}