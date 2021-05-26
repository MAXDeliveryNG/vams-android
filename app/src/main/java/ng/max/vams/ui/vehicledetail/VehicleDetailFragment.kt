package ng.max.vams.ui.vehicledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.VehicleDetailFragmentBinding
import ng.max.vams.util.Constant.DATETIME_FORMAT_API
import ng.max.vams.util.Constant.DATETIME_FORMAT_DETAIL
import ng.max.vams.util.hide
import ng.max.vams.util.showDialog
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class VehicleDetailFragment : Fragment() {

    private val vehicleDetailViewModel: VehicleDetailViewModel by viewModels()
    private lateinit var bnd: VehicleDetailFragmentBinding
    private val args: VehicleDetailFragmentArgs by navArgs()

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

        vehicleDetailViewModel.getVehiclesResponse.observe(viewLifecycleOwner){result->
            when(result){
                is Result.Error -> {
                    showDialog("Error", result.message)
                }
                Result.Loading -> {}
                is Result.Success -> {
                    bindViews(result.value)
                }
            }
        }
        vehicleDetailViewModel.actionGetVehicleDetail(args.vehicleId)
    }

    private fun bindViews(vehicle: DbVehicle) {
        bnd.vehicleIdTv.text = vehicle.maxVehicleId

        if (vehicle.lastVehicleMovement == null){
            bnd.reasonBlock.hide()
            bnd.currentStateCard.hide()
            bnd.vehicleDetailCard.hide()
        }else{
            bnd.reasonTv.text = vehicle.lastVehicleMovement.reason
            bnd.locationTv.text = getString(R.string.location_data, vehicle.lastVehicleMovement.locationName, vehicle.movementType)
            bnd.timeStampTv.text = getFormattedDate(vehicle.lastVehicleMovement.createdAt)
            bnd.emailTv.text = vehicle.lastVehicleMovement.creatorEmail?: "N/A"
            bnd.plateNumberTv.text = vehicle.lastVehicleMovement.plateNumber
            bnd.vehicleTypeTv.text = vehicle.lastVehicleMovement.vehicleType
            bnd.odometerTv.text = vehicle.lastVehicleMovement.odometer.toString()
        }

    }

    private fun getFormattedDate(dateString: String): String{
        val dateFormatIn = SimpleDateFormat(DATETIME_FORMAT_API, Locale.getDefault())
        return dateFormatIn.parse(dateString)?.let { dateIn ->
            val dateFormatOut = SimpleDateFormat(DATETIME_FORMAT_DETAIL, Locale.getDefault())
            dateFormatOut.format(dateIn)
        } ?: "N/A"
    }

}