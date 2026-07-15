package com.dadaschatpos.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.databinding.ItemOnboardingBinding

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

class OnboardingAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnboardingPage) = with(binding) {
            onboardingImage.setImageResource(page.imageRes)
            onboardingTitle.text = page.title
            onboardingDescription.text = page.description
        }
    }
}
