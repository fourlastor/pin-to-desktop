package com.example.pintodesktop

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class AppListAdapter(private val layoutInflater: LayoutInflater) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    private var models = emptyList<AppViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = layoutInflater.inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.updateWith(models[position])
    }

    override fun getItemCount(): Int {
        return models.size
    }

    fun updateWith(models: List<AppViewModel>) {
        this.models = models
        notifyDataSetChanged()
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById<TextView>(R.id.title)
        private val image: ImageView = itemView.findViewById<ImageView>(R.id.image)

        fun updateWith(model: AppViewModel) {
            title.text = model.label
            image.setImageDrawable(model.icon)
        }
    }

    data class AppViewModel(val icon: Drawable, val label: CharSequence)
}
