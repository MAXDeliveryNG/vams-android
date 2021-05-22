package ng.max.vams.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bnd: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_VAMS)
        super.onCreate(savedInstanceState)
        bnd = ActivityMainBinding.inflate(layoutInflater)
        val view = bnd.root
        setContentView(view)
        handleNavController()
    }

    private fun handleNavController(){
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val listener = NavController.OnDestinationChangedListener{ _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.appDialogFragment){
//                bnd.appToolbar.gone()
            }else{
//                bnd.appToolbar.show()
            }
        }
        navController.addOnDestinationChangedListener(listener)
    }

}