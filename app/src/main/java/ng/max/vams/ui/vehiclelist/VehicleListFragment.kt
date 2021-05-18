package ng.max.vams.ui.vehiclelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.VehicleListFragmentBinding
import ng.max.vams.util.Helper
import ng.max.vams.util.gone

@AndroidEntryPoint
class VehicleListFragment : Fragment() {

    private val vehicleListViewModel: VehicleListViewModel by viewModels()
    private val args: VehicleListFragmentArgs by navArgs()
    private lateinit var bnd: VehicleListFragmentBinding

    private val vehicleAdapter = BaseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = VehicleListFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()

    }

    private fun setupViews() {
        if (args.movementType == "entry") {
            bnd.headerLabel.text = "Entry (${args.assetType})"
        } else {
            bnd.headerLabel.text = "Exit (${args.assetType})"
        }
        bnd.dateTv.text = Helper.getFormattedDate()
        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        bnd.swipeRefresh.setOnRefreshListener {
            vehicleListViewModel.actionGetVehicles(args.movementType)
        }

        vehicleAdapter.viewType = 0
        vehicleAdapter.setOnItemClickListener { position ->
            val vehicle = vehicleAdapter.adapterList[position] as DbVehicle
            val action = VehicleListFragmentDirections.actionVehicleListFragmentToVehicleDetailFragment(vehicle.id)
            findNavController().navigate(action)
        }
        bnd.vehicleRv.apply {
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
                    Snackbar.make(bnd.vehicleRv, result.message, Snackbar.LENGTH_LONG)
                }
                is Result.Loading -> {
                    bnd.progressBar.show()
                }
                is Result.Success -> {
                    hideProgressBar()
                    vehicleAdapter.adapterList = result.value
                }
            }
        }
        vehicleListViewModel.actionGetVehicles(args.movementType)
    }

    private fun hideProgressBar() {
        bnd.progressBar.gone()
        bnd.swipeRefresh.isRefreshing = false
    }


}