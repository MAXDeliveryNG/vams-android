package ng.max.vams.ui.vehiclesearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_plate_number_search.*
import ng.max.vams.R
import ng.max.vams.data.wrapper.Result
import ng.max.vams.util.gone
import ng.max.vams.util.hide
import ng.max.vams.util.show
import ng.max.vams.util.showDialog

@AndroidEntryPoint
class PlateNumberSearchFragment : Fragment() {

    private val plateNumberSearchViewModel: PlateNumberSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plate_number_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupViewModel() {
        plateNumberSearchViewModel.getSearchResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    manageContentViews(false, result.message)
                }
                Result.Loading -> {
                    manageContentViews()
                }
                is Result.Success -> {
                    if(result.value.isEmpty()){
                        manageContentViews(false, "Vehicle with ${plateNumberEditText.text.toString()} not found.")
                    }else{
                        manageContentViews()
                        navigateToVehicleDetail()
                    }
                }
            }
        }
    }

    private fun manageContentViews(isLoading: Boolean = true, message: String = "") {
        if (isLoading){
            loadingContent.show()
            mainContent.gone()
            backBtn.hide()
        }else{
            loadingContent.gone()
            mainContent.show()
            backBtn.show()
            showDialog("Error", message)
        }
    }

    private fun navigateToVehicleDetail() {

    }

    private fun setupView() {
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        plateNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                submitButton.isEnabled = s.toString().isNotEmpty() && s.toString().isNotBlank()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        submitButton.setOnClickListener {
            plateNumberSearchViewModel.actionSearch(plateNumberEditText.text.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlateNumberSearchFragment()
    }
}