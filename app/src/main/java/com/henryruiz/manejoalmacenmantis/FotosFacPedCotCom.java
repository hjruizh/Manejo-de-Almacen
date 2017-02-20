package com.henryruiz.manejoalmacenmantis;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import sincronizacion.Conexion;
import sincronizacion.Variables;

public class FotosFacPedCotCom extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    String nuemro = "";
    String tipo = "";
    public FotosFacPedCotCom() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_fotos_fac_ped_cot_com, container, false);
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
        final Spinner tipoInv = (Spinner) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerSeleccionar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), com.henryruiz.manejoalmacenmantis.R.array.tipoFoto, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoInv.setAdapter(adapter);
        tipoInv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (tipoInv.getSelectedItem().toString().trim().equals("Pedido")) {
                    tipo = "ped";
                    new Correlativo().execute("V06","");
                }
                else if (tipoInv.getSelectedItem().toString().trim().equals("Factura")) {
                    tipo = "fac";
                    new Correlativo().execute("VEN","");
                }
                else if (tipoInv.getSelectedItem().toString().trim().equals("Compra")) {
                    tipo = "com";
                    new Correlativo().execute("COM","");
                }
                else  {//if (tipoInv.getSelectedItem().toString().trim().equals("Cotización"))
                    tipo = "cot";
                    Log.i("lista",tipoInv.getSelectedItem().toString().trim());
                    new Correlativo().execute("V02","");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        //Edicion de correlativo
        final EditText num = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCompra);
        num.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (tipo.equals("ped")) {
                    Variables.setPed(num.getText().toString());
                    Variables.setCot("");
                    Variables.setFac("");
                    Variables.setCom("");
                }
                if (tipo.equals("cot")) {
                    Variables.setCot(num.getText().toString());
                    Variables.setFac("");
                    Variables.setPed("");
                    Variables.setCom("");
                }
                if (tipo.equals("fac")) {
                    Variables.setFac(num.getText().toString());
                    Variables.setPed("");
                    Variables.setCot("");
                    Variables.setCom("");
                }
                if (tipo.equals("com")) {
                    Variables.setCom(num.getText().toString());
                    Variables.setPed("");
                    Variables.setCot("");
                    Variables.setFac("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        //Edicion de correlativo
        ImageView buscar = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imageViewBuscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CargarFoto.class);
                startActivity(intent);
            }
        });
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
                if (params[0].equals("COM")) {
                    Variables.setCom(nuemro);
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
                    EditText numero1 = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCompra);
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
