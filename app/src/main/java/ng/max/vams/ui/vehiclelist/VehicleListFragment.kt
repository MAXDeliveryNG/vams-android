package ng.max.vams.ui.vehiclelist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.coroutines.flow.MutableStateFlow
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.VehicleListFragmentBinding
import ng.max.vams.util.Helper
import ng.max.vams.util.gone
import ng.max.vams.util.show

@AndroidEntryPoint
class VehicleListFragment : Fragment() {

    private val vehicleListViewModel: VehicleListViewModel by viewModels()
    private val args: VehicleListFragmentArgs by navArgs()
    private lateinit var bnd: VehicleListFragmentBinding

    private val vehicleAdapter = BaseAdapter()
    val searchQuery = MutableStateFlow("")

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
        resetSearch()
        if (args.movementType == "entry") {
            bnd.headerLabel.text = getString(R.string.entry_header_label, args.assetType)
        } else {
            bnd.headerLabel.text = getString(R.string.exit_header_label, args.assetType)
        }
        bnd.dateTv.text = Helper.getFormattedDate()
        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        bnd.searchBtn.setOnClickListener {
            bnd.searchContainer.show()
            bnd.headerContainer.gone()
            bnd.searchEditText.requestFocus()
        }
        bnd.clearBtn.setOnClickListener {
            if (bnd.searchEditText.text.toString().isEmpty()){
                bnd.searchContainer.gone()
                 bnd.headerContainer.show()
            }else{
                bnd.searchEditText.text  = null
                vehicleListViewModel.actionGetVehicles(args.movementType)
            }
        }
        bnd.swipeRefresh.setOnRefreshListener {
            vehicleListViewModel.actionGetVehicles(args.movementType)
        }

        vehicleAdapter.viewType = 0
        vehicleAdapter.setOnItemClickListener { position ->
            val vehicle = vehicleAdapter.adapterList[position] as DbVehicle
//            val action = VehicleListFragmentDirections.actionVehicleListFragmentToVehicleDetailFragment(remoteVehicle.id)
//            findNavController().navigate(action)

        }
        bnd.vehicleRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vehicleAdapter
            setHasFixedSize(true)
        }
        bnd.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(input: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(input: CharSequence?, start: Int, before: Int, count: Int) {
                if (input.toString().isNotEmpty()){
                    searchQuery.value = input.toString()
//                    vehicleListViewModel.search(searchQuery, args.movementType)
                }
            }

            override fun afterTextChanged(input: Editable?) {

            }

        })
    }

    private fun resetSearch() {
        bnd.searchEditText.text = null
        bnd.searchContainer.gone()
        bnd.headerContainer.show()
    }

    private fun setupViewModel() {
        with(vehicleListViewModel){
            getVehiclesResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        hideProgressBar()
                        Snackbar.make(bnd.vehicleRv, result.message, Snackbar.LENGTH_LONG).show()
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

            getSearchResponse.observe(viewLifecycleOwner){ result ->
                when (result) {
                    is Result.Error -> {
                        hideProgressBar()
                        Snackbar.make(bnd.vehicleRv, result.message, Snackbar.LENGTH_LONG).show()
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
        }

        vehicleListViewModel.actionGetVehicles(args.movementType)
    }

    private fun hideProgressBar() {
        bnd.progressBar.gone()
        bnd.swipeRefresh.isRefreshing = false
    }


}