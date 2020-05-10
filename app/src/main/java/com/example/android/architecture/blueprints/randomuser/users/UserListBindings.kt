package com.example.android.architecture.blueprints.randomuser.users

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.users.adapters.SavedUsersAdapter
import com.example.android.architecture.blueprints.randomuser.users.adapters.UsersAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

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

suspend fun User.getImageContactAsByteArray(context: Context): ByteArray {
    val future = Glide.with(context)
            .asBitmap()
            .load(this.pictureLargeUrl)
            .submit()
    val resource: Bitmap = withContext(Dispatchers.IO) { future.get() }
    val bytesStream = ByteArrayOutputStream()
    resource.compress(Bitmap.CompressFormat.JPEG, 90, bytesStream)
    return bytesStream.toByteArray()
}