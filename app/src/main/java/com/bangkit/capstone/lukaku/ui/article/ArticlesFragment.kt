package com.bangkit.capstone.lukaku.ui.article

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bangkit.capstone.lukaku.databinding.FragmentArticlesBinding

class ArticlesFragment : Fragment() {
    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backToActivity()
    }

    private fun backToActivity() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}