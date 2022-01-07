package ng.max.vams.data.remote.response

data class FullMovementStat(
    val agentVehicleEntrySummary: List<VehicleDashboardPair>,
    val agentVehicleEntrySummaryToday: List<VehicleDashboardPair>,
    val agentVehicleExitSummary: List<VehicleDashboardPair>,
    val agentVehicleExitSummaryToday: List<VehicleDashboardPair>,
    val agentVehicleTransferSummary: List<VehicleDashboardPair>,
    val agentVehicleTransferSummaryToday: List<VehicleDashboardPair>,
    val totalVehicleEntryByAgent: Int,
    val totalVehicleEntryByAgentToday: Int,
    val totalVehicleEntryCount: Int,
    val totalVehicleExitByAgent: Int,
    val totalVehicleExitByAgentToday: Int,
    val totalVehicleExitCount: Int,
    val totalVehicleTransferByAgent: Int,
    val totalVehicleTransferByAgentToday: Int,
    val totalVehicleTransferCount: Int,
    val vehiclesEntrySummary: List<VehicleDashboardPair>,
    val vehiclesExitSummary: List<VehicleDashboardPair>,
    val vehiclesTransferSummary: List<VehicleDashboardPair>
)