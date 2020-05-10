package com.example.android.architecture.blueprints.randomuser.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.databinding.UserItemBinding
import com.example.android.architecture.blueprints.randomuser.users.UserListViewModel

/**
 * Adapter for the inner list of saved users.
 */
class SavedUsersAdapter(private val viewModel: UserListViewModel) : ListAdapter<User, SavedUsersAdapter.ViewHolder>(UserDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: UserItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: UserListViewModel, item: User) {

            binding.viewmodel = viewModel
            binding.user = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}