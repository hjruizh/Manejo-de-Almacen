package com.henryruiz.manejoalmacenmantis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import clases.Datasource;
import clases.Fecha;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.INV;
import tablas.USR;


public class ListadoDeInventario extends AbstractListViewActivity  implements View.OnClickListener{

    ArrayList<INV> invent = new ArrayList<INV>();
    static ArrayList<USR> usuario = new ArrayList<USR>();
    String user = "";
    AlertDialog alert;
    Conexion s = new Conexion(this);
    Context c = this;
    Boolean mensaje = false;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.henryruiz.manejoalmacenmantis.R.layout.activity_listado_de_inventario);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.listView = (ListView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.listView);

        ImageButton boton_comprar = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonBuscar);
        final EditText buscar = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextBuscar);

        boton_comprar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new BuscarInv().execute(buscar.getText().toString());
            }
        });
        ImageButton boton_qr = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonScan);

        boton_qr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(ListadoDeInventario.this);
                integrator.addExtra("SCAN_WIDTH", 680);
                integrator.addExtra("SCAN_HEIGHT", 480);
                integrator.addExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
                //customize the prompt message before scanning
                integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
                //integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            }
        });

        TextView t = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.txtHeader);
        if (!Variables.getInventario().equals("")) {
            t.setText("BD: " + Variables.getBd() + " Auditoria: " + Variables.getInventario());
            Log.i("Inventario General","-" + Variables.getInventario());
            new BuscarInv().execute("");
        }
        else if (!Variables.getPed().equals("") || !Variables.getCot().equals("")
                || !Variables.getFac().equals("") || !Variables.getCom().equals("")
                || !Variables.getTra().equals("")){
            if (!Variables.getPed().equals("")) {
                t.setText("BD: " + Variables.getBd() + " Ped: " + Variables.getPed());
                new BuscarInv().execute("");
            }
            if (!Variables.getCot().equals("")) {
                t.setText("BD: " + Variables.getBd() + " Cot: " + Variables.getCot());
                new BuscarInv().execute("");
            }
            if (!Variables.getFac().equals("")) {
                t.setText("BD: " + Variables.getBd() + " Fac: " + Variables.getFac());
                new BuscarInv().execute("");
            }
            if (!Variables.getCom().equals("")) {
                t.setText("BD: " + Variables.getBd() + " Com: " + Variables.getCom());
                new BuscarInv().execute("");
            }
            if (!Variables.getTra().equals("")) {
                t.setText("BD: " + Variables.getBd() + " Tra: " + Variables.getTra());
                new BuscarInv().execute("");
            }
        }
        else {
            t.setText("BD: " + Variables.getBd());
        }
        ImageView back = (ImageView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.imageViewBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
        String codigo = settings.getString("codigo", null);
        if(codigo!=null){
            //mensaje = true;
            new BuscarInv().execute(codigo);
            Log.i("codigo", codigo);
        }
        /*ImageButton scan = (ImageButton) findViewById(R.id.buttonScan);
        scan.setOnClickListener(this);*/
        /*Busqueda por voz*/
        ImageButton mic = (ImageButton)findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonVoz);
        PackageManager pm = getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            mic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startVoiceRecognitionActivity();
                }
            });
        }
        /*Busqueda por voz*/
    }
    //Metodo de Busqueda
    private void startVoiceRecognitionActivity() {        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    //Metodo de Busqueda

    public AlertDialog createSimpleDialog(String title, String alert) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final String[] select = {""};
        View layout = inflater.inflate(R.layout.fragment_iniciar_sesion, null);
        builder.setView(layout);
        final Button iniciar = (Button) layout.findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonGuardar);
        final Spinner usuarios = (Spinner) layout.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerUsuario);
        Log.i("tamaño", ""+usuario.size());
        String array_spinner[]=new String[usuario.size()+1];
        array_spinner[0] = "Seleccione Usuario";
        for (int i = 0; i<usuario.size(); i++){
            array_spinner[i+1] = usuario.get(i).getAlias();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,android.R.layout.simple_spinner_item, array_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usuarios.setAdapter(adapter);
        usuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                select[0] = usuarios.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        iniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!select[0].equals("Seleccione Usuario")) {
                        new EnviarUserTraslado().execute(select[0]);
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


            final CharSequence[] items = new CharSequence[matches.size()];

            for(int i = 0; i < matches.size(); i++){

                items[i] = matches.get(i);

            }


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    EditText buscar = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextBuscar);
                    buscar.setText(items[item].toString());
                }
            });

            AlertDialog alert = builder.create();

            alert.show();
            super.onActivityResult(requestCode, resultCode, intent);
        }
        else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (result != null) {
                String contents = result.getContents();
                if (contents != null) {
                    EditText numero = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextBuscar);
                    numero.setText(contents);
                } else {
                }
            }
        }
    }

    @Override
    public void onBackPressed() { finish(); }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.henryruiz.manejoalmacenmantis.R.menu.menu_listado_de_inventario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.henryruiz.manejoalmacenmantis.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    private class BuscarInv extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(ListadoDeInventario.this, "", "Cargando...",
                    true);
        }

        protected Integer doInBackground(String... params) {
                try {

                    String nivel = "1";

                    publishProgress();
                    if (!params[0].equals("")) {
                        if (!Variables.getFac().equals("")) {
                            invent = s.sincronizar_INV_pedido(Variables.getFac(),"fac",params[0]);
                        }
                        else if (!Variables.getPed().equals("")) {
                            invent = s.sincronizar_INV_pedido(Variables.getPed(),"ped",params[0]);
                        }
                        else if (!Variables.getCot().equals("")) {
                            invent = s.sincronizar_INV_pedido(Variables.getCot(), "cot",params[0]);
                        }
                        else if (!Variables.getCom().equals("")) {
						    invent = s.sincronizar_INV_comp(Variables.getCom(), params[0].trim());
                        }
                        else if (!Variables.getTra().equals("")) {
                            user = s.get_user_traslado(Variables.getTra());
                            if (user.trim().length() > 2)
                                invent = s.sincronizar_INV_tarslado(Variables.getTra());
                            else {
                                usuario = s.sincronizar_User();
                            }
                        }
                        else if (!Variables.getInventario().equals("") && Variables.getAudi().equals("")){
                            String fechaI = "";
                            String fechaF = "";
                            if(!Variables.getAnio().equals("") && !Variables.getMes().equals("")) {
                                String mes = Variables.agregarCero(Integer.parseInt(Variables.getMes()) + 1);
                                fechaI = String.valueOf(Variables.getAnio().toString()) + mes + "01";
                                fechaF = String.valueOf(Variables.getAnio().toString()) + mes + Fecha.diasDelMes(Integer.parseInt(Variables.getMes()), Variables.getAnio().toString());
                            }
                            Log.i("Inventario","Fecha i " + fechaI + "Fecha F" + fechaF);
                            invent = s.sincronizar_INV_CDI(fechaI,fechaF,params[0].trim());
                        }
                        else if (!Variables.getInventario().equals("")){
                            invent = s.sincronizar_INV_CDI(null,Variables.getAudi(),params[0].trim());
                        }
                        else {
                            invent = s.buscar_INV(params[0].trim());
                            if (mensaje){
                                s.sincronizar_Mensaje_guardar("1",Variables.getMensaje_pk());
                            }
                        }
                    }
                    else{
                        if (!Variables.getFac().equals("")) {
                            invent = s.sincronizar_INV_pedido(Variables.getFac(),"fac","");
                        }
                        else if (!Variables.getPed().equals("")) {
                            invent = s.sincronizar_INV_pedido(Variables.getPed(),"ped","");
                        }
                        else if (!Variables.getCot().equals("")) {
                            invent = s.sincronizar_INV_pedido(Variables.getCot(),"num","");
                            Log.i("coti_numero","coti_numero");
                        }
                        else if (!Variables.getCom().equals("")) {
                            invent = s.sincronizar_INV_comp(Variables.getCom(),"");
                        }
                        else if (!Variables.getTra().equals("")) {
                            user = s.get_user_traslado(Variables.getTra());
                            if (user.trim().length() > 2)
                                invent = s.sincronizar_INV_tarslado(Variables.getTra());
                            else {
                                usuario = s.sincronizar_User();
                            }
                        }
                        else if (!Variables.getInventario().equals("") && Variables.getAudi().equals("")){
                            String fechaI = "";
                            String fechaF = "";
                            if(!Variables.getAnio().equals("") && !Variables.getMes().equals("")) {
                                String mes = Variables.agregarCero(Integer.parseInt(Variables.getMes()) + 1);
                                fechaI = String.valueOf(Variables.getAnio().toString()) + mes + "01";
                                fechaF = String.valueOf(Variables.getAnio().toString()) + mes + Fecha.diasDelMes(Integer.parseInt(Variables.getMes()), Variables.getAnio().toString());
                            }
                            Log.i("Inventario","Fecha i " + fechaI + "Fecha F" + fechaF);
                            invent = s.sincronizar_INV_CDI(fechaI,fechaF,"");
                        }
                        else if (!Variables.getInventario().equals("") && !Variables.getAudi().equals("")){
                            //invent = s.sincronizar_INV_CDI(null,Variables.getAudi(),"");
                        }
                    }

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
            dialog.dismiss();
            if ((bytes == 1) && (invent != null)) {
                if (user.trim().length() > 2 || Variables.getTra().equals("")) {
                    datasource = Datasource.getInstance(invent);
                    footerView = ((LayoutInflater) c
                            .getSystemService(LAYOUT_INFLATER_SERVICE))
                            .inflate(com.henryruiz.manejoalmacenmantis.R.layout.footer, null, false);
                    listView.addFooterView(footerView, null, false);
                    asignar_adaptador(new CustomListAdapter(c, datasource.getData(
                            0, PAGESIZE)));
                    listView.removeFooterView(footerView);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView adapter, View view,
                                                int position, long arg) {
                            onListItemClick(position, "");

                        }
                    });

                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView arg0, int arg1) {
                            // nothing here
                        }

                        @Override
                        public void onScroll(AbsListView view,
                                             int firstVisibleItem, int visibleItemCount,
                                             int totalItemCount) {
                            if (load(firstVisibleItem, visibleItemCount,
                                    totalItemCount)) {
                                loading = true;
                                listView.addFooterView(footerView, null, false);
                                (new LoadNextPage()).execute("");
                            }
                        }
                    });

                    updateDisplayingTextView();
                }
                else {
                    alert = createSimpleDialog("", "");
                    alert.show();
                }
            }
        }

    }

    private class EnviarUserTraslado extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(c, "", "Cargando...",
                    true);
        }

        protected Integer doInBackground(String... params) {
            try {
                if (!params[0].equals("")) {
                    s.set_user_traslado(Variables.getTra(),params[0]);
                }
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
                alert.cancel();
                new BuscarInv().execute("");
            }catch (Exception e) {
                //Log.i("error",e.getMessage());
            }
        }

    }
}
