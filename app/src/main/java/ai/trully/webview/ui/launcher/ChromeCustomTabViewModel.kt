package ai.trully.webview.ui.launcher

import ai.trully.webview.api.NetworkManager
import ai.trully.webview.api.services.MagicLinkService
import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.ui.launcher.stateflow.IsTabOpenState
import ai.trully.webview.ui.launcher.stateflow.MagicLinkUrlState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChromeCustomTabViewModel : ViewModel() {

    companion object {
        private const val YOUR_API_KEY = "Vz7cExzjz0aXKcPrC78OhauUqr1t6oId3E2furct"
    }

    private val _magicLinkUrl = MutableStateFlow<MagicLinkUrlState>(MagicLinkUrlState.Idle)
    val magicLinkUrl: StateFlow<MagicLinkUrlState> = _magicLinkUrl

    fun getMagicLinkUrl(request: MagicLinkRequest) {
        viewModelScope.launch {
            val response = NetworkManager.buildRetrofit("https://sandbox.trully.ai/", YOUR_API_KEY).create(MagicLinkService::class.java).generateMagicLink(request)
            val body = response.body()

            if (body != null) {
                _magicLinkUrl.value = MagicLinkUrlState.Success(body.data.magic_link_url)
            } else {
                _magicLinkUrl.value = MagicLinkUrlState.Error("Error creando Magic Link")
            }
        }
    }
}