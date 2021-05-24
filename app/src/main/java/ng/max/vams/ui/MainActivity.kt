package ng.max.vams.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_VAMS)
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        handleNavController()
    }

    private fun handleNavController(){
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val listener = NavController.OnDestinationChangedListener{ _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.appDialogFragment){
//                activityMainBinding.appToolbar.gone()
            }else{
//                activityMainBinding.appToolbar.show()
            }
        }
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}