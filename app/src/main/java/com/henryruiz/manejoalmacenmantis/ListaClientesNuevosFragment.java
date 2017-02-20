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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import clases.ListaClienteAdapter;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.CLI;


public class ListaClientesNuevosFragment extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    ArrayList<CLI> NavItms = new ArrayList<CLI>();
    ListView listview;

    public ListaClientesNuevosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_lista_clientes_nuevos, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        Variables.setTituloVentana("ListaClientesNuevos");
        listview = (ListView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewGrupo);
        Variables.setEmailCliN("");
        new Clientes().execute("");

        ImageButton nuevo = (ImageButton) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonNuevo);
        nuevo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RegistrarClienteFragment fragment2 = new RegistrarClienteFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                fragmentTransaction.commit();
            }
        });

        SearchView search = (SearchView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.searchView);

        search.setQueryHint("Buscar Cliente");

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                new Clientes().execute(query);
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

        listview = (ListView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewGrupo);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Log.i("posicion", "posicion " + position);
                final CLI posActual = NavItms.get(position);
                Variables.setCliPk(String.valueOf(posActual.getPk()));
                Variables.setEmailCliN(posActual.getEmail());
                String pk = Variables.getCliPk();
                ConsultaListaPrecios fragment2 = new ConsultaListaPrecios();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    private class Clientes extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = "0";
                if (params[0].equals(""))
                    NavItms = s.sincronizar_cli_nuevos(pk);
                else
                    NavItms = s.sincronizar_cli_nuevos(pk, params[0]);
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
                        ListaClienteAdapter adaptadorGrid = new ListaClienteAdapter(c, NavItms);
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
