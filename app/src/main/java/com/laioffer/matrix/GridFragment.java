package com.laioffer.matrix;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.laioffer.matrix.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class GridFragment extends Fragment {
    private GridView gridView;
    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        gridView = (GridView) view.findViewById(R.id.view_grid);
        gridView.setAdapter(new EventAdapter(getActivity()));
        return view;

    }

    public void onItemSelected(int position){
        for (int i = 0; i < gridView.getChildCount(); i++){
            if (position == i) {
                gridView.getChildAt(i).setBackgroundColor(Color.BLUE);
            } else {
                gridView.getChildAt(i).setBackgroundColor(Color.parseColor("#FAFAFA"));
            }
        }
    }


}
