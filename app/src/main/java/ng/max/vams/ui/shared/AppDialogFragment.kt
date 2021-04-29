package ng.max.vams.ui.shared

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import ng.max.vams.R
import ng.max.vams.databinding.FragmentAppDialogBinding

class AppDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAppDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_app_dialog, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            binding.titleTextView.text = arguments?.getString("title") ?: "Info"
            binding.messageTextView.text = arguments?.getString("message") ?: "Something went wrong!"

            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        binding.okButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {

    }
}