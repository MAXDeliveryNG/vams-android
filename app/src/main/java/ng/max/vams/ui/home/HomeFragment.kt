package ng.max.vams.ui.home

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.manager.AppManager
import ng.max.vams.data.manager.UserManager
import ng.max.vams.data.remote.response.User
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.HomeFragmentBinding
import ng.max.vams.ui.login.LoginViewModel
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.Helper
import ng.max.vams.util.Helper.Companion.formatUserRole
import ng.max.vams.util.gone
import ng.max.vams.util.show
import ng.max.vams.util.showDialog


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private lateinit var bnd : HomeFragmentBinding
    private var user: User? = null
    private val notificationItemAdapter = BaseAdapter()
    private lateinit var navController: NavController

    //    private var clicked = false
    var cardControl = mutableMapOf(
        "A" to false,
        "B" to false,
        "C" to false,
        "D" to false,
        "E" to false,
        "F" to false,
        "G" to false,
        "H" to false,
        "I" to false
    )

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
                val options = NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
                navController.navigate(R.id.loginFragment, null, options)
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
        bnd.dateTv.text = Helper.getFormattedDate()
        bnd.profileIcon.setOnClickListener {
            displayProfilePopup()
        }
        bnd.fab.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_movementTypeDialogFragment)
        }
        bnd.notification.setOnClickListener {
            displayNotificationPopup()
        }

        bnd.apply {
            totalEntryCardOverall.setOnClickListener {
                if (cardControl["A"] == false) {
                    bnd.totalEntryLineDivider.show()
                    bnd.totalEntryDetailWrap.show()
                    bnd.totalEntryHeader.rotateArrow(180f)
                    cardControl["A"] = true
                } else {
                    bnd.totalEntryLineDivider.gone()
                    bnd.totalEntryDetailWrap.gone()
                    bnd.totalEntryHeader.rotateArrow(0f)
                    cardControl["A"] = false
                }
                homeViewModel.controlCard(cardControl, "A", true)
            }
            entryByNameCard.setOnClickListener {
                if (cardControl["B"] == false) {
                    homeViewModel.controlCard(cardControl, "B", true)
                    bnd.entryByNameLineDivider.show()
                    bnd.entryByNameDetailWrap.show()
                    bnd.entryByNameHeader.rotateArrow(180f)
                    cardControl["B"] = true
                } else {
                    bnd.entryByNameLineDivider.gone()
                    bnd.entryByNameDetailWrap.gone()
                    bnd.entryByNameHeader.rotateArrow(0f)
                    cardControl["B"] = false
                }
            }
            entryByDateCard.setOnClickListener {
                if (cardControl["C"] == false) {
                    homeViewModel.controlCard(cardControl, "C", true)
                    bnd.entryByDateLinedivider.show()
                    bnd.entryByDateDetailWrap.show()
                    bnd.entryByDateHeader.rotateArrow(180f)
                    cardControl["C"] = true
                } else {
                    bnd.entryByDateLinedivider.gone()
                    bnd.entryByDateDetailWrap.gone()
                    bnd.entryByDateHeader.rotateArrow(0f)
                    cardControl["C"] = false
                }
            }
            totalExitCardOverall.setOnClickListener {
                if (cardControl["D"] == false) {
                    homeViewModel.controlCard(cardControl, "D", true)
                    bnd.exitLinedivider.show()
                    bnd.exitDetailWrap.show()
                    bnd.totalExitHeader.rotateArrow(180f)
                    cardControl["D"] = true
                } else {
                    bnd.exitLinedivider.gone()
                    bnd.exitDetailWrap.gone()
                    bnd.totalExitHeader.rotateArrow(0f)
                    cardControl["D"] = false
                }
            }
            exitByNameCard.setOnClickListener {
                if (cardControl["E"] == false) {
                    homeViewModel.controlCard(cardControl, "E", true)
                    bnd.exitnamelinedivider.show()
                    bnd.exitnameDetailWrap.show()
                    bnd.exitByNameHeader.rotateArrow(180f)
                    cardControl["E"] = true
                } else {
                    bnd.exitnamelinedivider.gone()
                    bnd.exitnameDetailWrap.gone()
                    bnd.exitByNameHeader.rotateArrow(0f)
                    cardControl["E"] = false
                }
            }
            exitByDateCard.setOnClickListener {
                if (cardControl["F"] == false) {
                    homeViewModel.controlCard(cardControl, "F", true)
                    bnd.exitdateLinedivider.show()
                    bnd.exitdateDetailWrap.show()
                    bnd.exitByDateHeader.rotateArrow(180f)
                    cardControl["F"] = true
                } else {
                    bnd.exitdateLinedivider.gone()
                    bnd.exitdateDetailWrap.gone()
                    bnd.exitByDateHeader.rotateArrow(0f)
                    cardControl["F"] = false
                }
            }
            totalTransferCardOverall.setOnClickListener {
                if (cardControl["G"] == false) {
                    homeViewModel.controlCard(cardControl, "G", true)
                    bnd.transferLinedivider.show()
                    bnd.transferDetailWrap.show()
                    bnd.totalTransferHeader.rotateArrow(180f)
                    cardControl["G"] = true
                } else {
                    bnd.transferLinedivider.gone()
                    bnd.transferDetailWrap.gone()
                    bnd.totalTransferHeader.rotateArrow(0f)
                    cardControl["G"] = false
                }
            }
            transferByNameCard.setOnClickListener {
                if (cardControl["H"] == false) {
                    homeViewModel.controlCard(cardControl, "H", true)
                    bnd.transfernamelinedivider.show()
                    bnd.transfernameDetailWrap.show()
                    bnd.transferByNameHeader.rotateArrow(180f)
                    cardControl["H"] = true
                } else {
                    bnd.transfernamelinedivider.gone()
                    bnd.transfernameDetailWrap.gone()
                    bnd.transferByNameHeader.rotateArrow(0f)
                    cardControl["H"] = false
                }
            }
            transferByDateCard.setOnClickListener {
                if (cardControl["I"] == false) {
                    homeViewModel.controlCard(cardControl, "I", true)
                    bnd.transferdateLinedivider.show()
                    bnd.transferdateDetailWrap.show()
                    bnd.transferByDateHeader.rotateArrow(180f)
                    cardControl["I"] = true
                } else {
                    bnd.transferdateLinedivider.gone()
                    bnd.transferdateDetailWrap.gone()
                    bnd.transferByDateHeader.rotateArrow(0f)
                    cardControl["I"] = false
                }
            }
        }

        val userFullName = "${user?.firstName} ${user?.lastName}"
        bnd.entryByNameHeader.setSubtitle(getString(R.string.by_name_label, userFullName))
        bnd.entryByDateHeader.setSubtitle(getString(R.string.by_date_label, userFullName))
        bnd.exitByNameHeader.setSubtitle(getString(R.string.by_name_label, userFullName))
        bnd.exitByDateHeader.setSubtitle(getString(R.string.by_date_label, userFullName))
        bnd.transferByNameHeader.setSubtitle(getString(R.string.by_name_label, userFullName))
        bnd.transferByDateHeader.setSubtitle(getString(R.string.by_date_label, userFullName))


        bnd.retryButton.setOnClickListener {
//            homeViewModel.actionGetMovementStat()
            user?.let { homeViewModel.actionGetFullMovementStat(it.id) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun displayProfilePopup() {
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
        if(user?.role?.isEmpty() == true){
            user?.let { homeViewModel.getUserRole(it.id) }
        }
        roleTextView.text = formatUserRole(UserManager.getUserRole())

        viewProfileTextView.setOnClickListener {
            popupWindow.dismiss()
            navController.navigate(R.id.action_homeFragment_to_profileFragment)
        }

        popupView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun displayNotificationPopup() {
        val inflater =
            requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.layout_notification_view, null)

        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        //Create a popup window
        val popupWindow = PopupWindow(popupView, width, height, true)

        popupWindow.showAtLocation(bnd.homeFragment, Gravity.CENTER, 0, 0)

        val notificationCloseBtn = popupView.findViewById<ImageButton>(R.id.notificationCloseBtn)
        val notificationRv = popupView.findViewById<RecyclerView>(R.id.transferRV)


        notificationCloseBtn.setOnClickListener {
            popupWindow.dismiss()
        }

        notificationItemAdapter.viewType = 4
        notificationItemAdapter.setOnItemClickListener { position->
            popupWindow.dismiss()
            val vehicle = notificationItemAdapter.adapterList[position] as DbVehicle

            sharedViewModel.submitData(CaptureMovementData("entry", vehicle))

            val action = HomeFragmentDirections.actionHomeFragmentToRegisterVehicleFragment(
                vehicle.lastVehicleMovement!!.reason.id,
                vehicle.lastVehicleMovement.reason.name,
                vehicle.lastVehicleMovement.reason.parentReasonName,
                null
            )
            findNavController().navigate(action)

        }
        notificationRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationItemAdapter
            setHasFixedSize(true)
        }
    }


    private fun setupViewModel() {
        with(homeViewModel){
            getFullMovementStatResponse.observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Error -> {
                        bnd.errorTv.text = getString(R.string.error_message)
                        showErrorView(true)
                        showDialog("Error", result.message)
                    }
                    is Result.Loading -> {
                        showErrorView(false)
                    }
                    is Result.Success -> {
                        showErrorView(false)


                        bnd.apply {

                            //Card Count Update
                            bnd.totalEntryHeader.setCount(result.value.totalVehicleEntryCount.toString())
                            bnd.entryByNameHeader.setCount(result.value.totalVehicleEntryByAgent.toString())
                            bnd.entryByDateHeader.setCount(result.value.totalVehicleEntryByAgentToday.toString())
                            bnd.totalExitHeader.setCount(result.value.totalVehicleExitCount.toString())
                            bnd.exitByNameHeader.setCount(result.value.totalVehicleExitByAgent.toString())
                            bnd.exitByDateHeader.setCount(result.value.totalVehicleExitByAgentToday.toString())
                            bnd.totalTransferHeader.setCount(result.value.totalVehicleTransferCount.toString())
                            bnd.transferByNameHeader.setCount(result.value.totalVehicleTransferByAgent.toString())
                            bnd.transferByDateHeader.setCount(result.value.totalVehicleTransferByAgentToday.toString())



                            //Card Detail Count Update
                            totalEntryMotorcycleView.setData(result.value.vehiclesEntrySummary[0].count.toString())
                            totalEntryTricycleView.setData(result.value.vehiclesEntrySummary[1].count.toString())
                            totalEntryCarView.setData(result.value.vehiclesEntrySummary[2].count.toString())
                            totalEntryMiniBusView.setData(result.value.vehiclesEntrySummary[3].count.toString())
                            totalEntryEMotorcycleView.setData(result.value.vehiclesEntrySummary[4].count.toString())
                            totalEntryETricycleView.setData(result.value.vehiclesEntrySummary[5].count.toString())
                            totalEntryVanView.setData(result.value.vehiclesEntrySummary[6].count.toString())

                            entryByNameMotorcycleView.setData(result.value.agentVehicleEntrySummary[0].count.toString())
                            entryByNameTricycleView.setData(result.value.agentVehicleEntrySummary[1].count.toString())
                            entryByNameCarView.setData(result.value.agentVehicleEntrySummary[2].count.toString())
                            entryByNameMiniBusView.setData(result.value.agentVehicleEntrySummary[3].count.toString())
                            entryByNameEMotorcycleView.setData(result.value.agentVehicleEntrySummary[4].count.toString())
                            entryByNameETricycleView.setData(result.value.agentVehicleEntrySummary[5].count.toString())
                            entryByNameVanView.setData(result.value.agentVehicleEntrySummary[6].count.toString())

                            entryByDateMotorcycleView.setData(result.value.agentVehicleEntrySummaryToday[0].count.toString())
                            entryByDateTricycleView.setData(result.value.agentVehicleEntrySummaryToday[1].count.toString())
                            entryByDateCarView.setData(result.value.agentVehicleEntrySummaryToday[2].count.toString())
                            entryByDateMiniBusView.setData(result.value.agentVehicleEntrySummaryToday[3].count.toString())
                            entryByDateEMotorcycleView.setData(result.value.agentVehicleEntrySummaryToday[4].count.toString())
                            entryByDateETricycleView.setData(result.value.agentVehicleEntrySummaryToday[5].count.toString())
                            entryByDateVanView.setData(result.value.agentVehicleEntrySummaryToday[6].count.toString())

                            totalExitMotorcycleView.setData(result.value.vehiclesExitSummary[0].count.toString())
                            totalExitTricycleView.setData(result.value.vehiclesExitSummary[1].count.toString())
                            totalExitCarView.setData(result.value.vehiclesExitSummary[2].count.toString())
                            totalExitMiniBusView.setData(result.value.vehiclesExitSummary[3].count.toString())
                            totalExitEMotorcycleView.setData(result.value.vehiclesExitSummary[4].count.toString())
                            totalExitETricycleView.setData(result.value.vehiclesExitSummary[5].count.toString())
                            totalExitVanView.setData(result.value.vehiclesExitSummary[6].count.toString())

                            exitByNameMotorcycleView.setData(result.value.agentVehicleExitSummary[0].count.toString())
                            exitByNameTricycleView.setData(result.value.agentVehicleExitSummary[1].count.toString())
                            exitByNameCarView.setData(result.value.agentVehicleExitSummary[2].count.toString())
                            exitByNameMiniBusView.setData(result.value.agentVehicleExitSummary[3].count.toString())
                            exitByNameEMotorcycleView.setData(result.value.agentVehicleExitSummary[4].count.toString())
                            exitByNameETricycleView.setData(result.value.agentVehicleExitSummary[5].count.toString())
                            exitByNameVanView.setData(result.value.agentVehicleExitSummary[6].count.toString())

                            exitByDateMotorcycleView.setData(result.value.agentVehicleExitSummaryToday[0].count.toString())
                            exitByDateTricycleView.setData(result.value.agentVehicleExitSummaryToday[1].count.toString())
                            exitByDateCarView.setData(result.value.agentVehicleExitSummaryToday[2].count.toString())
                            exitByDateMiniBusView.setData(result.value.agentVehicleExitSummaryToday[3].count.toString())
                            exitByDateEMotorcycleView.setData(result.value.agentVehicleExitSummaryToday[4].count.toString())
                            exitByDateETricycleView.setData(result.value.agentVehicleExitSummaryToday[5].count.toString())
                            exitByDateVanView.setData(result.value.agentVehicleExitSummaryToday[6].count.toString())

                            totalTransferMotorcycleView.setData(result.value.vehiclesTransferSummary[0].count.toString())
                            totalTransferTricycleView.setData(result.value.vehiclesTransferSummary[1].count.toString())
                            totalTransferCarView.setData(result.value.vehiclesTransferSummary[2].count.toString())
                            totalTransferMiniBusView.setData(result.value.vehiclesTransferSummary[3].count.toString())
                            totalTransferEMotorcycleView.setData(result.value.vehiclesTransferSummary[4].count.toString())
                            totalTransferETricycleView.setData(result.value.vehiclesTransferSummary[5].count.toString())
                            totalTransferVanView.setData(result.value.vehiclesTransferSummary[6].count.toString())

                            transferByNameMotorcycleView.setData(result.value.agentVehicleTransferSummary[0].count.toString())
                            transferByNameTricycleView.setData(result.value.agentVehicleTransferSummary[1].count.toString())
                            transferByNameCarView.setData(result.value.agentVehicleTransferSummary[2].count.toString())
                            transferByNameMiniBusView.setData(result.value.agentVehicleTransferSummary[3].count.toString())
                            transferByNameEMotorcycleView.setData(result.value.agentVehicleTransferSummary[4].count.toString())
                            transferByNameETricycleView.setData(result.value.agentVehicleTransferSummary[5].count.toString())
                            transferByNameVanView.setData(result.value.agentVehicleTransferSummary[6].count.toString())

                            transferByDateMotorcycleView.setData(result.value.agentVehicleTransferSummaryToday[0].count.toString())
                            transferByDateTricycleView.setData(result.value.agentVehicleTransferSummaryToday[1].count.toString())
                            transferByDateCarView.setData(result.value.agentVehicleTransferSummaryToday[2].count.toString())
                            transferByDateMiniBusView.setData(result.value.agentVehicleTransferSummaryToday[3].count.toString())
                            transferByDateEMotorcycleView.setData(result.value.agentVehicleTransferSummaryToday[4].count.toString())
                            transferByDateETricycleView.setData(result.value.agentVehicleTransferSummaryToday[5].count.toString())
                            transferByDateVanView.setData(result.value.agentVehicleTransferSummaryToday[6].count.toString())


                        }
                    }
                }
            })

            getcardControlResponse.observe(viewLifecycleOwner, {
                when {
                    !it.containsKey("A") -> {
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("B") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("C") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("D") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("E") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("F") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("G") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["H"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    !it.containsKey("H") -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transferdateLinedivider.gone()
                        bnd.transferdateDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["I"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByDateHeader.rotateArrow(0f)
                    }
                    else -> {
                        bnd.totalEntryLineDivider.gone()
                        bnd.totalEntryDetailWrap.gone()
                        bnd.entryByNameLineDivider.gone()
                        bnd.entryByNameDetailWrap.gone()
                        bnd.entryByDateLinedivider.gone()
                        bnd.entryByDateDetailWrap.gone()
                        bnd.exitLinedivider.gone()
                        bnd.exitDetailWrap.gone()
                        bnd.exitnamelinedivider.gone()
                        bnd.exitnameDetailWrap.gone()
                        bnd.exitdateLinedivider.gone()
                        bnd.exitdateDetailWrap.gone()
                        bnd.transferLinedivider.gone()
                        bnd.transferDetailWrap.gone()
                        bnd.transfernamelinedivider.gone()
                        bnd.transfernameDetailWrap.gone()
                        cardControl["A"] = false
                        cardControl["B"] = false
                        cardControl["C"] = false
                        cardControl["D"] = false
                        cardControl["E"] = false
                        cardControl["F"] = false
                        cardControl["G"] = false
                        cardControl["H"] = false
                        bnd.totalEntryHeader.rotateArrow(0f)
                        bnd.entryByNameHeader.rotateArrow(0f)
                        bnd.entryByDateHeader.rotateArrow(0f)
                        bnd.totalExitHeader.rotateArrow(0f)
                        bnd.exitByNameHeader.rotateArrow(0f)
                        bnd.exitByDateHeader.rotateArrow(0f)
                        bnd.totalTransferHeader.rotateArrow(0f)
                        bnd.transferByNameHeader.rotateArrow(0f)
                    }
                }
            })

            getUserRoleResponse.observe(viewLifecycleOwner, { fullRole ->
                when (fullRole) {
                    is Result.Error -> {}
                    is Result.Loading -> {}
                    is Result.Success -> {
                        UserManager.saveUserRole(fullRole.value.role.name)
                    }
                }
            })

            getUnconfirmedVehicleResponse.observe(viewLifecycleOwner, {result ->
                when (result) {
                    is Result.Error -> {

                    }
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        if (result.value.isEmpty()){
                            bnd.notificationIcon.gone()
                        }else{
                            bnd.notificationIcon.show()
                        }
                        notificationItemAdapter.adapterList = result.value
                    }
                }
            })

            user?.let { homeViewModel.actionGetFullMovementStat(it.id) }
            user?.let { homeViewModel.getUserRole(it.id) }


            if (AppManager.getVehicleTableFlag() == 0) {
                homeViewModel.clearVehicleTable()
                AppManager.setVehicleTableFlag(-1)
            }



            actionGetAssetReasons()
            actionGetLocations()
            actionGetVehicleTypes()
            actionGetVehicleCheckListItem()
            actionGetUnconfirmedVehicles()
        }


    }

    private fun showErrorView(isError: Boolean) {
        if (isError) {
            bnd.errorView.show()
            bnd.dataView.gone()
            bnd.bottomView.gone()
        }else{
            bnd.errorView.gone()
            bnd.dataView.show()
            bnd.bottomView.show()
        }
    }

}