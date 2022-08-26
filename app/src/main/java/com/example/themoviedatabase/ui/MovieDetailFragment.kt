package com.example.themoviedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.themoviedatabase.R
import com.example.themoviedatabase.data.network.requests.MovieRatingDto
import com.example.themoviedatabase.data.network.results.moviedetails.MovieDetailsResult
import com.example.themoviedatabase.data.repository.MovieRemoteDataSource
import com.example.themoviedatabase.data.repository.MovieRepository
import com.example.themoviedatabase.databinding.FragmentMovieDetailBinding


/**
 * Fragment that displays the details of the selected item.
 */

class MovieDetailFragment : Fragment() {


    private val viewModel: DetailViewModel by viewModels {
        val remoteDataSource = MovieRemoteDataSource()
        val repository = MovieRepository(remoteDataSource)
        DetailViewModelFactory(repository)
    }

    private val navigationArgs: MovieDetailFragmentArgs by navArgs()

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private var currentMovieId = 0

    private var currentRating = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMovieDetails()
        setListeners()
        setObservers()
    }


    private fun getMovieDetails() {
        val movieId = navigationArgs.movieId
        currentMovieId = movieId
        viewModel.getMovieDetails(movieId)
    }


    //Listeners for UI
    private fun setListeners() {
        //When rateMovieButton is pressed call onSubmitRating()
        binding.rateMovieButton.setOnClickListener {
            onSubmitRating()
        }
    }

    //Observers for LiveData properties
    private fun setObservers() {

        viewModel.error.observe(viewLifecycleOwner) { error ->
            //Show Toast
            Toast.makeText(
                requireContext(),
                 error,
                Toast.LENGTH_LONG
            ).show()


        }


        //Attach an observer on the single movie LiveData to listen for the data changes.
        viewModel.movieDetails.observe(viewLifecycleOwner) { movieDetails ->
            // This will update the MovieDetailFragment UI with the new movie clicked
            bindMovieDetail(movieDetails)
        }
           }


    // Declare and initialize all of the list item UI components
    private fun bindMovieDetail(movieDetails: MovieDetailsResult) {

        with(binding) {
            movieTitle.text = movieDetails.title
            //Use substringBefore to leave only the year for the release date
            movieYear.text =  movieDetails.releaseDate.substringBefore(
                delimiter = '-',
                missingDelimiterValue = "- not found"
            )
            movieGenre.text = movieDetails.genres.first().name
            movieOverview.text = movieDetails.overview
            //Load movie images with coil
            movieBackdrop.load("https://image.tmdb.org/t/p/w780/${movieDetails.backdropPath}") {
             //Set image at error
             error(R.drawable.ic_broken_image)
            }
            moviePoster.load("https://image.tmdb.org/t/p/w185/${movieDetails.posterPath}") {
                //Set image at error
              error(R.drawable.ic_broken_image)
            }
        }
    }


    //Fun called when submit rating button is pressed
    private fun onSubmitRating() {        // Get rating value from rating bar
        //The MovieDataBase API allows rating value between 0.5 to 10
        //mul rating * 2 to match The Movie Database maximum rating value (10)
        var rating = binding.ratingBar.rating.toDouble() * 2

        //The MovieDataBase API allows minimum rating value of 0.5
        // so if rating bar is 0.0 then change it to 0.5
        when (rating) {
            0.0 -> rating = 0.5
        }

        //movieRatingBody for POST movie rating
        val movieRatingBody = MovieRatingDto(value = rating)

        //Store rating on currentRating. This will be used to show rating on a Toast message
        currentRating = rating

        ratingSubmitted()


    }

    /** Show Toast Message with movieId, rating and statusMsg from Movie Rating Response **/
    private fun ratingSubmitted() {
        //Show Toast
        Toast.makeText(
            requireContext(),
            "Movie id: is $currentMovieId Rating is $currentRating",
            Toast.LENGTH_LONG
        ).show()
    }
}