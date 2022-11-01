package com.likemagic.masters_beside.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import coil.dispose
import coil.load
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ActivityMainBinding
import com.likemagic.masters_beside.databinding.FragmentFavoriteListBinding
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.masters.ListOfMastersFragment
import com.likemagic.masters_beside.view.navigation.AboutFragment
import com.likemagic.masters_beside.view.navigation.FavoriteListFragment
import com.likemagic.masters_beside.view.navigation.ProfileMasterFragment
import com.likemagic.masters_beside.view.signIn.SignFragment
import com.likemagic.masters_beside.view.signIn.SignUpWithPhoneFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel
import com.likemagic.masters_beside.viewModel.SignViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val accountBase = FirebaseAuth.getInstance()
    private val signViewModel: SignViewModel by viewModels()
    private val mastersViewModel: MastersViewModel by viewModels()
    private lateinit var bitmap:Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        observeLiveData()
        setupBottomNav()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            try{
                checkProvider()
            }catch (e:Throwable){return@launch}
        }.start()
    }

    private fun init() {
        val toggle = ActionBarDrawerToggle(
            this, binding.mainDrawer, binding.mainContent.toolbar,
            R.string.open,
            R.string.close
        )
        binding.apply {
            mainDrawer.addDrawerListener(toggle)
            navView.setNavigationItemSelectedListener(this@MainActivity)
        }
        val header = binding.navView.getHeaderView(0)
        header.apply {
            findViewById<ImageView>(R.id.logOut).setOnClickListener {
                updateNavMenu(false)
                binding.mainDrawer.closeDrawer(GravityCompat.START)
                signViewModel.signOut()
                getSignInClient().signOut()
                Snackbar.make(binding.root, "Выход выполнен", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.mainContent.toolbarImg.setOnClickListener {
            binding.mainDrawer.openDrawer(GravityCompat.START)
        }
    }

    private suspend fun checkProvider(){
        val provider = accountBase.currentUser?.getIdToken(false)?.await()?.signInProvider
        if(provider != null){
            if(provider == PROVIDER_EMAIL){
                val sp = getSharedPreferences(PASSWORD, MODE_PRIVATE)
                val password = sp.getString(PASSWORD, "")
                if(!password.isNullOrEmpty()){
                    signViewModel.signInWithEmail(accountBase.currentUser!!.email!!, password, false)
                }
            }else if(provider == PROVIDER_GOOGLE){
                val user = GoogleSignIn.getLastSignedInAccount(this)
                if (user != null) {
                    signViewModel.signInWithGoogle(user.idToken!!, user)
                }
            }else if (provider == PROVIDER_PHONE){
                val email = accountBase.currentUser?.email
                val sp = getSharedPreferences(PASSWORD, MODE_PRIVATE)
                val password = sp.getString(PASSWORD, "")
                if (!email.isNullOrEmpty() && password != null){
                    signViewModel.signInWithEmail(email, password, false)
                }else{
                    navigateTo(SignUpWithPhoneFragment.newInstance(), SIGN_UP_WITH_PHONE_FRAGMENT, this)
                }
            }
        }else{
            navigateTo(SignFragment.newInstance(), SIGN_FRAGMENT, this)
            // TODO: Решить проблему с ПУСТЫМ СПИСКОМ
        }
    }

    private fun observeLiveData() {
        signViewModel.getLiveData().observe(this) {
            if (it is AppState.SuccessSignIn) {
                if (it.result == SUCCESSFUL_SIGN) {
                    logInUser()
                    updateNavMenu(true)
                    mastersViewModel.getMyData()
                }
            } else if (it is AppState.Logout) {
                signViewModel.signOut()
                logOutUser()
            } else if (it is AppState.SuccessSignInWithGoogle) {
                logInUser()
                updateNavMenu(true)
                mastersViewModel.getMyData()
            } else if(it is AppState.SuccessSignInWithPhone){
                logInUser()
                updateNavMenu(true)
                mastersViewModel.getMyData()
            } else if (it is AppState.NewMaster){
                mastersViewModel.getMasterById(accountBase.uid!!){result->
                    navigateTo(ProfileMasterFragment.newInstance(result),PROFILE_FRAGMENT, this)
                    binding.mainContent.bottomNav.menu.findItem(
                        R.id.actionProfile).isChecked = true
                    logInUser()
                    updateNavMenu(true)
                }
            }else if (it is AppState.UploadImage){
                mastersViewModel.updateMaster(it.master!!,true)
            }
        }
        mastersViewModel.getLiveData().observe(this){
            if (it is AppState.MasterPage){
                if (it.editState){
                    signViewModel.uploadImage(prepareImage(bitmap), it.master, null)
                }
            }else if ( it is AppState.UpdateMaster){
                mastersViewModel.getMasterById(accountBase.uid!!,){result->
                    updateNav(result)
                }
            }else if (it is AppState.DeleteMaster){
                accountBase.currentUser?.delete()
                updateNavMenu(false)
                logOutUser()
                binding.mainContent.bottomNav.selectedItemId = R.id.actionHome
            }else if (it is AppState.MyData){
                updateNav(it.master)
                mastersViewModel.addToOnline(it.master)
            }
        }
    }

    private fun logInUser() {
        try{
            findViewById<TextView>(R.id.signOrRegText).visibility = INVISIBLE
        }catch (e:Throwable){return}
        findViewById<ImageView>(R.id.logOut).visibility = VISIBLE
    }

    private fun updateNavMenu(flag: Boolean) {
        binding.navView.menu.apply {
            findItem(R.id.actionSettings).isVisible = flag
            findItem(R.id.actionHelp).isVisible = flag
            findItem(R.id.actionSign).isVisible = !flag
        }
    }

    private fun updateNav(master:Master){
        if(master.uriImage.isNotEmpty()){
            findViewById<CircleImageView>(R.id.userPhoto).load(master.uriImage)
            binding.mainContent.toolbarImg.load(master.uriImage)
        }
        findViewById<TextView>(R.id.drawerUserText).text = master.name
    }

    private fun setupBottomNav(){
        binding.mainContent.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.actionHome -> {
                    navigateTo(ListOfMastersFragment.newInstance(), LIST_OF_MASTERS_FRAGMENT, this)
                    true
                }
                R.id.actionOrders -> {
                    // TODO:
                    true
                }
                R.id.actionMessage -> {
                    // TODO:
                    true
                }
                R.id.actionFavorite -> {
                    navigateTo(FavoriteListFragment.newInstance(), FAVORITE_LIST_FRAGMENT, this)
                    true
                }
                R.id.actionProfile -> {
                    if(accountBase.uid != null){
                        binding.mainContent.loadingLayout.visibility = VISIBLE
                        mastersViewModel.getMasterById(accountBase.uid!!,){result->
                            navigateTo(ProfileMasterFragment.newInstance(result), PROFILE_FRAGMENT, this)
                            binding.mainContent.loadingLayout.visibility = GONE
                        }
                    }else{
                        navigateTo(ProfileMasterFragment.newInstance(Master(uid="")), PROFILE_FRAGMENT, this)
                    }
                    true
                }
                else -> {true}
            }
        }
        binding.mainContent.bottomNav.selectedItemId = R.id.actionHome
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                TODO()
            }
            R.id.actionNews -> {
                TODO()
            }
            R.id.actionHelp -> {
                TODO()
            }
            R.id.actionAbout -> {
                navigateTo(AboutFragment.newInstance(), ABOUT_FRAGMENT, this)
            }
            R.id.actionSign -> {
               navigateTo(SignFragment.newInstance(), SIGN_FRAGMENT, this)
            }
        }
        binding.mainDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOutUser() {
        findViewById<TextView>(R.id.drawerUserText).text = resources.getString(R.string.drawer_guest)
        findViewById<TextView>(R.id.signOrRegText).visibility = VISIBLE
        findViewById<ImageView>(R.id.logOut).visibility = INVISIBLE
        findViewById<ImageView>(R.id.userPhoto).dispose()
        findViewById<ImageView>(R.id.userPhoto).setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_account_black))
        binding.mainContent.toolbarImg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_account))
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(LIST_OF_MASTERS_FRAGMENT)
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1){
            (fragment as IOnBackPressed).onBackPressed()
            return;
        }else{
            setToolbarVisibility(this, true)
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_WITH_GOOGLE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val user = task.getResult(ApiException::class.java)
                if (user != null) {
                    signViewModel.signInWithGoogle(user.idToken!!, user)
                }
            } catch (e: ApiException) {
                Log.d("@@@", e.message!!)
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == ADD_IMAGE_REQUEST_CODE){
            val imageView = ImageView(this)
            imageView.setImageURI(data?.data)
            bitmap = (imageView.drawable as BitmapDrawable).bitmap
            if(accountBase.uid != null){
                mastersViewModel.getMasterById(accountBase.uid!! ){
                    mastersViewModel.getMaster(it, true)
                }
            }

        }
    }
}