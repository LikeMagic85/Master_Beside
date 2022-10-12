package com.likemagic.masters_beside.view.signIn

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentSignInBinding
import com.likemagic.masters_beside.utils.MASTER
import com.likemagic.masters_beside.utils.SIGN_UP_WITH_EMAIL_FRAGMENT
import com.likemagic.masters_beside.utils.USER
import com.likemagic.masters_beside.utils.USER_CATEGORY
import com.likemagic.masters_beside.viewModel.MainViewModel

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(USER_CATEGORY, Context.MODE_PRIVATE)
        editor = sp.edit()
        setUpUser()
    }

    private fun setUpUser() {
        binding.regBtn.setOnClickListener {
            if (binding.userChip.isChecked) {
                navigateToNextStep(SignUpWithEmailFragment.newInstance(),
                    SIGN_UP_WITH_EMAIL_FRAGMENT)
                editor.putInt(USER_CATEGORY, USER).apply()
            } else if (binding.masterChip.isChecked) {
                navigateToNextStep(SignUpWithEmailFragment.newInstance(), SIGN_UP_WITH_EMAIL_FRAGMENT)
                editor.putInt(USER_CATEGORY, MASTER).apply()
            } else {
                Snackbar.make(binding.root, "Пожалуйста, сделайте выбор", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToNextStep(fragment: Fragment, name:String) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(name)
            .replace(R.id.mainContainer, fragment, name)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}