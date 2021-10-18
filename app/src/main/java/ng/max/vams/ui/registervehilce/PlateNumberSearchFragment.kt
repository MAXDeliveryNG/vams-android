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
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.*

@AndroidEntryPoint
class PlateNumberSearchFragment : Fragment() {

    private val plateNumberSearchViewModel: PlateNumberSearchViewModel by viewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private val args: PlateNumberSearchFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plate_number_search, container, false)
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
                    manageContentViews(false)
                    showDialog("Error", result.message)
                }
                Result.Loading -> {
                    manageContentViews()
                }
                is Result.Success -> {
                    if(result.value.isEmpty()){
                        manageContentViews(false)
                        showDialog("Error", "Vehicle with ${plateNumberEditText.text.toString()} not found.")
                    }else{
                        navigateToVehicleDetail(result.value.first())
                    }
                }
            }
        }
    }

    private fun manageContentViews(isLoading: Boolean = true) {
        if (isLoading){
            loadingContent.show()
            mainContent.gone()
            backBtn.hide()
        }else{
            loadingContent.gone()
            mainContent.show()
            backBtn.show()

        }
    }

    private fun navigateToVehicleDetail(vehicle: Vehicle) {
        if(vehicle.vehicleMovement != null && vehicle.vehicleMovement == args.movementType){
            manageContentViews(false)
            val errorMessage = if(vehicle.vehicleMovement == "entry"){ getString(R.string.movement_error_message, " checked in")
            }else{
                getString(R.string.movement_error_message, "checked out")
            }
            showDialog("Error", errorMessage)
        }else{
            manageContentViews(true)
            sharedViewModel.submitData(CaptureMovementData(args.movementType, vehicle))
            findNavController().navigate(R.id.selectMovementReasonFragment)
        }
    }

    private fun setupView() {
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        plateNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                submitButton.isEnabled = s.toString().isNotEmpty() && s.toString().isNotBlank()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        submitButton.setOnClickListener {
            hideKeypad(requireActivity(), plateNumberEditText)
            plateNumberSearchViewModel.actionSearch(plateNumberEditText.text.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlateNumberSearchFragment()
    }
}