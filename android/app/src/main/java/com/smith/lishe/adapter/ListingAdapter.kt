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
import com.smith.lishe.FoodListingDetailsActivity
import com.smith.lishe.MainActivity
import com.smith.lishe.R
import com.smith.lishe.databinding.FoodListingItemBinding
import com.smith.lishe.model.ListingModel

class ListingAdapter(private val context: Context,
                     private val dataset: List<ListingModel>
) : RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    class ListingViewHolder( private val view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.food_listing_item_title_text_view)
        val descriptionTextView: TextView = view.findViewById(R.id.food_listing_item_description_text_view)
        val expirationTextView: TextView = view.findViewById(R.id.food_listing_item_expiration_text_view)
        val imageView: ImageView = view.findViewById(R.id.food_listing_item_image_view)
        val button: Button = view.findViewById(R.id.food_listing_item_more_icon_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        //Obtain an instance of LayoutInflater from the provided context
        // Create a new View
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_listing_item, parent, false)

        return ListingViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
        holder.expirationTextView.text = context.getString(R.string.expiration_date, item.expiration)

        val imgUri = item.imageUrl.toUri().buildUpon().scheme("https").build()
        holder.imageView.load(imgUri) {
            placeholder(R.drawable.ic_loading)
            error(R.drawable.ic_broken_image)
        }

        holder.button.setOnClickListener {
            val intent = Intent(context, FoodListingDetailsActivity::class.java)
            intent.putExtra(MainActivity.LISTING_ID, item._id)
            intent.putExtra(MainActivity.LISTING_USER_ID, item.creator)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size

}