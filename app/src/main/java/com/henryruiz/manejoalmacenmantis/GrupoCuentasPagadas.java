package com.henryruiz.manejoalmacenmantis;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import clases.ListaGrupoCliAdapter;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.GCL;


public class GrupoCuentasPagadas extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    ArrayList<GCL> NavItms = new ArrayList<GCL>();
    ListView listview;


    public GrupoCuentasPagadas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_grupo_cuentas_x_cobrar, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        listview = (ListView) rootView.findViewById(R.id.listViewGrupo);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Log.i("posicion", "posicion " + position);
                final GCL posActual = NavItms.get(position);
                Variables.setCliPk(String.valueOf(posActual.getPk()));
                String pk = Variables.getCliPk();
                ListaClientes fragment2 = new ListaClientes();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment2);
                fragmentTransaction.commit();
            }
        });

        new BuscarGrupoCuentasPagadas().execute("");

        return rootView;
    }

    private class BuscarGrupoCuentasPagadas extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Grupos...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                NavItms = s.sincronizar_GRUPO_cpa();
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
            /*if (!verificar_internet()) {
                //dialog.dismiss();
            }*/
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            if (bytes==1) {
                try {
                    if (NavItms!=null)
                    {
                        ListaGrupoCliAdapter adaptadorGrid = new ListaGrupoCliAdapter(c, NavItms);
                        listview.setAdapter(adaptadorGrid);
                    }
                } catch (Exception e) {
                    Log.i("error", e.getMessage());
                }
            }
            else {
                Log.i("error","Sin Grupo");
            }
        }

    }
}
