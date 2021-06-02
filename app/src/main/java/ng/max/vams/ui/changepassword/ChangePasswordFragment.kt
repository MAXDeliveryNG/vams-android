package ng.max.vams.ui.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.ChangePasswordFragmentBinding
import ng.max.vams.util.showDialog

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private lateinit var bnd: ChangePasswordFragmentBinding
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = ChangePasswordFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView()
    {
        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        bnd.confirmPasswordBtn.setOnClickListener {
            val currentPassword = bnd.oldPasswordEditText.text.toString()
            val newPassword = bnd.newPasswordEditText.text.toString()
            val confirmPassword = bnd.confirmPasswordEditText.text.toString()

            if (validateUserInputs(currentPassword, newPassword, confirmPassword)){
                changePasswordViewModel.actionChangePassword(currentPassword, newPassword, confirmPassword)
            }else{
                bnd.confirmPasswordBtn.loaded()
            }

        }
    }

    private fun setupViewModel(){

        with(changePasswordViewModel){
            getChangePasswordResponse.observe(viewLifecycleOwner){result->
                when(result){
                    is Result.Error -> {
                        bnd.confirmPasswordBtn.loaded()
                        showDialog("Error", result.message)
                    }
                    Result.Loading -> {}
                    is Result.Success -> {
                        bnd.confirmPasswordBtn.loaded()
                        Toast.makeText(requireContext(), "Your password has been changed.", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun validateUserInputs(currentPassword: String, newPassword: String, confirmPassword: String): Boolean{
        return if (currentPassword.isEmpty()){
            bnd.oldPasswordInputLayout.error = "Please enter current password"
            false
        }else{
            bnd.oldPasswordInputLayout.error = null
            true
        } && if (newPassword.isEmpty()){
            bnd.newPasswordInputLayout.error = "Please enter new password"
            false
        }else{
            bnd.newPasswordInputLayout.error = null
            true
        } && if (confirmPassword.isEmpty()){
            bnd.confirmPasswordInputLayout.error = "Please confirm new password"
            false
        }else{
            bnd.confirmPasswordInputLayout.error = null
            true
        }
    }

}