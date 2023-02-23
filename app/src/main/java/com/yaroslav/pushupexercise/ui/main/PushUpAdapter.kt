package com.yaroslav.pushupexercise.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yaroslav.pushupexercise.databinding.FragmentPushUpItemBinding
import com.yaroslav.pushupexercise.models.PushUp
import com.yaroslav.pushupexercise.utils.formatHour24AndMinutes
import java.text.SimpleDateFormat
import java.util.*

class PushUpAdapter(
    private val childFragmentManager: FragmentManager,
    private val deletePushUp: (Int) -> Unit,
    private val editPushUp: (Int) -> Unit) :
    ListAdapter<PushUp, PushUpAdapter.PushUpViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PushUpViewHolder {

        val viewHolder = PushUpViewHolder(
            FragmentPushUpItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            val id: Int = getItem(position).id ?: -1
            editPushUp(id)

        }

        viewHolder.itemView.setOnLongClickListener { _ ->
            PurchaseConfirmationDialogFragment(object : DialogActions {
                override fun onClickOK() {
                    val position = viewHolder.absoluteAdapterPosition
                    val id: Int = getItem(position).id ?: -1
                    deletePushUp(id)
                }
                override fun onClickNO() {

                }

            })
                .show(childFragmentManager, PurchaseConfirmationDialogFragment.TAG)
            true
        }


        return viewHolder
    }

    override fun onBindViewHolder(holder: PushUpViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PushUpViewHolder(private var binding: FragmentPushUpItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(pushUp: PushUp) {
            binding.pushUps.text = pushUp.countPushUps.toString()
            binding.time.text = formatHour24AndMinutes.format(Date(pushUp.recordTime.toLong()*1000)).toString()

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

// todo replace to fun
interface DialogActions{
    fun onClickOK()
    fun onClickNO()
}

class PurchaseConfirmationDialogFragment(private val dialogActions:DialogActions) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Czy na pewno chcesz to usunąć?")
            .setPositiveButton("Tak") { _,_ ->
                dialogActions.onClickOK()
            }.setNeutralButton("Anulować"){_,_-> }
            .setNegativeButton("Nie"){_,_->}
            .create()



    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}