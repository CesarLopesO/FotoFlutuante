package com.example.fotoflutuante

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MonthAdapter(
    private val months: List<File>,
    private val onMonthClick: (File) -> Unit
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val monthFolder = months[position]
        holder.bind(monthFolder)
    }

    override fun getItemCount(): Int = months.size

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: Button = view.findViewById(R.id.btnMonth)

        fun bind(monthFolder: File) {
            button.text = monthFolder.name // Nome da pasta, ex: "2024_01"
            button.setOnClickListener { onMonthClick(monthFolder) }
        }
    }
}
