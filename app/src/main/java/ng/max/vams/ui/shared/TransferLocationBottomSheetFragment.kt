package ng.max.vams.ui.shared

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.wrapper.Result

@AndroidEntryPoint
class TransferLocationBottomSheetFragment : BottomSheetDialogFragment() {

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
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_location_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {

        transferTitleTv.text = args.transferType
        hackFixForDropdownClickBehaviour(locationEditText, locationInputLayout)

        submitButton.setOnClickListener {
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
                        locations = result.value
                        populateLocationDropdown()
                    }
                }
            }


            actionGetAllLocation()
        }

        sharedBottomSheetViewModel.getSelectedTransferLocationResponse.observe(viewLifecycleOwner){selectedItem ->
            locationEditText.setText(selectedItem)
            submitButton.isEnabled = true
        }
    }

    fun populateLocationDropdown(){
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                locations.map { location -> location.name }.toTypedArray()
            )


        locationEditText.setAdapter(adapter)
        locationEditText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                locationEditText.setText(locations[position].name)
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