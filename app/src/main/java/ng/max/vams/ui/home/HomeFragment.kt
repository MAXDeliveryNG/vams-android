package ng.max.vams.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ng.max.vams.R
import ng.max.vams.ui.login.LoginViewModel

class HomeFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        loginViewModel.getLoggedInUser().observe(viewLifecycleOwner){ user->
            if (user == null) {
                navController.navigate(R.id.loginFragment)
            }
        }
    }
}