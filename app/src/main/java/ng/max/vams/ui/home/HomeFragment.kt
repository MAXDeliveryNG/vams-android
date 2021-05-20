package ng.max.vams.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.manager.UserManager
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.HomeFragmentBinding
import ng.max.vams.ui.login.LoginViewModel
import ng.max.vams.util.Helper
import ng.max.vams.util.showDialog

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
        loginViewModel.getLoggedInUser().observe(viewLifecycleOwner){user ->
            if (user == null){
                findNavController().navigate(R.id.loginFragment)
            }else{
                setupViews()
                setupViewModel()
            }
        }
    }

    private fun setupViews(){
        UserManager.getUser()?.let { user->
            if (user.photo != null){
                bnd.profileIcon.load(user.photo){
                    placeholder(R.drawable.ic_icon_placeholder)
                        .transformations(CircleCropTransformation())
                }
            }else{
                bnd.profileIcon.load(R.drawable.ic_icon_placeholder){
                        transformations(CircleCropTransformation())
                }
            }

        }

        bnd.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        bnd.fab.setOnClickListener {
            findNavController().navigate(R.id.movementTypeDialogFragment)
        }
        val date = Helper.getFormattedDate()
        bnd.entryCard.setDate(date)
        bnd.exitCard.setDate(date)

        bnd.entryCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                    "entry",
                    "Vehicle"
            )
            findNavController().navigate(action)
        }

        bnd.exitCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                    "exit",
                    "Vehicle"
            )
            findNavController().navigate(action)
        }
//        bnd.viewPager.adapter = AssetAdapter(this)
//
//        TabLayoutMediator(bnd.tabs, bnd.viewPager){ tab, position ->
//            when (position) {
//                0 -> {
//                    tab.text = "Vehicles"
//                }
//                1 -> {
//                    tab.text = "Spare Parts"
//                }
//                else -> {
//                    tab.text = "Trackers"
//                }
//            }
//        }.attach()
    }

    private fun setupViewModel(){
        homeViewModel.getMovementStatResponse.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Error -> {
                    showDialog("Error", result.message)
                }
                is Result.Loading -> {}
                is Result.Success -> {
                    bnd.entryCard.setCount(result.value.totalEntry)
                    bnd.exitCard.setCount(result.value.totalExit)
                }
            }
        }
        homeViewModel.actionGetMovementStat()
        homeViewModel.actionGetAssetReasons()
        homeViewModel.actionGetLocations()
        homeViewModel.actionGetVehicleTypes()
    }

}