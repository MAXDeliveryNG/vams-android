package ng.max.vams.ui.assetreason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.databinding.VehicleConfirmationFragmentBinding

@AndroidEntryPoint
class VehicleConfirmationFragment : BottomSheetDialogFragment() {

    private lateinit var bnd: VehicleConfirmationFragmentBinding
    private val sharedViewModel: VehicleConfirmationViewModel by activityViewModels()
    private val args: VehicleConfirmationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = VehicleConfirmationFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bnd.confirmBtn.setOnClickListener {
            findNavController().navigateUp()
            bnd.confirmBtn.loaded()
            sharedViewModel.submitConfirmation(true)

        }

        bnd.closeBtn.setOnClickListener {
            findNavController().navigateUp()
            sharedViewModel.submitConfirmation(false)
        }

        val movementType = if (args.movementType == "entry"){
            "Check-In"
        }else{"Check-Out"}
        bnd.textView3.text = getString(R.string.confirm_movement_label, movementType)
        bnd.textView6.text = getString(R.string.vehicle_detail_title, movementType)
        bnd.vehicleMaxIdTv.text = args.vehicleMaxId
        bnd.championTv.text = args.champion
        bnd.reasonTv.text = args.reason

    }

}