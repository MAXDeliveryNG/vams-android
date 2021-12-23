package ng.max.vams.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.remote.response.Location
import ng.max.vams.databinding.FragmentTransferCheckinConfirmationBottomSheetBinding

@AndroidEntryPoint
class TransferCheckInConfirmationBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var bnd: FragmentTransferCheckinConfirmationBottomSheetBinding
    private val transferLocationViewModel: TransferLocationViewModel by viewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val args: TransferCheckInConfirmationBottomSheetFragmentArgs by navArgs()

    private var locations: List<Location> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentTransferCheckinConfirmationBottomSheetBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true) //Allows touching outside window's bound to close dialog
        setupView()
    }

    private fun setupView() {
        if (args.actionType == "accept"){
            bnd.transferActionIv.setImageResource(R.drawable.ic_accept_transfer)
            bnd.transferInfoTv.text = "CONFIRM VEHICLE TRANSFER \n(ENTRY)"
            bnd.confirmTransferBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.button_state)
            bnd.confirmTransferBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_text_state))
        }else{
            bnd.transferActionIv.setImageResource(R.drawable.ic_reject_transfer)
            bnd.transferInfoTv.text = "REJECT VEHICLE TRANSFER \n(ENTRY)"
            bnd.confirmTransferBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.error_button_state)
            bnd.confirmTransferBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_button_text_state))
        }

        bnd.confirmTransferBtn.setOnClickListener {
            findNavController().navigateUp()
            val action = if (args.actionType == "accept"){
                "confirmed"
            }else{
                "rejected"
            }
            sharedBottomSheetViewModel.submitTransferAction(action)
        }
        bnd.cancelTransferBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}