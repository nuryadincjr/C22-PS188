package com.bangkit.capstone.lukaku.ui.onboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAction()
        onboardingFinished(view)
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveOnboarding(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startAction() {
        binding.btnJoin.setOnClickListener {
            viewModel.saveOnboarding(true)
        }
    }

    private fun onboardingFinished(view: View) {
        lifecycleScope.launch {
            viewModel.getOnboarding().collect {
                if (it == true) {
                    Navigation.findNavController(view).navigate(R.id.action_onboardingFragment_to_signFragment)
                }
            }
        }
    }
}

