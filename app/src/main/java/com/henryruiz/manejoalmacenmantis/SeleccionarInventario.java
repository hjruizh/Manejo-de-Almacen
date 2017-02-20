package com.henryruiz.manejoalmacenmantis;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.VN1;


public class SeleccionarInventario extends Fragment {

    private int year = 0;
    private int month = 0;
    private int day = 0;
    static ArrayList<VN1> invent = new ArrayList<VN1>();
    Context c;
    Conexion s;
    View rootView;
    static final int DATE_PICKER_ID = 1111;

    public SeleccionarInventario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_seleccionar_inventario, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        Variables.setFac("");
        Variables.setCot("");
        Variables.setPed("");
        Variables.setCom("");
        Variables.setInventario("");
        Variables.setAnio("");
        Variables.setMes("");
        Variables.setAudi("");
        final Spinner auditoria = (Spinner) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerAuditoria);
        final Spinner tipoInv = (Spinner) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerSeleccionarInv);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), com.henryruiz.manejoalmacenmantis.R.array.inventario, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoInv.setAdapter(adapter);

        tipoInv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (tipoInv.getSelectedItem().toString().trim().equals("Mensual")) {
                    auditoria.setVisibility(View.GONE);
                    Variables.setAudi("");
                    createDialogWithoutDateField().show();//onCreateDialog(DATE_PICKER_ID).show();
                }
                else if (tipoInv.getSelectedItem().toString().trim().equals("Diario")) {
                    auditoria.setVisibility(View.GONE);
                    Variables.setAudi("");
                    Variables.setInventario("Diario");
                }
                else{
                    Variables.setInventario("General");
                    auditoria.setVisibility(View.VISIBLE);
                    new Inventario().execute("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        ImageView buscar = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imageViewBuscarD);
        buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Variables.getInventario().equals("General") && !Variables.getAudi().equals("")) {
                    Log.i("Año", "-" + Variables.getAnio());
                    Log.i("Mes", "-" + Variables.getMes());
                    Intent intent = new Intent(getActivity(), ListadoDeInventario.class);
                    startActivity(intent);
                }
                else if (!Variables.getInventario().equals("General")){
                    Log.i("Año", "-" + Variables.getAnio());
                    Log.i("Mes", "-" + Variables.getMes());
                    Intent intent = new Intent(getActivity(), ListadoDeInventario.class);
                    startActivity(intent);
                }
                else{
                    alerta_cantidad("Seleccione un Inventario");
                }
            }
        });
        return  rootView;

    }

    //Mensaje de Alerta
    private void alerta_cantidad(String mensaje) {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(mensaje);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;

            }
        });
        alertDialog.show();
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(getActivity(), pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            Log.i("a�o",year+"");
            Variables.setAnio(String.valueOf(year));
            Variables.setMes(String.valueOf(Variables.agregarCero(month)));
            Variables.setInventario("Mensual");
            // Show selected date
            /*Output.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));*/

        }
    };

    private DatePickerDialog createDialogWithoutDateField(){

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), pickerListener, year, month,day);
        try{
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }

            }
        }catch(Exception ex){
        }
        return dpd;

    }

    private class Inventario extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            //dialog = ProgressDialog.show(MainActivity.this, "", "Cargando...",true);
        }

        protected Integer doInBackground(String... params) {
            try {

                //String nivel = "1";

                //publishProgress();
                invent = s.sincronizar_VN1();

            } catch (Exception e) {

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
            //dialog.dismiss();
            try{
                if ((bytes == 1) && (invent != null)) {
                    Log.i("tama�o", ""+invent.size());
                    String array_spinner[]=new String[invent.size()+1];
                    array_spinner[0] = "Sin Auditoria";
                    for (int i = 0; i<invent.size(); i++){
                        if (invent.get(i).getMensaje().equals("")){
                            array_spinner[i+1] = invent.get(i).getNombre();
                            Log.i("valor", "con"+invent.get(i).getNombre());
                        }
                        else{
                            Log.i("sin valor", "sin"+invent.get(i).getMensaje());
                            array_spinner[i+1] = invent.get(i).getMensaje();
                        }
                    }
                    final Spinner auditoria = (Spinner) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerAuditoria);
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
                Log.i("error",e.getMessage());
            }
        }

    }

}
