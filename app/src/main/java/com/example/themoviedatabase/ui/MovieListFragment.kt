package com.example.themoviedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedatabase.R
import com.example.themoviedatabase.data.repository.MovieRemoteDataSource
import com.example.themoviedatabase.data.repository.MovieRepository
import com.example.themoviedatabase.databinding.FragmentMovieListBinding


/**
 * Main fragment displaying list of movies.
 */
class MovieListFragment : Fragment() {

    //View model with remoteDataSource and repository as parameters
    private val viewModel: MainViewModel by viewModels {
        val remoteDataSource = MovieRemoteDataSource()
        val repository = MovieRepository(remoteDataSource)
        MovieViewModelFactory(repository)
    }

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MovieListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapters()
        setObservers()

    }

    //Adapters for Recyclerview
    private fun setAdapters() {
        //Adapter for recyclerview
        adapter = MovieListAdapter { movie ->
            //Capture data of movieClicked, pass movie.id argument to MovieDetail Fragment
            //Navigate to the movie Detail Fragment
            val action =   MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie.id)
            this.findNavController().navigate(action)
           }
        //Bind recyclerview adapter
        binding.recyclerView.adapter = adapter
    }

    //Observers for LiveData properties
    private fun setObservers() {
        //Attach an observer on the movies to listen for the data changes.
        //Call submitList() on the adapter and pass in the new list.
        // This will update the RecyclerView with the new items on the list.
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movies.let {
                adapter.submitList(it)
            }
        }
        //Attach an observer on the Api Status for changes.
        //Call updateStatus()
        // This will update statusImageView according to the status state.
        viewModel.status.observe(viewLifecycleOwner) { status ->
            val statusImageView = binding.statusImage
            val recyclerView = binding.recyclerView
            updateStatus(statusImageView, recyclerView, status)
        }
    }

    //Update statusImageView according to the status state.
    private fun updateStatus(
        statusImageView: ImageView,
        recyclerView: RecyclerView,
        status: TheMovieDbApiStatus?
    ) {
        when (status) {
            TheMovieDbApiStatus.LOADING -> {
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.loading_animation)
            }
            TheMovieDbApiStatus.DONE -> {
                statusImageView.visibility = View.GONE
            }
            TheMovieDbApiStatus.ERROR, null -> {
                statusImageView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                statusImageView.setImageResource(R.drawable.ic_connection_error)
                showToastError()
            }
        }
    }

    //Show Error Message Toast
    private fun showToastError() {
        //Show toast
        val toast = Toast.makeText(requireContext(), "An Error occurred", Toast.LENGTH_SHORT)
        toast.show()
    }
}