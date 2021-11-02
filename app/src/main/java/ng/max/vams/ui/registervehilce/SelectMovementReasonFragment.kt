package ng.max.vams.ui.registervehilce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import ng.max.vams.databinding.SelectMovementReasonFragmentBinding
import ng.max.vams.ui.shared.SharedBottomSheetViewModel
import ng.max.vams.ui.shared.SharedRegistrationViewModel
import ng.max.vams.util.GridSpacingItemDecoration
import ng.max.vams.util.show

@AndroidEntryPoint
class SelectMovementReasonFragment : Fragment() {

    private lateinit var bnd: SelectMovementReasonFragmentBinding
    private val viewModel: SelectMovementReasonViewModel by viewModels()
    private val sharedViewModel: SharedRegistrationViewModel by activityViewModels()
    private val sharedBottomSheetViewModel: SharedBottomSheetViewModel by activityViewModels()
    private val reasonAdapter = BaseAdapter()
    private var selectedItem: String? = null
    private var selectedReasonSlug: String? = null
    private lateinit var captureMovementData: CaptureMovementData
    var subReason: SubReason? = null
    var lastMovementSubReason: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = SelectMovementReasonFragmentBinding.inflate(inflater, container, false)
        return bnd.root
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
                    reasonAdapter.adapterList = if (captureMovementData.movementType == "entry") {
                        result.value.filter { it.slug != "activated" }
                    } else {
                        result.value.filter { it.slug != "new" }
                    }
                }
            }
        })

        sharedBottomSheetViewModel.getSelectedTransferLocationIdResponse.observe(
            viewLifecycleOwner,
            { _locationToId ->
                navigateToRegisterVehicle(subReason, _locationToId)
            })

        sharedBottomSheetViewModel.getSelectedItemResponse.observe(viewLifecycleOwner, {
            selectedItem = it

            (reasonAdapter.adapterList as List<Reason>).forEach { reason ->
                reason.subReasons?.forEach { _subReason ->
                    if (_subReason.slug == it) {

                        if (captureMovementData.movementType == "exit" &&
                            lastMovementSubReason != null && lastMovementSubReason != _subReason.name) {
                            Toast.makeText(
                                requireContext(),
                                "Please select $lastMovementSubReason and try again.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@observe
                        } else {
                            subReason = _subReason
                        }
                    }
                }
            }

            if (selectedReasonSlug == "transfer") {
                val action =
                    SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToTransferLocationBottomSheetFragment(
                        subReason!!.name, captureMovementData.vehicle.locationId
                    )
                findNavController().navigate(action)
            } else {
                navigateToRegisterVehicle(subReason, null)
            }
        })

    }

    private fun navigateToRegisterVehicle(subReason: SubReason?, _locationToId: String?) {
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToRegisterVehicleFragment(
                vehicleId = captureMovementData.vehicle.id,
                vehicleMaxId = captureMovementData.vehicle.maxVehicleId,
                vehicleMovement = captureMovementData.movementType,
                locationId = captureMovementData.vehicle.locationId,
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
            bnd.vehicleDetailHeaderTv.text = getString(R.string.enter_reason_title, "Check-in")
        } else {
            bnd.vehicleDetailHeaderTv.text = getString(R.string.enter_reason_title, "Check-out")

        }
        bnd.vehicleMaxId.text = _captureData.vehicle.maxVehicleId
        bnd.plateNumberView.setSubtitle(_captureData.vehicle.plateNumber)
        val champion = _captureData.vehicle.champion?.let {
            getString(
                R.string.default_name, it.firstName,
                it.lastName
            )
        } ?: "N/A"
        bnd.championView.setSubtitle(champion)

        _captureData.vehicle.status?.let {
            bnd.vehicleStatusContainer.show()
            bnd.vehicleStatus.text = it.name
            when (it.slug) {
                "active" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status_bg
                        )
                    )
                }
                "in_active" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.inactive_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.inactive_status_bg
                        )
                    )
                }
                "hp_completed" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hp_completed_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.hp_completed_status_bg
                        )
                    )
                }
                "missing" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.missing_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.missing_status_bg
                        )
                    )
                }
                "scrapped" -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.scrapped_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.scrapped_status_bg
                        )
                    )
                }
                else -> {
                    bnd.vehicleStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status
                        )
                    )
                    bnd.vehicleStatusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.active_status_bg
                        )
                    )
                }
            }
        }
        viewModel.actionGetReasons()
        viewModel.actionGetLocations()

    }

    private fun setupView() {

        bnd.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        reasonAdapter.viewType = 1
        bnd.reasonRv.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = reasonAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 16, true))
            setHasFixedSize(true)
        }

        reasonAdapter.setOnItemClickListener { position ->
            val selectedReason = (reasonAdapter.adapterList[position] as Reason)
            selectedReasonSlug = selectedReason.slug

            if (captureMovementData.movementType == "exit") {
                val lastMovement = captureMovementData.vehicle.lastVehicleMovement!!
                val lastMovementReason = lastMovement.reason
                val reason =
                    (reasonAdapter.adapterList as List<Reason>).find { it.id == lastMovementReason.parentReasonId }!!

                if (selectedReason.id != lastMovementReason.parentReasonId && selectedReasonSlug != "activated") {
                    Toast.makeText(
                        requireContext(),
                        "Please check out with ${reason.name}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (selectedReasonSlug == "activated" && reason.slug != "new") {
                        Toast.makeText(
                            requireContext(),
                            "Please check out with ${reason.name}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        lastMovementSubReason = lastMovementReason.name
                        displaySubReasons(selectedReason)
                    }
                }
            } else {
                displaySubReasons(selectedReason)
            }
        }
    }

    private fun displaySubReasons(selectedReason: Reason) {
        val subReasons: List<SubReason> = if (selectedReason.slug == "completed_hp") {
            val _subReason = if (captureMovementData.movementType == "entry") {
                selectedReason.subReasons?.find { it.slug == "pick_up_papers" }!!
            } else {
                selectedReason.subReasons?.find { it.slug == "completed_hp" }!!
            }
            listOf(_subReason)
        } else {
            selectedReason.subReasons!!
        }
        sharedBottomSheetViewModel.submitSubReasons(subReasons)
        val action =
            SelectMovementReasonFragmentDirections.actionSelectMovementReasonFragmentToListBottomSheetFragment(
                selectedItem,
                "REASON"
            )
        findNavController().navigate(action)
    }
}