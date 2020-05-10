package com.example.android.architecture.blueprints.randomuser.users

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.users.adapters.SavedUsersAdapter
import com.example.android.architecture.blueprints.randomuser.users.adapters.UsersAdapter

/**
 * [BindingAdapter]s for the [User]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: PagedList<User>?) {
    items?.let {
        (listView.adapter as UsersAdapter).submitList(it)
    }
}

/**
 * [BindingAdapter]s for the [User]s list.
 */
@BindingAdapter("app:setSavedUsersItems")
fun setSavedUsersItems(listView: RecyclerView, items: List<User>?) {
    items?.let {
        (listView.adapter as SavedUsersAdapter).submitList(it)
    }
}

@BindingAdapter("app:profileImage")
fun loadImage(view: ImageView, imageUrl: String?) {
    Glide.with(view.context)
            .load(imageUrl)
            .into(view)
}