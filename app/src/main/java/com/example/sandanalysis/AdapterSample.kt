package com.example.sandanalysis

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterSample() : RecyclerView.Adapter<AdapterSample.HolderSample>() {
    private var context: Context? = null
    private var samplesList: ArrayList<ModelSample>? = null

    constructor(context: Context, samplesList: ArrayList<ModelSample>?) : this() {
        this.context = context
        this.samplesList = samplesList
    }

    class HolderSample(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fSampleImage: ImageView = itemView.findViewById(R.id.fSampleImage)
        var fInn: TextView = itemView.findViewById(R.id.fInn)
        var moreBtn: Button = itemView.findViewById(R.id.moreBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSample {
        return HolderSample(
            LayoutInflater.from(context).inflate(R.layout.samples_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HolderSample, position: Int) {
        val modelSample = samplesList!!.get(position)
        val id = modelSample.id
        val image = modelSample.image
        val inn = modelSample.inn
        val addedTimeStamp = modelSample.addTimeStamp
        val updatedTimeStamp = modelSample.updatedTimeStamp

        holder.fInn.text = inn
        if (image == "null") {
            holder.fSampleImage.setImageResource(R.drawable.ic_action_sample)
        } else {
            holder.fSampleImage.setImageURI(Uri.parse(image))
        }
        holder.itemView.setOnClickListener {

        }
        holder.moreBtn.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return samplesList!!.size
    }
}