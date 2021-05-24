package ng.max.vams.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentLoginBinding
import ng.max.vams.util.showDialog


@AndroidEntryPoint
class LoginFragment : Fragment(){

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var bnd: FragmentLoginBinding

    private lateinit var username: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        bnd = FragmentLoginBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bnd.loginButton.setOnClickListener {
            username = bnd.usernameEditText.text.toString()
            password = bnd.passwordEditText.text.toString()

            if (validateUserInputs(username, password)){
                loginViewModel.actionLogin(username, password)
            }else{
                bnd.loginButton.loaded()
            }

        }
        bnd.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        loginViewModel.getLoginResponse.observe(viewLifecycleOwner){ result ->

            when(result){
                is Result.Error -> {
                    bnd.loginButton.loaded()
                    showDialog("Error", result.message)
                }
                is Result.Loading -> {
                    //Button is in loading state
                }
                is Result.Success -> {
                    bnd.loginButton.loaded()
                    findNavController().popBackStack(R.id.homeFragment, true)

                }
            }
        }
    }

    private fun validateUserInputs(email: String, password: String): Boolean{
        return if (email.isEmpty()){
            bnd.usernameInputLayout.error = "Please enter your email"
            false
        }else {
            bnd.usernameInputLayout.error = null
            true
        } && if (password.isEmpty()){
            bnd.passwordInputLayout.error = "Please enter your password"
            false
        }else{
            bnd.passwordInputLayout.error = null
            true
        }
    }

}