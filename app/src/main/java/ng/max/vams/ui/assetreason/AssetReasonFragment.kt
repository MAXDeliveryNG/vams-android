package ng.max.vams.ui.assetreason

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
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.AssetReasonFragmentBinding
import ng.max.vams.util.gone

@AndroidEntryPoint
class AssetReasonFragment : Fragment() {

   private lateinit var binding: AssetReasonFragmentBinding
   private val args: AssetReasonFragmentArgs by navArgs()
    private val assetReasonViewModel: AssetReasonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AssetReasonFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.checkType == "checked_in"){
            binding.reasonHeader.text = getString(R.string.check_in_reason_header)
        }else{
            binding.reasonHeader.text = getString(R.string.check_out_reason_header)
        }
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.reasonSwipeRefresh.setOnRefreshListener {
            assetReasonViewModel.actionGetReasons(args.checkType)
        }

        val reasonAdapter = BaseAdapter()
        reasonAdapter.viewType = 1
        binding.reasonRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reasonAdapter
            setHasFixedSize(true)
        }
        assetReasonViewModel.getReasonsResponse.observe(viewLifecycleOwner){ result->
            when (result) {
                is Result.Error -> {
                    hideProgressBar()
                    Snackbar.make(binding.reasonRv, result.message, Snackbar.LENGTH_LONG)
                }
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    hideProgressBar()
                    reasonAdapter.adapterList = result.value
                }
            }
        }
        assetReasonViewModel.actionGetReasons(args.checkType)
    }

    private fun hideProgressBar() {
        binding.progressBar.gone()
        binding.reasonSwipeRefresh.isRefreshing = false
    }
}