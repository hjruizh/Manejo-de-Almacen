package com.henryruiz.manejoalmacenmantis;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import clases.ListaCPAAdapter;
import sincronizacion.Conexion;
import sincronizacion.Variables;
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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Log.i("posicion", "posicion " + position);
                final CPA posActual = NavItms.get(position);
                Variables.setCliPk(String.valueOf(posActual.getCPA_PK()));
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Cantidad de Productos");
                // Set up the input
                final EditText input = new EditText(c);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                // Set up the buttons
                builder.setNeutralButton("Enviar pago por correo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Eliminar pago", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
}
