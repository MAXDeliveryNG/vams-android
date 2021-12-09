package ng.max.vams.ui.shared

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_transfer_location_bottom_sheet.*
import ng.max.vams.R
import ng.max.vams.adapter.SpinnerAdapter
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.wrapper.Result
import ng.max.vams.databinding.FragmentTransferLocationBottomSheetBinding

@AndroidEntryPoint
class TransferLocationBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var bnd: FragmentTransferLocationBottomSheetBinding
    private val transferLocationViewModel: TransferLocationViewModel by viewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val args: TransferLocationBottomSheetFragmentArgs by navArgs()

    private var locations: List<Location> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.dialogBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentTransferLocationBottomSheetBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true) //Allows touching outside window's bound to close dialog
        setupView()
        setupViewModel()
    }

    private fun setupView() {

        bnd.transferTitleTv.text = args.transferType
        hackFixForDropdownClickBehaviour(locationEditText, locationInputLayout)

        bnd.submitButton.setOnClickListener {
            findNavController().navigateUp()
            val locationName = locationEditText.text.toString()
            val locationId = locations.first { _location ->
                _location.name == locationName
            }.id
            sharedBottomSheetViewModel.submitSelectedTransferLocationId(locationId)
        }
    }

    private fun setupViewModel() {
        with(transferLocationViewModel) {

            getLocationsResponse.observe(viewLifecycleOwner){result->
                when(result){
                    is Result.Error -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        locations = result.value.filter { args.locationId != it.id }
                        populateLocationDropdown()
                    }
                }
            }


            actionGetAllLocation()
        }

        sharedBottomSheetViewModel.getSelectedTransferLocationResponse.observe(viewLifecycleOwner){selectedItem ->
            bnd.locationEditText.setText(selectedItem)
            bnd.submitButton.isEnabled = true
        }
    }

    private fun populateLocationDropdown(){
        val adapter: SpinnerAdapter<String> =
            SpinnerAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                locations.map { location -> location.name }.sorted().toTypedArray()
            )


        bnd.locationEditText.setAdapter(adapter)
        bnd.locationEditText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                locationEditText.setText(adapter.getItem(position))
                submitButton.isEnabled = true
            }

    }


    var atvToggleMap: HashMap<AutoCompleteTextView, Boolean> = HashMap()

    @Suppress("ObjectLiteralToLambda")
    @SuppressLint("ClickableViewAccessibility")
    fun hackFixForDropdownClickBehaviour(
        atv: AutoCompleteTextView,
        textInputLayout: TextInputLayout
    ) {
        atvToggleMap[atv] = false

        atv.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                if (motionEvent?.action == MotionEvent.ACTION_UP) {
                    textInputLayout.performClick()
                    if (atvToggleMap[atv]!!) {
                        atv.dismissDropDown()
                        atvToggleMap[atv] = false
                    } else {
                        atv.showDropDown()
                        atvToggleMap[atv] = true
                    }
                }
                return false
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransferLocationBottomSheetFragment()
    }
}