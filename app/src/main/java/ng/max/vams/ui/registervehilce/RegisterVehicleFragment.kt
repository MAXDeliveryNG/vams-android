package ng.max.vams.ui.registervehilce

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.BR
import ng.max.vams.R
import ng.max.vams.adapter.SearchResultsAdapter
import ng.max.vams.data.MovementData
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.RegisterVehicleFragmentBinding
import ng.max.vams.util.Helper
import ng.max.vams.util.gone
import ng.max.vams.util.showDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class RegisterVehicleFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterVehicleFragment()
    }

    private var requiredFieldsValidationEnabledStates: HashMap<String, Boolean> = HashMap()
    private lateinit var bnd: RegisterVehicleFragmentBinding
    private val registerVehicleViewModel: RegisterVehicleViewModel by viewModels()
    private val args: RegisterVehicleFragmentArgs by navArgs()
    private var movementData = MovementData()
    private var suggestionsAdapter: SearchResultsAdapter? = null
    private var exitSearch: Boolean = false
    private var vehicleId: String? = null
    private var valueMap: HashMap<String, String> = HashMap()
    var atvToggleMap: HashMap<MaterialAutoCompleteTextView, Boolean> = HashMap()


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
        bnd.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        populateMovementType()


        hackFixForDropdownClickBehaviour(bnd.locationEditText, bnd.locationInputLaout)
        hackFixForDropdownClickBehaviour(bnd.vehicleTypeEditText, bnd.vehicleTypeInputLayout)
        hackFixForDropdownClickBehaviour(bnd.movementTypeEditText, bnd.movementTypeInputLayout)
        bnd.plateNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { term ->
                    if (!exitSearch) {
                        registerVehicleViewModel.actionSearch(term.toString())
                    }
                    exitSearch = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        suggestionsAdapter = SearchResultsAdapter(requireContext(), ArrayList())
        bnd.plateNumberEditText.threshold = 2
        bnd.plateNumberEditText.dropDownVerticalOffset = 6
        bnd.plateNumberEditText.dropDownHeight = resources.getDimension(R.dimen.dimen_100dp).toInt()
        bnd.plateNumberEditText.setAdapter(suggestionsAdapter)

        bnd.plateNumberEditText.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p3 ->
                exitSearch = true
                val vehicle = suggestionsAdapter?.getItem(position)

                vehicle?.let {
                    vehicleId = it.id
                    populateForm(it)
                }
            }

        bnd.submitButton.setOnClickListener {
            if (vehicleId != null) {
                movementData.apply {
                    movementType = this.movementType?.toLowerCase(Locale.getDefault())
                }
                registerVehicleViewModel.registerMovement(movementData, vehicleId!!)
            }else{
                bnd.submitButton.loaded()
                Toast.makeText(requireContext(), "Missing Vehicle Id", Toast.LENGTH_LONG).show()
            }
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
                    BR.plateNumber -> {
                        field = bnd.plateNumberInputLayout
                        value = movementData.plateNumber
                    }
                    BR.location -> {
                        field = bnd.locationInputLaout
                        value = movementData.location
                    }
                    BR.vehicleType -> {
                        field = bnd.vehicleTypeInputLayout
                        value = movementData.vehicleType
                    }
                    BR.movementType -> {
                        field = bnd.movementTypeInputLayout
                        value = movementData.movementType
                    }
                    BR.vehicleId -> {
                        field = bnd.vehicleIdInputLayout
                        value = movementData.vehicleId
                    }
                    BR.odometerReading -> {
                        field = bnd.odometerReadingInputLayout
                        value = movementData.odometerReading
                    }
                    BR.reason -> {
                        field = bnd.reasonInputLayout
                        value = movementData.reason
                    }
                    else ->{
                        ignoreProperty = true
                    }
                }
                val fieldKey: String = field?.tag?.toString() ?: ""
                val validationEnabled = checkValidationEnabledStates(fieldKey)

                if (!ignoreProperty){
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
                movementData.keyVehiclePlateNumber,
                movementData.keyLocation,
                movementData.keyVehicleType,
                movementData.keyMovementType,
                movementData.keyVehicleId,
                movementData.keyReason,
                movementData.keyOdometer
            )
        }
    }
    private fun setupViewModel() {
        with(registerVehicleViewModel) {
            getReasonResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(requireContext(), "Something wrong happened.", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }
                    Result.Loading -> {}
                    is Result.Success -> {
                        val reason = result.value.name
                        bnd.registerHeader.text = reason
                        bnd.reasonEditText.setText(reason)
                        bnd.reasonEditText.isEnabled = false
                    }
                }

            }

            getVehicleTypeResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                    Result.Loading -> {
                    }
                    is Result.Success -> {
                        val vehicleType = result.value.name
                        bnd.vehicleTypeEditText.setText(vehicleType)
                    }
                }

            }

            getLocationResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                    Result.Loading -> { }
                    is Result.Success -> {
                        val location = result.value.name
                        validateLocation(location)
                    }
                }

            }

            getSearchResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        bnd.plateNumberProgressbar.gone()
                        showDialog("Error", result.message)
                    }
                    Result.Loading -> {
                        bnd.plateNumberProgressbar.show()
                    }
                    is Result.Success -> {
                        bnd.plateNumberProgressbar.gone()
                        suggestionsAdapter?.update(ArrayList(result.value))
                    }
                }
            }

            getLocationsResponse.observe(viewLifecycleOwner){result->
                when(result){
                    is Result.Error -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        val adapter: ArrayAdapter<String> =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                result.value.map { location -> location.name }
                            )
                        bnd.locationEditText.setAdapter(adapter)
                    }
                }
            }

            getVehiclesTypeResponse.observe(viewLifecycleOwner){result->
                when(result){
                    is Result.Error -> {}
                    Result.Loading -> {
                    }
                    is Result.Success -> {
                        val adapter: ArrayAdapter<String> =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                result.value.map { vehicleType -> vehicleType.name }
                            )
                        bnd.vehicleTypeEditText.setAdapter(adapter)
                    }
                }
            }

            getRegisterMovementResponse.observe(viewLifecycleOwner){result ->
                when(result){
                    is Result.Error -> {
                        bnd.submitButton.loaded()
                        showDialog("Error", result.message)
                    }
                    Result.Loading -> {}
                    is Result.Success -> {
                        bnd.submitButton.loaded()
                        val action  = RegisterVehicleFragmentDirections.actionRegisterVehicleFragmentToCompleteRegistrationFragment(movementData.movementType!!, movementData.vehicleId!!)
                        findNavController().navigate(action)
                    }
                }

            }
        }

        registerVehicleViewModel.actionGetReason(args.reasonId)
        registerVehicleViewModel.actionGetAllLocation()
        registerVehicleViewModel.actionGetAllVehicleType()
    }

    private fun validateLocation(location: String) {
        if (Helper.supportedLocation.contains(location)){
            bnd.locationEditText.setText(location)
            bnd.locationEditText.isEnabled = false
        }else{
            bnd.locationEditText.text = null
            bnd.vehicleIdEditText.text = null
            bnd.vehicleTypeEditText.text = null
            bnd.plateNumberEditText.text = null

            bnd.locationEditText.isEnabled = true
            bnd.vehicleTypeEditText.isEnabled = true
            bnd.vehicleIdEditText.isEnabled = true

        }
    }

    private fun populateForm(vehicle: Vehicle) {
        bnd.vehicleIdEditText.setText(vehicle.maxVehicleId)
        bnd.vehicleIdEditText.isEnabled = false
        bnd.plateNumberEditText.setText(vehicle.plateNumber)

        registerVehicleViewModel.actionGetLocation(vehicle.locationId)
        registerVehicleViewModel.actionGetVehicleType(vehicle.vehicleTypeId)
    }

    @Suppress("ObjectLiteralToLambda")
    @SuppressLint("ClickableViewAccessibility")
    private fun hackFixForDropdownClickBehaviour(
        atv: MaterialAutoCompleteTextView,
        textInputLayout: TextInputLayout
    ) {
        atvToggleMap[atv] = false

        atv.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                if (motionEvent?.action == MotionEvent.ACTION_UP) {
                    textInputLayout.performClick()
                    if (atvToggleMap[atv]!!) {
                        atv.dismissDropDown()
                        atvToggleMap[atv] = false
                    } else {
                        atv.showDropDown()
                        atvToggleMap[atv] = true
                    }
                }
                return false
            }
        })
    }

    private fun populateMovementType(){
        val movementType = arrayListOf("Entry", "Exit")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                movementType
            )
        bnd.movementTypeEditText.setAdapter(adapter)
        bnd.movementTypeEditText.postDelayed({
            if (args.movementType == "entry"){
                bnd.movementTypeEditText.setText(movementType[0], false)
            }else{
                bnd.movementTypeEditText.setText(movementType[1], false)
            }
            bnd.movementTypeEditText.setSelection(bnd.movementTypeEditText.text.count())
            bnd.movementTypeEditText.isEnabled = false
        }, 10)
    }

}