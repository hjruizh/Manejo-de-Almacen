package com.henryruiz.manejoalmacenmantis;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        vista = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_cpa, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        listview = (ListView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewCPA);
        new ListaCPA().execute();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Log.i("posicion", "posicion " + position);
                final CPA posActual = NavItms.get(position);
                final CharSequence colors[] = new CharSequence[] {"Enviar por correo", "Eliminar cuenta pagada"};

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c);
                builder.setTitle("Cuenta pagada del dia " + posActual.getCPA_FECHA());
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //colors[which];
                        switch (which) {
                            case 0:
                                new enviarCPA().execute(posActual.getCPA_PK());
                                break;
                            case 1:
                                new eliminarCPA().execute(posActual.getCPA_PK());
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

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
    private class enviarCPA extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;
        String mensaje = "";
        //ArrayList<FOTO> grupo = new ArrayList<FOTO>();
        protected void onPreExecute() {		 //Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Reenviando Cuentas Pagadas...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                mensaje = s.enviar_CPA(params[0]);
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
                Log.i("enviar correo","-"+mensaje);
                Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(c, "Problemas para enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class eliminarCPA extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;
        String mensaje = "";
        //ArrayList<FOTO> grupo = new ArrayList<FOTO>();
        protected void onPreExecute() {		 //Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Eliminado Cuentas Pagadas...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                mensaje = s.eliminar_CPA(params[0]);
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
                Log.i("enviar correo","-"+mensaje);
                Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();
                if (NavItms!=null)
                {
                    //NavItms = grupo;
                    ListaCPAAdapter adaptadorGrid = new ListaCPAAdapter(c, NavItms);
                    listview.setAdapter(adaptadorGrid);
                }
            }
            else
            {
                Toast.makeText(c, "Problemas para enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
