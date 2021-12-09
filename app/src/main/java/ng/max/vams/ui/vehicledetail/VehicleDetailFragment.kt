package ng.max.vams.ui.vehicledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.databinding.VehicleDetailFragmentBinding
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.Constant.DATETIME_FORMAT_API
import ng.max.vams.util.Constant.DATETIME_FORMAT_DETAIL
import ng.max.vams.util.hide
import ng.max.vams.util.show
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class VehicleDetailFragment : Fragment() {

    private val vehicleDetailViewModel: VehicleDetailViewModel by viewModels()
    private lateinit var bnd: VehicleDetailFragmentBinding
    private val args: VehicleDetailFragmentArgs by navArgs()
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
        } else {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.dialog_exit_label).uppercase()
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


        bnd.continueButton.setOnClickListener {
            findNavController().navigate(VehicleDetailFragmentDirections.actionVehicleDetailFragmentToSelectMovementReasonFragment())
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

    private fun bindViews(vehicle: DbVehicle) {
        bnd.vehicleIdTv.text = vehicle.maxVehicleId

        if (vehicle.lastVehicleMovement == null){
            bnd.championDetailCard.hide()
            bnd.movementTypeCard.hide()
            bnd.vehicleDetailCard.hide()
        }else{
            bnd.reasonTv.text = vehicle.lastVehicleMovement.reason.name
            bnd.locationTv.text = vehicle.lastVehicleMovement.locationName
            bnd.plateNumberTv.text = vehicle.lastVehicleMovement.plateNumber
            bnd.vehicleTypeTv.text = vehicle.lastVehicleMovement.vehicleType
            bnd.odometerTv.text = getString(R.string.odometer_data, vehicle.lastVehicleMovement.odometer.toString())
        }
    }

    private fun getFormattedDate(dateString: String): String{
        val dateFormatIn = SimpleDateFormat(DATETIME_FORMAT_API, Locale.getDefault())
        return dateFormatIn.parse(dateString)?.let { dateIn ->
            val offset = TimeZone.getDefault().getOffset(dateIn.time)
            val cal = Calendar.getInstance().apply {
                time = dateIn
                add(Calendar.MILLISECOND, offset)
            }
            val dateFormatOut = SimpleDateFormat(DATETIME_FORMAT_DETAIL, Locale.getDefault())
            dateFormatOut.format(cal.time)
        } ?: "N/A"
    }

}