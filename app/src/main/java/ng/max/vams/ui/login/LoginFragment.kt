package ng.max.vams.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentLoginBinding


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var fragmentLoginBinding: FragmentLoginBinding

    private lateinit var username: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentLoginBinding.loginButton.setOnClickListener {
            username = fragmentLoginBinding.usernameEditText.text.toString()
            password = fragmentLoginBinding.passwordEditText.text.toString()
            loginViewModel.actionValidate(username, password)
        }

        loginViewModel.getValidateResponse.observe(viewLifecycleOwner){ result ->
            when(result){
                is Result.Error -> {
                    fragmentLoginBinding.loginButton.loaded()
                    showDialog("Error", result.message)
                }

                is Result.Success -> {
                    loginViewModel.actionLogin(username, password)
                }
                else -> {}
            }
        }
        loginViewModel.getLoginResponse.observe(viewLifecycleOwner){ result ->

            when(result){
                is Result.Error -> {
                    fragmentLoginBinding.loginButton.loaded()
                    showDialog("Error", result.message)
                }
                is Result.Loading -> {
                    if (!result.isLoading){
                        fragmentLoginBinding.loginButton.loaded()
                    }
                }
                is Result.Success -> {
                    fragmentLoginBinding.loginButton.loaded()
                    findNavController().popBackStack()

                }
            }
        }
    }

    private fun showDialog(title : String, message : String) {
        val bundle = bundleOf("title" to title, "message" to message)
        findNavController().navigate(R.id.appDialogFragment, bundle)
    }

}