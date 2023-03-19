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
import com.example.themoviedatabase.MovieApplication
import com.example.themoviedatabase.R
import com.example.themoviedatabase.data.remote.requests.MovieRatingBodyRequest
import com.example.themoviedatabase.data.remote.results.MovieDetailsResult
import com.example.themoviedatabase.data.repository.guestsessionid.GuestSessionIdLocalDataSource
import com.example.themoviedatabase.data.repository.guestsessionid.GuestSessionIdRemoteDataSource
import com.example.themoviedatabase.data.repository.guestsessionid.GuestSessionIdRepository
import com.example.themoviedatabase.data.repository.movie.MovieLocalDataSource
import com.example.themoviedatabase.data.repository.movie.MovieRemoteDataSource
import com.example.themoviedatabase.data.repository.movie.MovieRepository
import com.example.themoviedatabase.data.repository.movierating.*
import com.example.themoviedatabase.data.utils.Error
import com.example.themoviedatabase.data.utils.Loaded
import com.example.themoviedatabase.data.utils.Loading
import com.example.themoviedatabase.data.utils.None
import com.example.themoviedatabase.databinding.FragmentMovieDetailBinding


/**
 * Fragment that displays the details of the selected movie.
 */

class MovieDetailFragment : Fragment() {

    //View model with movieRepository and movieRatingRepository as parameters
    //Initialize remote and local data sources
    private val viewModel: DetailViewModel by viewModels {
        val movieRemoteDataSource = MovieRemoteDataSource()
        val movieDetailsDao = (activity?.application as MovieApplication).database
            .movieDetailsDao()
        val movieLocalDataSource = MovieLocalDataSource(movieDetailsDao)
        val movieRepository = MovieRepository(movieRemoteDataSource, movieLocalDataSource)
        val guestSessionIdRemoteDataSource = GuestSessionIdRemoteDataSource()
        val guestSessionIdLocalDataSource = GuestSessionIdLocalDataSource(requireContext())
        val guestSessionIdRepository =
            GuestSessionIdRepository(guestSessionIdRemoteDataSource, guestSessionIdLocalDataSource)
        val movieRatingRemoteDataSource = MovieRatingRemoteDataSource()
        val movieRatingLocalDataSource = MovieRatingLocalDataSource(requireContext())
        val movieRatingRepository =
            MovieRatingRepository(
                guestSessionIdRepository,
                movieRatingRemoteDataSource,
                movieRatingLocalDataSource
            )
        DetailViewModelFactory(movieRepository, movieRatingRepository)
    }

    //Delegate to access fragment arguments as fragment instance
    private val navigationArgs: MovieDetailFragmentArgs by navArgs()

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    //To be used on a Toast message when rating a movie
    private var currentMovieId = 0

