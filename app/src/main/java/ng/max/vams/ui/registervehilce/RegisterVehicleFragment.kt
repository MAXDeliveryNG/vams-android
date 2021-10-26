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
import ng.max.vams.data.MovementData
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
    private var locations: Array<String> = arrayOf()


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
        bnd.submitButton.setButtonEnable(false)
        bnd.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        bnd.locationEditText.setOnClickListener {
            val selected = bnd.locationEditText.text.toString()
            val action =
                RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToListBottomSheetFragment(
                    selected,
                    locations,
                    "REGISTER"
                )
            findNavController().navigate(action)
        }


        bnd.submitButton.setOnClickListener {
            bnd.submitButton.loaded()
            val action =
                RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToVehicleConfirmationFragment(
                    vehicleMaxId = args.vehicleMaxId,
                    champion = args.champion, reason = args.subReasonName, movementType = args.vehicleMovement
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
                                field?.error = null
                                true
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
            listOf(
                movementData.keyLocation,
                movementData.keyOdometer
            )
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
                        locations = result.value.map { location -> location.name }.toTypedArray()
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
                        args.subReasonId, args.locationToId, args.vehicleMovement
                    )
                }
            })
            actionGetAllLocation()
        }

        sharedBottomSheetViewModel.getSelectedItemResponse.observe(viewLifecycleOwner) { selectedItem ->
            bnd.locationEditText.setText(selectedItem)
        }
    }

    private fun cleanVehicleTable(id: String) {
        registerVehicleViewModel.deleteVehicle(id)
    }


}