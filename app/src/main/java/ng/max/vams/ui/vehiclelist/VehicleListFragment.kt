package ng.max.vams.ui.vehiclelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.VehicleListFragmentBinding
import ng.max.vams.util.Helper
import ng.max.vams.util.gone
import ng.max.vams.util.show

@AndroidEntryPoint
class VehicleListFragment : Fragment() {

    private val vehicleListViewModel: VehicleListViewModel by viewModels()
    private val args: VehicleListFragmentArgs by navArgs()
    private lateinit var binding: VehicleListFragmentBinding

    private val vehicleAdapter = BaseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VehicleListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()

    }

    private fun setupViews() {
        if (args.availabilityType == "checked_in") {
            binding.checkTypeHeader.text = "Checked-in (${args.assetType})"
        } else {
            binding.checkTypeHeader.text = "Checked-out (${args.assetType})"
        }
        binding.dateTv.text = Helper.getFormattedDate()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.swipeRefresh.setOnRefreshListener {
            vehicleListViewModel.actionGetVehicles(args.availabilityType)
        }

        vehicleAdapter.viewType = 0
        binding.vehicleRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vehicleAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupViewModel() {
        vehicleListViewModel.getVehiclesResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    hideProgressBar()
                    Snackbar.make(binding.vehicleRv, result.message, Snackbar.LENGTH_LONG)
                }
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    hideProgressBar()
                    vehicleAdapter.adapterList = result.value
                }
            }
        }
        vehicleListViewModel.actionGetVehicles(args.availabilityType)
    }

    private fun hideProgressBar() {
        binding.progressBar.gone()
        binding.swipeRefresh.isRefreshing = false
    }


}