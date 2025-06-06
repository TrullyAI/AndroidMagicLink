package ai.trully.webview.ui.launcher.stateflow

sealed class MagicLinkUrlState {
    data object Idle : MagicLinkUrlState()
    data class Success(val url: String) : MagicLinkUrlState()
    data class Error(val msg: String) : MagicLinkUrlState()
}