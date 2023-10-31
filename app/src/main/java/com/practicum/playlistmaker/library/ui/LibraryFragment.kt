package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentLibraryBinding

class LibraryFragment: Fragment() {

    private lateinit var binding: FragmentLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = LibraryViewPagerAdapter(
            childFragmentManager,
            lifecycle
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            when (position) {
                FIRST_POSITION -> tab.text = resources.getString(R.string.library_tab_name_favourites)
                SECOND_POSITION -> tab.text = resources.getString(R.string.library_tab_name_playlists)
            }
        }

        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

    companion object {
        private const val FIRST_POSITION = 0
        private const val SECOND_POSITION = 1
    }
}