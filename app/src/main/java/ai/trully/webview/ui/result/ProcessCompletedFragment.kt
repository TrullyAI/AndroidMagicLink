package ai.trully.webview.ui.result

import ai.trully.webview.databinding.FragmentProcessCompletedBinding
import ai.trully.webview.model.response.MLResponse
import ai.trully.webview.ui.result.stateflow.DataState
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProcessCompletedFragment : Fragment() {
    private var _binding: FragmentProcessCompletedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProcessCompletedViewModel by viewModels()

    private val args by navArgs<ProcessCompletedFragmentArgs>()

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
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initUI() {
        viewModel.getData(args.userID)
    }

    private fun initListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collect { state ->
                    when (state) {
                        DataState.Idle -> Log.i("info", "Obteniendo resultados")
                        is DataState.Success -> showResult(state.response)
                        is DataState.Error -> showError(state.msg)
                    }
                }
            }
        }
    }

    private fun showResult(mlResponse: MLResponse) {
        binding.tvLabel.text = mlResponse.label
        binding.ivDoc.setImageBitmap(mlResponse.doc?.let { viewModel.base64ToBitmap(it) })
        binding.ivSelfie.setImageBitmap(mlResponse.selfie?.let { viewModel.base64ToBitmap(it) })

        binding.loading.isVisible = false
        binding.result.isVisible = true
    }

    private fun showError(msg: String) {
        view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT) }?.apply {
            addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    onSnackBarDismissed()
                }
            })
            show()
        }
    }

    private fun onSnackBarDismissed() {
        val toChromeCustomTab =
            ProcessCompletedFragmentDirections.actionProcessCompletedFragmentToChromeCustomTabFragment()
        findNavController().navigate(toChromeCustomTab)
    }
}