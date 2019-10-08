package com.menard.go4lunch.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.menard.go4lunch.R;
import com.menard.go4lunch.adapter.WorkmatesAdapter;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.model.User;

public class WorkmatesFragment extends BaseFragment{

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workmates_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list_workmates);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));

        Query query = UserHelper.getAllUser();
        FirestoreRecyclerOptions<User> list = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class)
                .setLifecycleOwner(this).build();

        //-- Layout manager --
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        WorkmatesAdapter workmatesAdapter = new  WorkmatesAdapter(requireActivity(),list, true);
        recyclerView.setAdapter(workmatesAdapter);

        return view;
    }

}