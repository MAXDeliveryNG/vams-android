package ng.max.vams.ui.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.ResetPasswordFragmentBinding
import ng.max.vams.util.showDialog

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private lateinit var bnd: ResetPasswordFragmentBinding
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.loginFragment, true)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = ResetPasswordFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }
    private fun setupView(){
        bnd.submitButton.setOnClickListener {
            val email = bnd.emailEditText.text.toString()
            val tempPassword = bnd.tempPasswordEditText.text.toString()
            val password = bnd.newPasswordEditText.text.toString()

            if (validateUserInputs(email, tempPassword, password)){
                resetPasswordViewModel.actionResetPassword(email, tempPassword, password)
            }else{
                bnd.submitButton.loaded()
            }
        }

    }
    private fun setupViewModel(){
        with(resetPasswordViewModel){
            getResetPasswordResponse.observe(viewLifecycleOwner){result->
                when(result){
                    is Result.Error -> {
                        bnd.submitButton.loaded()
                        showDialog("Error", result.message)
                    }
                    Result.Loading -> {}
                    is Result.Success -> {
                        bnd.submitButton.loaded()
                        findNavController().popBackStack(R.id.loginFragment, true)
                    }
                }
            }
        }
    }

    private fun validateUserInputs(email: String, tempPassword: String, password: String): Boolean{
        return if (email.isEmpty()){
            bnd.emailInputLayout.error = "Please enter your email"
            false
        }else {
            bnd.emailInputLayout.error = null
            true
        } && if (tempPassword.isEmpty()){
            bnd.tempPasswordInputLayout.error = "Please enter your temp password"
            false
        }else{
            bnd.tempPasswordInputLayout.error = null
            true
        } && if (password.isEmpty()){
            bnd.newPasswordInputLayout.error = "Please enter your new password"
            false
        }else{
            bnd.newPasswordInputLayout.error = null
            true
        }
    }
}