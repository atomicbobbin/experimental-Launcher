package org.fossify.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.fossify.home.activities.SimpleActivity
import org.fossify.home.databinding.ItemLauncherLabelBinding
import org.fossify.home.models.AppLauncher

class SuggestionsAdapter(
    private val activity: SimpleActivity,
    private val itemClick: (AppLauncher) -> Unit
) : ListAdapter<AppLauncher, SuggestionsAdapter.ViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLauncherLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(launcher: AppLauncher) {
            val binding = ItemLauncherLabelBinding.bind(itemView)
            binding.launcherLabel.text = launcher.title
            binding.launcherIcon.setImageDrawable(launcher.drawable)
            itemView.setOnClickListener { itemClick(launcher) }
        }
    }

    private class Diff : DiffUtil.ItemCallback<AppLauncher>() {
        override fun areItemsTheSame(oldItem: AppLauncher, newItem: AppLauncher): Boolean =
            oldItem.getLauncherIdentifier() == newItem.getLauncherIdentifier()

        override fun areContentsTheSame(oldItem: AppLauncher, newItem: AppLauncher): Boolean =
            oldItem.title == newItem.title
    }
}


