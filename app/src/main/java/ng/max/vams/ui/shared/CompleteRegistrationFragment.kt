package ng.max.vams.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ng.max.vams.R
import ng.max.vams.databinding.FragmentCompleteRegistrationBinding

class CompleteRegistrationFragment : Fragment() {

    private lateinit var bnd: FragmentCompleteRegistrationBinding
    private val args: CompleteRegistrationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentCompleteRegistrationBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bnd.successMessageTv.text = getString(
            R.string.you_have_successfully_label, if (args.movementType == "entry") {
                "checked in"
            } else {
                "checked out"
            }
        )
        bnd.vehicleIdTv.text = args.vehicleId

        bnd.backToDashboardBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}