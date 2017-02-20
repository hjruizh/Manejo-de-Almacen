package com.henryruiz.manejoalmacenmantis;

//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import sincronizacion.Conexion;
import sincronizacion.Variables;


public class BuscarProductosCompras extends Fragment {

    View vista;
    Context c;
    Conexion s;
    static String nuemro = "";
    public BuscarProductosCompras() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_buscar_productos_compras, container, false);
        Variables.setFac("");
        Variables.setCot("");
        Variables.setPed("");
        Variables.setCom("");
        Variables.setAnio("");
        Variables.setMes("");
        Variables.setInventario("");
        Variables.setAudi("");
        c = (Context)getActivity();
        s = new Conexion(c);
        //Edicion de correlativo
        final EditText num = (EditText) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCompra);
        num.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Variables.setCom(num.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        //Edicion de correlativo
        new Correlativo().execute("COM","COMPRA");
        ImageView buscar = (ImageView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imageViewBuscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListadoDeInventario.class);
                startActivity(intent);
            }
        });
        return vista;
    }

    private class Correlativo extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(c, "", "Cargando...",true);
        }

        protected Integer doInBackground(String... params) {
            try {
                nuemro = s.buscarUltimoCorrelativo(params[0]);
            } catch (Exception e) {

                e.printStackTrace();
                return 0;
            }
            return 1;
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
                    EditText numero1 = (EditText) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCompra);
                    numero1.setText(nuemro.trim());
                } catch (Exception e) {
                    Log.i("error", e.getMessage());
                }
            }
            else {
                Toast.makeText(c, "No se ha podido conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
