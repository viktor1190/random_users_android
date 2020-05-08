package com.example.android.architecture.blueprints.randomuser.users

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.architecture.blueprints.randomuser.data.User

/**
 * [BindingAdapter]s for the [User]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<User>) {
    (listView.adapter as UsersAdapter).submitList(items)
}

@BindingAdapter("app:profileImage")
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view.context)
            .load(imageUrl)
            .into(view)
}