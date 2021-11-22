package ng.max.vams.ui.registervehilce

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.BR
import ng.max.vams.adapter.RetrievedItemsAdapter
import ng.max.vams.data.MovementData
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.RegisterVehicleFragmentBinding
import ng.max.vams.ui.assetreason.VehicleConfirmationViewModel
import ng.max.vams.ui.shared.SharedBottomSheetViewModel
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
    private val sharedViewModel: VehicleConfirmationViewModel by activityViewModels()
    private val args: RegisterVehicleFragmentArgs by navArgs()
    private var movementData = MovementData()
    private var valueMap: HashMap<String, String> = HashMap()
    private val recoveredItemList: ArrayList<String> = ArrayList()
    private var locations = listOf<Location>()
    private var recochecklist = listOf<RetrivalChecklistItem>()


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

        bnd.registerHeader.text = args.subReasonName
        //populateRecoveredItemsCheckBoxes()
        bnd.submitButton.setButtonEnable(false)
        bnd.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        bnd.locationEditText.setOnClickListener {
            val selected = bnd.locationEditText.text.toString()
            sharedBottomSheetViewModel.submitLocations(locations.toList())
            val action =
                RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToListBottomSheetFragment(
                    selected,
                    "REGISTER"
                )
            findNavController().navigate(action)
        }

        bnd.submitButton.setOnClickListener {
            bnd.submitButton.loaded()
            val action =
                RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToVehicleConfirmationFragment(
                    vehicleMaxId = args.vehicleMaxId,
                    champion = args.champion,
                    reason = args.subReasonName,
                    movementType = args.vehicleMovement
                )
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
                        field = bnd.locationInputLayout
                        value = movementData.location
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
                                if (fieldKey == "odometer" && value!!.toDoubleOrNull() == null) {
                                    field?.error = "Please enter $fieldKey"
                                    false
                                } else {
                                    field?.error = null
                                    true
                                }

                            }
                        }

                    }
                    isCompleted = passesValidation && isRequiredFieldsProvided()
                            && recoveredItemList.count() != 0
                }

                bnd.submitButton.setButtonEnable(isCompleted)
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
            val retrievedSubReasonId = args.retrievedSubReasonIds.find { it == args.subReasonId }
            if (retrievedSubReasonId == null) {
                listOf(
                    movementData.keyLocation,
                    movementData.keyOdometer
                )
            } else {
                listOf(
                    movementData.keyLocation,
                    movementData.keyOdometer,
                    movementData.keyRetrievalAgent
                )
            }

        }
    }

    private fun setupViewModel() {
        with(registerVehicleViewModel) {

//            getLocationResponse.observe(viewLifecycleOwner) { result ->
//                when (result) {
//                    is Result.Error -> {
//                    }
//                    Result.Loading -> {
//                    }
//                    is Result.Success -> {
//                        locationEditText.setText(result.value.name)
//                        locationEditText.isEnabled = false
//                    }
//                }
//            }

            getLocationsResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                    }
                    Result.Loading -> {
                    }
                    is Result.Success -> {
                        locations = result.value
                    }
                }
            }

            getRetrivalChecklistItemResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {

                    }
                    Result.Loading -> {

                    }
                    is Result.Success -> {
                        recochecklist = result.value
                        val retrivedList = recochecklist.map {
                            it
                        }
                        populateRecoveredItemsCheckBoxes(retrivedList)
                    }
                }
            }

            getRegisterMovementResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        bnd.submitButton.loaded()
                        showDialog("Error", result.message)
                    }
                    Result.Loading -> {
                    }
                    is Result.Success -> {
                        cleanVehicleTable(result.value.id)
                        bnd.submitButton.loaded()
                        //TODO BUGGY SEE : https://console.firebase.google.com/project/max-v2/crashlytics/app/android:ng.max.vams/issues/911c072b6f325531954d7250a9d19506?time=last-seven-days&sessionEventKey=6177DD9B007600014D26708AD5E1202E_1601780252919286962
                        val action = RegisterVehicleFragmentDirections
                            .actionRegisterVehicleFragmentToCompleteRegistrationFragment(
                                args.vehicleMovement, result.value.maxVehicleId
                            )
                        findNavController().navigate(action)
                    }
                }
            }

            sharedViewModel.getConfirmationResponse.observe(viewLifecycleOwner, { hasConfirm ->
                if (hasConfirm) {
                    registerVehicleViewModel.registerMovement(
                        movementData, args.vehicleId,
                        args.subReasonId, args.locationToId
                    )
                }
            })
//            actionGetLocationById(args.locationId)
            actionGetAllLocation()
            actionGetRetrivalChecklist()
        }

        sharedBottomSheetViewModel.getSelectedItemResponse.observe(viewLifecycleOwner) { selectedItem ->
            bnd.locationEditText.setText(selectedItem)
        }
    }

    private fun populateRecoveredItemsCheckBoxes(retrivedItems: List<RetrivalChecklistItem>) {

        val adapter = RetrievedItemsAdapter()

        adapter.setOnItemClickListener { position, isChecked ->
            val retrievalChecklistItem = adapter.recoveredItems[position]
            if (isChecked) {
                recoveredItemList.add(retrievalChecklistItem.id)
            } else {
                recoveredItemList.remove(retrievalChecklistItem.id)
            }
            movementData.recoveredItems = recoveredItemList
            val retrievedSubReasonId = args.retrievedSubReasonIds.find { it == args.subReasonId }
            if (retrievedSubReasonId == null) {
                bnd.submitButton.setButtonEnable(
                    isRequiredFieldsProvided()
                            && recoveredItemList.count() != 0
                )
            }
        }
        bnd.retrievedItemsRv.adapter = adapter
        adapter.recoveredItems = retrivedItems
    }

    private fun cleanVehicleTable(id: String) {
        registerVehicleViewModel.deleteVehicle(id)
    }

}