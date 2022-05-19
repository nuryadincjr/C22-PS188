package com.bangkit.capstone.lukaku.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.VISIBLE
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.FragmentProfileBinding
import com.bangkit.capstone.lukaku.utils.loadCircleImage
import com.bangkit.capstone.lukaku.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        setProfile()
        binding.ivSettings.setOnClickListener {
            showPopup(binding.ivSettings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProfile() {
        val user = auth.currentUser
        binding.apply {
            ivProfile.loadCircleImage(user?.photoUrl)
            tvName.text = user?.displayName
            tvEmail.text = user?.email
        }
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.menuInflater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }
        inflater.inflate(R.menu.popup_menu_profile, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_profile -> {
                    requireActivity().toast(getString(R.string.still_under_development))
                }
                R.id.app_settings -> {
                    findNavController().navigate(R.id.action_navigation_profile_to_appSettingsFragment)
                }
                R.id.sign_out -> {
                    showSignOutDialog()
                }
            }
            true
        }
        popup.show()
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_sign_out))
            .setMessage(getString(R.string.dialog_message_sign_out))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                signOut()
            }.show()
    }

    private fun signOut() {
        auth.signOut()
        findNavController().navigate(R.id.action_navigation_profile_to_containerActivity)
        binding.progressBar.visibility = VISIBLE
        requireActivity().finish()
    }
}