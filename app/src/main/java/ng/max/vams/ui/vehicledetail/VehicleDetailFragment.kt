package ng.max.vams.ui.vehicledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.databinding.VehicleDetailFragmentBinding
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.show

@AndroidEntryPoint
class VehicleDetailFragment : Fragment() {

    private lateinit var bnd: VehicleDetailFragmentBinding
    private lateinit var captureMovementData: CaptureMovementData
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = VehicleDetailFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView(){
        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        bnd.continueButton.setOnClickListener {
            sharedViewModel.submitData(captureMovementData)
            findNavController().navigate(VehicleDetailFragmentDirections.actionVehicleDetailFragmentToSelectMovementReasonFragment())
        }

    }

    private fun setupViewModel(){

        sharedViewModel.getCaptureMovementDataResponse.observe(viewLifecycleOwner, {
            captureMovementData = it
            populateView(it)
        })
    }


    private fun populateView(_captureData: CaptureMovementData) {
        if (_captureData.movementType == "entry") {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_entry_label).uppercase()
            bnd.movementTypeHeaderTv.text = getString(R.string.movement_type_header, "Check Out").uppercase()
        } else {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_exit_label).uppercase()
            bnd.movementTypeHeaderTv.text = getString(R.string.movement_type_header, "Check In").uppercase()
        }

        bnd.vehicleIdTv.text = _captureData.vehicle.maxVehicleId
        bnd.plateNumberTv.text = _captureData.vehicle.plateNumber

        _captureData.vehicle.champion?.let {
            val champion = getString(
                R.string.default_name, it.firstName,
                it.lastName
            )
            bnd.championNameTv.text = champion
            bnd.championIdTv.text = it.maxChampionId
            bnd.championDetailCard.show()
        }

        _captureData.vehicle.lastVehicleMovement?.let {
            bnd.reasonTv.text = it.reason.parentReasonName
            bnd.subreasonTv.text = it.reason.name
            bnd.vehicleTypeTv.text = it.vehicleType
            bnd.odometerTv.text = getString(
                R.string.odometer_data,
                it.odometer.toString()
            )
            bnd.locationTv.text = it.locationName
            bnd.movementTypeCard.show()
        }

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