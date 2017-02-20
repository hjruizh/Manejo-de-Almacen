package com.henryruiz.manejoalmacenmantis;

import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.GCL;


public class RegistrarClienteFragment extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    ArrayList<GCL> NavItms;
    String valor = "";

    public RegistrarClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_registrar_cliente, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);

        Variables.setTituloVentana("ClientesNuevos");
        Button guardar = (Button) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonGuardar);

        final EditText rif = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextRif);
        final EditText nom = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextNombre);
        final EditText tel = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextTel);
        final EditText dir = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextDir);
        final EditText obs = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextObs);
        final EditText mail = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextEmail);
        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!rif.getText().toString().trim().equals(""))
                    new Guardar().execute(rif.getText().toString(),nom.getText().toString(),
                        tel.getText().toString(),dir.getText().toString(),
                        obs.getText().toString(),mail.getText().toString());
                else
                    alerta_cantidad("Ingrese al menos el rif del cliente");
            }
        });

        new Grupo().execute("");

        return rootView;
    }

    private class Grupo extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando grupos de clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                NavItms = s.sincronizar_GRUPO_cli();
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
                        String array_spinner[]=new String[NavItms.size()];
                        for (int i = 0; i<NavItms.size(); i++){
                            array_spinner[i] = NavItms.get(i).getNombre();
                        }
                        final Spinner auditoria = (Spinner) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerProveedor);
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

    private class Guardar extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando grupos de clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                valor = s.guardar_cli_nuevo(params[0],params[1],params[2],params[3],params[4],params[5]);
                if (valor!= "")
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
                if (valor.contains("Datos guardados de manera exitosa")){
                    final EditText rif = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextRif);
                    final EditText nom = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextNombre);
                    final EditText tel = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextTel);
                    final EditText dir = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextDir);
                    final EditText obs = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextObs);
                    final EditText mail = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextEmail);
                    rif.setText("");
                    nom.setText("");
                    tel.setText("");
                    dir.setText("");
                    obs.setText("");
                    mail.setText("");
                    alerta_cantidad(valor);
                }
            }
            else {
                alerta_cantidad(valor);
                //Log.i("error","Sin Grupo");
            }
        }

    }
    //Mensaje de Alerta
    private void alerta_cantidad(String mensaje) {

        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(mensaje);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;

            }
        });
        alertDialog.show();
    }
    //Mensaje de Alerta
}
