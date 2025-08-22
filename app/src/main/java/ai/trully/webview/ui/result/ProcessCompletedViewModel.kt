package ai.trully.webview.ui.result

import ai.trully.webview.api.NetworkManager
import ai.trully.webview.api.services.ApiService
import ai.trully.webview.model.response.MLResponse
import ai.trully.webview.ui.result.stateflow.DataState
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val RETRY_DELAY_MS = 500L

class ProcessCompletedViewModel : ViewModel() {

    companion object {
        private const val YOUR_API_KEY = "YOUR_API_KEY"
    }

    private val _data = MutableStateFlow<DataState>(DataState.Idle)
    val data: StateFlow<DataState> = _data

    fun getData(token: String) {
        viewModelScope.launch {
            // There is a small delay between the moment the user end the process and the response is ready to be collected
            // We recommend running a recursive GET if you want to retrieve the data immediately after the user has completed the process
            recursiveGetData(token)
        }
    }

    private suspend fun recursiveGetData(token: String) {
        try {
            val response =
                NetworkManager.buildRetrofit("https://sandbox.trully.ai/", YOUR_API_KEY).create(
                    ApiService::class.java
                ).requestResponse(token)

            _data.value = DataState.Success(
                MLResponse(
                    doc = response.getAsJsonObject("data").getAsJsonObject("images")
                        .get("document_image").asString,
                    selfie = response.getAsJsonObject("data").getAsJsonObject("images")
                        .get("selfie").asString,
                    result = response.getAsJsonObject("data").getAsJsonObject("response")
                        .getAsJsonObject("unico").get("result").asString,
                )
            )
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Log.d(
                    "GetData",
                    "Recibido 404. Reintentando en ${RETRY_DELAY_MS}ms."
                )
                delay(RETRY_DELAY_MS)
                recursiveGetData(token)
            } else {
                Log.e("GetData", "Error HTTP no manejado: ${e.code()}", e)
                _data.value = DataState.Error("Error de comunicación con el servidor: ${e.code()}")
            }
        } catch (e: Exception) {
            Log.e("GetData", "Error inesperado", e)
            _data.value =
                DataState.Error("Ha ocurrido un error obteniendo el resultado del análisis")
        }
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}