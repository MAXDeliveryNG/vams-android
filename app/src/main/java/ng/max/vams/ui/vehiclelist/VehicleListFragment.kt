package ng.max.vams.ui.vehiclelist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.vehicle_list_fragment.view.*
import ng.max.vams.R
import ng.max.vams.adapter.VehicleAdapter
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentLoginBinding
import ng.max.vams.databinding.VehicleListFragmentBinding
import ng.max.vams.ui.login.LoginViewModel
import ng.max.vams.util.Helper

@AndroidEntryPoint
class VehicleListFragment : Fragment() {

    private val vehicleListViewModel: VehicleListViewModel by viewModels()
    private val args: VehicleListFragmentArgs by navArgs()
    private lateinit var binding: VehicleListFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VehicleListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(args.availabilityType == "checked_in"){
            binding.checkTypeHeader.text = "Checked-in (${args.assetType})"
        }else{
            binding.checkTypeHeader.text = "Checked-out (${args.assetType})"
        }
        binding.dateTv.text = Helper.getFormattedDate()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.swipeRefresh.setOnRefreshListener {
            vehicleListViewModel.actionGetVehicles(args.availabilityType)
        }
        val vehicleAdapter = VehicleAdapter()
        binding.vehicleRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vehicleAdapter
            setHasFixedSize(true)
        }
        vehicleListViewModel.getVehiclesResponse.observe(viewLifecycleOwner){ result->

            when(result){
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.swipeRefresh.setRefreshing(false)
                    vehicleAdapter.submitList(result.value)
                }
            }
        }
        vehicleListViewModel.actionGetVehicles(args.availabilityType)

    }


}