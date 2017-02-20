package com.henryruiz.manejoalmacenmantis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Spinner;

import java.util.ArrayList;

import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.USR;
import tablas.VN1;


public class IniciarSesion extends Fragment {

    View vista;
    Context c;
    static ArrayList<VN1> invent = new ArrayList<VN1>();
    static ArrayList<USR> usuario = new ArrayList<USR>();
    Conexion s;
    Variables user = new Variables();

    public IniciarSesion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_iniciar_sesion, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        final Button iniciar = (Button) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonGuardar);
        final Spinner usuarios = (Spinner) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerUsuario);
        iniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Variables.setUser(usuarios.getSelectedItem().toString().trim());
                //new BuscarUsuario().execute("");
                for (int i = 0; i<usuario.size(); i++){
                    Log.i("revision","6");
                    if (Variables.getUser().equals(usuario.get(i).getAlias())){
                        Log.i("revision", "7");
                        user.guardarUsuario(c,usuario.get(i).getPk(), usuario.get(i).getAlias());
                        Variables.setPk(String.valueOf(usuario.get(i).getPk()));
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        new BuscarUsuario().execute("");
        return vista;
    }

    private class BuscarUsuario extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(c, "", "Cargando...",
                    true);
        }

        protected Integer doInBackground(String... params) {
            try {
                //publishProgress();
                Log.i("revision","1");
                usuario = s.sincronizar_User();
                Log.i("revision","2");
            }
            catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }




        protected void onProgressUpdate(Float... valores) {
            /*if (!verificar_internet()) {
                dialog.dismiss();
            }*/
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            try{
                if (usuario != null) {
                    Log.i("tamaño", ""+usuario.size());
                    String array_spinner[]=new String[usuario.size()+1];
                    Log.i("revision","3");
                    array_spinner[0] = "Seleccione Usuario";
                    Log.i("revision","4");
                    Log.i("revision","5");
                    for (int i = 0; i<usuario.size(); i++){
                        Log.i("revision","6");
                        if (Variables.getUser().equals(usuario.get(i).getAlias())){
                            Log.i("revision", "7");
                            //user.guardarUsuario(usuario.get(i).getPk(), usuario.get(i).getAlias());
                            Variables.setPk(String.valueOf(usuario.get(i).getPk()));
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                        array_spinner[i+1] = usuario.get(i).getAlias();
                        Log.i("valor", "con"+usuario.get(i).getAlias());
                    }
                    final Spinner auditoria = (Spinner) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerUsuario);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, array_spinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    auditoria.setAdapter(adapter);
                    auditoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                            Variables.setAudi(auditoria.getSelectedItem().toString().trim());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }catch (Exception e) {
                //Log.i("error",e.getMessage());
            }
        }

    }

}
