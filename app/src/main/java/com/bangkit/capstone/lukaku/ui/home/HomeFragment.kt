package com.bangkit.capstone.lukaku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstone.lukaku.adapters.HeadlineAdapter
import com.bangkit.capstone.lukaku.data.resources.HeadlineData
import com.bangkit.capstone.lukaku.databinding.FragmentHomeBinding
import com.bangkit.capstone.lukaku.utils.Constants.INTERVAL
import com.bangkit.capstone.lukaku.utils.ViewPager.autoScroll
import com.bangkit.capstone.lukaku.utils.ViewPager.mediator
import com.bangkit.capstone.lukaku.utils.ViewPager.transformer

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onStartHeadline()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onStartHeadline() {
        val headlines = HeadlineData.getHeadlines()
        val headlineAdapter = HeadlineAdapter(headlines)

        val title = mutableListOf<String>()
        for (titleItem in headlines) {
            title.add(titleItem.title.toString())
        }

        binding.vpHeadline.apply {
            adapter = headlineAdapter
            transformer()
            autoScroll(viewLifecycleOwner.lifecycleScope, INTERVAL)
            mediator(binding.tabLayout, title)
        }
    }
}