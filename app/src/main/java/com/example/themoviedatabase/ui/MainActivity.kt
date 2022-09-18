package com.example.themoviedatabase.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.themoviedatabase.MovieApplication
import com.example.themoviedatabase.R
import com.example.themoviedatabase.data.repository.movierating.MovieLocalDataSource
import com.example.themoviedatabase.data.repository.movierating.MovieRemoteDataSource
import com.example.themoviedatabase.data.repository.movierating.MovieRepository


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //navcontroller for navigation component
    private lateinit var navController: NavController

    //View model with remoteDataSource, localDataSource and repository as parameters
    private val viewModel: MainViewModel by viewModels {
        val remoteDataSource = MovieRemoteDataSource()
        val movieDetailsDao = (this.application as MovieApplication).database
            .movieDetailsDao()
        val localDataSource = MovieLocalDataSource(movieDetailsDao)
        val repository = MovieRepository(remoteDataSource,localDataSource)
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //Call Splash Screen (Remember to always call it before super.onCreate()
        val splashScreen = installSplashScreen()

        //Show splash until data is loaded (wait until isDataLoading false)
        splashScreen.setKeepOnScreenCondition {
            viewModel.isDataLoading
        }

        super.onCreate(savedInstanceState)


        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}

