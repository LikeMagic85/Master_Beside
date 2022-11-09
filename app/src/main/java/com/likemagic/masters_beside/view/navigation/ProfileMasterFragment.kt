package com.likemagic.masters_beside.view.navigation

import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.*
import com.likemagic.masters_beside.repository.*
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.masters.ListOfCityAdapter
import com.likemagic.masters_beside.view.masters.ListOfProfessionAdapter
import com.likemagic.masters_beside.view.navigation.dialogs.DialogFragment
import com.likemagic.masters_beside.view.signIn.LinkPhoneFragment
import com.likemagic.masters_beside.view.signIn.SignUpWithEmailFragment
import com.likemagic.masters_beside.viewModel.*


class ProfileMasterFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val mastersViewModel: MastersViewModel by activityViewModels()
    private val signViewModel: SignViewModel by activityViewModels()
    private val dialogsViewModel: DialogsViewModel by viewModels()
    private val user = FirebaseAuth.getInstance().currentUser
    private var myData = Master()
    private lateinit var resultLauncher:ActivityResultLauncher<Intent>
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
        changePhoto()
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
        val master = initMaster()
        mastersViewModel.getLiveData().observe(viewLifecycleOwner) {
            renderData(it)
        }
        mastersViewModel.getMaster(master!!, false)
        signViewModel.getLiveData().observe(viewLifecycleOwner) {
            if (it is AppState.SuccessPostEmail) {
                makeSnackBar(it.result)
                it.result = false
            }
        }
        dialogsViewModel.getLiveData().observe(viewLifecycleOwner){
            removeFragment(PROFILE_FRAGMENT, requireActivity())
            removeFragment(JOB_FRAGMENT, requireActivity())
            navigateToDialog((it as DialogState.DialogPage).dialog)
        }
    }

    private fun makeSnackBar(isSend: Boolean) {
        if (isSend) {
            Snackbar.make(binding.root, "Письмо успешно отправлено", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun initMaster(): Master? {
        val arguments = arguments
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(MASTER_ID, Master::class.java)
        } else {
            arguments?.getParcelable(MASTER_ID)
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = VISIBLE
            }
            is AppState.EmptyList -> {
                binding.loadingLayout.visibility = GONE
                createDialog(appState.isEmpty)
                appState.isEmpty = false
            }
            is AppState.MasterPage -> {
                mastersViewModel.getMyData()
                binding.loadingLayout.visibility = GONE
                setUpFields(appState.master, appState.isMy)
                checkProfession(appState.master)
                showContacts(appState.showContacts, appState.master, appState.isMy)
                confirmContacts(appState.master, appState.isMy)
                addDeleteToFavorite(appState.master)
                goToDialogWithMaster(appState.master)
                if (appState.isMy) {
                    setUpEditFields(appState.master)
                    setUpSendingButton(appState.master)
                    openGalleryForImage()
                    binding.deleteAccount.setOnClickListener {
                        createRemoveDialog(appState.master)
                    }
                }
                if (appState.editState){
                    signViewModel.uploadImage(prepareImage(bitmap), appState.master)
                }
            }
            is AppState.UpdateMaster -> {
                binding.loadingLayout.visibility = GONE
                if(appState.isNeed){
                    setUpFields(appState.master, true)
                    checkProfession(appState.master)
                }else{
                    myData = appState.master
                }
            }
            is AppState.NotRegUser -> {
                binding.loadingLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.progressBar.visibility = GONE
                makeActionSnackBar(appState.isNotReg)
                appState.isNotReg = false
            }
            is AppState.MyData ->{
                binding.loadingLayout.visibility = GONE
                myData = appState.master
            }
            else -> {}
        }
    }

    private fun makeActionSnackBar(isNotReg: Boolean) {
        if (isNotReg) {
            Snackbar.make(binding.root, "Необходима регистрация", Snackbar.LENGTH_LONG)
                .setAction(requireContext().getString(R.string.sign_in_title)) {
                    navigateTo(
                        SignUpWithEmailFragment.newInstance(),
                        SIGN_UP_WITH_EMAIL_FRAGMENT,
                        requireActivity()
                    )
                }.show()
        }
    }

    private fun checkOwner(isMy: Boolean) {
        if (isMy) {
            binding.apply {
                editProfile.visibility = VISIBLE
                deleteAccount.visibility = VISIBLE
                changePhoto.visibility = VISIBLE
                binding.sendMessageBtn.visibility = GONE
            }
        } else {
            binding.apply {
                editProfile.visibility = GONE
                deleteAccount.visibility = GONE
                changePhoto.visibility = GONE
                binding.sendMessageBtn.visibility = VISIBLE
            }
        }
    }

    private  fun checkProfession(master: Master){
        if(master.profession.name == NOT_MASTER){
            binding.apply {
                profileProfession.text = "Заказчик"
                ratingContainer.visibility = GONE
                experience.visibility = GONE
                experienceTitle.visibility = GONE
                aboutText.visibility = GONE
                aboutTitle.visibility = GONE
                costTitle.visibility = GONE
                costText.visibility = GONE
                reviewsTitle.visibility = GONE
                reviewsRecycler.visibility = GONE
                inFavBtn.visibility = GONE
            }
        }
    }

    private fun setUpFields(master: Master, isMy: Boolean) {
        if(isMy){binding.inFavBtn.visibility = GONE}
        else{binding.inFavBtn.visibility = VISIBLE}
        checkOwner(isMy)
        binding.apply {
            setUpProfileImage(master)
            setUpMainInformation(master)
        }
    }

    private fun setUpMainInformation(master: Master) {
        binding.apply {
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
            aboutText.text = master.about
            costText.text = "от ${master.cost} BYN"
        }
    }

    private fun setUpProfileImage(master: Master) {
        binding.apply {
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
    }

    private fun confirmContacts(master: Master, isMy: Boolean) {
        binding.contactsContainer.apply {
            if (isMy) {
                isNotConfirmPhone.visibility = GONE
                isNotConfirmEmail.visibility = GONE
                if (master.isPhoneChecked) {
                    confirmPhone.visibility = VISIBLE
                    notConfirmPhone.visibility = GONE
                    isCheckedPhone.visibility = VISIBLE
                    notCheckedPhone.visibility = GONE

                } else {
                    confirmPhone.visibility = GONE
                    notConfirmPhone.visibility = VISIBLE
                    isCheckedPhone.visibility = GONE
                    notCheckedPhone.visibility = VISIBLE
                }
                if (master.isEmailChecked) {
                    confirmEmail.visibility = VISIBLE
                    notConfirmEmail.visibility = GONE
                    isCheckedEmail.visibility = VISIBLE
                    notCheckedEmail.visibility = GONE

                } else {
                    confirmEmail.visibility = GONE
                    notConfirmEmail.visibility = VISIBLE
                    isCheckedEmail.visibility = GONE
                    notCheckedEmail.visibility = VISIBLE
                }
            } else {
                notConfirmPhone.visibility = GONE
                notConfirmEmail.visibility = GONE
                if (master.isPhoneChecked) {
                    confirmPhone.visibility = VISIBLE
                    isNotConfirmPhone.visibility = GONE
                    isCheckedPhone.visibility = VISIBLE
                    notCheckedPhone.visibility = GONE

                } else {
                    confirmPhone.visibility = GONE
                    isNotConfirmPhone.visibility = VISIBLE
                    isCheckedPhone.visibility = GONE
                    notCheckedPhone.visibility = VISIBLE
                }
                if (master.isEmailChecked) {
                    confirmEmail.visibility = VISIBLE
                    isNotConfirmEmail.visibility = GONE
                    isCheckedEmail.visibility = VISIBLE
                    notCheckedEmail.visibility = GONE

                } else {
                    confirmEmail.visibility = GONE
                    isNotConfirmEmail.visibility = VISIBLE
                    isCheckedEmail.visibility = GONE
                    notCheckedEmail.visibility = VISIBLE
                }
            }
        }
    }

    private fun setUpEditFields(master: Master) {
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
        if(master.profession.name == NOT_MASTER){
            view.apply {
                profEdit.visibility = GONE
                costEdit.visibility = GONE
                experienceTitle.visibility = GONE
                expEditContainer.visibility = GONE
                expEdit.visibility = GONE
                aboutTitle.visibility = GONE
                editAbout.visibility = GONE
            }
        }
        var newCity = City()
        var newProfession = Profession()
        val profAdapter = ListOfProfessionAdapter()
        val cityAdapter = ListOfCityAdapter()
        cityAdapter.onItemClick = {
            view.cityEdit.setText(it.name)
            view.cityRecycler.visibility = GONE
            newCity = it
        }
        profAdapter.onItemClick = {
            view.profEdit.setText(it.name)
            view.profRecycler.visibility = GONE
            newProfession = it
        }
        view.apply {
            if (master.isPhoneChecked) {
                phoneContainer.visibility = GONE
            }
            if (master.isEmailChecked) {
                mailContainer.visibility = GONE
            }
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
            costEdit.setText(master.cost)
            initCityRecycler(cityAdapter)
            initProfRecycler(profAdapter)
            saveBtn.setOnClickListener {
                if (newProfession.name.isEmpty()) newProfession = master.profession
                if (newCity.name.isEmpty()) newCity = master.city
                var newCost = costEdit.text.toString()
                if (newCost.isEmpty()) newCost = master.cost
                val newContact = Contact(
                    phoneEdit.text.toString(),
                    mailEdit.text.toString(),
                    viberEdit.text.toString(),
                    telegramEdit.text.toString()
                )
                if (newProfession in ProfessionGetter().getAllProfession() || newProfession.name == NOT_MASTER) {
                    if (newCity in CityGetter().getAllCities(requireContext())) {
                        val newMaster = master.copy()
                        newMaster.apply {
                            profession = newProfession
                            city = newCity
                            name = nameEdit.text.toString()
                            about = editAbout.text.toString()
                            cost = newCost
                            if (newContact.phone != contact.phone) {
                                newMaster.isPhoneChecked = false
                            }
                            if (newContact.email != contact.email) {
                                isEmailChecked = false
                            }
                            contact = newContact
                            age = ageEdit.text.toString()
                            experience = expEdit.text.toString()
                        }
                        mastersViewModel.updateMaster(newMaster, true)
                        mastersViewModel.getMaster(newMaster, false)
                        dialog.cancel()
                    } else {
                        Toast.makeText(requireContext(),"Укажите город из списка", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText( requireContext(),"Выберите профессию из списка",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun EditProfileDialogBinding.initProfRecycler(profAdapter: ListOfProfessionAdapter) {
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

    private fun EditProfileDialogBinding.initCityRecycler(cityAdapter: ListOfCityAdapter) {
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

    private fun showContacts(isContactOpen: Boolean, master: Master, isMy: Boolean) {
        if (isContactOpen) {
            setUpContacts(master, isMy)
        } else {
            binding.contactsContainer.visibleContacts.visibility = GONE
            binding.contactsContainer.disableContacts.visibility = VISIBLE
        }
    }

    private fun setUpContacts(master: Master, isMy: Boolean) {
        binding.apply {
            if (isMy) {
                binding.contactsContainer.visibleContacts.visibility = VISIBLE
                binding.contactsContainer.disableContacts.visibility = GONE
                if (master.contact.phone.isEmpty()) {
                    contactsContainer.phoneNumber.text = "указать"
                } else {
                    contactsContainer.phoneNumber.text = master.contact.phone
                }
            } else {
                if (master.contact.phone.isEmpty()) {
                    contactsContainer.phoneNumber.text = "не указан"
                } else {
                    if (master.isPhoneChecked) {
                        contactsContainer.visibleContacts.visibility = VISIBLE
                        contactsContainer.disableContacts.visibility = GONE
                        contactsContainer.disableContacts.text =
                            requireContext().getString(R.string.is_email_ver_text)
                        contactsContainer.phoneNumber.text = master.contact.phone
                    } else {
                        contactsContainer.visibleContacts.visibility = GONE
                        contactsContainer.disableContacts.visibility = VISIBLE
                        contactsContainer.disableContacts.text =
                            requireContext().getString(R.string.contacts_is_shadow)
                    }
                }
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
    }

    private fun setUpSendingButton(master: Master) {
        binding.contactsContainer.apply {
            notConfirmPhone.setOnClickListener {
                val phone = binding.contactsContainer.phoneNumber.text.toString()
                if (isValidPhone(phone)) {
                    createLinkDialog(phone, master)
                } else {
                    Snackbar.make(binding.root, "Неверный формат телефона", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
            notConfirmEmail.setOnClickListener {
                signViewModel.sendVerificationEmail(user!!)
            }
        }
    }

    private fun openGalleryForImage() {
        AndroidPermissionChecker(requireActivity()).checkPermissions()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        binding.changePhoto.setOnClickListener {
            resultLauncher.launch(intent)
        }
    }

    private fun changePhoto() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val imageView = ImageView(requireContext())
                    imageView.setImageURI(data?.data)
                    bitmap = (imageView.drawable as BitmapDrawable).bitmap
                    mastersViewModel.getMasterById(user?.uid!!) {
                        mastersViewModel.getMaster(it, true)
                    }
                }
            }
    }

    private fun addDeleteToFavorite(master: Master){
        if(master.uid in myData.favorite){
            binding.inFavBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite))
        }else {binding.inFavBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_favorite))}
        binding.inFavBtn.setOnClickListener {
            if(master.uid in myData.favorite){
                myData.favorite.remove(master.uid!!)
                mastersViewModel.updateMaster(myData, false)
                binding.inFavBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_favorite))
            }else{
                myData.favorite.add(master.uid!!)
                mastersViewModel.updateMaster(myData, false)
                binding.inFavBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite))
            }
        }
    }

    private fun createDialog(isEmpty: Boolean) {
        if (isEmpty) {
            binding.root.removeAllViews()
            val builder = AlertDialog.Builder(requireContext())
            val alertBinding = NotRegisterDialogBinding.inflate(requireActivity().layoutInflater)
            builder.setView(alertBinding.root)
            val dialog = builder.show()
            alertBinding.alertBtn.setOnClickListener {
                dialog.dismiss()
                requireActivity().findViewById<DrawerLayout>(R.id.mainDrawer)
                    .openDrawer(GravityCompat.START)
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId =
                    R.id.actionHome
            }
        }
    }

    private fun createRemoveDialog(master: Master) {
        val builder = AlertDialog.Builder(requireContext())
        val alertBinding = DeleteDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(alertBinding.root)
        val dialog = builder.show()
        alertBinding.alert.animate().scaleX(0.5f).scaleY(0.5f).setDuration(700)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    alertBinding.alert.animate().scaleX(1f).scaleY(1f).duration = 700
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        alertBinding.alertBtn.setOnClickListener {
            mastersViewModel.deleteMaster(master)
            dialog.dismiss()
        }
    }

    private fun createLinkDialog(phone: String, master: Master) {
        val builder = AlertDialog.Builder(requireContext())
        val alertBinding = LinkPhoneDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(alertBinding.root)
        alertBinding.mail.animate().x(1000f).setDuration(1500)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    alertBinding.mail.animate().x(50f).duration = 1500
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        val dialog = builder.show()
        alertBinding.alertBtn.setOnClickListener {
            navigateToAndAdd(LinkPhoneFragment.newInstance(phone), LINK_PHONE_FRAGMENT, requireActivity())
            mastersViewModel.confirmPhone(master)
            dialog.dismiss()
        }
    }

    private fun goToDialogWithMaster(master: Master){
        val members = "${myData.uid}${master.uid}"
        binding.sendMessageBtn.setOnClickListener {
            dialogsViewModel.findDialog(members, myData, master)
        }
    }

    private fun navigateToDialog(dialog:Dialog){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(DIALOG_FRAGMENT)
            .add(R.id.mainContainer, DialogFragment.newInstance(dialog))
            .commit()
    }

    companion object {
        fun newInstance(master: Master): ProfileMasterFragment {
            val args = Bundle()
            args.putParcelable(MASTER_ID, master)
            val fragment = ProfileMasterFragment()
            fragment.arguments = args
            return fragment
        }
    }
}