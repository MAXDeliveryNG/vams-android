package ng.max.vams.ui.assetreason

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
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.AssetReasonFragmentBinding
import ng.max.vams.util.gone

@AndroidEntryPoint
class AssetReasonFragment : Fragment() {

   private lateinit var bnd: AssetReasonFragmentBinding
   private val args: AssetReasonFragmentArgs by navArgs()
    private val assetReasonViewModel: AssetReasonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = AssetReasonFragmentBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.movementType == "entry"){
            bnd.reasonHeader.text = getString(R.string.entry_reason_header)
        }else{
            bnd.reasonHeader.text = getString(R.string.exit_reason_header)
        }
        bnd.closeButton.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        bnd.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        bnd.reasonSwipeRefresh.setOnRefreshListener {
            assetReasonViewModel.actionGetReasons(args.movementType)
        }

        val reasonAdapter = BaseAdapter()
        reasonAdapter.setOnItemClickListener {position->
            val reason = reasonAdapter.adapterList[position] as Reason
            val action = AssetReasonFragmentDirections.actionAssetReasonFragmentToRegisterVehicleFragment(reason.id, args.movementType)
            findNavController().navigate(action)
        }
        reasonAdapter.viewType = 1
        bnd.reasonRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reasonAdapter
            setHasFixedSize(true)
        }
        assetReasonViewModel.getReasonsResponse.observe(viewLifecycleOwner){ result->
            when (result) {
                is Result.Error -> {
                    hideProgressBar()
                    Snackbar.make(bnd.reasonRv, result.message, Snackbar.LENGTH_LONG)
                }
                is Result.Loading -> {
                    bnd.progressBar.show()
                }
                is Result.Success -> {
                    hideProgressBar()
                    reasonAdapter.adapterList = result.value
                }
            }
        }
        assetReasonViewModel.actionGetReasons(args.movementType)
    }

    private fun hideProgressBar() {
        bnd.progressBar.gone()
        bnd.reasonSwipeRefresh.isRefreshing = false
    }
}