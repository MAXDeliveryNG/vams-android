package ng.max.vams.data.remote.response

data class FullMovementStat(
    val agentVehicleEntrySummary: List<AgentVehicleEntrySummary>,
    val agentVehicleEntrySummaryToday: List<AgentVehicleEntrySummaryToday>,
    val agentVehicleExitSummary: List<AgentVehicleExitSummary>,
    val agentVehicleExitSummaryToday: List<AgentVehicleExitSummaryToday>,
    val agentVehicleTransferSummary: List<AgentVehicleTransferSummary>,
    val agentVehicleTransferSummaryToday: List<AgentVehicleTransferSummaryToday>,
    val totalVehicleEntryByAgent: Int,
    val totalVehicleEntryByAgentToday: Int,
    val totalVehicleEntryCount: Int,
    val totalVehicleExitByAgent: Int,
    val totalVehicleExitByAgentToday: Int,
    val totalVehicleExitCount: Int,
    val totalVehicleTransferByAgent: Int,
    val totalVehicleTransferByAgentToday: Int,
    val totalVehicleTransferCount: Int,
    val vehiclesEntrySummary: List<VehiclesEntrySummary>,
    val vehiclesExitSummary: List<VehiclesExitSummary>,
    val vehiclesTransferSummary: List<VehiclesTransferSummary>
)