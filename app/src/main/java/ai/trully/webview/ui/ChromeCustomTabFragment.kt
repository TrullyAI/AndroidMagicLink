package ai.trully.webview.ui

import ai.trully.webview.R
import ai.trully.webview.databinding.FragmentChromeCustomTabBinding
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ChromeCustomTabFragment : Fragment() {
    private var _binding: FragmentChromeCustomTabBinding? = null
    private val binding get() = _binding!!

    private var tabIsOpen = false

    private val url = "YOUR_MAGIC_LINK"
    private val userID = "YOUR_USER_ID"

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
        binding.btn.setOnClickListener {
            openCustomTab(
                requireContext(),
                "$url?user_id=$userID"
            )
        }
    }

    private fun onCustomTabClosed() {
        if (tabIsOpen) {
            tabIsOpen = false

            val toProcessCompleted =
                ChromeCustomTabFragmentDirections.actionChromeCustomTabFragmentToProcessCompletedFragment(
                    userID
                )
            findNavController().navigate(toProcessCompleted)
        }
    }

    private fun openCustomTab(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(context.getColor(R.color.white))
            .setBookmarksButtonEnabled(false)
            .setUrlBarHidingEnabled(true)
            .setShowTitle(false)
            .build()

        tabIsOpen = true
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}