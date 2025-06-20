package ai.trully.webview.ui.launcher

import ai.trully.webview.R
import ai.trully.webview.databinding.FragmentChromeCustomTabBinding
import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.model.request.Metadata
import ai.trully.webview.ui.launcher.stateflow.MagicLinkUrlState
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChromeCustomTabFragment : Fragment() {
    private var _binding: FragmentChromeCustomTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChromeCustomTabViewModel by viewModels()
    private var customTabsServiceConnection: CustomTabsServiceConnection? = null

    companion object {
        private const val YOUR_LOGO_URL =
            "https://trully-api-documentation.s3.us-east-1.amazonaws.com/trully-sdk/icon-trully.svg"
        private const val YOUR_USER_ID = "test"
        private const val DEEP_LINK_URL =
            "webview://ai.trully.webview/process-completed?user_id=$YOUR_USER_ID"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChromeCustomTabBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        customTabsServiceConnection?.let { requireContext().unbindService(it) }
        _binding = null
    }

    private fun initListeners() {
        clickListener()
        observeMagicLinkUrl()
    }

    private fun clickListener() {
        binding.btn.setOnClickListener {
            generateMagicLink()
        }
    }

    private fun openCustomTab(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(requireContext().getColor(R.color.white))
            .setUrlBarHidingEnabled(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()

        customTabsIntent.intent.apply {
            // 1. Enables JavaScript and storage
            putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, true)

            // 2. CustomTab will be launch using Chrome
            // even when the user has set other default Browser
            setPackage("com.android.chrome")

            // 3. Additional flags to improve compatibility
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        customTabsServiceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                navigateToCompleted()
            }
        }.also { connection ->
            CustomTabsClient.bindCustomTabsService(
                requireContext(),
                "com.android.chrome",
                connection
            )
        }

        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    private fun observeMagicLinkUrl() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.magicLinkUrl.collect { state ->
                    when (state) {
                        is MagicLinkUrlState.Success -> openCustomTab(state.url)
                        is MagicLinkUrlState.Error -> showError(state.msg)
                        MagicLinkUrlState.Idle -> Unit // No action needed
                    }
                }
            }
        }
    }

    private fun navigateToCompleted() {
        findNavController().navigate(
            ChromeCustomTabFragmentDirections.actionChromeCustomTabFragmentToProcessCompletedFragment(
                YOUR_USER_ID
            )
        )
    }


    private fun generateMagicLink() {
        val request = MagicLinkRequest(
            one_time_only = true,
            user_id = YOUR_USER_ID,
            metadata = Metadata(
                logo = YOUR_LOGO_URL,
                redirect_url = DEEP_LINK_URL
            )
        )

        viewModel.getMagicLinkUrl(request)
    }

    private fun showError(msg: String) {
        view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT) }?.show()
    }
}