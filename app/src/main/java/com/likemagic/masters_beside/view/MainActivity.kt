package com.likemagic.masters_beside.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ActivityMainBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.masters.CreateNewMasterFragment
import com.likemagic.masters_beside.view.masters.ListOfMastersFragment
import com.likemagic.masters_beside.view.signIn.SignFragment
import com.likemagic.masters_beside.view.signIn.SignUpWithPhoneFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.SignViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val accountBase = FirebaseAuth.getInstance()
    private val viewModel: SignViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        observeLiveData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_WITH_GOOGLE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val user = task.getResult(ApiException::class.java)
                if (user != null) {
                    updateNavMenuWithGoogle(user)
                    viewModel.signInWithGoogle(user.idToken!!, user)
                }
            } catch (e: ApiException) {
                Log.d("@@@", e.message!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkProvider()
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
                viewModel.signOut()
                getSignInClient().signOut()
                Snackbar.make(binding.root, "Выход выполнен", Snackbar.LENGTH_SHORT).show()
            }
        }
        navigateTo(CreateNewMasterFragment.newInstance(), LIST_OF_MASTERS_FRAGMENT)
    }

    private fun observeLiveData() {
        viewModel.getLiveData().observe(this) {
            if (it is AppState.SuccessSignIn) {
                if (it.result == SUCCESSFUL_SIGN) {
                    logInUser(it)
                    updateNavMenu(true)
                }
            } else if (it is AppState.Logout) {
                viewModel.signOut()
                logOutUser()
            } else if (it is AppState.SuccessSignInWithGoogle) {
                updateNavMenuWithGoogle(it.user)
                updateNavMenu(true)
            } else if(it is AppState.SuccessSignInWithPhone){
                updateNavMenu(true)
                updateNavMenuWithPhone(it.user)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionMyOrders -> {
                TODO()
            }
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
                navigateTo(AboutFragment.newInstance(), ABOUT_FRAGMENT)
            }
            R.id.actionSign -> {
                navigateTo(SignFragment.newInstance(), SIGN_FRAGMENT)
            }
        }
        binding.mainDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOutUser() {
        findViewById<TextView>(R.id.drawerUserText).text =
            resources.getString(R.string.drawer_guest)
        findViewById<TextView>(R.id.signOrRegText).visibility = VISIBLE
        findViewById<ImageView>(R.id.logOut).visibility = GONE
    }

    private fun logInUser(appState: AppState.SuccessSignIn) {
        findViewById<TextView>(R.id.drawerUserText).text = appState.name
        findViewById<TextView>(R.id.signOrRegText).visibility = GONE
        findViewById<ImageView>(R.id.logOut).visibility = VISIBLE
    }

    private fun updateNavMenu(flag: Boolean) {
        binding.navView.menu.apply {
            findItem(R.id.actionMyOrders).isVisible = flag
            findItem(R.id.actionSettings).isVisible = flag
            findItem(R.id.actionHelp).isVisible = flag
            findItem(R.id.actionSign).isVisible = !flag
        }
    }

    private fun updateNavMenuWithGoogle(user: GoogleSignInAccount) {
        findViewById<TextView>(R.id.drawerUserText).text = user.displayName
        findViewById<TextView>(R.id.signOrRegText).visibility = GONE
        findViewById<ImageView>(R.id.logOut).visibility = VISIBLE
    }

    private fun updateNavMenuWithPhone(user: FirebaseUser) {
        findViewById<TextView>(R.id.drawerUserText).text = user.phoneNumber
        findViewById<TextView>(R.id.signOrRegText).visibility = GONE
        findViewById<ImageView>(R.id.logOut).visibility = VISIBLE
    }

    private fun navigateTo(fragment: Fragment, name: String) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(name)
            .replace(R.id.mainContainer, fragment, name)
            .commit()
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun checkProvider(){
        val provider = accountBase.currentUser?.getIdToken(false)?.result?.signInProvider
        if(provider != null){
            if(provider == PROVIDER_EMAIL){
                val sp = getSharedPreferences(PASSWORD, MODE_PRIVATE)
                val password = sp.getString(PASSWORD, "")
                if(!password.isNullOrEmpty()){
                    viewModel.signInWithEmail(accountBase.currentUser!!.email!!, password!!)
                }
            }else if(provider == PROVIDER_GOOGLE){
                val user = GoogleSignIn.getLastSignedInAccount(this)
                if (user != null) {
                    viewModel.signInWithGoogle(user.idToken!!, user)
                }
            }else if (provider == PROVIDER_PHONE){

            }
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(LIST_OF_MASTERS_FRAGMENT)
        if(fragment != null){
            if (fragment.isVisible) {
                navigateTo(ListOfMastersFragment.newInstance(), LIST_OF_MASTERS_FRAGMENT)
            }
        }
        super.onBackPressed()
    }
}