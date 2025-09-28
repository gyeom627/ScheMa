package com.schema.app.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.schema.app.databinding.ActivityPastEventsBinding;
import com.schema.app.ui.adapter.EventListAdapter;
import com.schema.app.ui.viewmodel.PastEventsViewModel;

public class PastEventsActivity extends AppCompatActivity {

    private ActivityPastEventsBinding binding;
    private PastEventsViewModel viewModel;
    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPastEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PastEventsViewModel.class);

        setupRecyclerView();

        viewModel.getPastEvents().observe(this, events -> {
            adapter.submitList(events);
        });
    }

    private void setupRecyclerView() {
        adapter = new EventListAdapter(new EventListAdapter.EventDiff(), event -> {
            // Past events are not clickable for detail view in this implementation
        });
        binding.recyclerviewPastEvents.setAdapter(adapter);
        binding.recyclerviewPastEvents.setLayoutManager(new LinearLayoutManager(this));
    }
}
