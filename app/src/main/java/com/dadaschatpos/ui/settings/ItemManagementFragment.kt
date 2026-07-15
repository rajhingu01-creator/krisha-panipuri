package com.dadaschatpos.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.databinding.FragmentItemManagementBinding
import com.dadaschatpos.data.model.ItemEntity
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.ImageLoader
import com.dadaschatpos.util.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ItemManagementFragment : Fragment() {
    private var _binding: FragmentItemManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemManagementViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }

    private lateinit var adapter: ManageItemsAdapter
    private var selectedImage: String? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@registerForActivityResult
        runCatching {
            requireContext().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        selectedImage = uri.toString()
        ImageLoader.load(binding.selectedImageView, selectedImage)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ManageItemsAdapter(
            onEdit = ::editItem,
            onDelete = ::confirmDelete
        )
        binding.manageItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.manageItemsRecyclerView.adapter = adapter
        setupClicks()
        observeViewModel()
    }

    private fun setupClicks() = with(binding) {
        uploadImageButton.setOnClickListener { imagePicker.launch(arrayOf("image/*")) }
        saveItemButton.setOnClickListener {
            viewModel.save(
                name = itemNameEditText.text?.toString().orEmpty(),
                price = priceEditText.text?.toString().orEmpty(),
                category = categoryEditText.text?.toString().orEmpty(),
                image = selectedImage
            )
        }
        clearFormButton.setOnClickListener { clearForm() }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items -> adapter.submitList(items) }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> Unit
                UiState.Loading -> Snackbar.make(binding.root, "Saving...", Snackbar.LENGTH_SHORT).show()
                is UiState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                    clearForm()
                    viewModel.resetState()
                }
            }
        }
    }

    private fun editItem(item: ItemEntity) = with(binding) {
        viewModel.startEditing(item)
        selectedImage = item.image
        itemNameEditText.setText(item.name)
        priceEditText.setText(item.price.toString())
        categoryEditText.setText(item.category)
        saveItemButton.text = "Update Item"
        ImageLoader.load(selectedImageView, item.image)
    }

    private fun confirmDelete(item: ItemEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Delete ${item.name}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(item) }
            .show()
    }

    private fun clearForm() = with(binding) {
        viewModel.clearEditing()
        selectedImage = null
        itemNameEditText.text = null
        priceEditText.text = null
        categoryEditText.text = null
        selectedImageView.setImageResource(com.dadaschatpos.R.drawable.ic_upload_image)
        saveItemButton.text = "Add Item"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
