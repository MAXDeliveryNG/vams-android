package ng.max.vams.ui.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentForgotPasswordBinding
import ng.max.vams.util.showDialog

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var bnd: FragmentForgotPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView(){
        bnd.submitButton.setOnClickListener {
            val userInput = bnd.emailEditText.text.toString()
            if (userInput.isNotEmpty()){
                bnd.emailInputLayout.error = null
                forgotPasswordViewModel.actionForgotPassword(userInput)
            }else{
                bnd.submitButton.loaded()
                bnd.emailInputLayout.error = "Please enter your email"
            }
        }

        bnd.loginTv.setOnClickListener {
            findNavController().popBackStack(R.id.loginFragment, true)
        }
    }
    private fun setupViewModel(){
        forgotPasswordViewModel.getForgotPasswordResponse.observe(viewLifecycleOwner){result->
            when(result){
                is Result.Error -> {
                    bnd.submitButton.loaded()
                    showDialog("Error", result.message)
                }
                Result.Loading -> {}
                is Result.Success -> {
                    bnd.submitButton.loaded()
                    Toast.makeText(requireContext(), "Password has been sent to your inbox", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.resetPasswordFragment)
                }
            }
        }
    }
    companion object {
    }
}