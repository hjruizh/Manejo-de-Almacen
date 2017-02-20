package com.henryruiz.manejoalmacenmantis;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sincronizacion.Variables;
import tablas.USR;


public class Principal extends Fragment {

    public Principal() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_principal, container, false);
        TextView bienvenida = (TextView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.textView11);
        bienvenida.setText("Bienvenido " + Variables.getUser());
        return vista;
    }

}
