package ai.trully.webview.ui.result.stateflow

import ai.trully.webview.model.response.SDKResponse

sealed class WebhookDataState {
    data object Idle : WebhookDataState()
    data class Success(val response: SDKResponse) : WebhookDataState()
    data class Error(val msg: String) : WebhookDataState()
}