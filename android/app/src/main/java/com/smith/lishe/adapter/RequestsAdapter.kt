package com.smith.lishe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.smith.lishe.FoodRequestDetailsActivity
import com.smith.lishe.MainActivity
import com.smith.lishe.R
import com.smith.lishe.databinding.FoodRequestItemBinding
import com.smith.lishe.model.RequestModel

class RequestsAdapter(private val context: Context,
                      private val dataset: List<RequestModel>
) : RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder>() {

    class RequestsViewHolder( private val view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.food_request_item_title_text_view)
        val descriptionTextView: TextView = view.findViewById(R.id.food_request_item_description_text_view)
        val statusTextView: TextView = view.findViewById(R.id.food_request_item_status_text_view)
        val imageView: ImageView = view.findViewById(R.id.food_request_item_image_view)
        val statusIconImageView: ImageView = view.findViewById(R.id.food_request_item_status_icon)
        val button: Button = view.findViewById(R.id.food_request_item_more_icon_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        //Obtain an instance of LayoutInflater from the provided context
        // Create a new View
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_request_item, parent, false)

        return RequestsViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        val item = dataset[position]
        Log.d("Requests Adapter", item.listingDetails[0].title)

        holder.titleTextView.text = item.listingDetails[0].title
        holder.descriptionTextView.text = item.listingDetails[0].description
        holder.statusTextView.text = item.status

        val statusIcon = when(item.status) {
            "confirmed" -> R.drawable.ic_food_confirmed
            "cancelled" -> R.drawable.ic_request_cancelled
            "completed" -> R.drawable.ic_complete
            else -> R.drawable.ic_pending
        }

        holder.statusIconImageView.setImageResource(statusIcon)

        val imgUri = item.listingDetails[0].imageUrl.toUri().buildUpon().scheme("https").build()
        holder.imageView.load(imgUri) {
            placeholder(R.drawable.ic_loading)
            error(R.drawable.ic_broken_image)
        }

        holder.button.setOnClickListener {
            val intent = Intent(context, FoodRequestDetailsActivity::class.java)
            intent.putExtra(MainActivity.LISTING_ID, item._id)
            intent.putExtra(MainActivity.LISTING_USER_ID, item.creator)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size

}