package com.likemagic.masters_beside.view.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentSignBinding
import com.likemagic.masters_beside.utils.CHOOSE_FRAGMENT
import com.likemagic.masters_beside.utils.SIGN_IN_WITH_EMAIL_FRAGMENT
import com.likemagic.masters_beside.utils.SIGN_IN_WITH_GOOGLE_REQUEST_CODE
import com.likemagic.masters_beside.utils.SIGN_UP_WITH_EMAIL_FRAGMENT

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
        binding.regBtn.setOnClickListener {
            navigateTo(SignUpWithEmailFragment.newInstance(), SIGN_UP_WITH_EMAIL_FRAGMENT)
        }
        binding.emailBtn.setOnClickListener {
            navigateTo(SignInWithEmailFragment.newInstance(), SIGN_IN_WITH_EMAIL_FRAGMENT)
        }
        binding.phoneBtn.setOnClickListener {
            navigateTo(SignUpWithPhoneFragment.newInstance())
        }
        binding.signWithGoogleBtn.setOnClickListener {
            //FIXME
            signInWithGoogle()
            val fm = requireActivity().supportFragmentManager
            fm.popBackStack()
            navigateTo(ChoseCategoryFragment.newInstance(), CHOOSE_FRAGMENT)
        }
    }


    private fun navigateTo(fragment: Fragment, name: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(name)
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
        requireActivity().startActivityForResult(
            getSignInClient().signInIntent,
            SIGN_IN_WITH_GOOGLE_REQUEST_CODE
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignFragment()
    }
}