package com.likemagic.masters_beside.view

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ActivityMainBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.masters.ListOfMastersFragment
import com.likemagic.masters_beside.view.signIn.SignFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MainViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val accountBase = FirebaseAuth.getInstance()
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        observeLiveData()
    }

    override fun onStart() {
        super.onStart()
        if (accountBase.currentUser != null) {
            val sp = getSharedPreferences(PASSWORD, MODE_PRIVATE)
            val password = sp.getString(PASSWORD, "")
            viewModel.signInWithEmail(accountBase.currentUser!!.email!!, password!!)
        }
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
        toggle.syncState()
        val header = binding.navView.getHeaderView(0)
        header.apply {
            findViewById<ImageView>(R.id.logOut).setOnClickListener {
                updateNavMenu(false)
                binding.mainDrawer.closeDrawer(GravityCompat.START)
                viewModel.signOut()
                Snackbar.make(binding.root, "Выход выполнен", Snackbar.LENGTH_SHORT).show()
            }
        }
        navigateTo(ListOfMastersFragment.newInstance(), LIST_OF_MASTERS_FRAGMENT)
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
                TODO()
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
            findItem(R.id.actionSign).isVisible = !flag
        }
    }

    private fun navigateTo(fragment: Fragment, name: String) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(name)
            .replace(R.id.mainContainer, fragment, name)
            .commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(LIST_OF_MASTERS_FRAGMENT)
        if(fragment!!.isDetached){
            navigateTo(ListOfMastersFragment.newInstance(), LIST_OF_MASTERS_FRAGMENT)
        }
        super.onBackPressed()
    }
}