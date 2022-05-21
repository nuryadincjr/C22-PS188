package com.bangkit.capstone.lukaku.ui.sign

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.FragmentSignBinding
import com.bangkit.capstone.lukaku.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.ibrahimsn.lib.SmoothBottomBar

class SignFragment : Fragment() {
    private var _binding: FragmentSignBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var client: GoogleSignInClient
    private lateinit var dialog: Dialog
    private lateinit var bottomBar: SmoothBottomBar

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("TAG", "Google sign in failed", e)
                }
                dialog.show()
            }
        }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomBar = requireActivity().findViewById(R.id.bottomBar)
        bottomBar.visibility = GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Init Firebase Auth
        auth = Firebase.auth
        client = GoogleSignIn.getClient(requireActivity(), gso)

        initProgressDialog()

        binding.btnSignIn.setOnClickListener { signIn() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signIn() {
        val signInIntent = client.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { task ->
                // Get LoggedIn User
                val user = auth.currentUser
                // Check if user is new or existing
                if (task.additionalUserInfo!!.isNewUser) {
                    requireActivity().toast(getString(R.string.account_created_success_message))
                } else {
                    requireActivity().toast(
                        getString(
                            R.string.sign_in_welcome_message,
                            user?.displayName
                        )
                    )
                }
                moveToMainActivity()
            }
            .addOnFailureListener {
                // Sign in failed
                requireActivity().toast(getString(R.string.sign_in_failed_message))
                dialog.dismiss()
            }
    }

    private fun moveToMainActivity() {
        findNavController().navigate(R.id.action_signFragment_to_navigation_home)
        dialog.dismiss()
    }

    private fun initProgressDialog() {
        dialog = Dialog(requireActivity()).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }
    }
}