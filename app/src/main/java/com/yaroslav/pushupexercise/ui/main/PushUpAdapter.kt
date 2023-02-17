package com.yaroslav.pushupexercise.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yaroslav.pushupexercise.databinding.FragmentPushUpItemBinding
import com.yaroslav.pushupexercise.models.PushUp
import java.text.SimpleDateFormat
import java.util.*

class PushUpAdapter() :
    ListAdapter<PushUp, PushUpAdapter.PushUpViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PushUpViewHolder {

        val viewHolder = PushUpViewHolder(
            FragmentPushUpItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
//        viewHolder.itemView.setOnClickListener {
//            val position = viewHolder.absoluteAdapterPosition
//            actionListener.onProductDetails(getItem(position))
//        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PushUpViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PushUpViewHolder(private var binding: FragmentPushUpItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(pushUp: PushUp) {
            //binding.testText.text = pushUp.toString()
            binding.pushUps.text = pushUp.countPushUps.toString()

            //"yyyy/M/dd"
            val sdf = SimpleDateFormat( "hh:mm")
            binding.time.text = sdf.format(Date(pushUp.recordTime.toLong()*1000)).toString()

//            binding.buttonBasketDelete.setOnClickListener {
//                actionListener.onProductDelete(productBasket)
//            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PushUp>() {
            override fun areItemsTheSame(oldItem: PushUp, newItem: PushUp): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PushUp, newItem: PushUp): Boolean {
                // todo compare visible elements
                return oldItem == newItem
            }

        }
    }


}