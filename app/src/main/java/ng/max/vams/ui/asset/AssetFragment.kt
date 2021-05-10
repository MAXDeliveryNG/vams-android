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
    private lateinit var bnd : AssetFragmentBinding
    private var assetType = "Vehicles"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{
        bnd = AssetFragmentBinding.inflate(inflater, container, false)
        return bnd.root
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
        bnd.entryCard.setDate(date)
        bnd.exitCard.setDate(date)

        bnd.entryCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                "entry",
                assetType
            )
            findNavController().navigate(action)
        }

        bnd.exitCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToVehicleListFragment(
                "exit",
                assetType
            )
            findNavController().navigate(action)
        }
    }

    private fun setupViewModel(){
        assetViewModel.getMovementStatResponse.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Error -> {}
                is Result.Loading -> {}
                is Result.Success -> {
                    bnd.entryCard.setCount(result.value.totalEntry)
                    bnd.exitCard.setCount(result.value.totalExit)
                }
            }
        }
        assetViewModel.actionGetMovementStat()
    }

}