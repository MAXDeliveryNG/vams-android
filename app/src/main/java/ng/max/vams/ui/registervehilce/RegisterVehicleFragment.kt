package ng.max.vams.ui.registervehilce

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.register_vehicle_fragment.*
import ng.max.vams.BR
import ng.max.vams.R
import ng.max.vams.adapter.RetrievedItemsAdapter
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.MovementData
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.RegisterVehicleFragmentBinding
import ng.max.vams.ui.assetreason.VehicleConfirmationViewModel
import ng.max.vams.ui.shared.SharedBottomSheetViewModel
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.formatDate
import ng.max.vams.util.gone
import ng.max.vams.util.show
import ng.max.vams.util.showDialog

@AndroidEntryPoint
class RegisterVehicleFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterVehicleFragment()
    }

    private var requiredFieldsValidationEnabledStates: HashMap<String, Boolean> = HashMap()
    private lateinit var bnd: RegisterVehicleFragmentBinding
    private val registerVehicleViewModel: RegisterVehicleViewModel by viewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val sharedRegistrationViewModel: SharedRegistrationViewModel by activityViewModels()
    private val sharedViewModel: VehicleConfirmationViewModel by activityViewModels()
    private val args: RegisterVehicleFragmentArgs by navArgs()
    private var movementData = MovementData()
    private lateinit var captureMovementData: CaptureMovementData
    private var valueMap: HashMap<String, String> = HashMap()
    private val recoveredItemList: ArrayList<String> = ArrayList()
    private var locations = listOf<Location>()
    private var recochecklist = listOf<RetrivalChecklistItem>()
    private var transferStatus: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = RegisterVehicleFragmentBinding.inflate(inflater, container, false)
        bnd.formModel = movementData
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupDataBinding()
        setupViewModel()
    }

    private fun setupView() {
        bnd.submitButton.setButtonEnable(false)
        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }



        if (args.parentReasonName == "Transfer") {
            bnd.currentLocationHeader.text = getString(R.string.location_from_label)
            destLocationLayout.show()
        } else {
            bnd.currentLocationHeader.text = getString(R.string.location_checkin_label)
        }

        if (args.parentReasonName == "Retrieved"){
            bnd.retrievalAgentLayout.show()
        }



        bnd.currentLocationEditText.setOnClickListener {
            sharedBottomSheetViewModel.submitLocations(mapOf("from" to locations))
            val action = RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToListBottomSheetFragment(
                    selectedItem = bnd.currentLocationEditText.text.toString(),
                    fromSource = "FROM",
                    movementType = captureMovementData.movementType
                )
            findNavController().navigate(action)
        }

        bnd.destLocationEditText.setOnClickListener {
            sharedBottomSheetViewModel.submitLocations(mapOf("dest" to locations.filter {
                it.name != bnd.currentLocationEditText.text.toString()
            }))
            val action = RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToListBottomSheetFragment(
                selectedItem = bnd.destLocationEditText.text.toString(),
                fromSource = "DEST",
                movementType = captureMovementData.movementType
            )
            findNavController().navigate(action)
        }

        bnd.submitButton.setOnClickListener {
            val action =
                RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToVehicleConfirmationFragment(
                    vehicleMaxId = captureMovementData.vehicle.maxVehicleId!!,
                    champion = captureMovementData.vehicle.champion?.let {
                        getString(
                            R.string.default_name, it.firstName,
                            it.lastName
                        )
                    } ?: "N/A",
                    reason = args.subReasonName,
                    movementType = captureMovementData.movementType
                )
            findNavController().navigate(action)
        }

        bnd.acceptTransferBtn.setOnClickListener {
            val action =  RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToTransferCheckInConfirmationBottomSheetFragment("accept")
            findNavController().navigate(action)
        }

        bnd.rejectTransferBtn.setOnClickListener {
            val action =  RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToTransferCheckInConfirmationBottomSheetFragment("reject")
            findNavController().navigate(action)
        }
    }

    private fun setupDataBinding() {
        bnd.formModel?.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                var value: String? = ""
                var field: TextInputLayout? = null
                var ignoreProperty = false
                var isCompleted = false

                when (propertyId) {
                    BR.location -> {
                        field = bnd.currentLocationInputLayout
                        value = movementData.location
                    }
                    BR.destLocation -> {
                        field = bnd.destLocationInputLayout
                        value = movementData.destLocation
                    }
                    BR.odometerReading -> {
                        field = bnd.odometerReadingInputLayout
                        value = movementData.odometerReading
                    }
                    BR.retrievalAgent -> {
                        field = bnd.retrievalAgentInputLayout
                        value = movementData.retrievalAgent
                    }
                    else -> {
                        ignoreProperty = true
                    }
                }
                val fieldKey: String = field?.tag?.toString() ?: ""
                val validationEnabled = checkValidationEnabledStates(fieldKey)

                if (!ignoreProperty) {
                    var passesValidation = true

                    if (!TextUtils.isEmpty(fieldKey)) {
                        valueMap[fieldKey] = value ?: ""

                        // Validation for required field
                        if (validationEnabled && isRequiredField(fieldKey)) {
                            passesValidation = if (TextUtils.isEmpty(value)) {
                                field?.error = "Please enter $fieldKey"
                                false
                            } else {
                                var isValid: Boolean
                                if (fieldKey == "odometer" && value!!.toDoubleOrNull() == null) {
                                    field?.error = "Please enter $fieldKey"
                                    isValid = false
                                } else {
                                    field?.error = null
                                    isValid = true
                                }

                                if (fieldKey == "location_from" && args.parentReasonName == "Transfer"){
                                    if(bnd.destLocationEditText.text.toString().isNotEmpty()){
                                        bnd.destLocationEditText.text = null
                                        isValid = false
                                    }else{
                                        isValid = true
                                    }
                                }

                                isValid
                            }
                        }

                    }
                    isCompleted = passesValidation && isRequiredFieldsProvided()
                            && recoveredItemList.count() != 0
                }

                bnd.submitButton.setButtonEnable(isCompleted)
                bnd.acceptTransferBtn.isEnabled = isCompleted
                bnd.rejectTransferBtn.isEnabled = isCompleted
            }

        })
    }

    private fun checkValidationEnabledStates(fieldKey: String): Boolean {
        var allEnabled = true

        for (key in requiredFieldsValidationEnabledStates.keys) {
            if (requiredFieldsValidationEnabledStates[fieldKey] == false) {
                allEnabled = false
                break
            }
        }

        if (requiredFieldsValidationEnabledStates[fieldKey] == false) {
            requiredFieldsValidationEnabledStates[fieldKey] = true
        }

        return allEnabled
    }

    private fun isRequiredFieldsProvided(): Boolean {
        for (tag in getRequiredKeys()) {
            if (TextUtils.isEmpty(valueMap[tag])) {
                return false
            }
        }
        return true
    }

    private fun isRequiredField(key: String): Boolean {
        return getRequiredKeys().contains(key)
    }

    private fun getRequiredKeys(): List<String> {
        return movementData.let { movementData ->
            val requiredKeys = mutableListOf(movementData.keyLocationFrom,
                movementData.keyOdometer)

            if (args.parentReasonName == "Transfer") {
                requiredKeys.add(movementData.keyLocationTo)
            }

            if (args.parentReasonName == "Retrieved") {
                requiredKeys.add(movementData.keyRetrievalAgent)
            }

            return@let requiredKeys

        }
    }

    private fun setupViewModel() {
        with(registerVehicleViewModel) {

            getLocationsResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                    }
                    Result.Loading -> {
                    }
                    is Result.Success -> {
                        locations = result.value

                        if (captureMovementData.movementType == "entry" && args.parentReasonName == "Transfer"){
                            bnd.rowOneTv.text = locations.find { captureMovementData.vehicle.lastVehicleMovement!!.locationFromId == it.id }?.name ?: "N/A"
                            bnd.rowTwoTv.text = locations.find { captureMovementData.vehicle.lastVehicleMovement!!.locationToId == it.id }?.name ?: "N/A"
                            bnd.currentLocationEditText.setText(bnd.rowOneTv.text)
                            bnd.destLocationEditText.setText(bnd.rowTwoTv.text)
                            bnd.odometerReadingEditText.setText(captureMovementData.vehicle.lastVehicleMovement!!.odometer.toString())
                        }
                    }
                }
            }

            getRetrievalChecklistItemResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {

                    }
                    Result.Loading -> {

                    }
                    is Result.Success -> {
                        recochecklist = result.value
                        val retrievedList = recochecklist.map {
                            it
                        }
                        populateRecoveredItemsCheckBoxes(retrievedList)
                    }
                }
            }
            actionGetAllLocation()
            actionGetRetrievalChecklist()
        }

        sharedRegistrationViewModel.getRegisterMovementResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    bnd.submitButton.loaded()
                    showDialog("Error", result.message)
                }
                Result.Loading -> {
                }
                is Result.Success -> {
                    bnd.submitButton.loaded()
                    val action = RegisterVehicleFragmentDirections
                        .actionRegisterVehicleFragmentToCompleteRegistrationFragment(
                            result.value.vehicleMovement!!, result.value.maxVehicleId, transferStatus
                        )
                    findNavController().navigate(action)
                }
            }
        }

        sharedRegistrationViewModel.getCaptureMovementDataResponse.observe(viewLifecycleOwner, {
            captureMovementData = it
            populateView(it)

        })

        sharedViewModel.getConfirmationResponse.observe(viewLifecycleOwner, { hasConfirm ->
            if (hasConfirm) {
                movementData.apply {
                    this.amountDefaulted = args.amountDefaulted
                }
                sharedRegistrationViewModel.registerMovement(
                    movementData, captureMovementData.vehicle.id,
                    args.subReasonId,
                    if (args.parentReasonName == "Transfer"){
                        locations.first { it.name == movementData.destLocation }.id
                    }else{
                        null
                    }
                )
            }else{
                bnd.submitButton.loaded()
            }
        })

        with(sharedBottomSheetViewModel) {
            getSelectedItemResponse.observe(viewLifecycleOwner) { selectedItem ->
                if (selectedItem.containsKey("FROM")) {
                    bnd.currentLocationEditText.setText(selectedItem["FROM"])
                } else {
                    bnd.destLocationEditText.setText(selectedItem["DEST"])
                }

            }

            getCheckInTransferActionResponse.observe(viewLifecycleOwner){
                transferStatus = it
                sharedRegistrationViewModel.registerMovement(
                    movementData, captureMovementData.vehicle.id,
                    args.subReasonId,
                    if (args.parentReasonName == "Transfer"){
                        locations.first {location ->  location.name == movementData.destLocation }.id
                    }else{
                        null
                    }, it
                )
            }
        }
    }

    private fun populateRecoveredItemsCheckBoxes(retrievedItems: List<RetrivalChecklistItem>) {

        val adapter = RetrievedItemsAdapter()
        if (args.parentReasonName == "Transfer"){
            adapter.selectedItems = getSelectedItems(captureMovementData.vehicle.lastVehicleMovement!!.checkListItems,
            retrievedItems)
        }
        adapter.setOnItemClickListener { position, isChecked ->
            val retrievalChecklistItem = adapter.retrievedItems[position]
            if (isChecked) {
                recoveredItemList.add(retrievalChecklistItem.id)
            } else {
                recoveredItemList.remove(retrievalChecklistItem.id)
            }
            movementData.recoveredItems = recoveredItemList
            val enableButton = isRequiredFieldsProvided()
                    && recoveredItemList.count() != 0
            bnd.submitButton.setButtonEnable(
                enableButton
            )
            bnd.rejectTransferBtn.isEnabled = enableButton
            bnd.acceptTransferBtn.isEnabled = enableButton
        }
        bnd.retrievedItemsRv.adapter = adapter
        adapter.retrievedItems = retrievedItems
    }

    private fun getSelectedItems(
        checkListItems: List<String>?,
        retrievedItems: List<RetrivalChecklistItem>
    ): List<RetrivalChecklistItem> {
        val _retrievedItems = mutableListOf<RetrivalChecklistItem>()
        checkListItems?.forEach {retrievedItemName ->
            val filteredData = retrievedItems.filter {
                it.name == retrievedItemName
            }
            if (filteredData.isNotEmpty()){
                _retrievedItems.add(filteredData.first())
            }
        }
        return _retrievedItems
    }

    private fun populateView(_captureData: CaptureMovementData) {

        if (_captureData.movementType == "entry" && args.parentReasonName == "Transfer"){
            bnd.backButton.setImageResource(R.drawable.ic_home)
            bnd.movementTypeIcon.setImageResource(R.drawable.ic_location)
            bnd.movementTypeHeaderTv.text = "Transfer Details"
            bnd.registerVehicleHeaderTv.text = "CONFIRM VEHICLE TRANSFER ENTRY"
            bnd.rowOneLabel.text = "From"
            bnd.rowTwoLabel.text = "Destination"
            bnd.timeOfExitTv.text = _captureData.vehicle.lastVehicleMovement!!.createdAt.formatDate()
            bnd.transferAgentTv.text = _captureData.vehicle.lastVehicleMovement.creatorName
            bnd.movementTypeRowThree.show()
            bnd.movementTypeRowFour.show()

            bnd.transferCheckInButtonsContainer.show()
            bnd.retrievalAgentLayout.gone()
            bnd.submitButton.gone()
        }else{
            if (_captureData.movementType == "entry") {
                bnd.registerVehicleHeaderTv.text = getString(R.string.dialog_entry_label).uppercase()
                bnd.submitButton.setButtonText(getString(R.string.check_label, "In"))
            } else {
                bnd.registerVehicleHeaderTv.text = getString(R.string.dialog_exit_label).uppercase()
                bnd.submitButton.setButtonText(getString(R.string.check_label, "Out"))
            }
            bnd.rowOneTv.text = args.parentReasonName
            bnd.rowTwoTv.text = args.subReasonName
        }

        bnd.vehicleMaxId.text = _captureData.vehicle.maxVehicleId

        _captureData.vehicle.status?.let {
            bnd.vehicleStatusContainer.show()
            bnd.vehicleStatus.text = it.name
            when (it.slug) {
                "active" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status_bg
                        )
                    )
                }
                "inactive" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.inactive_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.inactive_status_bg
                        )
                    )
                }
                "hp_completed" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hp_completed_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hp_completed_status_bg
                        )
                    )
                }
                "missing" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.missing_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.missing_status_bg
                        )
                    )
                }
                "scrapped" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.scrapped_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.scrapped_status_bg
                        )
                    )
                }
                else -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status_bg
                        )
                    )
                }
            }
        }
    }

}