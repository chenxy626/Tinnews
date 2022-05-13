package com.example.tinnews.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tinnews.databinding.FragmentSearchBinding;
import com.example.tinnews.repository.NewsRepository;
import com.example.tinnews.repository.NewsViewModelFactory;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private FragmentSearchBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_search, container, false);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchNewsAdapter newsAdapter = new SearchNewsAdapter();
        newsAdapter.setItemCallback(article -> {
            SearchFragmentDirections.ActionNavigationSearchToNavigationDetails direction = SearchFragmentDirections.actionNavigationSearchToNavigationDetails(article);
            NavHostFragment.findNavController(SearchFragment.this).navigate(direction);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.newsResultsRecyclerView.setLayoutManager(gridLayoutManager);
        binding.newsResultsRecyclerView.setAdapter(newsAdapter);


        binding.newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                          @Override
                                                          public boolean onQueryTextSubmit(String query) {
                                                              if (!query.isEmpty()) {
                                                                  viewModel.setSearchInput(query);
                                                              }
                                                              binding.newsSearchView.clearFocus();
                                                              return true;
                                                          }

                                                          @Override
                                                          public boolean onQueryTextChange(String newText) {
                                                              return false;
                                                          }
                                                      });

            NewsRepository repository = new NewsRepository();
        // ViewModelProvider保证了在rotate的时候保留当前viewModel的，如果之前的存在则不会创建新的
            viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(SearchViewModel.class);
            //viewModel.setSearchInput("Covid-19");
            viewModel.searchNews().observe(getViewLifecycleOwner(), newsResponse -> {
                if (newsResponse != null) {
                    newsAdapter.setArticles(newsResponse.articles);
                    Log.d("SearchFragment", newsResponse.toString());
                }
            });



    }

}