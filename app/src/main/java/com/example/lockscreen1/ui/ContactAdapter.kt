package com.example.lockscreen1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lockscreen1.R
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.interfaces.CallInterface
import kotlinx.android.synthetic.main.rv_item.view.*

class ContactAdapter(private val listener: CallInterface): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {


    var item : MutableList<ContactData> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun popMod(data: ContactData){
                itemView.tvContactName.text = data.name
                itemView.tvContactNumber.text = data.number
                itemView.btnCall.setOnClickListener {
                    listener.onCallItemClick(data.number)
                }

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.popMod(item[position])
    }
}