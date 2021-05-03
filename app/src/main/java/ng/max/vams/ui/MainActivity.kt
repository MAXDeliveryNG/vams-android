package ng.max.vams.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.databinding.ActivityMainBinding
import ng.max.vams.util.gone
import ng.max.vams.util.show

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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

}