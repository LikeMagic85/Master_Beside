package com.likemagic.masters_beside.view.signIn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentSignUpUserBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.SignViewModel

class SignInWithEmailFragment : Fragment() {

    private var _binding: FragmentSignUpUserBinding? = null
    private val binding: FragmentSignUpUserBinding
        get() = _binding!!

    private val viewModel: SignViewModel by activityViewModels()

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
        _binding = FragmentSignUpUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        viewModel.getLiveData().observe(viewLifecycleOwner) {
            renderSignResult(it)
        }
        signIn()
        setUpFragment()
    }

    private fun setUpFragment() {
        binding.apply {
            title.text = "Вход"
            signInBtn.text = "Войти"
            forgetPassword.visibility = VISIBLE
            forgetPassword.setOnClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(RESET_PASSWORD_FRAGMENT)
                    .replace(R.id.mainContainer, ResetPasswordFragment.newInstance())
                    .commit()
            }
        }
    }

    private fun signIn() {
        binding.signInBtn.setOnClickListener {
            val email = binding.loginEmailInput.text.toString()
            val password = binding.loginPasswordInput.text.toString()
            val sp = requireActivity().getSharedPreferences(PASSWORD, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(PASSWORD, password).apply()
            if (isValidEmail(email)) {
                if (isValidPassword(password)) {
                    viewModel.signInWithEmail(email, password, false)
                } else {
                    binding.loginPassword.error = resources.getString(R.string.helper_password_text)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Пожалуйста, введите правильный E-mail",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            it.hideKeyboard()
        }
    }

    private fun renderSignResult(appState: AppState) {
        if (appState is AppState.SuccessSignIn) {
            binding.loadingLayout.visibility = GONE
            when (appState.result) {
                SUCCESSFUL_SIGN -> {
                    Snackbar.make(binding.root, "Вход выполнен", Snackbar.LENGTH_SHORT).show()
                    removeFragment(SIGN_FRAGMENT)
                }
            }
        }
        if (appState is AppState.ErrorSignIn) {
            binding.loadingLayout.visibility = GONE
            when (appState.result) {
                USER_NOT_FOUND -> {
                    Snackbar.make(
                        binding.root,
                        "Пользователь с таким E-mail не зарегистрирован",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                ERROR_WRONG_PASSWORD -> {
                    Snackbar.make(binding.root, "Неверный пароль", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        if(appState is AppState.Loading){
            binding.loadingLayout.visibility = VISIBLE
        }
    }

    private fun removeFragment(name: String) {
        val fm = requireActivity().supportFragmentManager
        fm.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInWithEmailFragment()
    }
}