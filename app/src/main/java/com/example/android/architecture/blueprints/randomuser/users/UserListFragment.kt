package com.example.android.architecture.blueprints.randomuser.users

import android.app.SearchManager
import android.content.Context.INPUT_METHOD_SERVICE
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.architecture.blueprints.randomuser.EventObserver
import com.example.android.architecture.blueprints.randomuser.R
import com.example.android.architecture.blueprints.randomuser.data.User
import com.example.android.architecture.blueprints.randomuser.databinding.FragmentUserListBinding
import com.example.android.architecture.blueprints.randomuser.users.adapters.SavedUsersAdapter
import com.example.android.architecture.blueprints.randomuser.users.adapters.UsersAdapter
import com.example.android.architecture.blueprints.randomuser.util.setupRefreshLayout
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject


/**
 * Display a grid of [User]s.
 */
class UserListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<UserListViewModel> { viewModelFactory }

    private lateinit var viewDataBinding: FragmentUserListBinding

    private lateinit var listAdapter: UsersAdapter

    private val suggestionsMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentUserListBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.randomUsersList)
        setupNavigation()
        setupHintsSearchAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchView: SearchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.queryHint = this.getString(R.string.search)

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.text_suggestion_label)
        val cursorAdapter = SimpleCursorAdapter(context, R.layout.hint_row, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        searchView.suggestionsAdapter = cursorAdapter
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(searchView)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                query?.let {
                    var index = suggestionsMap.size - 1
                    suggestionsMap.forEach { userId, userName ->
                        if (userName.contains(query, true)) {
                            cursor.addRow(arrayOf(index, userName))
                            index++
                        }
                    }
                }
                cursorAdapter.changeCursor(cursor)
                return false
            }
        })
        searchView.setOnSuggestionListener(object: SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard(searchView)
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))

                // Clear the searchView for future searches
                searchView.setQuery(null, false)

                val keys = suggestionsMap.filterValues { it == selection }.keys
                openUserDetails(keys.first())
                return true
            }
        })
    }

    private fun setupNavigation() {
        viewModel.openUserEvent.observe(viewLifecycleOwner, EventObserver {
            openUserDetails(it)
        })
    }

    private fun openUserDetails(userId: String) {
        val action = UserListFragmentDirections.actionUserListFragmentToUserDetailFragment(userId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = UsersAdapter(viewModel)
            viewDataBinding.randomUsersList.adapter = listAdapter
            viewDataBinding.randomUsersList.layoutManager = GridAutoFitLayoutManager(requireActivity(), 120)
            viewDataBinding.savedUsersList.adapter = SavedUsersAdapter(viewModel)
            viewDataBinding.savedUsersList.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupHintsSearchAdapter() {
        viewModel.searchSuggestions.observe(viewLifecycleOwner, Observer {
            suggestionsMap.putAll(it)
        })
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
