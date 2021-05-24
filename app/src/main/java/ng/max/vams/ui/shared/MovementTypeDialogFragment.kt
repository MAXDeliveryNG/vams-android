package ng.max.vams.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_movement_type_dialog.*
import ng.max.vams.R
import ng.max.vams.databinding.FragmentCompleteRegistrationBinding
import ng.max.vams.databinding.FragmentMovementTypeDialogBinding

class MovementTypeDialogFragment : BottomSheetDialogFragment() {

    private lateinit var bnd: FragmentMovementTypeDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentMovementTypeDialogBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        entryView.setOnClickListener {
            val action = MovementTypeDialogFragmentDirections.actionMovementTypeDialogFragmentToAssetReasonFragment("entry")
            findNavController().navigate(action)

        }

        exitView.setOnClickListener {
            val action = MovementTypeDialogFragmentDirections.actionMovementTypeDialogFragmentToAssetReasonFragment("exit")
            findNavController().navigate(action)

        }
    }


    companion object {
    }
}