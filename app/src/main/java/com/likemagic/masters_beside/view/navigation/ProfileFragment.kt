package com.likemagic.masters_beside.view.navigation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.EditProfileDialogBinding
import com.likemagic.masters_beside.databinding.FragmentProfileBinding
import com.likemagic.masters_beside.databinding.NotRegisterDialogBinding
import com.likemagic.masters_beside.repository.*
import com.likemagic.masters_beside.utils.ADD_IMAGE_REQUEST_CODE
import com.likemagic.masters_beside.utils.AndroidPermissionChecker
import com.likemagic.masters_beside.utils.MASTER_ID
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.view.masters.ListOfCityAdapter
import com.likemagic.masters_beside.view.masters.ListOfProfessionAdapter
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val mastersViewModel: MastersViewModel by activityViewModels()

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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        val arguments = arguments
        val uid = arguments?.getString(MASTER_ID)
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        mastersViewModel.getMaster(uid!!,false)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = VISIBLE
            }
            is AppState.MyProfile -> {
                binding.loadingLayout.visibility = GONE
                setUpFields(appState.master, appState.isMy)
                setUpEditFields(appState.master)
                binding.sendMessageBtn.visibility = GONE
                binding.changePhoto.setOnClickListener {
                    openGalleryForImage()
                }
                binding.deleteAccount.setOnClickListener {
                    createRemoveDialog(appState.master)
                }
            }
            is AppState.EmptyList -> {
                binding.loadingLayout.visibility = GONE
                createDialog()
            }
            is AppState.MasterPage -> {
                binding.loadingLayout.visibility = GONE
                setUpFields(appState.master, appState.isMy)
                showContacts(appState.isEmailVer)
            }
            is AppState.UpdateMaster -> {
                binding.loadingLayout.visibility = GONE
                setUpFields(appState.master, true)
            }
            else -> {}
        }
    }

    private fun setUpFields(master: Master, isMy: Boolean){
        binding.apply {
            setUpProfileImage(master)
            setUpMainInformation(master)
        }
        checkOwner(isMy)
    }

    private fun checkOwner(isMy: Boolean) {
        if (isMy) {
            binding.apply {
                editProfile.visibility = VISIBLE
                deleteAccount.visibility = VISIBLE
                changePhoto.visibility = VISIBLE
            }
        }
    }

    private fun FragmentProfileBinding.setUpMainInformation(
        master: Master
    ) {
        profileName.text = master.name
        profileProfession.text = master.profession.name
        mainInfCityText.text = master.city.name
        gradleProfile.text = master.rating.toString()
        countReviewsProfile.text = master.reviews.size.toString()
        if (master.age.isNotEmpty()) {
            mainInfAgeNum.text = master.age
        } else {
            mainInfAgeNum.text = "не указан"
        }
        if (master.experience.isEmpty()) {
            experience.text = "не указан"
        } else {
            experience.text = master.experience
        }
        setUpContacts(master)
        aboutText.text = master.about
        costText.text = "от ${master.cost} BYN"
    }

    private fun FragmentProfileBinding.setUpProfileImage(master: Master) {
        if (master.uriImage.isNotEmpty()) {
            profileImage.load(master.uriImage)
        } else {
            profileImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_account
                )
            )
        }
    }

    private fun FragmentProfileBinding.setUpContacts(master: Master) {
        if (master.contact.phone.isEmpty()) {
            contactsContainer.phoneNumber.text = "не указан"
        } else {
            contactsContainer.phoneNumber.text = master.contact.phone
        }
        if (master.contact.vider.isEmpty()) {
            contactsContainer.viberNumber.text = "не указан"
        } else {
            contactsContainer.viberNumber.text = master.contact.vider
        }
        if (master.contact.email.isEmpty()) {
            contactsContainer.emailText.text = "не указан"
        } else {
            contactsContainer.emailText.text = master.contact.email
        }
        if (master.contact.telegram.isEmpty()) {
            contactsContainer.telegramNumber.text = "не указан"
        } else {
            contactsContainer.telegramNumber.text = master.contact.telegram
        }
    }

    private fun setUpEditFields(master: Master){
        binding.apply {
            editProfile.setOnClickListener {
                createEditDialog(master)
            }
        }
    }

    private fun createEditDialog(master: Master) {
        val builder = AlertDialog.Builder(requireContext())
        val view = EditProfileDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(view.root)
        val dialog = builder.show()
        var city = City()
        var profession = Profession()
        val profAdapter = ListOfProfessionAdapter()
        val cityAdapter = ListOfCityAdapter()
        cityAdapter.onItemClick = {
           view.cityEdit.setText(it.name)
            view.cityRecycler.visibility = GONE
            city = it
        }
        profAdapter.onItemClick = {
            view.profEdit.setText(it.name)
            view.profRecycler.visibility = GONE
            profession = it
        }
        view.apply {
            profEdit.setText(master.profession.name)
            nameEdit.setText(master.name)
            cityEdit.setText(master.city.name)
            ageEdit.setText(master.age)
            expEdit.setText(master.experience)
            phoneEdit.setText(master.contact.phone)
            mailEdit.setText(master.contact.email)
            viberEdit.setText(master.contact.vider)
            telegramEdit.setText(master.contact.telegram)
            editAbout.setText(master.about)
            initCityRecycler(cityAdapter)
            initProfRecycler(profAdapter)
            saveBtn.setOnClickListener {
                if(profession.name.isEmpty())profession = master.profession
                if (city.name.isEmpty())city = master.city
                var cost = costEdit.text.toString()
                if(cost.isEmpty()) cost = master.cost
                val contact = Contact(phoneEdit.text.toString(), mailEdit.text.toString(), viberEdit.text.toString(), telegramEdit.text.toString() )
                if(profession in ProfessionGetter().getAllProfession()){
                    if(city in CityGetter().getAllCities(requireContext())){
                        val newMaster = Master(profession,city, nameEdit.text.toString(),editAbout.text.toString(),cost, contact, master.rating, master.vipStatus,master.key, master.uid, master.reviews, ageEdit.text.toString(), master.uriImage, expEdit.text.toString())
                        mastersViewModel.updateMaster(newMaster)
                        mastersViewModel.getMaster(arguments?.getString(MASTER_ID)!!, false)
                        dialog.cancel()
                    }else{
                        Toast.makeText(requireContext(), "Укажите город из списка", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "Выберите профессию из списка", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun EditProfileDialogBinding.initProfRecycler(
        profAdapter: ListOfProfessionAdapter
    ) {
        profRecycler.adapter = profAdapter
        profEdit.addTextChangedListener {
            val professionName = profEdit.text.toString()
            profRecycler.visibility = VISIBLE
            ProfessionGetter().getProfessionByName(professionName) {
                requireActivity().runOnUiThread {
                    profAdapter.setList(it)
                }
            }
        }
    }

    private fun EditProfileDialogBinding.initCityRecycler(
        cityAdapter: ListOfCityAdapter
    ) {
        cityRecycler.adapter = cityAdapter
        cityEdit.addTextChangedListener {
            cityRecycler.visibility = VISIBLE
            val cityName = cityEdit.text.toString()
            CityGetter().getCityByName(cityName, requireContext()) {
                requireActivity().runOnUiThread {
                    cityAdapter.setList(it)
                }
            }

        }
    }

    private fun showContacts(isEmailVer:Boolean){
        if(isEmailVer){
            binding.contactsContainer.visibleContacts.visibility = VISIBLE
        }else{
            binding.contactsContainer.visibleContacts.visibility = GONE
            binding.contactsContainer.disableContacts.visibility = VISIBLE
        }
    }

    private fun openGalleryForImage() {
        AndroidPermissionChecker(requireActivity()).checkPermissions()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        requireActivity().startActivityForResult(intent, ADD_IMAGE_REQUEST_CODE)
    }

    private fun createDialog(){
            val builder = AlertDialog.Builder(requireContext())
            val alertBinding = NotRegisterDialogBinding.inflate(requireActivity().layoutInflater)
            builder.setView(alertBinding.root)
            val dialog = builder.show()
            alertBinding.alertBtn.setOnClickListener {
                requireActivity().findViewById<DrawerLayout>(R.id.mainDrawer).openDrawer(GravityCompat.START)
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.actionHome
                dialog.dismiss()
        }
    }

    private fun createRemoveDialog(master: Master){
        mastersViewModel.deleteMaster(master)
    }

    companion object {
        fun newInstance(uid:String):ProfileFragment{
            val args = Bundle()
            args.putString(MASTER_ID, uid)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}