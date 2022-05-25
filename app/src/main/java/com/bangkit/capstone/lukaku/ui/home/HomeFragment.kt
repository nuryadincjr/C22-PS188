package com.bangkit.capstone.lukaku.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.adapters.HeadlineAdapter
import com.bangkit.capstone.lukaku.data.resources.HeadlineData
import com.bangkit.capstone.lukaku.databinding.FragmentHomeBinding
import com.bangkit.capstone.lukaku.utils.ActivityLifeObserver
import com.bangkit.capstone.lukaku.utils.Constants.INTERVAL
import com.bangkit.capstone.lukaku.utils.ViewPager.autoScroll
import com.bangkit.capstone.lukaku.utils.ViewPager.mediator
import com.bangkit.capstone.lukaku.utils.ViewPager.transformer
import com.bangkit.capstone.lukaku.utils.loadCircleImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.ibrahimsn.lib.SmoothBottomBar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomBar: SmoothBottomBar
    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(ActivityLifeObserver {
            bottomBar = requireActivity().findViewById(R.id.bottomBar)
        })
    }

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
        auth = Firebase.auth

        setProfile()
        onStartHeadline()
    }

    override fun onResume() {
        super.onResume()
        bottomBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomBar.visibility = View.GONE
    }

    private fun setProfile() {
        val user = auth.currentUser
        binding.apply {
            ivProfile.loadCircleImage(user?.photoUrl)
            tvName.text = getString(R.string.name_display, user?.displayName)
        }
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