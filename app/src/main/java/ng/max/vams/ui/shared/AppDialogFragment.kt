package ng.max.vams.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ng.max.vams.R
import ng.max.vams.data.manager.AppManager
import ng.max.vams.databinding.FragmentAppDialogBinding
import ng.max.vams.util.gone
import ng.max.vams.util.show

class AppDialogFragment : DialogFragment() {

    private lateinit var bnd: FragmentAppDialogBinding
    private var shouldLogout: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        bnd = FragmentAppDialogBinding.inflate(
            inflater, container, false
        )
        shouldLogout = arguments?.getBoolean("shouldLogOut") ?: false
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            bnd.titleTextView.text = arguments?.getString("title") ?: "Info"
            bnd.messageTextView.text = arguments?.getString("message") ?: "Something went wrong!"

            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            if (shouldLogout){
                bnd.cancelButton.show()
                bnd.okButton.text = getString(R.string.sign_out_label)
            }else{
                bnd.cancelButton.gone()
                bnd.okButton.text = getString(R.string.ok_label)
            }
        }

        bnd.okButton.setOnClickListener {
            if(shouldLogout){
                AppManager.logout()
                findNavController().navigate(R.id.action_appDialogFragment_to_homeFragment)
            }
            dismiss()
        }

        bnd.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {

    }
}