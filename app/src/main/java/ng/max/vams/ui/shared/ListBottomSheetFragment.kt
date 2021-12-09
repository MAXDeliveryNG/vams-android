package ng.max.vams.ui.shared

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentListBottomSheetBinding
import ng.max.vams.ui.registervehilce.SelectMovementReasonViewModel

class ListBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var bnd: FragmentListBottomSheetBinding
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val sharedReasonViewModel : SelectMovementReasonViewModel by activityViewModels()
    private val args: ListBottomSheetFragmentArgs by navArgs()

    private val formListItemAdapter = BaseAdapter()
    private val reasonAdapter = BaseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentListBottomSheetBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }


    private fun setupView() {

        formListItemAdapter.viewType = 2
        reasonAdapter.viewType = 1

        formListItemAdapter.setOnItemClickListener { position ->
            findNavController().navigateUp()

            val selectedItem = if (args.fromSource != "SUBREASON") {
                formListItemAdapter.adapterList[position] as String
            }else{
                (formListItemAdapter.adapterList[position] as SubReason).name
            }

            sharedBottomSheetViewModel.submitSelectedItem(mapOf(Pair(args.fromSource,selectedItem)))

        }

        reasonAdapter.setOnItemClickListener { position ->
            findNavController().navigateUp()

            val selectedItem = (reasonAdapter.adapterList[position] as Reason).name
            Log.d("TAGSELECTED", "setupView: $selectedItem")
            sharedReasonViewModel.actionGetReasonByName(selectedItem)
            sharedBottomSheetViewModel.submitSelectedItem(mapOf(Pair(args.fromSource,selectedItem)))

        }

        bnd.listRv.apply {
            layoutManager = LinearLayoutManager(requireContext())

            if(args.fromSource != "REASON") {
                adapter = formListItemAdapter
            }else{
                adapter = reasonAdapter
            }
            setHasFixedSize(true)
        }
    }

    private fun setupViewModel() {
        sharedReasonViewModel.getReasonsResponse.observe(viewLifecycleOwner, { reasons ->
            when(reasons){
                is Result.Error ->{}
                is Result.Loading ->{}
                is Result.Success -> {
                    if(!reasons.value.isNullOrEmpty()) {
                        reasonAdapter.adapterList = reasons.value.map{ it }
                    }
                }
            }
        })
        with(sharedBottomSheetViewModel) {
            if (args.fromSource == "SUBREASON") {
                getSubReasonsResponse.observe(viewLifecycleOwner, { subReasons ->
                    if(subReasons.isNotEmpty()){
                        formListItemAdapter.adapterList = subReasons
                    }
//                    formListItemAdapter.adapterList = subReasons
//                    if (!args.selectedItem.isNullOrEmpty()) {
//                        formListItemAdapter.selectedItemPosition =
//                            subReasons.indexOf(subReasons.find { subreason ->
//                                subreason.slug == args.selectedItem
//                            })
//                    }
                })
            } else{
                getLocationsResponse.observe(viewLifecycleOwner) { locations ->
                    formListItemAdapter.adapterList = locations.map { it.name }.sorted()
                    if (!args.selectedItem.isNullOrEmpty()) {
                        formListItemAdapter.selectedItemPosition =
                            locations.indexOf(locations.find { location ->
                                location.name == args.selectedItem
                            })
                    }
                }
            }
        }
    }

    companion object {
    }
}