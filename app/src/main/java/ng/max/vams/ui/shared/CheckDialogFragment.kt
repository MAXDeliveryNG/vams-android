package ng.max.vams.ui.shared

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_check_dialog.*
import ng.max.vams.R

class CheckDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_check_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkInStateView.setOnClickListener {
            val action = CheckDialogFragmentDirections.actionCheckDialogFragmentToAssetReasonFragment("checked_in")
            findNavController().navigate(action)

        }

        checkOutStateView.setOnClickListener {
            val action = CheckDialogFragmentDirections.actionCheckDialogFragmentToAssetReasonFragment("checked_out")
            findNavController().navigate(action)

        }
    }


    companion object {
    }
}