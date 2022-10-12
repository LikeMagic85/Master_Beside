package com.likemagic.masters_beside.view.signIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentSignBinding
import com.likemagic.masters_beside.utils.SIGN_IN_WITH_EMAIL_FRAGMENT
import com.likemagic.masters_beside.utils.SIGN_IN_WITH_GOOGLE_REQUEST_CODE
import com.likemagic.masters_beside.utils.SIGN_UP_FRAGMENT
import com.likemagic.masters_beside.view.MainActivity

class SignFragment : Fragment() {
    private var _binding: FragmentSignBinding? = null
    private val binding: FragmentSignBinding
        get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBtn()
    }

    private fun setUpBtn() {
        binding.regBtn.setOnClickListener{
            navigateTo(SignUpFragment.newInstance(), SIGN_UP_FRAGMENT)
        }
        binding.emailBtn.setOnClickListener {
            navigateTo(SignInWithEmailFragment.newInstance(),SIGN_IN_WITH_EMAIL_FRAGMENT)
        }
        binding.signWithGoogleBtn.setOnClickListener {
            signInWithGoogle()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun navigateTo(fragment: Fragment, name:String){
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(SIGN_UP_FRAGMENT)
            .replace(R.id.mainContainer, fragment, name)
            .commit()
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireActivity().resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signInWithGoogle() {
        requireActivity().startActivityForResult(getSignInClient().signInIntent, SIGN_IN_WITH_GOOGLE_REQUEST_CODE)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignFragment()
    }
}