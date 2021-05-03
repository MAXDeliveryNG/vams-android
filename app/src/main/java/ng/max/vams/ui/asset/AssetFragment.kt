package ng.max.vams.ui.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.AssetFragmentBinding
import ng.max.vams.ui.home.HomeFragmentDirections
import ng.max.vams.util.ARG_OBJECT
import ng.max.vams.util.Helper

@AndroidEntryPoint
class AssetFragment : Fragment() {

    private val assetViewModel: AssetViewModel by viewModels()
    private lateinit var binding : AssetFragmentBinding
    private var assetType = "Vehicles"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{
        binding = AssetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            if (getInt(ARG_OBJECT) == 2){
                assetType = "Spare Parts"
            }else if (getInt(ARG_OBJECT) == 3){
                assetType = "Trackers"
            }
        }
        setupViews()
        if (assetType == "Vehicles"){
            setupViewModel()
        }
    }

    private fun setupViews(){
        val date = Helper.getFormattedDate()
        binding.checkInCard.setDate(date)
        binding.checkOutCard.setDate(date)

        binding.checkInCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                "checked_in",
                assetType
            )
            findNavController().navigate(action)
        }

        binding.checkOutCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                "checked_out",
                assetType
            )
            findNavController().navigate(action)
        }
//        binding.assetSwipeRefresh.setOnRefreshListener {
//            assetViewModel.actionGetVehicleCounts()
//        }
    }

    private fun setupViewModel(){
        assetViewModel.getCheckInCountResponse.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.checkInCard.setCount(result.value)
                }
            }
        }

        assetViewModel.getCheckOutCountResponse.observe(viewLifecycleOwner){result->
            when(result){
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.checkOutCard.setCount(result.value)
                }
            }
        }
        assetViewModel.actionGetVehicleCounts()
    }

}