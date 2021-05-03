package ng.max.vams.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import ng.max.vams.R
import ng.max.vams.adapter.AssetAdapter
import ng.max.vams.databinding.FragmentLoginBinding
import ng.max.vams.databinding.HomeFragmentBinding
import ng.max.vams.ui.login.LoginViewModel
import ng.max.vams.util.Helper

class HomeFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var binding : HomeFragmentBinding
    private lateinit var assetAdapter: AssetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        loginViewModel.getLoggedInUser().observe(viewLifecycleOwner){user ->
            if (user == null){
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun setupViews(){

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.checkDialogFragment)
        }
        binding.viewPager.adapter = AssetAdapter(this)

        TabLayoutMediator(binding.tabs, binding.viewPager){ tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Vehicles"
                }
                1 -> {
                    tab.text = "Spare Parts"
                }
                else -> {
                    tab.text = "Trackers"
                }
            }
        }.attach()
    }

}