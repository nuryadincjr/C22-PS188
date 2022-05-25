package com.bangkit.capstone.lukaku.ui.notifications.updates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.capstone.lukaku.databinding.FragmentNotifsBinding

class UpdatesFragment : Fragment() {
    private var _binding: FragmentNotifsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotifsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text = "This is Notif Updates."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}