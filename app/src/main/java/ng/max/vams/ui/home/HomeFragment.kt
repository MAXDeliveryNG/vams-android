package ng.max.vams.ui.home

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.remote.response.User
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
    private var user: User? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = HomeFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        loginViewModel.getLoggedInUser().observe(viewLifecycleOwner){ _user ->
            if (_user == null){
                navController.navigate(R.id.loginFragment)
            }else{
                user = _user
                setupViews()
                setupViewModel()
            }
        }
    }

    private fun setupViews(){
        user?.let { user->
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
            displayPopup()
        }
        bnd.fab.setOnClickListener {
            navController.navigate(R.id.movementTypeDialogFragment)
        }
        val date = Helper.getFormattedDate()
        bnd.entryCard.setDate(date)
        bnd.exitCard.setDate(date)

        bnd.entryCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                "entry",
                "Vehicle"
            )
            navController.navigate(action)
        }

        bnd.exitCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                "exit",
                "Vehicle"
            )
            navController.navigate(action)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun displayPopup() {
        val inflater =
            requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.layout_profile_popup_view, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        //Create a popup window
        val popupWindow = PopupWindow(popupView, width, height, true)

        popupWindow.showAsDropDown(bnd.profileIcon, 0, 0, Gravity.BOTTOM)

        val usernameTextView = popupView.findViewById<TextView>(R.id.profileUsernameTV)
        val emailTextView = popupView.findViewById<TextView>(R.id.profileEmailTv)
        val roleTextView = popupView.findViewById<TextView>(R.id.profileRoleTv)
        val viewProfileTextView = popupView.findViewById<TextView>(R.id.viewProfileTv)


        usernameTextView.text = user?.fullName
        emailTextView.text = user?.email
        roleTextView.text = user?.role?.capitalize()

        viewProfileTextView.setOnClickListener {
            popupWindow.dismiss()
            navController.navigate(R.id.profileFragment)
        }

        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }
    }

    private fun setupViewModel(){
        homeViewModel.getMovementStatResponse.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Error -> {
                    showDialog("Error", result.message)
                }
                is Result.Loading -> {
                }
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