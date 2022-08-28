package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidAdapter
import com.udacity.asteroidradar.AsteroidDatabase
import com.udacity.asteroidradar.OnClickListener
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    // For ViewModelFactory
    private lateinit var binding: FragmentMainBinding
    private val viewModelFactory : MainViewModelFactory by lazy {
        val application = requireNotNull(this.activity).application
        val dataSource = AsteroidDatabase.getInstance(application).asteroidDatabaseDao
        MainViewModelFactory(dataSource)
    }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // For the recyclerViewAdapter
        val adapter = AsteroidAdapter(OnClickListener {
            viewModel.displayAsteroidDetails(it)

        })
        // Setting the adapter to the recyclerView
        binding.asteroidRecycler.adapter = adapter

        // Observing Navigating to Detail Fragment
        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.doneNavigatingToDetailFragment()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.show_today_asteroids_menu -> viewModel.getTodayAsteroidsOnWards()
            R.id.show_saved_asteroids_menu -> viewModel.getAllSavedAsteroidsFromDatabase()
            else-> viewModel.getNewWeeklyAsteroids()
        }
        binding.invalidateAll()
        return true
    }
}
