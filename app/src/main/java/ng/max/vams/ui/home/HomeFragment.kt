package ng.max.vams.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.adapter.AssetAdapter
import ng.max.vams.databinding.HomeFragmentBinding
import ng.max.vams.ui.login.LoginViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var bnd : HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = HomeFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        loginViewModel.getLoggedInUser().observe(viewLifecycleOwner){user ->
            if (user == null){
                findNavController().navigate(R.id.loginFragment)
            }else{
                homeViewModel.actionGetAssetReasons()
                homeViewModel.actionGetLocations()
                homeViewModel.actionGetVehicleTypes()
            }
        }
    }

    private fun setupViews(){

        bnd.fab.setOnClickListener {
            findNavController().navigate(R.id.movementTypeDialogFragment)
        }
        bnd.viewPager.adapter = AssetAdapter(this)

        TabLayoutMediator(bnd.tabs, bnd.viewPager){ tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Vehicles"
                }
                1 -> {
                    tab.text = "Spare Parts"
                }
                else -> {
                    tab.text = "Trackers"
                }
            }
        }.attach()
    }

}