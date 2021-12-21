package ng.max.vams.ui.home

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.layout_movement_type_card_view.*
import ng.max.vams.R
import ng.max.vams.adapter.VehicleTypeDashboardAdapter
import ng.max.vams.customview.MovementTypeCardView
import ng.max.vams.data.manager.AppManager
import ng.max.vams.data.manager.UserManager
import ng.max.vams.data.remote.response.User
import ng.max.vams.data.remote.response.VehicleDashboardPair
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.HomeFragmentBinding
import ng.max.vams.ui.login.LoginViewModel
import ng.max.vams.util.Helper
import ng.max.vams.util.Helper.Companion.formatUserRole
import ng.max.vams.util.gone
import ng.max.vams.util.show
import ng.max.vams.util.showDialog


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var bnd: HomeFragmentBinding
    private var user: User? = null
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
        loginViewModel.getLoggedInUser().observe(viewLifecycleOwner) { _user ->
            if (_user == null) {
                val options = NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
                navController.navigate(R.id.loginFragment, null, options)
            } else {
                user = _user
                setupViews()
                setupViewModel()
            }
        }
    }

    private fun setupViews() {
        user?.let { user ->
            if (user.photo != null) {
                bnd.profileIcon.load(user.photo) {
                    placeholder(R.drawable.ic_icon_placeholder)
                        .transformations(CircleCropTransformation())
                }
            } else {
                bnd.profileIcon.load(R.drawable.ic_icon_placeholder) {
                    transformations(CircleCropTransformation())
                }
            }

        }

        bnd.profileIcon.setOnClickListener {
            displayPopup()
        }
        bnd.fab.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_movementTypeDialogFragment)
        }

        bnd.apply {
            totalentryCardOverall.setOnClickListener {
                if (cardControl["A"] == false) {
                    homeViewModel.controlCard(cardControl, "A", true)
                    bnd.totalentryLinedivider.visibility = View.VISIBLE
                    bnd.totalentryDetailWrap.visibility = View.VISIBLE
                    cardControl["A"] = true
                } else {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                }
            }
            entrynameCard.setOnClickListener {
                if (cardControl["B"] == false) {
                    homeViewModel.controlCard(cardControl, "B", true)
                    bnd.entrynamelinedivider.visibility = View.VISIBLE
                    bnd.entrynameDetailWrap.visibility = View.VISIBLE
                    cardControl["B"] = true
                } else {
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    cardControl["B"] = false
                }
            }
            entryDateCard.setOnClickListener {
                if (cardControl["C"] == false) {
                    homeViewModel.controlCard(cardControl, "C", true)
                    bnd.entrydateLinedivider.visibility = View.VISIBLE
                    bnd.entrydateDetailWrap.visibility = View.VISIBLE
                    cardControl["C"] = true
                } else {
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    cardControl["C"] = false
                }
            }
            exitCardOverall.setOnClickListener {
                if (cardControl["D"] == false) {
                    homeViewModel.controlCard(cardControl, "D", true)
                    bnd.exitLinedivider.visibility = View.VISIBLE
                    bnd.exitDetailWrap.visibility = View.VISIBLE
                    cardControl["D"] = true
                } else {
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE

                    cardControl["D"] = false
                }
            }
            exitnameCard.setOnClickListener {
                if (cardControl["E"] == false) {
                    homeViewModel.controlCard(cardControl, "E", true)
                    bnd.exitnamelinedivider.visibility = View.VISIBLE
                    bnd.exitnameDetailWrap.visibility = View.VISIBLE
                    cardControl["E"] = true
                } else {
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    cardControl["E"] = false
                }
            }
            exitDateCard.setOnClickListener {
                if (cardControl["F"] == false) {
                    homeViewModel.controlCard(cardControl, "F", true)
                    bnd.exitdateLinedivider.visibility = View.VISIBLE
                    bnd.exitdateDetailWrap.visibility = View.VISIBLE
                    cardControl["F"] = true
                } else {
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    cardControl["F"] = false
                }
            }
            transferCardOverall.setOnClickListener {
                if (cardControl["G"] == false) {
                    homeViewModel.controlCard(cardControl, "G", true)
                    bnd.transferLinedivider.visibility = View.VISIBLE
                    bnd.transferDetailWrap.visibility = View.VISIBLE
                    cardControl["G"] = true
                } else {
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    cardControl["G"] = false
                }
            }
            transfernameCard.setOnClickListener {
                if (cardControl["H"] == false) {
                    homeViewModel.controlCard(cardControl, "H", true)
                    bnd.transfernamelinedivider.visibility = View.VISIBLE
                    bnd.transfernameDetailWrap.visibility = View.VISIBLE
                    cardControl["H"] = true
                } else {
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    cardControl["H"] = false
                }
            }
            transferDateCard.setOnClickListener {
                if (cardControl["I"] == false) {
                    homeViewModel.controlCard(cardControl, "I", true)
                    bnd.transferdateLinedivider.visibility = View.VISIBLE
                    bnd.transferdateDetailWrap.visibility = View.VISIBLE
                    cardControl["I"] = true
                } else {
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["I"] = false
                }
            }
        }

