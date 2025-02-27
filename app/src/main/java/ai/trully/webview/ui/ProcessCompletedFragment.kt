package ai.trully.webview.ui

import ai.trully.webview.api.NetworkManager
import ai.trully.webview.api.WebService
import ai.trully.webview.databinding.FragmentProcessCompletedBinding
import ai.trully.webview.model.SDKResponse
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ProcessCompletedFragment : Fragment() {
    private var _binding: FragmentProcessCompletedBinding? = null
    private val binding get() = _binding!!

    val args by navArgs<ProcessCompletedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProcessCompletedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initUI() {
        retrieveData()
    }

    private fun retrieveData() {
        lifecycleScope.launch {
            val response = NetworkManager.buildRetrofit().create(WebService::class.java)
                .getData("content:${args.userID}")
            val body = response.body()
            val listSize = body?.data?.size ?: 0

            if (listSize > 0) {
                val content = body?.data?.get(1)?.content

                if (content != null) {
                    val sdkResponse: SDKResponse = Gson().fromJson(content, SDKResponse::class.java)
                    binding.tvLabel.text = sdkResponse.label
                    binding.ivDoc.setImageBitmap(sdkResponse.doc?.let { base64ToBitmap(it) })
                    binding.ivSelfie.setImageBitmap(sdkResponse.selfie?.let { base64ToBitmap(it) })
                    showResult()
                } else {
                    showError()
                }
            } else {
                showError()
            }
        }
    }

    private fun showResult() {
        binding.loading.isVisible = false
        binding.result.isVisible = true
    }

    private fun showError() {
        val snackbar =
            view?.let { Snackbar.make(it, "Debes terminar el proceso", Snackbar.LENGTH_SHORT) }
        snackbar?.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                onSnackbarDismissed()
            }
        })
        snackbar?.show()
    }

    private fun onSnackbarDismissed() {
        val toChromeCustomTab =
            ProcessCompletedFragmentDirections.actionProcessCompletedFragmentToChromeCustomTabFragment()
        findNavController().navigate(toChromeCustomTab)
    }

    private fun base64ToBitmap(base64Str: String): Bitmap? {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}