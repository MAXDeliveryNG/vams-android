package ng.max.vams.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
import java.util.*

private const val ARG_PARAM = "notification"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private lateinit var bnd: HomeFragmentBinding
    private var user: User? = UserManager.getUser()
    private val notificationItemAdapter = BaseAdapter()
    private lateinit var navController: NavController
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private lateinit var resolutionForResult : ActivityResultLauncher<IntentSenderRequest>
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private var cancellationTokenSource = CancellationTokenSource()

    //    private var clicked = false
    private var cardControl = mutableMapOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resolutionForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK){
                    getCurrentLocationSettings()
                }
            }

        locationPermissionRequest =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION]!!
                            && permissions[Manifest.permission.ACCESS_COARSE_LOCATION]!! -> {
                                getUserCurrentLocation()
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "Please enable permissions for location based notifications",
                            Toast.LENGTH_LONG
                        )
                            .show()

                    }
                }
            }
    }


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
                getCurrentLocationSettings()
            }
        }


    }


    private fun getCurrentLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getUserCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    private fun getUserCurrentLocation() {
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED)
        ) {
            val currentLocationTask = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location?> ->
                if (task.isSuccessful) {
                    try {
                        val result: Location? = task.result
                        result?.let {_location ->
                            user?.let {_user->
                                homeViewModel.getSavedUserLocation(
                                    _user.id,
                                    _user.city!!,
                                    _location,
                                    Firebase.firestore
                                )
                            }
                        }
                    }catch (ex: Exception){
                        Toast.makeText(requireContext(), "Failed to get user location.", Toast.LENGTH_LONG
                        )
                            .show()
                        getLocationPermission()
                    }
                } else {
                    val exception = task.exception
                    Toast.makeText(
                        requireContext(),
                        "Location Error : $exception",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    getLocationPermission()
                }

            }
        } else {
            getLocationPermission()
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
            user?.let { homeViewModel.actionGetFullMovementStat(it.id, Firebase.firestore) }
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
        if (user?.role?.isEmpty() == true) {
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

        if(notificationItemAdapter.adapterList.isEmpty()){
            homeViewModel.actionGetUnconfirmedVehicles()
        }
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
        notificationItemAdapter.setOnItemClickListener { position ->
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
        with(homeViewModel) {
            AppManager.getMessagingServiceToken()?.let {token ->
                val tokenBody = HashMap<String, String>().apply {
                    this["user_id"] = UserManager.getUser()!!.id
                    this["registration_token"] = token
                }
                registerTokenToServer(tokenBody)
            }
            getFullMovementStatResponse.observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Error -> {
                        bnd.errorTv.text = result.message
                        showErrorView(true)
                    }
                    is Result.Loading -> {
                        showErrorView(false)
                    }
                    is Result.Success -> {
                        showErrorView(false)


                        bnd.apply {

                            //Card Count Update
                            bnd.totalEntryHeader.setCount(result.value.totalEntry.total.toString())
                            bnd.entryByNameHeader.setCount(result.value.entryByAgentName.total.toString())
                            bnd.entryByDateHeader.setCount(result.value.entryByDate.total.toString())
                            bnd.totalExitHeader.setCount(result.value.totalExit.total.toString())
                            bnd.exitByNameHeader.setCount(result.value.exitByAgentName.total.toString())
                            bnd.exitByDateHeader.setCount(result.value.exitByDate.total.toString())
                            bnd.totalTransferHeader.setCount(result.value.totalTransfer.total.toString())
                            bnd.transferByNameHeader.setCount(result.value.transferByAgentName.total.toString())
                            bnd.transferByDateHeader.setCount(result.value.transferByDate.total.toString())


                            //Card Detail Count Update
                            totalEntryMotorcycleView.setData(result.value.totalEntry.motorcycle.toString())
                            totalEntryTricycleView.setData(result.value.totalEntry.tricycle.toString())
                            totalEntryCarView.setData(result.value.totalEntry.car.toString())
                            totalEntryMiniBusView.setData(result.value.totalEntry.minibus.toString())
                            totalEntryEMotorcycleView.setData(result.value.totalEntry.emotorcycle.toString())
                            totalEntryETricycleView.setData(result.value.totalEntry.etricycle.toString())
                            totalEntryVanView.setData(result.value.totalEntry.van.toString())

                            entryByNameMotorcycleView.setData(result.value.entryByAgentName.motorcycle.toString())
                            entryByNameTricycleView.setData(result.value.entryByAgentName.tricycle.toString())
                            entryByNameCarView.setData(result.value.entryByAgentName.car.toString())
                            entryByNameMiniBusView.setData(result.value.entryByAgentName.minibus.toString())
                            entryByNameEMotorcycleView.setData(result.value.entryByAgentName.emotorcycle.toString())
                            entryByNameETricycleView.setData(result.value.entryByAgentName.etricycle.toString())
                            entryByNameVanView.setData(result.value.entryByAgentName.van.toString())

                            entryByDateMotorcycleView.setData(result.value.entryByDate.motorcycle.toString())
                            entryByDateTricycleView.setData(result.value.entryByDate.tricycle.toString())
                            entryByDateCarView.setData(result.value.entryByDate.car.toString())
                            entryByDateMiniBusView.setData(result.value.entryByDate.minibus.toString())
                            entryByDateEMotorcycleView.setData(result.value.entryByDate.emotorcycle.toString())
                            entryByDateETricycleView.setData(result.value.entryByDate.etricycle.toString())
                            entryByDateVanView.setData(result.value.entryByDate.van.toString())

                            totalExitMotorcycleView.setData(result.value.totalExit.motorcycle.toString())
                            totalExitTricycleView.setData(result.value.totalExit.tricycle.toString())
                            totalExitCarView.setData(result.value.totalExit.car.toString())
                            totalExitMiniBusView.setData(result.value.totalExit.minibus.toString())
                            totalExitEMotorcycleView.setData(result.value.totalExit.emotorcycle.toString())
                            totalExitETricycleView.setData(result.value.totalExit.etricycle.toString())
                            totalExitVanView.setData(result.value.totalExit.van.toString())

                            exitByNameMotorcycleView.setData(result.value.exitByAgentName.motorcycle.toString())
                            exitByNameTricycleView.setData(result.value.exitByAgentName.tricycle.toString())
                            exitByNameCarView.setData(result.value.exitByAgentName.car.toString())
                            exitByNameMiniBusView.setData(result.value.exitByAgentName.minibus.toString())
                            exitByNameEMotorcycleView.setData(result.value.exitByAgentName.emotorcycle.toString())
                            exitByNameETricycleView.setData(result.value.exitByAgentName.etricycle.toString())
                            exitByNameVanView.setData(result.value.exitByAgentName.van.toString())

                            exitByDateMotorcycleView.setData(result.value.exitByDate.motorcycle.toString())
                            exitByDateTricycleView.setData(result.value.exitByDate.tricycle.toString())
                            exitByDateCarView.setData(result.value.exitByDate.car.toString())
                            exitByDateMiniBusView.setData(result.value.exitByDate.minibus.toString())
                            exitByDateEMotorcycleView.setData(result.value.exitByDate.emotorcycle.toString())
                            exitByDateETricycleView.setData(result.value.exitByDate.etricycle.toString())
                            exitByDateVanView.setData(result.value.exitByDate.van.toString())

                            totalTransferMotorcycleView.setData(result.value.totalTransfer.motorcycle.toString())
                            totalTransferTricycleView.setData(result.value.totalTransfer.tricycle.toString())
                            totalTransferCarView.setData(result.value.totalTransfer.car.toString())
                            totalTransferMiniBusView.setData(result.value.totalTransfer.minibus.toString())
                            totalTransferEMotorcycleView.setData(result.value.totalTransfer.emotorcycle.toString())
                            totalTransferETricycleView.setData(result.value.totalTransfer.etricycle.toString())
                            totalTransferVanView.setData(result.value.totalTransfer.van.toString())

                            transferByNameMotorcycleView.setData(result.value.transferByAgentName.motorcycle.toString())
                            transferByNameTricycleView.setData(result.value.transferByAgentName.tricycle.toString())
                            transferByNameCarView.setData(result.value.transferByAgentName.car.toString())
                            transferByNameMiniBusView.setData(result.value.transferByAgentName.minibus.toString())
                            transferByNameEMotorcycleView.setData(result.value.transferByAgentName.emotorcycle.toString())
                            transferByNameETricycleView.setData(result.value.transferByAgentName.etricycle.toString())
                            transferByNameVanView.setData(result.value.transferByAgentName.van.toString())

                            transferByDateMotorcycleView.setData(result.value.transferByDate.motorcycle.toString())
                            transferByDateTricycleView.setData(result.value.transferByDate.tricycle.toString())
                            transferByDateCarView.setData(result.value.transferByDate.car.toString())
                            transferByDateMiniBusView.setData(result.value.transferByDate.minibus.toString())
                            transferByDateEMotorcycleView.setData(result.value.transferByDate.emotorcycle.toString())
                            transferByDateETricycleView.setData(result.value.transferByDate.etricycle.toString())
                            transferByDateVanView.setData(result.value.transferByDate.van.toString())


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

            getUnconfirmedVehicleResponse.observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Error -> {

                    }
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        if (result.value.isEmpty()) {
                            bnd.notificationIcon.gone()
                        } else {
                            bnd.notificationIcon.show()
                        }
                        notificationItemAdapter.adapterList = result.value
                    }
                }
            })

            user?.let { homeViewModel.actionGetFullMovementStat(it.id, Firebase.firestore) }
            user?.let { homeViewModel.getUserRole(it.id) }


            if (AppManager.getVehicleTableFlag() == 0) {
                homeViewModel.clearVehicleTable()
                AppManager.setVehicleTableFlag(-1)
            }

            if (AppManager.getReasonTableFlag() == 0) {
                homeViewModel.clearReasonTable()
                AppManager.setReasonTableFlag(-1)
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
        } else {
            bnd.errorView.gone()
            bnd.dataView.show()
            bnd.bottomView.show()
        }
    }

    private fun getLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param: Boolean) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM, param)
                }
            }
    }
}