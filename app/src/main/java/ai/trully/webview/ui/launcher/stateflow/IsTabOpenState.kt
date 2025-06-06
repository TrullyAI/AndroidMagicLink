package ai.trully.webview.ui.launcher.stateflow

sealed class IsTabOpenState{
    data object Open : IsTabOpenState()
    data object Close : IsTabOpenState()
}
