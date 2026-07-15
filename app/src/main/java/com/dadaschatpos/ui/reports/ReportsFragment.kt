package com.dadaschatpos.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.R
import com.dadaschatpos.data.model.CategorySale
import com.dadaschatpos.data.model.ChartPoint
import com.dadaschatpos.data.model.ReportFilter
import com.dadaschatpos.data.model.ReportSummary
import com.dadaschatpos.databinding.FragmentReportsBinding
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.CurrencyFormatter
import com.dadaschatpos.util.UiState
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar

class ReportsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }
    private val topProductsAdapter = TopProductsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topProductsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topProductsAdapter
        }
        setupFilters()
        observeViewModel()
        viewModel.load(ReportFilter.TODAY)
    }

    private fun setupFilters() = with(binding) {
        todayChip.setOnClickListener { viewModel.load(ReportFilter.TODAY) }
        weeklyChip.setOnClickListener { viewModel.load(ReportFilter.WEEKLY) }
        monthlyChip.setOnClickListener { viewModel.load(ReportFilter.MONTHLY) }
    }

    private fun observeViewModel() {
        viewModel.summaryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                is UiState.Success -> renderSummary(state.data)
            }
        }
    }

    private fun renderSummary(summary: ReportSummary) = with(binding) {
        totalSalesText.text = CurrencyFormatter.format(summary.totalSales)
        totalBillsText.text = summary.totalBills.toString()
        totalItemsText.text = summary.totalItemsSold.toString()
        averageBillText.text = CurrencyFormatter.format(summary.averageBillValue)
        topProductsAdapter.submitList(summary.topProducts)
        renderBarChart(summary.salesChart)
        renderPieChart(summary.categoryChart)
    }

    private fun renderBarChart(points: List<ChartPoint>) = with(binding.barChart) {
        if (points.isEmpty()) {
            clear()
            setNoDataText("No sales data")
            invalidate()
            return@with
        }
        val entries = points.mapIndexed { index, point -> BarEntry(index.toFloat(), point.total.toFloat()) }
        val dataSet = BarDataSet(entries, "Sales").apply {
            color = ContextCompat.getColor(requireContext(), R.color.dark_red)
            valueTextColor = Color.DKGRAY
            valueTextSize = 10f
        }
        data = BarData(dataSet).apply { barWidth = 0.45f }
        description.isEnabled = false
        axisRight.isEnabled = false
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String = points.getOrNull(value.toInt())?.label.orEmpty()
        }
        animateY(700)
        invalidate()
    }

    private fun renderPieChart(categories: List<CategorySale>) = with(binding.pieChart) {
        if (categories.isEmpty()) {
            clear()
            setNoDataText("No category data")
            invalidate()
            return@with
        }
        val entries = categories.map { PieEntry(it.total.toFloat(), it.category) }
        val colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.dark_red),
            ContextCompat.getColor(requireContext(), R.color.gold),
            ContextCompat.getColor(requireContext(), R.color.success_green),
            ContextCompat.getColor(requireContext(), R.color.danger_red)
        )
        val dataSet = PieDataSet(entries, "Categories").apply {
            this.colors = colors
            valueTextColor = Color.WHITE
            valueTextSize = 11f
        }
        data = PieData(dataSet)
        description.isEnabled = false
        centerText = "Sales"
        setEntryLabelColor(Color.DKGRAY)
        animateY(700)
        invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
