package com.chistyakov.drwebtesttask

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chistyakov.drwebtesttask.databinding.ItemAppBinding

class AppsRecyclerAdapter(
    private var data: MutableList<App>,
    private val onAppClick: (App) -> Unit,
    ) : RecyclerView.Adapter<AppsRecyclerAdapter.AppsViewHolder>() {

    class AppsViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: App, onAppClick: (App) -> Unit) {
            Log.d("AppsViewHolder", "Binding App: ${app.name}")
            binding.imageView.setImageDrawable(app.icon)
            binding.nameTv.text = app.name
            binding.versionTv.text = app.version ?: ""
            binding.root.setOnClickListener {
                onAppClick(app)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val binding = ItemAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppsViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(apps: List<App>) {
        Log.d("AppsRecyclerAdapter", "Setting new data, size: ${apps.size}")
        data.clear()
        data.addAll(apps)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        Log.d("AppsRecyclerAdapter", "Binding Apps at position: $position")
        holder.bind(data[position], onAppClick)
    }

    override fun getItemCount(): Int = data.size
}