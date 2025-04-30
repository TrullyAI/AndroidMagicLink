package ai.trully.webview.ui.launcher

import ai.trully.webview.R
import ai.trully.webview.databinding.FragmentChromeCustomTabBinding
import ai.trully.webview.model.request.MagicLinkRequest
import ai.trully.webview.model.request.Metadata
import ai.trully.webview.ui.launcher.stateflow.MagicLinkUrlState
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
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

    private var tabIsOpen = false

    companion object {
        private const val MAGIC_LINK_TITLE = "YOUR_MAGIC_LINK_TITLE"
        private const val YOUR_USER_ID = "YOUR_USER_ID"
        private const val YOUR_WEBHOOK_URL = "YOUR_WEBHOOK_URL"
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

    override fun onResume() {
        super.onResume()
        onCustomTabClosed()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initListeners() {
        clickListener()
        magicLinkUrl()
    }

    private fun clickListener() {
        binding.btn.setOnClickListener {
            generateMagicLink()
        }
    }

    private fun onCustomTabClosed() {
        if (tabIsOpen) {
            tabIsOpen = false
            val toProcessCompleted =
                ChromeCustomTabFragmentDirections.actionChromeCustomTabFragmentToProcessCompletedFragment(
                    YOUR_USER_ID
                )
            findNavController().navigate(toProcessCompleted)
        }
    }

    private fun openCustomTab(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setSecondaryToolbarColor(requireContext().getColor(R.color.white))
            .setBookmarksButtonEnabled(false)
            .setUrlBarHidingEnabled(true)
            .setShowTitle(false)
            .build()

        tabIsOpen = true
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    private fun magicLinkUrl() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.magicLinkUrl.collect { state ->
                    when (state) {
                        MagicLinkUrlState.Idle -> Log.i("info", "esperando acción de usuario")
                        is MagicLinkUrlState.Success -> {
                            openCustomTab(state.url)
                            viewModel.resetMagicLinkUrlState()
                        }

                        is MagicLinkUrlState.Error -> showError(state.msg)
                    }
                }
            }
        }
    }

    private fun generateMagicLink() {
        val request = MagicLinkRequest(
            title = MAGIC_LINK_TITLE,
            one_time_only = true,
            user_id = YOUR_USER_ID,
            metadata = Metadata(
                webhook_url = YOUR_WEBHOOK_URL
            )
        )

        viewModel.getMagicLinkUrl(request)
    }

    private fun showError(msg: String) {
        view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT) }?.show()
    }
}