package ng.max.vams.ui.home

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.*
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.RoleData
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.userrole.UserRoleUseCase
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val vehicleService: VehicleService,
                                        private val vehicleRepo: VehicleRepository,
                                        private val locationRepo: LocationRepository,
                                        private val vehicleTypeRepo: VehicleTypeRepository,
                                        private val movementReasonRepo: MovementReasonRepository,
                                        private val retrievalChecklistRepo: RetrievalChecklistRepository,
                                        private val userRoleUseCase: UserRoleUseCase

) :
    ViewModel() {

    private val unconfirmedVehicleResponse = MutableLiveData<Result<List<DbVehicle>>>()
    private val fullMovementStatResponse = MutableLiveData<Result<DashboardStat>>()
    private val userRoleResponse = MutableLiveData<Result<RoleData>>()
    private val cardControlResponse = MutableLiveData<MutableMap<String,Boolean>>()

    val getUserRoleResponse: LiveData<Result<RoleData>> = userRoleResponse
    val getcardControlResponse: LiveData<MutableMap<String,Boolean>> = cardControlResponse
    val getUnconfirmedVehicleResponse: LiveData<Result<List<DbVehicle>>> = unconfirmedVehicleResponse
    val getFullMovementStatResponse: LiveData<Result<DashboardStat>> = fullMovementStatResponse

    fun controlCard(control: MutableMap<String, Boolean>, controlKey: String, controlState: Boolean){
        val newControl = mutableMapOf<String, Boolean>()
        if(controlState){
                control.map{
                    if(it.key != controlKey){
                        newControl[it.key] = false
                    }
            }
        }
        cardControlResponse.value = newControl
    }

    fun actionGetUnconfirmedVehicles() {
        viewModelScope.launch {
            vehicleRepo.getUnConfirmedVehicles().collect {
                unconfirmedVehicleResponse.value = it
            }
        }
    }

    fun actionGetFullMovementStat(userId: String, fireStoreDB: FirebaseFirestore){
        val collectionRef = fireStoreDB.collection("vams-mobile-app-dashboard")
        val entryDocRef = collectionRef.document("entry").collection(userId)
        val exitDocRef = collectionRef.document("exit").collection(userId)
        val transferDocRef = collectionRef.document("transfer").collection(userId)
        val dashboardStat = DashboardStat()

        collectionRef.addSnapshotListener{querySnapshot, error ->

            if (error != null) {
                fullMovementStatResponse.value = Result.Error(error.localizedMessage?: "Something went wrong.")
                return@addSnapshotListener
            }

            if (querySnapshot != null){
                for (document in querySnapshot.documents){
                    val path = document.reference.path

                    if (path.contains("entry", ignoreCase = true)){
                        dashboardStat.totalEntry = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )

                    }

                    if (path.contains("exit", ignoreCase = true)){
                        dashboardStat.totalExit = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }

                    if (path.contains("transfer", ignoreCase = true)){
                        dashboardStat.totalTransfer = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }
                }
                fullMovementStatResponse.value = Result.Success(dashboardStat)
            }
        }

        entryDocRef.addSnapshotListener{ agentQuerySnapshot, error ->
            if (error != null) {
                fullMovementStatResponse.value = Result.Error(error.localizedMessage?: "Something went wrong.")
                return@addSnapshotListener
            }

            if (agentQuerySnapshot != null){
                Log.d("TAG", "Current data: ${agentQuerySnapshot.documents}")
                for (document in agentQuerySnapshot.documents){
                    val path = document.reference.path

                    if (path.contains("total", ignoreCase = true)){
                        dashboardStat.entryByAgentName = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }

                    if (path.contains("today", ignoreCase = true)){
                        dashboardStat.entryByDate = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }
                }
                fullMovementStatResponse.value = Result.Success(dashboardStat)
            }
        }

        exitDocRef.addSnapshotListener{ agentQuerySnapshot, error ->
            if (error != null) {
                fullMovementStatResponse.value = Result.Error(error.localizedMessage?: "Something went wrong.")
                return@addSnapshotListener
            }

            if (agentQuerySnapshot != null){
                Log.d("TAG", "Current data: ${agentQuerySnapshot.documents}")
                for (document in agentQuerySnapshot.documents){
                    val path = document.reference.path

                    if (path.contains("total", ignoreCase = true)){
                        dashboardStat.exitByAgentName = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }

                    if (path.contains("today", ignoreCase = true)){
                        dashboardStat.exitByDate = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }
                }
                fullMovementStatResponse.value = Result.Success(dashboardStat)
            }
        }

        transferDocRef.addSnapshotListener{ agentQuerySnapshot, error ->
            if (error != null) {
                fullMovementStatResponse.value = Result.Error(error.localizedMessage?: "Something went wrong.")
                return@addSnapshotListener
            }

            if (agentQuerySnapshot != null){
                for (document in agentQuerySnapshot.documents){
                    val path = document.reference.path

                    if (path.contains("total", ignoreCase = true)){
                        dashboardStat.transferByAgentName = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }

                    if (path.contains("today", ignoreCase = true)){
                        dashboardStat.transferByDate = DashboardStatData(
                            total = document.data?.get("total") as Long,
                            car = document.data?.get("car") as Long,
                            motorcycle = document.data?.get("motorcycle") as Long,
                            van = document.data?.get("van") as Long,
                            etricycle = document.data?.get("etricycle") as Long,
                            minibus = document.data?.get("minibus") as Long,
                            tricycle = document.data?.get("tricycle") as Long,
                            emotorcycle = document.data?.get("emotorcycle") as Long
                        )
                    }
                }
                fullMovementStatResponse.value = Result.Success(dashboardStat)
            }
        }

    }

    fun actionGetAssetReasons() {
        viewModelScope.launch {
            movementReasonRepo.getMovementReasons() //silently download
        }
    }

    fun actionGetLocations() {
        viewModelScope.launch {
            locationRepo.getLocations() //silently download
        }
    }

    fun actionGetVehicleTypes() {
        viewModelScope.launch {
            vehicleTypeRepo.getVehicleTypes() //silently download
        }
    }

    fun actionGetVehicleCheckListItem() {
        viewModelScope.launch {
            retrievalChecklistRepo.getRetrievalChecklistItems() //silently download
        }
    }

    fun clearVehicleTable() {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleRepo.deleteVehicleData()
        }
    }

    fun clearReasonTable() {
        viewModelScope.launch(Dispatchers.IO) {
            movementReasonRepo.deleteReasonData()
        }
    }

    fun getUserRole(userId: String){
        viewModelScope.launch {
            userRoleResponse.value = userRoleUseCase.invokeRole(userId)
        }
    }

    fun registerTokenToServer(tokenBody: HashMap<String, String>) {
        viewModelScope.launch {
            try {
                val response = vehicleService.saveToken(tokenBody)
                if (response.isSuccessful) {
                    Result.Success(response.body()!!)
                }else{
                    Result.Error("Something went wrong")
                }
            } catch (ex: Exception) {
                Result.Error("Error saving token to server")
            }
        }
    }

    fun getSavedUserLocation(userId: String, userCity: String, location: Location, fireStoreDB: FirebaseFirestore){
        val collectionRef = fireStoreDB.collection("Location")

        collectionRef.get().addOnCompleteListener {
            if (it.exception != null){
                return@addOnCompleteListener
            }

            if (it.isSuccessful)
            {
                try {
                    val document = it.result.documents.find {document->
                        document.getDocumentReference(userId) != null
                    }
                    if (document == null){
                        saveLocationToServer(userId, location, userCity)
                    }
                }catch (ex: NoSuchElementException){
                    saveLocationToServer(userId, location, userCity)
                }
            }
        }
    }

    private fun saveLocationToServer(
        userId: String,
        location: Location,
        userCity: String
    ) {
        val locationBody = HashMap<String, Any>().apply {
            this["user_id"] = userId
            this["longitude"] = location.longitude
            this["latitude"] = location.latitude
            this["city_name"] = userCity
        }

        viewModelScope.launch {
            locationRepo.saveLocationToServer(locationBody)
        }
    }
}