    //To be used on a Toast message when rating a movie
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
        getMovieRating(currentMovieId)
        setListeners()
        setObservers()
    }

    //Get movie details passing movieId to the viewModel
    private fun getMovieDetails() {
        //Take movieId passed from Movie List fragment
        val movieId = navigationArgs.movieId
        //Store movieId on currentMovieId (This Id will be used to rate the movie)
        currentMovieId = movieId
        //Call getMovieDetails from viewModel passing movieId
        viewModel.getMovieDetails(movieId)
    }

    //Get movie Rating passing movieId to the viewModel
    //This data will be used to update movie rating bar
    private fun getMovieRating(movieId: Int) {
        viewModel.getMovieRating(movieId)
    }


    //Listeners for UI
    private fun setListeners() {
        //When rateMovieButton is pressed call onSubmitRating()
        binding.rateMovieButton.setOnClickListener {
            val movieIdToRate = currentMovieId
            //Call onSubmitRating passing movieIdToRate
            onSubmitRating(movieIdToRate)
        }
    }

    //Observers for LiveData properties
    private fun setObservers() {
        //Attach an observer on the movieDetails to listen for the data changes.
        //Use a when to update UI according operation state
        //Call  bindMovieDetails() and pass movieDetails as parameter
        // This will update the UI with the new movie details.
        viewModel.movieDetails.observe(viewLifecycleOwner) { operation ->
            //Bind Status image to show operation state accordingly
            val statusImageView = binding.statusImage
            val movieDetailsDataGroup = binding.movieDetailsData

            when (operation) {
                is None -> {}
                is Loading -> {
                    movieDetailsDataGroup.visibility = View.GONE
                    statusImageView.setImageResource(R.drawable.loading_animation)
                    statusImageView.visibility = View.VISIBLE
                }
                is Loaded -> {
                    statusImageView.visibility = View.GONE
                    // This will update the MovieDetailFragment UI with the new movie clicked
                    val movieDetails = operation.data
                    bindMovieDetail(movieDetails)
                    movieDetailsDataGroup.visibility = View.VISIBLE
                }
                is Error -> {
                    movieDetailsDataGroup.visibility = View.GONE
                    statusImageView.setImageResource(R.drawable.ic_connection_error)
                    statusImageView.visibility = View.VISIBLE
                    showToastMsg(operation.error)
                }
            }
        }
        //Attach an observer on the movieRatingValue to listen for the data changes.
        //Use a when to update UI according operation state
        viewModel.movieRatingValue.observe(viewLifecycleOwner) { operation ->
            //Bind movieRatingBar to show operation state accordingly
            val movieRatingBar = binding.ratingBar

            when (operation) {
                is None -> {}
                is Loading -> {}
                is Loaded -> {
                    //Get movieRatingValue from operation.data.value and
                    //transform it to float to pass it to the ratingBar widget

                    //The Movie Database API movie rating minimum value is 0.5 but
                    // movieRatingBar.rating widget minimum value is 0
                    when (val movieRatingValue = operation.data.value.toFloat()) {
                        0.5F -> {
                            movieRatingBar.rating = 0F
                        }
                        //The Movie Database movie rating maximum value is 10 but
                        // movieRatingBar.rating widget minimum value is 5
                        else -> {
                            movieRatingBar.rating = movieRatingValue / 2
                        }
                    }
                }
                is Error -> {
                    //movieRatingBar rating value is 0.0
                    movieRatingBar.rating = 0F
                    // Show Error Msg on a Toast
                    showToastMsg(operation.error)
                }
            }
        }
        //Observe movieRatingResponse and show operation state accordingly
        viewModel.movieRatingResponse.observe(viewLifecycleOwner) { operation ->

            //Bind movieRatingProgressBar to show operation state accordingly
            val movieRatingProgressBar = binding.progressBar

            when (operation) {
                is None -> {}
                is Loading -> {
                    movieRatingProgressBar.visibility = View.VISIBLE
                }
                is Loaded -> {
                    movieRatingProgressBar.visibility = View.GONE
                    // Show Rating Submitted Msg
                    val statusMessage = operation.data.statusMessage
                    showRatingSubmittedMsg(statusMessage)
                }
                is Error -> {
                    movieRatingProgressBar.visibility = View.GONE
                    // Show Error Msg on a Toast
                    showToastMsg(operation.error)
                }
            }
        }
    }

    // Declare and initialize all of the list item UI components
    private fun bindMovieDetail(movieDetails: MovieDetailsResult) {

        with(binding) {
            movieTitle.text = movieDetails.title
            //Use substringBefore to leave only the year for the release date
            movieYear.text = movieDetails.releaseDate.substringBefore(
                delimiter = '-',
                missingDelimiterValue = getString(R.string.not_found)
            )
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

    //Show Toast Message
    private fun showToastMsg(message: String) {
        //Show toast
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
        toast.show()
    }


    //Fun called when submit rating button is pressed
    private fun onSubmitRating(movieId: Int) {
        // Get rating value from rating bar. Transform rating type which is float
        // to Double (needed by the API).
        //The MovieDataBase API allows rating value between 0.5 to 10
        //mul rating * 2 to match The Movie Database maximum rating value (10)
        var rating = binding.ratingBar.rating.toDouble() * 2

        //The MovieDataBase API allows minimum rating value of 0.5
        // so if rating bar is 0.0 then change it to 0.5
        when (rating) {
            0.0 -> rating = 0.5
        }
        // Pass rating value on movieRatingBody for POST movie rating
        val movieRatingValue = MovieRatingBodyRequest(value = rating)

        //call viewModel.postMovieRating and pass movieId and movieRatingValue
        viewModel.postMovieRating(movieId, movieRatingValue)

        //Store rating on currentRating. This will be used to show rating on a Toast message
        currentRating = rating
    }

    /** Show Toast Message with movieId, rating and statusMsg from Movie Rating Response **/
    private fun showRatingSubmittedMsg(message: String) {
        //Show Toast
        Toast.makeText(
            requireContext(),
            getString(R.string.rating_success, message, currentRating.toString()),
            Toast.LENGTH_LONG
        ).show()
    }
}