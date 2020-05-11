/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.randomuser.userdetail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.android.architecture.blueprints.randomuser.EventObserver
import com.example.android.architecture.blueprints.randomuser.R
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.databinding.FragmentUserDetailBinding
import com.example.android.architecture.blueprints.randomuser.users.getImageContactAsByteArray
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main UI for the user detail screen.
 */
class UserDetailFragment : DaggerFragment() {
    private lateinit var viewDataBinding: FragmentUserDetailBinding

    private val args: UserDetailFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<UserDetailViewModel> { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
    }

    private fun setupFab() {
        viewModel.addUserToContactsCommand.observe(viewLifecycleOwner, EventObserver {
            viewModel.user.value?.let {
                createContactFromUserIntent(it)
            }

        })
        activity?.findViewById<View>(R.id.fab_add_contacts)?.setOnClickListener {
            viewModel.addUserToContacts()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_detail, container, false)
        viewDataBinding = FragmentUserDetailBinding.bind(view).apply {
            viewmodel = viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.start(args.userId)

        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_favorite -> {
                if (viewModel.user.value?.isSavedAsFavorite == true) {
                    viewModel.deleteUser()
                } else {
                    viewModel.saveUser()
                }
                updateMenuDrawableState(item, viewModel.user.value?.isSavedAsFavorite == true)
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.userdetail_fragment_menu, menu)
        val favoriteMenuItem = menu.findItem(R.id.menu_favorite)
        updateMenuDrawableState(favoriteMenuItem, args.isFavorite)
    }

    private fun createContactFromUserIntent(user: User) {
        lifecycleScope.launch {
            // Attaching the photo for the contacts app
            val imageData = ArrayList<ContentValues>()
            val row = ContentValues()
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, user.getImageContactAsByteArray(requireContext()))
            imageData.add(row)
            // Creating the contact intent
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.NAME, user.fullName)
                putExtra(ContactsContract.Intents.Insert.EMAIL, user.email)
                putExtra(ContactsContract.Intents.Insert.PHONE, user.phone)
                putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, user.cellphone)
                putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, imageData)
            }

            startActivity(intent)
        }
    }

    private fun updateMenuDrawableState(menuItem: MenuItem, isFavorite: Boolean) {
        if (menuItem.itemId == R.id.menu_favorite) {
            val stateListFavoriteDrawable = resources.getDrawable(R.drawable.menu_item_favorite_selector)
            val state = if (isFavorite) android.R.attr.state_checked else android.R.attr.state_empty
            stateListFavoriteDrawable.setState(intArrayOf(state))
            menuItem.setIcon(stateListFavoriteDrawable.current)
            menuItem.isChecked = isFavorite
        }
    }
}
