package com.example.myappejemstic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myappejemstic.R;
import com.example.myappejemstic.adapters.adapterUsuarios;
import com.example.myappejemstic.pojos.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class usuariosFragment extends Fragment {





    public usuariosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        final ProgressBar progressBar;


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        

        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        TextView tv_user = view.findViewById(R.id.tv_user);
        ImageView img_user = view.findViewById(R.id.img_user);

        progressBar = view.findViewById(R.id.progressbarfrag);


        assert user != null;
        tv_user.setText(user.getDisplayName());
        Glide.with(this).load(user.getPhotoUrl()).into(img_user);

        RecyclerView rv;
        ArrayList<Users> usersArrayList;
        adapterUsuarios adapter;
        LinearLayoutManager mLayoutManager;

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(mLayoutManager);
        usersArrayList = new ArrayList<>();
        adapter = new adapterUsuarios(usersArrayList,getContext());
        rv.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        // se cambio de Users a user ____________________ // oto cambio
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    rv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);


                    usersArrayList.removeAll(usersArrayList);
                    for (DataSnapshot snapshots:snapshot.getChildren()){
                        Users user = snapshots.getValue(Users.class);
                        usersArrayList.add(user);
                    }
                    adapter.notifyDataSetChanged();

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "NO existen usuarios", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}