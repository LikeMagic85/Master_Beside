package com.likemagic.masters_beside.view.masters

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentCreateNewMasterBinding
import com.likemagic.masters_beside.repository.*
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.SignViewModel

class CreateNewMasterFragment : Fragment() {

    private var _binding: FragmentCreateNewMasterBinding? = null
    private val binding: FragmentCreateNewMasterBinding
        get() = _binding!!

    private val viewModel: SignViewModel by activityViewModels()
    private var category = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewMasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        binding.newMasterContainer.visibility = GONE
        setUpFields()
        setUpUser()
        detectBackClick()
    }


    private fun setUpFields() {
        var profession = Profession()
        var city = City()
        val professionList = ProfessionGetter().getAllProfession()
        val profAdapter = ListOfProfessionAdapter()
        val cityList = CityGetter().getAllCities(requireContext())
        val cityAdapter = ListOfCityAdapter()

        binding.profRecycler.adapter = profAdapter
        initProfRecycler(profAdapter)
        profAdapter.onItemClick = {
            binding.masterProfessionInput.setText(it.name)
            binding.profRecycler.visibility = GONE
            binding.masterProfession.isErrorEnabled = false
            profession = it
        }

        binding.cityRecycler.adapter = cityAdapter
        initCityRecycler(cityAdapter)
        cityAdapter.onItemClick = {
            binding.masterCityInput.setText(it.name)
            binding.cityRecycler.visibility = GONE
            binding.masterCity.isErrorEnabled = false
            city = it
        }
        binding.newMasterSave.setOnClickListener {
            saveMaster(profession, city, cityList, professionList)
        }

    }

    private fun initProfRecycler(profAdapter: ListOfProfessionAdapter
    ) {
        binding.masterProfessionInput.addTextChangedListener {
            val professionName = binding.masterProfessionInput.text.toString()
            binding.profRecycler.visibility = VISIBLE
            ProfessionGetter().getProfessionByName(professionName){
               requireActivity().runOnUiThread{
                   profAdapter.setList(it)
               }
            }
        }
    }

    private fun initCityRecycler(cityAdapter: ListOfCityAdapter) {
        binding.masterCityInput.addTextChangedListener {
            binding.cityRecycler.visibility = VISIBLE
            val cityName = binding.masterCityInput.text.toString()
            CityGetter().getCityByName(cityName, requireContext()){
                requireActivity().runOnUiThread {
                    cityAdapter.setList(it)
                }
            }

        }
    }

    private fun saveMaster(profession: Profession, city: City, cityList: List<City>, professionList:List<Profession>) {
        val name = binding.masterNameInput.text.toString()
        val about = binding.masterAboutInput.text.toString()
        val contact = Contact()
        contact.email = arguments?.getString(REGISTER_EMAIL)!!
        contact.phone = binding.masterPhoneInput.text.toString()
        if (binding.masterProfessionInput.text.isNullOrEmpty()) {
            binding.masterProfession.error = resources.getString(R.string.no_empty)
        } else if (binding.masterCityInput.text.isNullOrEmpty()) {
            binding.masterCity.error = resources.getString(R.string.no_empty)
        } else if (binding.masterNameInput.text.isNullOrEmpty()) {
            binding.masterName.error = resources.getString(R.string.no_empty)
        } else if (binding.masterCity.isErrorEnabled) {
            Snackbar.make(binding.root, "Мы не знаем такого города", Snackbar.LENGTH_SHORT)
                .show()
        } else  {
            if(city in cityList ){
                if(profession in professionList){
                    createNewMaster(profession, city,name, about, contact)
                    requireActivity().findViewById<BottomNavigationItemView>(R.id.actionHome).isSelected = false
                    requireActivity().findViewById<BottomNavigationItemView>(R.id.actionProfile).isSelected = true
                    removeFragment(CREATE_NEW_MASTER_FRAGMENT, requireActivity())
                }else(Snackbar.make(binding.root, "Выберите профессию из списка", Snackbar.LENGTH_SHORT)
                    .show())
            }else(Snackbar.make(binding.root, "Выберите город из списка", Snackbar.LENGTH_SHORT)
                .show())
        }
        binding.root.hideKeyboard()
    }

    private fun createNewMaster(
        profession: Profession,
        city: City,
        name: String,
        about: String,
        contact: Contact,
    ) {
        val master = Master(profession, city, name, about, "0", contact, 0.0, false)
        viewModel.createNewMaster(master)
    }

    private fun detectBackClick() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                createNewMaster(Profession(),City(),"Мастер", "", Contact( email = arguments?.getString(
                    REGISTER_EMAIL)!!))
                true
            } else false
        }
    }

    private fun setUpUser() {
        binding.regBtn.setOnClickListener {
            if (binding.userChip.isChecked) {

            } else if (binding.masterChip.isChecked) {
                binding.choseContainer.animate().scaleX(0f).scaleY(0f).setListener(object : AnimatorListener{
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        binding.newMasterContainer.visibility = VISIBLE
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            } else {
                Snackbar.make(binding.root, "Пожалуйста, сделайте выбор", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(email:String):CreateNewMasterFragment{
            val fragment = CreateNewMasterFragment()
            val args = Bundle()
            args.putString(REGISTER_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }
}