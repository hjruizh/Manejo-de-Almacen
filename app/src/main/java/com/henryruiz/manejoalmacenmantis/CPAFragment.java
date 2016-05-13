package com.henryruiz.manejoalmacenmantis;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import clases.ListaCPAAdapter;
import sincronizacion.Conexion;
import tablas.CPA;


public class CPAFragment extends Fragment {

    View vista;
    Context c;
    Conexion s;
    ArrayList<CPA> NavItms = new ArrayList<CPA>();
    ListView listview;

    public CPAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_cpa, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        listview = (ListView) vista.findViewById(R.id.listViewCPA);
        new ListaCPA().execute();

        return vista;
    }

    //Lista de Fotos
    private class ListaCPA extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;
        //ArrayList<FOTO> grupo = new ArrayList<FOTO>();
        protected void onPreExecute() {		 //Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Cuentas Pagadas...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                NavItms = s.sincronizar_CPA();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (NavItms!= null)
            {
                return 1;
            }
            else
                return 0;
        }
        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            if (bytes==1)
            {
                if (NavItms!=null)
                {
                    //NavItms = grupo;
                    ListaCPAAdapter adaptadorGrid = new ListaCPAAdapter(c, NavItms);
                    listview.setAdapter(adaptadorGrid);
                }
            }
            else
            {
                Toast.makeText(c, "No Hay Fotos Disponibles", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Lista de Fotos

}
