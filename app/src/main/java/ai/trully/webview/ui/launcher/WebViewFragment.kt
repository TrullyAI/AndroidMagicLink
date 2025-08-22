package ai.trully.webview.ui.launcher

import ai.trully.webview.databinding.FragmentWebviewBinding
import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.model.request.Metadata
import ai.trully.webview.ui.launcher.stateflow.MagicLinkUrlState
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class WebViewFragment : Fragment() {
    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WebViewViewModel by viewModels()

    private var magicLinkToken = "";

    companion object {
        private const val DEEP_LINK_URL =
            "webview://ai.trully.webview/process-completed"
        private const val DEEP_LINK_SCHEME = "webview"
    }

    // In order to use the WebView, we need to request camera permissions
    // and check if the user has granted them. If not, the process will not work.
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            binding.webView.reload()

            // 2. Create Magic Link
            if (permission) generateMagicLink()
            else showError("Debe permitir acceso a la cámara")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        initListeners()
    }

    private fun initListeners() {
        binding.btn.setOnClickListener {
            // 1. Ask for camera permission
            requestPermissionsLauncher.launch(Manifest.permission.CAMERA)
        }
        observeMagicLinkUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            // Allows media to start playing with out user gesture
            settings.mediaPlaybackRequiresUserGesture = false

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.progressBar.progress = newProgress
                    binding.progressBar.isVisible = newProgress < 100
                }

                // Request camera permission to the Webview
                override fun onPermissionRequest(request: PermissionRequest) {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasCameraPermission) {
                        request.grant(request.resources)
                    } else {
                        Toast.makeText(
                            context,
                            "Se necesitan permisos de cámara",
                            Toast.LENGTH_SHORT
                        ).show()
                        request.deny()
                    }
                }
            }

            // Handle Deep Link redirection
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url ?: return false

                    // Check if the URL contains the correct redirect URL
                    if (url.scheme == DEEP_LINK_SCHEME) {
                        navigateToCompleted(magicLinkToken)
                        return true
                    }

                    return false
                }
            }
        }
    }

    private fun loadUrlInWebView(url: String) {
        binding.btn.isVisible = false
        binding.webView.isVisible = true
        binding.webView.loadUrl(url)
    }

    private fun observeMagicLinkUrl() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.magicLinkUrl.collect { state ->
                    when (state) {
                        is MagicLinkUrlState.Success -> {
                            // 3. Launch Webview
                            loadUrlInWebView(state.url)
                            magicLinkToken = state.magic_link_token ?: ""
                        }

                        is MagicLinkUrlState.Error -> {
                            showError(state.msg)
                            binding.btn.isVisible = true
                        }

                        MagicLinkUrlState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun navigateToCompleted(token: String) {
        val action =
            WebViewFragmentDirections.actionWebViewFragmentToProcessCompletedFragment(
                token
            )
        findNavController().navigate(action)
    }

    private fun generateMagicLink() {
        val request = MagicLinkRequest(
            one_time_only = true,
            metadata = Metadata(
                redirect_url = DEEP_LINK_URL
            )
        )
        viewModel.getMagicLinkUrl(request)
    }

    private fun showError(msg: String) {
        view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT) }?.show()
    }
}