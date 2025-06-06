package ai.trully.webview.ui.result.stateflow

import ai.trully.webview.model.response.MLResponse

sealed class DataState {
    data object Idle : DataState()
    data class Success(val response: MLResponse) : DataState()
    data class Error(val msg: String) : DataState()
}