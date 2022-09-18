package com.example.themoviedatabase.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.themoviedatabase.MovieApplication
import com.example.themoviedatabase.R
import com.example.themoviedatabase.data.repository.movierating.MovieLocalDataSource
import com.example.themoviedatabase.data.repository.movierating.MovieRemoteDataSource
import com.example.themoviedatabase.data.repository.movierating.MovieRepository
import com.example.themoviedatabase.databinding.FragmentMovieListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Main fragment displaying list of movies.
 */
class MovieListFragment : Fragment(), MenuProvider {

    //View model with remoteDataSource, localDataSource and repository as parameters
    private val viewModel: MainViewModel by viewModels {
        val remoteDataSource = MovieRemoteDataSource()
        val movieDetailsDao = (activity?.application as MovieApplication).database
            .movieDetailsDao()
        val localDataSource = MovieLocalDataSource(movieDetailsDao)
        val repository = MovieRepository(remoteDataSource, localDataSource)
        MainViewModelFactory(repository)
    }

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    //Adapters for recyclerView

    //adapter will be the recyclerview adapter when using Search View
    //This is to avoid requesting more pages when filtering movie list.
    private lateinit var adapter: MovieListAdapter

    //pagedListAdapter will be the recyclerview adapter to enable paging.
    private lateinit var pagedListAdapter: MoviePagedListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Add Menu to fragment
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
        setListeners()
        collectMovies()
        loadStateListener()
    }

    //Adapters for Recyclerview
    private fun setAdapters() {
        //Adapter for recyclerview when using searchView
        adapter = MovieListAdapter { movie ->
            //Capture data of movieClicked, pass movie.id argument to MovieDetail Fragment
            //Navigate to the movie Detail Fragment
            val action =
                MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie.id)
            this.findNavController().navigate(action)
        }

        //Adapter for recyclerview by default (pagingListAdapter)
        pagedListAdapter = MoviePagedListAdapter { movie ->
            //Capture data of movieClicked, pass movie.id argument to MovieDetail Fragment
            //Navigate to the movie Detail Fragment
            val action =
                MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie.id)
            this.findNavController().navigate(action)
        }

        //Default recyclerView adapter is pagedListAdapter
        binding.recyclerView.adapter = pagedListAdapter
    }

    private fun setListeners() {
        //Retry button after an error, to reload movies
        binding.retryButton.setOnClickListener { pagedListAdapter.retry() }
    }


    //Use coroutines to collect pagingData Flow
    private fun collectMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            //We use collectLatest on the pagingData Flow so that collection on previous pagingData emissions
            // is canceled when a new pagingData instance is emitted.
            viewModel.getPagedPopularMovies().collectLatest { pagingData ->
                //We notify the adapter of changes with submitData() instead of submitList().
                pagedListAdapter.submitData(pagingData)
            }
        }
    }
    //Use coroutines to collect loadState Flow
    private fun loadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            pagedListAdapter.loadStateFlow.collectLatest { loadState ->

                with(binding) {
                    //Progress Bar is visible when LoadState.Loading
                    progressBar.isVisible = loadState.refresh is LoadState.Loading
                    //Retry Button is visible when LoadState.Error
                    retryButton.isVisible = loadState.refresh is LoadState.Error
                    //Status Image is visible when LoadState.Error
                    statusImage.isVisible = loadState.refresh is LoadState.Error
                    //If LoadState.Error show toast error
                    if (loadState.refresh is LoadState.Error) {
                        val errorMsg = getString(R.string.error_message)
                        showToastError(errorMsg)
                    }
                }
            }
        }
    }

    //Show Error Message Toast
    private fun showToastError(error: String) {
        //Show toast
        val toast = Toast.makeText(requireContext(), error, Toast.LENGTH_LONG)
        toast.show()
    }

    //Create Menu for searchView
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //Inflate menu
        menuInflater.inflate(R.menu.main_menu, menu)

        //Find Menu item (Search View widget)
        val item: MenuItem = menu.findItem(R.id.action_search)

        //Object actionView casted as SearchView type
        val searchView = item.actionView as SearchView

        //Call setOnQueryTextListener so whenever the user keeps typing this method will be called.
        //Pass listener object
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //Return true for this method. I will use onQueryTextChange.
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //Whenever the user keeps typing this method will be called all the time.
            override fun onQueryTextChange(newText: String?): Boolean {
                //Check if newText in not empty (query text)
                if (newText!!.isNotEmpty()) {
                    //submit a snapshot list from the current items present on the pagedListAdapter
                    //The list is submitted to the adapter used in searchView
                    adapter.submitList(pagedListAdapter.snapshot().items)
                    //RecyclerView adapter is adapter
                    binding.recyclerView.adapter = adapter
                    //No results found message is GONE
                    binding.noResultsTv.visibility = View.GONE
                    //Search to lowercase
                    val search = newText.lowercase()
                    //movieFilteredList is snapshot list filtered by title.lowercase()
                    val movieFilteredList = pagedListAdapter.snapshot().items.filter {
                        it.title.lowercase().contains(search)
                    }
                    //submit movieFilteredList to the adapter
                    adapter.submitList(movieFilteredList)


                    //if no results found, show no results found text view.
                    if (movieFilteredList.isEmpty()) {
                        binding.noResultsTv.visibility = View.VISIBLE
                    }

                }
                //Query text is empty so pagedListAdapter will be the recyclerView adapter
                else {
                    //Hide No results found text
                    binding.noResultsTv.visibility = View.GONE
                    //PagedListAdapter will be the recyclerView adapter
                    binding.recyclerView.adapter = pagedListAdapter
                }
                return true
            }
        })
    }

    //No use of onMenuItemSelected
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}