//        bnd.entryNamecardSubtitleTv.text = getString()
        bnd.entryDateSubtitleTv
        bnd.exitNamecardSubtitleTv
        bnd.exitDateSubtitleTv
        bnd.transferNamecardSubtitleTv
        bnd.transferDateSubtitleTv


        val date = Helper.getFormattedDate()

        bnd.retryButton.setOnClickListener {
//            homeViewModel.actionGetMovementStat()
            user?.let { homeViewModel.actionGetFullMovementStat(it.id) }
        }

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
        if (user?.let { it.role.isEmpty() } == true) {
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

    private fun setupViewModel() {
        homeViewModel.getFullMovementStatResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Error -> {
                    bnd.errorTv.text = getString(R.string.error_message)
                    showErrorView(true)
                    showDialog("Error", result.message)
                }
                is Result.Loading -> {
                    showErrorView(false)
                    bnd.entryDateProgressBar.visibility = View.VISIBLE
                    bnd.totalentrycountProgressBar.visibility = View.VISIBLE
                    bnd.entryDateProgressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    showErrorView(false)


                    bnd.apply {

                        //Card Count Update
                        totalentryCardCountTv.text =
                            result.value.totalVehicleEntryCount.toString()
                        entrynamecardCountTv.text =
                            result.value.totalVehicleEntryByAgent.toString()
                        entryDateCountTv.text =
                            result.value.totalVehicleEntryByAgentToday.toString()

                        exitCardCountTv.text = result.value.totalVehicleExitCount.toString()
                        exitnamecardCountTv.text =
                            result.value.totalVehicleExitByAgent.toString()
                        exitDateCountTv.text =
                            result.value.totalVehicleExitByAgentToday.toString()

                        transferCardCountTv.text =
                            result.value.totalVehicleTransferCount.toString()
                        transfernamecardCountTv.text =
                            result.value.totalVehicleTransferByAgent.toString()
                        transferDateCountTv.text =
                            result.value.totalVehicleTransferByAgentToday.toString()

                        //Card Detail Count Update
                        entryitemonecount.text =
                            result.value.vehiclesEntrySummary[0].count.toString()
                        entryitemtwocount.text =
                            result.value.vehiclesEntrySummary[1].count.toString()
                        entryitemthreeCount.text =
                            result.value.vehiclesEntrySummary[2].count.toString()
                        entryitemFourCount.text =
                            result.value.vehiclesEntrySummary[3].count.toString()
                        entryitemfiveCount.text =
                            result.value.vehiclesEntrySummary[4].count.toString()
                        entryitemsixCount.text =
                            result.value.vehiclesEntrySummary[5].count.toString()
                        entryitemsevenCount.text =
                            result.value.vehiclesEntrySummary[6].count.toString()

                        entrybynameitemonecount.text =
                            result.value.agentVehicleEntrySummary[0].count.toString()
                        entrybynameitemtwocount.text =
                            result.value.agentVehicleEntrySummary[1].count.toString()
                        entrybynameitemthreeCount.text =
                            result.value.agentVehicleEntrySummary[2].count.toString()
                        entrybynameitemFourCount.text =
                            result.value.agentVehicleEntrySummary[3].count.toString()
                        entrybynameitemfiveCount.text =
                            result.value.agentVehicleEntrySummary[4].count.toString()
                        entrybynameitemsixCount.text =
                            result.value.agentVehicleEntrySummary[5].count.toString()
                        entrybynameitemsevenCount.text =
                            result.value.agentVehicleEntrySummary[6].count.toString()

                        entrybyDateitemonecount.text =
                            result.value.agentVehicleEntrySummaryToday[0].count.toString()
                        entrybyDateitemtwocount.text =
                            result.value.agentVehicleEntrySummaryToday[1].count.toString()
                        entrybyDateitemthreeCount.text =
                            result.value.agentVehicleEntrySummaryToday[2].count.toString()
                        entrybyDateitemFourCount.text =
                            result.value.agentVehicleEntrySummaryToday[3].count.toString()
                        entrybyDateitemfiveCount.text =
                            result.value.agentVehicleEntrySummaryToday[4].count.toString()
                        entrybyDateitemsixCount.text =
                            result.value.agentVehicleEntrySummaryToday[5].count.toString()
                        entrybyDateitemsevenCount.text =
                            result.value.agentVehicleEntrySummaryToday[6].count.toString()


                        exititemonecount.text =
                            result.value.vehiclesExitSummary[0].count.toString()
                        exititemtwocount.text =
                            result.value.vehiclesExitSummary[1].count.toString()
                        exititemthreeCount.text =
                            result.value.vehiclesExitSummary[2].count.toString()
                        exititemFourCount.text =
                            result.value.vehiclesExitSummary[3].count.toString()
                        exititemfiveCount.text =
                            result.value.vehiclesExitSummary[4].count.toString()
                        exititemsixCount.text =
                            result.value.vehiclesExitSummary[5].count.toString()
                        exititemsevenCount.text =
                            result.value.vehiclesExitSummary[6].count.toString()

                        exitbynameitemonecount.text =
                            result.value.agentVehicleExitSummary[0].count.toString()
                        exitbynameitemtwocount.text =
                            result.value.agentVehicleExitSummary[1].count.toString()
                        exitbynameitemthreeCount.text =
                            result.value.agentVehicleExitSummary[2].count.toString()
                        exitbynameitemFourCount.text =
                            result.value.agentVehicleExitSummary[3].count.toString()
                        exitbynameitemfiveCount.text =
                            result.value.agentVehicleExitSummary[4].count.toString()
                        exitbynameitemsixCount.text =
                            result.value.agentVehicleExitSummary[5].count.toString()
                        exitbynameitemsevenCount.text =
                            result.value.agentVehicleExitSummary[6].count.toString()

                        exitdateitemonecount.text =
                            result.value.agentVehicleExitSummaryToday[0].count.toString()
                        exitdateitemtwocount.text =
                            result.value.agentVehicleExitSummaryToday[1].count.toString()
                        exitdateitemthreeCount.text =
                            result.value.agentVehicleExitSummaryToday[2].count.toString()
                        exitdateitemFourCount.text =
                            result.value.agentVehicleExitSummaryToday[3].count.toString()
                        exitdateitemfiveCount.text =
                            result.value.agentVehicleExitSummaryToday[4].count.toString()
                        exitdateitemsixCount.text =
                            result.value.agentVehicleExitSummaryToday[5].count.toString()
                        exitdateitemsevenCount.text =
                            result.value.agentVehicleExitSummaryToday[6].count.toString()


                        transferitemonecount.text =
                            result.value.vehiclesTransferSummary[0].count.toString()
                        transferitemtwocount.text =
                            result.value.vehiclesTransferSummary[1].count.toString()
                        transferitemthreeCount.text =
                            result.value.vehiclesTransferSummary[2].count.toString()
                        transferitemFourCount.text =
                            result.value.vehiclesTransferSummary[3].count.toString()
                        transferitemfiveCount.text =
                            result.value.vehiclesTransferSummary[4].count.toString()
                        transferitemsixCount.text =
                            result.value.vehiclesTransferSummary[5].count.toString()
                        transferitemsevenCount.text =
                            result.value.vehiclesTransferSummary[6].count.toString()

                        transfernameitemonecount.text =
                            result.value.agentVehicleTransferSummary[0].count.toString()
                        transfernameitemtwocount.text =
                            result.value.agentVehicleTransferSummary[1].count.toString()
                        transfernameitemthreeCount.text =
                            result.value.agentVehicleTransferSummary[2].count.toString()
                        transfernameitemFourCount.text =
                            result.value.agentVehicleTransferSummary[3].count.toString()
                        transfernameitemfiveCount.text =
                            result.value.agentVehicleTransferSummary[4].count.toString()
                        transfernameitemsixCount.text =
                            result.value.agentVehicleTransferSummary[5].count.toString()
                        transfernameitemsevenCount.text =
                            result.value.agentVehicleTransferSummary[6].count.toString()

                        transferdateitemonecount.text =
                            result.value.agentVehicleTransferSummaryToday[0].count.toString()
                        transferdateitemtwocount.text =
                            result.value.agentVehicleTransferSummaryToday[1].count.toString()
                        transferdateitemthreeCount.text =
                            result.value.agentVehicleTransferSummaryToday[2].count.toString()
                        transferdateitemFourCount.text =
                            result.value.agentVehicleTransferSummaryToday[3].count.toString()
                        transferdateitemfiveCount.text =
                            result.value.agentVehicleTransferSummaryToday[4].count.toString()
                        transferdateitemsixCount.text =
                            result.value.agentVehicleTransferSummaryToday[5].count.toString()
                        transferdateitemsevenCount.text =
                            result.value.agentVehicleTransferSummaryToday[6].count.toString()


                    }
                }
            }
        })


        homeViewModel.getcardControlResponse.observe(viewLifecycleOwner, {
            when {
                !it.containsKey("A") -> {
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false
                }
                !it.containsKey("B") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false
                }
                !it.containsKey("C") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false
                }
                !it.containsKey("D") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false                }
                !it.containsKey("E") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false                }
                !it.containsKey("F") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false                }
                !it.containsKey("G") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["H"] = false
                    cardControl["I"] = false
                }
                !it.containsKey("H") -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transferdateLinedivider.visibility = View.GONE
                    bnd.transferdateDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["I"] = false
                }
                else -> {
                    bnd.totalentryLinedivider.visibility = View.GONE
                    bnd.totalentryDetailWrap.visibility = View.GONE
                    bnd.entrynamelinedivider.visibility = View.GONE
                    bnd.entrynameDetailWrap.visibility = View.GONE
                    bnd.entrydateLinedivider.visibility = View.GONE
                    bnd.entrydateDetailWrap.visibility = View.GONE
                    bnd.exitLinedivider.visibility = View.GONE
                    bnd.exitDetailWrap.visibility = View.GONE
                    bnd.exitnamelinedivider.visibility = View.GONE
                    bnd.exitnameDetailWrap.visibility = View.GONE
                    bnd.exitdateLinedivider.visibility = View.GONE
                    bnd.exitdateDetailWrap.visibility = View.GONE
                    bnd.transferLinedivider.visibility = View.GONE
                    bnd.transferDetailWrap.visibility = View.GONE
                    bnd.transfernamelinedivider.visibility = View.GONE
                    bnd.transfernameDetailWrap.visibility = View.GONE
                    cardControl["A"] = false
                    cardControl["B"] = false
                    cardControl["C"] = false
                    cardControl["D"] = false
                    cardControl["E"] = false
                    cardControl["F"] = false
                    cardControl["G"] = false
                    cardControl["H"] = false
                }
            }
        })


        user?.let { homeViewModel.actionGetFullMovementStat(it.id) }
        homeViewModel.actionGetAssetReasons()
        homeViewModel.actionGetLocations()
        homeViewModel.actionGetVehicleTypes()
        homeViewModel.actionGetVehicleCheckListItem()

        if (AppManager.getVehicleTableFlag() == 0) {
            homeViewModel.clearVehicleTable()
            AppManager.setVehicleTableFlag(-1)
        }

        user?.let { homeViewModel.getUserRole(it.id) }

        homeViewModel.getUserRoleResponse.observe(viewLifecycleOwner, { fullRole ->
            when (fullRole) {
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success -> {
                    UserManager.saveUserRole(fullRole.value.role.name)
                }
            }
        })
    }

    private fun showErrorView(isError: Boolean) {
        if (isError) {
            bnd.errorView.show()
            bnd.dataView.gone()
            bnd.bottomView.gone()
        } else {
            bnd.errorView.gone()
            bnd.dataView.show()
            bnd.bottomView.show()
        }
    }


}
