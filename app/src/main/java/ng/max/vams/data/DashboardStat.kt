package ng.max.vams.data

data class DashboardStatData (
    val motorcycle: Long = 0,
    val total: Long = 0,
    val van: Long = 0,
    val car: Long = 0,
    val etricycle: Long = 0,
    val minibus: Long = 0,
    val tricycle: Long = 0,
    val emotorcycle: Long = 0,
        )

data class DashboardStat (
    var totalEntry: DashboardStatData = DashboardStatData(),
    var totalExit: DashboardStatData = DashboardStatData(),
    var totalTransfer: DashboardStatData = DashboardStatData(),
    var entryByAgentName: DashboardStatData = DashboardStatData(),
    var exitByAgentName: DashboardStatData = DashboardStatData(),
    var transferByAgentName: DashboardStatData = DashboardStatData(),
    var entryByDate: DashboardStatData = DashboardStatData(),
    var exitByDate: DashboardStatData = DashboardStatData(),
    var transferByDate: DashboardStatData = DashboardStatData()
)