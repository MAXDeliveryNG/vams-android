package ng.max.vams.ui.registervehilce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.select_movement_reason_fragment.*
import ng.max.vams.R
import ng.max.vams.adapter.BaseAdapter
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.data.wrapper.Result
import ng.max.vams.ui.shared.SharedBottomSheetViewModel
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.GridSpacingItemDecoration
import ng.max.vams.util.show

@AndroidEntryPoint
class SelectMovementReasonFragment : Fragment() {

    companion object {
        fun newInstance() = SelectMovementReasonFragment()
    }

    private val viewModel: SelectMovementReasonViewModel by viewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val reasonAdapter = BaseAdapter()
    private var selectedItem: String? = null
    private var selectedReason: String? = null
    private lateinit var captureMovementData: CaptureMovementData
    var subReason: SubReason? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_movement_reason_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }


    private fun setupViewModel() {
        sharedViewModel.getCaptureMovementDataResponse.observe(viewLifecycleOwner, {
            captureMovementData = it
            populateView(it)

        })

        viewModel.getReasonsResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Error -> {
                    Snackbar.make(reasonRv, result.message, Snackbar.LENGTH_LONG).show()
                }
                is Result.Loading -> {
                }
                is Result.Success -> {
                    reasonAdapter.adapterList = result.value
                }
            }
        })

        sharedBottomSheetViewModel.getSelectedTransferLocationIdResponse.observe(
            viewLifecycleOwner,
            { _locationToId ->
                navigateToRegisterVehicle(subReason, _locationToId)
            })

        sharedBottomSheetViewModel.getSelectedItemResponse.observe(viewLifecycleOwner, Observer {
            selectedItem = it

            (reasonAdapter.adapterList as List<Reason>).forEach { reason ->
                reason.subReasons?.forEach { _subReason ->
                    if (_subReason.name == it) {
                        subReason = _subReason
                    }
                }
            }

            if (selectedReason == "transfer") {
                val action =
                    SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToTransferLocationBottomSheetFragment(
                        subReason!!.name
                    )
                findNavController().navigate(action)
            } else {
                navigateToRegisterVehicle(subReason)
            }
        })

    }

    private fun navigateToRegisterVehicle(subReason: SubReason?, _locationToId: Int = 0) {
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToRegisterVehicleFragment(
                vehicleId = captureMovementData.vehicle.id,
                vehicleMaxId = captureMovementData.vehicle.maxVehicleId,
                vehicleMovement = captureMovementData.movementType,
                locationToId = _locationToId,
                subReasonId = subReason!!.id,
                subReasonName = subReason.name,
                champion = captureMovementData.vehicle.champion?.let {
                    getString(
                        R.string.default_name, it.firstName,
                        it.lastName
                    )
                } ?: "N/A"
            )

        findNavController().navigate(action)
    }

    private fun populateView(_captureData: CaptureMovementData) {
        if (_captureData.movementType == "entry") {
            vehicleDetailHeaderTv.text = getString(R.string.enter_reason_title, "Check-in")
        } else {
            vehicleDetailHeaderTv.text = getString(R.string.enter_reason_title, "Check-out")

        }
        vehicleMaxId.text = _captureData.vehicle.maxVehicleId
        plateNumberView.setSubtitle(_captureData.vehicle.plateNumber)
        val champion = _captureData.vehicle.champion?.let {
            getString(
                R.string.default_name, it.firstName,
                it.lastName
            )
        } ?: "N/A"
        championView.setSubtitle(champion)

        _captureData.vehicle.status?.let {
            vehicleStatusContainer.show()
            vehicleStatus.text = it.name
            when (it.slug) {
                "active" -> {
                    vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status
                        )
                    )
                    vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status_bg
                        )
                    )
                }
                "in_active" -> {
                    vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.inactive_status
                        )
                    )
                    vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.inactive_status_bg
                        )
                    )
                }
                "hp_completed" -> {
                    vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hp_completed_status
                        )
                    )
                    vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hp_completed_status_bg
                        )
                    )
                }
                "missing" -> {
                    vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.missing_status
                        )
                    )
                    vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.missing_status_bg
                        )
                    )
                }
                "scrapped" -> {
                    vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.scrapped_status
                        )
                    )
                    vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.scrapped_status_bg
                        )
                    )
                }
                else -> {
                    vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status
                        )
                    )
                    vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status_bg
                        )
                    )
                }
            }
        }
        viewModel.actionGetReasons(_captureData.movementType)

    }

    private fun setupView() {

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        reasonAdapter.viewType = 1
        reasonRv.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = reasonAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 16, true))
            setHasFixedSize(true)
        }

        reasonAdapter.setOnItemClickListener { position ->
            val reason = (reasonAdapter.adapterList[position] as Reason)
            selectedReason = reason.slug
            val subReasons = if (reason.slug == "hp_complete") {
                val subReason = if (captureMovementData.movementType == "entry") {
                    reason.subReasons?.find { it.slug == "pick_up_papers" }!!
                } else {
                    reason.subReasons?.find { it.slug == "completed_hp" }!!
                }
                arrayOf(subReason.name)
            } else {
                reason.subReasons?.map {
                    it.name
                }!!.toTypedArray()
            }
            val action =
                SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToListBottomSheetFragment(
                    selectedItem,
                    subReasons,
                    "REASON"
                )
            findNavController().navigate(action)
        }
    }
}