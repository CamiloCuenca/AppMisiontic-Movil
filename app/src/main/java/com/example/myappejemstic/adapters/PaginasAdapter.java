package com.example.myappejemstic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myappejemstic.fragments.chatsFragment;
import com.example.myappejemstic.fragments.mis_solicitudesFragment;
import com.example.myappejemstic.fragments.solisitudesFragment;
import com.example.myappejemstic.fragments.usuariosFragment;

public class PaginasAdapter extends FragmentStateAdapter {


    public PaginasAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0 :
                return new usuariosFragment();

            case 1:
                return new chatsFragment();

            case 2:
                return new solisitudesFragment();

            case 3:
                return new mis_solicitudesFragment();

            default:
                return new usuariosFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
