package com.bangkit.capstone.lukaku.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.FragmentOnborardingBinding

class OnborardingFragment : Fragment() {

    private var _binding: FragmentOnborardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOnborardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAction()
    }

    private fun startAction() {
        binding.btnJoin.setOnClickListener {
            findNavController().navigate(R.id.action_onborardingFragment_to_mainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}