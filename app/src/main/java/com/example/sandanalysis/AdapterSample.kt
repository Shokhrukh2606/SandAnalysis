package com.example.sandanalysis

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sandanalysis.util.gmt0
import com.example.sandanalysis.util.toDatetime

class AdapterSample() : RecyclerView.Adapter<AdapterSample.HolderSample>() {
    private var context: Context? = null
    private var samplesList: MutableList<ModelSample>? = null

    constructor(context: Context, samplesList: MutableList<ModelSample>?) : this() {
        this.context = context
        this.samplesList = samplesList
    }

    class HolderSample(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fSampleImage: ImageView = itemView.findViewById(R.id.fSampleImage)
        var fInn: TextView = itemView.findViewById(R.id.fInn)
        var moreBtn: ImageButton = itemView.findViewById(R.id.moreBtn)
        var addedTimeStamp:TextView=itemView.findViewById(R.id.addedTimestamp)
        var fIndex:TextView=itemView.findViewById(R.id.fIndex)
//        var fLat:TextView=itemView.findViewById(R.id.fLat)
//        var fLong:TextView=itemView.findViewById(R.id.fLong)
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
        val pLat = modelSample.pLat
        val pLong = modelSample.pLong
        holder.fIndex.text=(position+1).toString()
        holder.fInn.text = inn
        holder.fSampleImage.setImageResource(R.drawable.ic_action_sample)
        holder.addedTimeStamp.text=addedTimeStamp.toLong().gmt0()?.toDatetime("dd-MM-yyyy HH:mm")
//        holder.fLat.text=pLat
//        holder.fLong.text=pLong
        holder.itemView.setOnClickListener {

        }
        holder.moreBtn.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return samplesList!!.size
    }
}