package com.bangkit.capstone.lukaku.ui.viewer

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.FragmentViewerBinding

class ViewerFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentViewerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageFile = ViewerFragmentArgs.fromBundle(arguments as Bundle).image
        val imageSelected = BitmapFactory.decodeFile(imageFile.path)

        binding.apply {
            ivSelectedImage.setImageBitmap(imageSelected)

            tvReshoot.setOnClickListener(this@ViewerFragment)
            tvContinue.setOnClickListener(this@ViewerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_continue -> {}
            R.id.tv_reshoot -> {
                findNavController().navigate(R.id.action_viewerFragment_to_captureFragment)
            }
        }
    }
}