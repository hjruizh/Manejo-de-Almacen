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
import android.widget.RadioButton;
import android.widget.Toast;

import sincronizacion.Conexion;
import sincronizacion.Variables;

public class BuscarProductosPCF extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    static String nuemro = "";

    public BuscarProductosPCF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         // Inflate the layout for this fragment
         rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_buscar_productos_pcf, container, false);
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

         ImageView buscar = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imageViewBuscar);
         buscar.setOnClickListener(new View.OnClickListener() {
             public void onClick(View view) {
                 Intent intent = new Intent(getActivity(), ListadoDeInventario.class);
                 startActivity(intent);
             }
         });

         final RadioButton ped = (RadioButton) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.radioButtonPed);
         final RadioButton cot = (RadioButton) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.radioButtonCot);
         final RadioButton fac = (RadioButton) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.radioButtonFac);
         final RadioButton tra = (RadioButton) rootView.findViewById(R.id.radioButtonTra);
         //Buscar Ultimo Correlativo
         View.OnClickListener list = new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 switch(view.getId()) {
                     case com.henryruiz.manejoalmacenmantis.R.id.radioButtonPed:
                         new Correlativo().execute("V06","");
                         break;
                     case com.henryruiz.manejoalmacenmantis.R.id.radioButtonCot:
                         new Correlativo().execute("V02","");
                         break;
                     case com.henryruiz.manejoalmacenmantis.R.id.radioButtonFac:
                         new Correlativo().execute("VEN","");
                         break;
                     case com.henryruiz.manejoalmacenmantis.R.id.radioButtonTra:
                         new Correlativo().execute("I09","");
                         break;
                 }
             }
         };
         ped.setOnClickListener(list);
         cot.setOnClickListener(list);
         fac.setOnClickListener(list);
         tra.setOnClickListener(list);
         //Buscar Ultimo Correlativo
         //Edicion de correlativo
         final EditText num = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextNumero);
         num.addTextChangedListener(new TextWatcher() {

             public void afterTextChanged(Editable s) {
                 if (ped.isChecked()) {
                     Variables.setPed(num.getText().toString());
                     Variables.setCot("");
                     Variables.setFac("");
                     Variables.setTra("");
                 }
                 if (cot.isChecked()) {
                     Variables.setCot(num.getText().toString());
                     Variables.setFac("");
                     Variables.setPed("");
                     Variables.setTra("");
                 }
                 if (fac.isChecked()) {
                     Variables.setFac(num.getText().toString());
                     Variables.setPed("");
                     Variables.setCot("");
                     Variables.setTra("");
                 }
                 if (tra.isChecked()) {
                     Variables.setTra(num.getText().toString());
                     Variables.setPed("");
                     Variables.setCot("");
                     Variables.setFac("");
                 }
             }

             public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

             public void onTextChanged(CharSequence s, int start, int before, int count) {}
         });
         //Edicion de correlativo
         return rootView;
    }

    private class Correlativo extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(c, "", "Cargando...",true);
        }

        protected Integer doInBackground(String... params) {
            try {
                nuemro = s.buscarUltimoCorrelativo(params[0]);
                if (params[0].equals("V06")) {
                    Variables.setPed(nuemro);
                    Variables.setCot("");
                    Variables.setFac("");
                }
                if (params[0].equals("V02")) {
                    Variables.setCot(nuemro);
                    Variables.setFac("");
                    Variables.setPed("");
                }
                if (params[0].equals("VEN")) {
                    Variables.setFac(nuemro);
                    Variables.setPed("");
                    Variables.setCot("");
                }
                if (params[0].equals("I09")) {
                    Variables.setFac(nuemro);
                    Variables.setPed("");
                    Variables.setCot("");
                }
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
                    EditText numero1 = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextNumero);
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
