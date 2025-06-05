package ai.trully.webview.ui.launcher

import ai.trully.webview.api.NetworkManager
import ai.trully.webview.api.services.ApiService
import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.ui.launcher.stateflow.MagicLinkUrlState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChromeCustomTabViewModel : ViewModel() {

    companion object {
        private const val YOUR_API_KEY = "GcOBKQZ1Mn2nVSY4cWSRV3Y23gFpS8yHa06Tw2Rc"
    }

    private val _magicLinkUrl = MutableStateFlow<MagicLinkUrlState>(MagicLinkUrlState.Idle)
    val magicLinkUrl: StateFlow<MagicLinkUrlState> = _magicLinkUrl

    fun getMagicLinkUrl(request: MagicLinkRequest) {
        viewModelScope.launch {
            val response = NetworkManager.buildRetrofit("https://sandbox.trully.ai/", YOUR_API_KEY)
                .create(ApiService::class.java).generateMagicLink(request)
            val body = response.body()

            if (body != null) {
                _magicLinkUrl.value = MagicLinkUrlState.Success(body.data.magic_link_url)
            } else {
                _magicLinkUrl.value = MagicLinkUrlState.Error("Error creando Magic Link")
            }
        }
    }

    fun resetMagicLinkUrlState() {
        _magicLinkUrl.value = MagicLinkUrlState.Idle
    }
}