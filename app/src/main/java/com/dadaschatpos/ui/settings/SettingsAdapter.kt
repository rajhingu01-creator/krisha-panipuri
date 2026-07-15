package com.dadaschatpos.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.databinding.ItemSettingsBinding

enum class SettingsAction {
    ITEM_MANAGEMENT,
    CHANGE_PRICES,
    DAILY_SALES_REPORT,
    BACKUP_RESTORE,
    PRINTER_SETTINGS,
    APP_SHARE,
    ABOUT_APP,
    LOGOUT
}

data class SettingsOption(
    val action: SettingsAction,
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int
)

class SettingsAdapter(
    private val options: List<SettingsOption>,
    private val onClick: (SettingsOption) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    class SettingsViewHolder(
        private val binding: ItemSettingsBinding,
        private val onClick: (SettingsOption) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: SettingsOption) = with(binding) {
            settingsIcon.setImageResource(option.icon)
            settingsTitleText.text = option.title
            settingsSubtitleText.text = option.subtitle
            root.setOnClickListener { onClick(option) }
        }
    }
}
