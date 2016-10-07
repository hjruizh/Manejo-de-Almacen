package com.henryruiz.manejoalmacenmantis;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.GRU;
import tablas.INV;

public class ConsultaListaPrecios extends Fragment {
    View rootView;
    Context c;
    Conexion s;
    ArrayList<GRU> NavItms = new ArrayList<GRU>();
    ArrayList<INV> invent = new ArrayList<INV>();
    ListView listview;

    public ConsultaListaPrecios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_consulta_lista_precios, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        new Grupo().execute("");

        ImageButton enviar = (ImageButton) rootView.findViewById(R.id.buttonBuscar);
        android.widget.SearchView search = (android.widget.SearchView) rootView.findViewById(R.id.searchView2);

        search.setQueryHint("Buscar Cliente");

        search.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                new BuscarInv().execute(query);
                Toast.makeText(c, query,
                        Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                /*Toast.makeText(c, newText,
                Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });
        return rootView;
    }

    private class Grupo extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando grupos de productos...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                NavItms = s.sincronizar_GRU();
                if (NavItms!= null)
                {
                    return 1;
                }
                else
                    return 0;
            } catch (Exception e) {
                Log.i("error_grupo", "-"+e.getLocalizedMessage());
                e.printStackTrace();
                return 0;
            }
        }

        protected void onProgressUpdate(Float... valores) {
            dialog.dismiss();
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            if (bytes==1) {
                try {
                    if (NavItms!=null)
                    {
                        String array_spinner[]=new String[NavItms.size()+1];
                        for (int i = 0; i<NavItms.size(); i++){
                            array_spinner[i] = NavItms.get(i).getNombre();
                        }
                        final Spinner auditoria = (Spinner) rootView.findViewById(R.id.spinnerGrupo);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, array_spinner);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        auditoria.setAdapter(adapter);
                        auditoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Variables.setGruPK(String.valueOf(NavItms.get(auditoria.getSelectedItemPosition()).getPk()));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub

                            }
                        });
                    }
                } catch (Exception e) {
                    //Log.i("error", e.getMessage());
                }
            }
            else {
                //Log.i("error","Sin Grupo");
            }
        }

    }

    private class BuscarInv extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(ListadoDeInventario.this, "", "Cargando...",
                    true);
        }

        protected Integer doInBackground(String... params) {
            try {

                String nivel = "1";

                publishProgress();
                invent = s.buscar_INV(params[0].trim());

            } catch (Exception e) {

                e.printStackTrace();
                return 0;
            }
            return 1;
        }
}
