package com.dadaschatpos.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.R
import com.dadaschatpos.databinding.FragmentOrderBinding
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.CurrencyFormatter
import com.dadaschatpos.util.UiState
import com.google.android.material.snackbar.Snackbar

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }

    private lateinit var foodItemAdapter: FoodItemAdapter
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerViews()
        setupClicks()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        foodItemAdapter = FoodItemAdapter { item -> viewModel.addItem(item) }
        cartAdapter = CartAdapter(
            onIncrease = viewModel::increase,
            onDecrease = viewModel::decrease,
            onRemove = viewModel::remove
        )
        binding.itemsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = foodItemAdapter
        }
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupClicks() = with(binding) {
        searchEditText.doAfterTextChanged { editable -> viewModel.updateSearch(editable?.toString().orEmpty()) }
        resetButton.setOnClickListener { viewModel.resetCart() }
        generateBillButton.setOnClickListener { viewModel.generateBill() }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items -> foodItemAdapter.submitList(items) }
        viewModel.cart.observe(viewLifecycleOwner) { cart ->
            cartAdapter.submitList(cart)
            val totalQty = cart.sumOf { it.quantity }
            binding.cartBadgeText.text = "$totalQty items"
            binding.emptyCartText.isVisible = cart.isEmpty()
            binding.cartRecyclerView.isVisible = cart.isNotEmpty()
        }
        viewModel.total.observe(viewLifecycleOwner) { total ->
            binding.totalText.text = "Total: ${CurrencyFormatter.format(total)}"
        }
        viewModel.generateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> setGenerating(false)
                UiState.Loading -> setGenerating(true)
                is UiState.Error -> {
                    setGenerating(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    viewModel.resetGenerateState()
                }
                is UiState.Success -> {
                    setGenerating(false)
                    val orderId = state.data
                    viewModel.resetGenerateState()
                    findNavController().navigate(R.id.action_order_to_bill, bundleOf("orderId" to orderId))
                }
            }
        }
    }

    private fun setGenerating(generating: Boolean) {
        binding.generateBillButton.isEnabled = !generating
        binding.generateBillButton.text = if (generating) "Generating..." else "Generate Bill"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
