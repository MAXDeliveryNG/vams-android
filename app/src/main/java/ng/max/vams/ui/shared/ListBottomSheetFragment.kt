package ng.max.vams.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.databinding.FragmentListBottomSheetBinding

class ListBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var bnd: FragmentListBottomSheetBinding
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val args: ListBottomSheetFragmentArgs by navArgs()

    private val formListItemAdapter = BaseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        bnd = FragmentListBottomSheetBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }


    private fun setupView() {

        formListItemAdapter.viewType = 2
        formListItemAdapter.adapterList = args.dropDownList.asList()
        if (!args.selectedItem.isNullOrEmpty()){
            formListItemAdapter.selectedItemPosition = args.dropDownList.indexOf(args.selectedItem)
        }

        formListItemAdapter.setOnItemClickListener { position ->
            val selectedItem = formListItemAdapter.adapterList[position] as String
            val item = mapOf(args.listType to selectedItem)
            sharedBottomSheetViewModel.submitSelectedItem(item)
            dialog?.dismiss()
        }
        bnd.listRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = formListItemAdapter
            setHasFixedSize(true)
        }
    }

    companion object {
    }
}