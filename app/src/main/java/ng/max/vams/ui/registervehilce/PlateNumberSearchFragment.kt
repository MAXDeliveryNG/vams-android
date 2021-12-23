package ng.max.vams.ui.registervehilce

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_plate_number_search.*
import ng.max.vams.R
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.DataMapper
import ng.max.vams.data.remote.response.RemoteVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentPlateNumberSearchBinding
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.hideKeypad
import ng.max.vams.util.showDialog

@AndroidEntryPoint
class PlateNumberSearchFragment : Fragment() {

    private lateinit var bnd: FragmentPlateNumberSearchBinding
    private val plateNumberSearchViewModel: PlateNumberSearchViewModel by viewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private val args: PlateNumberSearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentPlateNumberSearchBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupViewModel() {
        plateNumberSearchViewModel.getSearchResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    bnd.submitButton.loaded()
                    showDialog("Error", result.message)
                }
                Result.Loading -> {
                    //button is in loading state
                }
                is Result.Success -> {
                    bnd.submitButton.loaded()
                    if (result.value.isEmpty()) {
                        showDialog(
                            "Error",
                            "RemoteVehicle with ${plateNumberEditText.text.toString()} not found."
                        )
                    } else {
                        navigateToVehicleDetail(result.value.first())
                    }
                }
            }
        }
    }


    private fun navigateToVehicleDetail(remoteVehicle: RemoteVehicle) {
        if(remoteVehicle.vehicleMovement == null && args.movementType == "exit"){
            val errorMessage = getString(R.string.mo_movement_error_message)
            showDialog("Error", errorMessage)
        } else if (remoteVehicle.vehicleMovement != null && remoteVehicle.vehicleMovement == args.movementType) {
            val errorMessage = if (remoteVehicle.vehicleMovement == "entry") {
                getString(R.string.movement_error_message, " checked in")
            } else {
                getString(R.string.movement_error_message, "checked out")
            }
            showDialog("Error", errorMessage)
        } else {
            sharedViewModel.submitData(CaptureMovementData(args.movementType, DataMapper().invoke(remoteVehicle)))
            findNavController().navigate(R.id.action_plateNumberSearchFragment_to_vehicleDetailFragment)
        }
    }

    private fun setupView() {
        bnd.submitButton.setButtonEnable(false)
        bnd.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if(args.movementType == "entry"){
            bnd.searchToolbar.text = getString(R.string.dialog_entry_label).uppercase()
            bnd.searchInfoTip.text = getString(R.string.entry_search_info_tip)
        }else{
            bnd.searchToolbar.text = getString(R.string.dialog_exit_label).uppercase()
            bnd.searchInfoTip.text = getString(R.string.exit_search_info_tip)
        }

        bnd.plateNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bnd.submitButton.setButtonEnable(s.toString().isNotEmpty() && s.toString().isNotBlank())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        submitButton.setOnClickListener {
            hideKeypad(requireActivity(), bnd.plateNumberEditText)
            plateNumberSearchViewModel.actionSearch(bnd.plateNumberEditText.text.toString())
        }
    }
}