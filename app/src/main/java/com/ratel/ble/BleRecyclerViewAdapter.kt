package com.ratel.ble

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.polidea.rxandroidble3.scan.ScanResult

class BleRecyclerViewAdapter: RecyclerView.Adapter<BleRecyclerViewAdapter.BleViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var items: ArrayList<ScanResult>? = ArrayList()

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.onItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val vh = BleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ble, parent, false)
        )
        onItemClickListener?.let { vh.setOnItemClickListener(it) }
        return vh
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        holder.bind(items?.get(position))
    }

    override fun getItemCount(): Int {
        return items?.size?:0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<ScanResult>?) {
        if (items == null) return
        this.items = items
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(view: View, scanResult: ScanResult?)
    }

    inner class BleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var onItemClickListener: OnItemClickListener? = null

        init {
            itemView.setOnClickListener { view ->
                onItemClickListener?.onClick(view, itemView.tag as? ScanResult)
            }
        }

        fun bind(scanResult: ScanResult?) {
            itemView.tag = scanResult

            val bleName = itemView.findViewById<TextView>(R.id.ble_text_name)
            bleName.text = scanResult?.bleDevice?.name

            val bleAddress = itemView.findViewById<TextView>(R.id.ble_text_address)
            bleAddress.text = scanResult?.bleDevice?.macAddress
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
            this.onItemClickListener = onItemClickListener
        }
    }
}