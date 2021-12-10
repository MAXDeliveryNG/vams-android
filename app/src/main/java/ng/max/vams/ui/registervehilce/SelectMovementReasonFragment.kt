package ng.max.vams.ui.registervehilce

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.select_movement_reason_fragment.*
import ng.max.vams.BR
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.MovementData
import ng.max.vams.data.remote.response.LoginData
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.SelectMovementReasonFragmentBinding
import ng.max.vams.ui.shared.ListBottomSheetFragment
import ng.max.vams.ui.shared.SharedBottomSheetViewModel
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.GridSpacingItemDecoration
import ng.max.vams.util.hide
import ng.max.vams.util.show

@AndroidEntryPoint
class SelectMovementReasonFragment : Fragment() {

    private lateinit var bnd: SelectMovementReasonFragmentBinding
    private val viewModel: SelectMovementReasonViewModel by activityViewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private var movementData = MovementData()
    private var requiredFieldsValidationEnabledStates: HashMap<String, Boolean> = HashMap()
    private var selectedItem: String? = null
    private var selectedReasonName: String? = null
    private lateinit var captureMovementData: CaptureMovementData
    var subReasonPicked: String? = null
    var lastMovementSubReason: String? = null
    lateinit var retrievedReason: Reason

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

    private fun setupDatabinding(){
        bnd.reasonModel?.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                var value: String? = ""
                var field: TextInputLayout? = null
                var ignoreProperty = false
                var isCompleted = false

                when (propertyId) {
                    BR.reason -> {
                        field = bnd.movementReasonSpinner
                        value = selectedReasonName
                    }
                    BR.subreason -> {
                        field = bnd.movementSubreasonSpinner
                        value = subReasonPicked
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
                                bnd.subreasonWrap.show()
                                    Log.d(
                                        "TAGFETCHEDREASON",
                                        "onPropertyChanged: $subReasonPicked"
                                    )
                                    if (subReasonPicked == "Financial Default") {
                                        bnd.amountWrap.show()
                                    }else{
                                        bnd.amountWrap.hide()
                                    }
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
                movementData.keyReason,
                movementData.keySubReason
            )
        }
    }


    private fun setupViewModel() {
        sharedViewModel.getCaptureMovementDataResponse.observe(viewLifecycleOwner, {
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

//        viewModel.getReasonsResponse.observe(viewLifecycleOwner, { result ->
//            when (result) {
//                is Result.Error -> {
//                    Snackbar.make(reasonRv, result.message, Snackbar.LENGTH_LONG).show()
//                }
//                is Result.Loading -> {
//                }
//                is Result.Success -> {
//                        retrievedReason = result.value.find { it.name == "Retrieved" }!!
//                    reasonAdapter.adapterList = if (captureMovementData.movementType == "entry") {
//                        result.value.filter { it.slug != "activated" }
//                    } else {
//                        result.value.filter { it.slug != "new" }
//                    }
//                }
//            }
//        })

        viewModel.getReasonByNameResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success -> {
                    retrievedReason = result.value.find { it.name == selectedReasonName }!!
                }
            }
        })


//        sharedBottomSheetViewModel.getSelectedTransferLocationIdResponse.observe(
//            viewLifecycleOwner,
//            { _locationToId ->
//                navigateToRegisterVehicle(subReason, _locationToId)
//            })


//            (reasonAdapter.adapterList as List<Reason>).forEach { reason ->
//                reason.subReasons?.forEach { _subReason ->
//                    if (_subReason.slug == it) {
//
//                        subReason = _subReason
//                        if (captureMovementData.movementType == "exit" &&
//                            lastMovementSubReason != null && lastMovementSubReason != _subReason.name) {
//                            Toast.makeText(
//                                requireContext(),
//                                "Please select $lastMovementSubReason and try again.",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            return@observe
//                        } else {
//                            subReason = _subReason
//                        }
//                    }
//                }
//            }
//
//            if (selectedReasonSlug == "transfer") {
//                val action =
//                    SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToTransferLocationBottomSheetFragment(
//                        subReason!!.name, captureMovementData.vehicle.locationId
//                    )
//                findNavController().navigate(action)
//            } else {
//                navigateToRegisterVehicle(subReason, null)
//            }


    }

    private fun navigateToRegisterVehicle(subReason: SubReason?, _locationToId: String?) {
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToRegisterVehicleFragment(
                vehicleId = captureMovementData.vehicle.id,
                vehicleMaxId = captureMovementData.vehicle.maxVehicleId,
                vehicleMovement = captureMovementData.movementType,
                locationId = captureMovementData.vehicle.locationId,
                locationToId = _locationToId,
                subReasonId = subReason!!.id,
                subReasonName = subReason.name,
                champion = captureMovementData.vehicle.champion?.let {
                    getString(
                        R.string.default_name, it.firstName,
                        it.lastName
                    )
                } ?: "N/A",
                retrievedSubReasonIds = retrievedReason.subReasons!!.map { it.id }.toTypedArray()
            )

        findNavController().navigate(action)
    }
    
    

    private fun populateView(_captureData: CaptureMovementData) {

        if (_captureData.movementType == "entry") {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_entry_label).uppercase()
        } else {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_exit_label).uppercase()
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
//        viewModel.actionGetLocations()
    }

    private fun setupView() {

        bnd.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        bnd.movementReasonTv.setOnClickListener {
            val selectedReasonItem = bnd.movementReasonTv.toString()
            displayReasons(selectedReasonItem)

        }

        bnd.movementSubreasonTv.setOnClickListener {
            displaySubReasons(retrievedReason)
        }

        bnd.submitButton.setButtonEnable(false)

        /*if (captureMovementData.movementType == "exit") {
            val lastMovement = captureMovementData.vehicle.lastVehicleMovement!!
            val lastMovementReason = lastMovement.reason
            val reason =
                (reasonAdapter.adapterList as List<Reason>).find { it.id == lastMovementReason.parentReasonId }!!

            if (selectedReason.id != lastMovementReason.parentReasonId && selectedReasonSlug != "activated") {
                Toast.makeText(
                    requireContext(),
                    "Please check out with ${reason.name}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (selectedReasonSlug == "activated" && reason.slug != "new") {
                    Toast.makeText(
                        requireContext(),
                        "Please check out with ${reason.name}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    lastMovementSubReason = lastMovementReason.name
                    displaySubReasons(selectedReason)
                }
            }
        } else {
            displaySubReasons(selectedReason)
        }*/

        bnd.submitButton.setOnClickListener {
            bnd.submitButton.loaded()
            navigateToRegisterVehicle(retrievedReason.subReasons?.last(),"")
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
        findNavController().navigate(action)
    }

    private fun displayReasons(clicked: String) {
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToListBottomSheetFragment(
                clicked,
                "REASON",
                captureMovementData.movementType
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TAGONDESTROY", "onDestroyView:************** WAS CALLED")

    }

}