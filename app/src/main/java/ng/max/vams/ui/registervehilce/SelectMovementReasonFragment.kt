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
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.BR
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.MovementData
import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.SelectMovementReasonFragmentBinding
import ng.max.vams.ui.assetreason.VehicleConfirmationViewModel
import ng.max.vams.ui.shared.SharedBottomSheetViewModel
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.gone
import ng.max.vams.util.navigate
import ng.max.vams.util.show
import ng.max.vams.util.showDialog


@AndroidEntryPoint
class SelectMovementReasonFragment : Fragment() {

    private lateinit var bnd: SelectMovementReasonFragmentBinding
    private val viewModel: SelectMovementReasonViewModel by activityViewModels()
    private val sharedRegistrationViewModel: SharedRegistrationViewModel by activityViewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val sharedVehicleConfirmationViewModel: VehicleConfirmationViewModel by activityViewModels()
    private var movementData = MovementData()
    private var requiredFieldsValidationEnabledStates: HashMap<String, Boolean> = HashMap()
    private var selectedItem: String? = null
    private var selectedReasonName: String? = null
    private lateinit var captureMovementData: CaptureMovementData
    var subReasonPicked: String? = null
    var retrievedReason: Reason? = null
    var retrievedItems: List<String> = emptyList()

    private var valueMap: HashMap<String, String> = HashMap()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = SelectMovementReasonFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        bnd.reasonModel = movementData
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDatabinding()
        setupViewModel()
    }

    private fun setupDatabinding() {
        bnd.reasonModel?.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                var value: String? = ""
                var field: TextInputLayout? = null
                var ignoreProperty = false
                var isCompleted = false

                when (propertyId) {
                    BR.reason -> {
                        field = bnd.reasonInputLayout
                        value = movementData.reason
                    }
                    BR.subreason -> {
                        field = bnd.subReasonInputLayout
                        value = movementData.subreason
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
                                field?.error = "Please select $fieldKey"
                                false
                            } else {
                                var isValid = true
                                if (fieldKey == "reason") {
                                    bnd.subReasonLayout.show()
                                    if (captureMovementData.movementType == "exit" && value != "Transfer") {
                                        bnd.submitButton.setButtonText("Check Out")
                                    } else {
                                        bnd.retrievedItemsContainer.gone()
                                        bnd.submitButton.setButtonText("Continue")
                                    }
                                    //Reset sub reason field
                                    if(bnd.subReasonEditText.text.toString().isNotEmpty()){
                                        bnd.subReasonEditText.text = null
                                        isValid = false
                                    }else{
                                        isValid = true
                                    }
                                }

                                if ((value != "Inter City" || value != "Intra City") && fieldKey == "subreason" && captureMovementData.movementType == "exit") {
                                    bnd.retrievedItemsContainer.show()
                                }

                                if (value == "Financial Default" && fieldKey == "subreason") {
                                    bnd.amountDefaultedLayout.show()
                                } else {
                                    bnd.amountDefaultedLayout.gone()
                                }

                                field?.error = null
                                isValid
                            }
                        }
                    }
                    isCompleted = passesValidation && isRequiredFieldsProvided()
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
            if (bnd.subReasonEditText.text.toString() == "Financial Default"){
                listOf(
                    movementData.keyReason,
                    movementData.keySubReason,
                    movementData.keyAmountDefaulted
                )
            }else{
                listOf(
                movementData.keyReason,
                movementData.keySubReason
                )
            }
        }
    }


    private fun setupViewModel() {

        sharedRegistrationViewModel.getCaptureMovementDataResponse.observe(viewLifecycleOwner, {
            captureMovementData = it
            populateView(it)
        })

        sharedBottomSheetViewModel.getSelectedItemResponse.observe(viewLifecycleOwner, {
            if (it.containsKey("REASON")) {
                selectedItem = it["REASON"]
                selectedReasonName = it["REASON"]
                bnd.reasonModel?.reason = it["REASON"]
                viewModel.actionGetReasonByName(it["REASON"]!!)

            } else {
                selectedItem = it["SUBREASON"]
                subReasonPicked = it["SUBREASON"]
                bnd.reasonModel?.subreason = it["SUBREASON"]
            }
        })

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

                    val action = SelectMovementReasonFragmentDirections
                        .actionSelectMovementReasonFragmentToCompleteRegistrationFragment(
                            result.value.vehicleMovement!!, result.value.maxVehicleId, null
                        )
                    navigate(action)
                }
            }
        }

        sharedVehicleConfirmationViewModel.getConfirmationResponse.observe(viewLifecycleOwner, { hasConfirm ->
            if (hasConfirm) {
                val lastVehicleMovement = captureMovementData.vehicle.lastVehicleMovement
                val movementBody = MovementBody(
                    vehicleId = captureMovementData.vehicle.id,
                    locationFromId = lastVehicleMovement?.locationFromId,
                    locationToId = lastVehicleMovement?.locationToId,
                    odormeter = lastVehicleMovement?.odometer,
                    subReasonId = getSubReasonId(movementData.subreason),
                    recoveredItems = lastVehicleMovement?.checkListItems?: emptyList(),
                    retrievalAgent = lastVehicleMovement?.retrievalAgent,
                    amountDefaulted = movementData.amountDefaulted
                )
                sharedRegistrationViewModel.registerMovementFromReasonScreen(
                    movementBody
                )
            }
        })

        viewModel.getReasonByNameResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Error -> {
                }
                is Result.Loading -> {
                }
                is Result.Success -> {
                    retrievedReason = result.value.find { it.name == selectedReasonName }!!
                }
            }
        })

    }


    private fun getSubReasonId(subreason: String?): String {
        return retrievedReason?.subReasons?.filter {_subReason->
            subreason == _subReason.name
        }!!.first().id
    }

    private fun navigateToRegisterVehicle() {
        sharedRegistrationViewModel.submitData(captureMovementData)
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToRegisterVehicleFragment(
                subReasonId = getSubReasonId(movementData.subreason),
                subReasonName = movementData.subreason!!,
                parentReasonName = movementData.reason!!,
                amountDefaulted = if (movementData.subreason != "Financial Default"){
                    null
                }else{
                    bnd.amountDefaultedEditText.text.toString()
                }
            )

        navigate(action)
    }


    private fun populateView(_captureData: CaptureMovementData) {

        if (_captureData.movementType == "entry") {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_entry_label).uppercase()
        } else {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_exit_label).uppercase()
        }

        if (_captureData.movementType == "exit"){
            _captureData.vehicle.lastVehicleMovement?.let {
                it.checkListItems?.let { checkedList->
                    retrievedItems = checkedList
                    val itemsAdapter = BaseAdapter()
                    itemsAdapter.viewType = 3
                    itemsAdapter.adapterList = checkedList
                    bnd.retrievedItemsRv.apply {
                        adapter = itemsAdapter
                        setHasFixedSize(true)
                    }
                }
            }
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

    private fun setupView() {

        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        bnd.reasonEditText.setOnClickListener {
            val selectedReasonItem = bnd.reasonEditText.toString()
            displayReasons(selectedReasonItem)

        }

        bnd.subReasonEditText.setOnClickListener {
            retrievedReason?.let {
                displaySubReasons(it)
            }
        }

        bnd.submitButton.setButtonEnable(false)


        bnd.submitButton.setOnClickListener {
            bnd.submitButton.loaded()
            if (captureMovementData.movementType == "exit" && bnd.reasonEditText.text.toString() != "Transfer") {
                val action =
                    SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToVehicleConfirmationFragment(
                        vehicleMaxId = captureMovementData.vehicle.maxVehicleId!!,
                        champion = captureMovementData.vehicle.champion?.let {
                            getString(
                                R.string.default_name, it.firstName,
                                it.lastName
                            )
                        } ?: "N/A",
                        reason = bnd.subReasonEditText.text.toString(),
                        movementType = captureMovementData.movementType
                    )
                navigate(action)
            } else {
                navigateToRegisterVehicle()
            }

        }

    }

    private fun displaySubReasons(selectedReason: Reason) {
        val subReasons: List<SubReason> = if (selectedReason.slug == "completed_hp") {
            val _subReason = if (captureMovementData.movementType == "entry") {
                selectedReason.subReasons?.find { it.slug == "pick_up_papers" }!!
            } else {
                selectedReason.subReasons?.find { it.slug == "completed_hp" }!!
            }
            listOf(_subReason)
        } else {
            selectedReason.subReasons!!
        }
        sharedBottomSheetViewModel.submitSubReasons(subReasons)
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToListBottomSheetFragment(
                selectedItem,
                "SUBREASON",
                captureMovementData.movementType
            )
        navigate(action)
    }

    private fun displayReasons(clicked: String) {
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToListBottomSheetFragment(
                clicked,
                "REASON",
                captureMovementData.movementType
            )
        navigate(action)
    }
}