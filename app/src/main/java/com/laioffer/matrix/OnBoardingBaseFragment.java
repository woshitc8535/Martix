package com.laioffer.matrix;


import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class OnBoardingBaseFragment extends Fragment {
    protected EditText usernameEditText;
    protected EditText passwordEditText;
    protected Button submitButton;
    protected DatabaseReference database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayout(), container, false);
        usernameEditText = (EditText) view.findViewById(R.id.editTextLogin);
        passwordEditText = (EditText) view.findViewById(R.id.editTextPassword);
        submitButton = (Button) view.findViewById(R.id.submit);

        database = FirebaseDatabase.getInstance().getReference();
        return view;

    }

    @LayoutRes
    protected abstract int getLayout();
}