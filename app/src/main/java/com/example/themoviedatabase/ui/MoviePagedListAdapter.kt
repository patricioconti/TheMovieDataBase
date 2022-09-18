package com.example.themoviedatabase.ui


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.themoviedatabase.R
import com.example.themoviedatabase.databinding.MovieListItemBinding
import com.example.themoviedatabase.data.remote.results.MovieDb

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */

//Using PagingDataAdapter and DiffUtils to update changes on the recycler view elements
//Pass lambda fun val movieClickedListener: (MovieDb) -> Unit for event click on Movie Item
class MoviePagedListAdapter (private val movieClickedListener: (MovieDb) -> Unit) :
   PagingDataAdapter<MovieDb, MoviePagedListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data at the current position
        val movie = getItem(position)
        // Bind Movie Data with UI components
        if (movie != null) {
            holder.bind(movie)
            // When clicking on an itemView of the recycler view then
            // call lambda movieClickedListener passing parameter movie (movie in current the position)
            holder.itemView.setOnClickListener { movieClickedListener(movie)
            }
        }
    }

    /**
     * Initialize view elements
     */
    class ViewHolder(private val binding: MovieListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Declare and initialize all of the list item UI components
        fun bind(movie: MovieDb) {

            with(binding) {
                movieTitle.text = movie.title
                //Use substringBefore to leave only the year for the release date
                movieYear.text = movie.releaseDate.substringBefore(
                    delimiter = '-',
                    missingDelimiterValue = "- not found"
                )
                movieVoteAverage.text = movie.voteAverage.toString()
                movieVoteCount.text = movie.voteCount.toString()
                //Set movie cover with coil
                movieCover.load("https://image.tmdb.org/t/p/w185/${movie.posterPath}") {
                    //Set image while loading and at error
                    placeholder(R.drawable.loading_animation)
                    error(R.drawable.ic_broken_image)
                }
            }
        }
    }

    //Companion Object for DiffUtils
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MovieDb>() {
            override fun areItemsTheSame(oldItem: MovieDb, newItem: MovieDb): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: MovieDb, newItem: MovieDb): Boolean {
                return oldItem == newItem
            }
        }
    }
}

