package com.dadaschatpos.data.model

data class ReportSummary(
    val totalSales: Double = 0.0,
    val totalBills: Int = 0,
    val totalItemsSold: Int = 0,
    val averageBillValue: Double = 0.0,
    val topProducts: List<TopProduct> = emptyList(),
    val salesChart: List<ChartPoint> = emptyList(),
    val categoryChart: List<CategorySale> = emptyList()
)

data class TopProduct(
    val name: String,
    val qty: Int,
    val total: Double
)

data class ChartPoint(
    val label: String,
    val total: Double
)

data class CategorySale(
    val category: String,
    val total: Double
)

enum class ReportFilter {
    TODAY,
    WEEKLY,
    MONTHLY
}
