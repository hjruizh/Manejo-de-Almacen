package com.henryruiz.manejoalmacenmantis;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import clases.Envio;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.MED;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrearDetalleDVIFragment extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    private int year = 0;
    private int month = 0;
    private int day = 0;
    static final int DATE_PICKER_ID = 1111;
    EditText fecha;
    String id;
    String idPro;
    String msg;
    static ArrayList<MED> NavItms = new ArrayList<MED>();

    private static String CARPETA = "/sdcard/DCIM/Camera/";
    private String archivo = "foto.jpg";
    private String dir = "";
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String name = "";
    Envio f;
    String foto = "";

    @SuppressLint("ValidFragment")
    public CrearDetalleDVIFragment(String pk, String proPk) {
        // Required empty public constructor
        id = pk;
        idPro = proPk;
    }

    public CrearDetalleDVIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_crear_detalle_dvi, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        f = new Envio(c);
        Variables.setTituloVentana("CrearDetalleDVI");
        fecha = (EditText) rootView.findViewById(R.id.editTextFecha);
        Calendar miCalendario = Calendar.getInstance();
        day = miCalendario.get(Calendar.DAY_OF_MONTH);
        month = miCalendario.get(Calendar.MONTH);
        year = miCalendario.get(Calendar.YEAR);
        fecha.setText(Variables.agregarCero(day) + "/" + Variables.agregarCero((month + 1)) + "/" + String.valueOf(year));
        fecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                createDialogWithoutDateField().show();
            }
        });
        Button save = (Button) rootView.findViewById(R.id.buttonGuardar);
        final EditText monto = (EditText) rootView.findViewById(R.id.editTextMonto);
        final EditText ref = (EditText) rootView.findViewById(R.id.editTextReferencia);
        final EditText obs = (EditText) rootView.findViewById(R.id.editTextDescrip);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fecha.getText().toString().equals("") && !monto.getText().toString().equals("") &&
                        !ref.getText().toString().equals("") && !obs.getText().toString().equals("")){
                    new SetDVI().execute(monto.getText().toString(),ref.getText().toString(),obs.getText().toString(),fecha.getText().toString());
                    try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    if (!Variables.getIdDetalleDVI().equals("")) {
                        name = CARPETA + archivo;
                        /**
                         * Para todos los casos es necesario un intent, si accesamos la c?mara con la acci?n
                         * ACTION_IMAGE_CAPTURE, si accesamos la galer?a con la acci?n ACTION_PICK.
                         * En el caso de la vista previa (thumbnail) no se necesita m?s que el intent,
                         * el c?digo e iniciar la actividad. Por eso inicializamos las variables intent y
                         * code con los valores necesarios para el caso del thumbnail, as? si ese es el
                         * bot?n seleccionado no validamos nada en un if.
                         */
                        final Intent[] intent = {new Intent(MediaStore.ACTION_IMAGE_CAPTURE)};
                        final int[] code = {TAKE_PICTURE};
                        /**
                         * Obtenemos los botones de imagen completa y de galer?a para revisar su estatus
                         * m?s adelante
                         */
                        final CharSequence colors[] = new CharSequence[]{"Tomar Foto", "Seleccionar Foto de Galeria"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(c);
                        builder.setTitle("Tomar Foto");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //colors[which];
                                switch (which) {
                                    case 0:
                                        Uri output = Uri.fromFile(new File(name));
                                        intent[0].putExtra(MediaStore.EXTRA_OUTPUT, output);
                                        /**
                                         * Si la opci?n seleccionada es ir a la galer?a, el intent es diferente y el c?digo
                                         * tambi?n, en la consecuencia de que est? chequeado el bot?n de la galer?a se hacen
                                         * esas asignaciones
                                         */
                                        break;
                                    case 1:
                                        intent[0] = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                        code[0] = SELECT_PICTURE;
                                        break;
                                }
                                startActivityForResult(intent[0], code[0]);
                            }
                        });
                        builder.show();
                    }
                    /**
                     * Luego, con todo preparado iniciamos la actividad correspondiente.
                     */
                }
                else{
                    alerta_cantidad("Seleccione un Inventario");
                }
            }
        });

        if (!Variables.getIdDetalleDVI().equals("")) {
            new GetDVI().execute();
        }
        Button volver = (Button) rootView.findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetallePagoDVIFragment fragment1 = new DetallePagoDVIFragment(id, idPro);
                FragmentManager fragmentManager1 = getFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                fragmentTransaction1.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment1);
                fragmentTransaction1.commit();
            }
        });
        return rootView;
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
            fecha.setText(Variables.agregarCero(day) + "/" + Variables.agregarCero((month + 1)) + "/" + String.valueOf(year));
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

    private class SetDVI extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                if (!params[0].equals("") && !params[1].equals("") && !params[2].equals("") && !params[3].equals(""))
                    msg = s.save_DetalleDVI(id,idPro,params[0],params[1],params[2],params[3]);
                else {
                    //NavItms = s.sincronizar_cli(params[0]);
                }
                if (msg!= null)
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
                    if (msg!=null)
                    {
                        alerta_cantidad(msg);
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

    //Cargar Foto
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * Se revisa si la imagen viene de la c?mara (TAKE_PICTURE) o de la galer?a (SELECT_PICTURE)
         */
        //name = Environment.getExternalStorageDirectory() + archivo;
        //Log.i("Seleccionado",""+requestCode+TAKE_PICTURE);
        if (requestCode == TAKE_PICTURE) {
            /**
             * Si se reciben datos en el intent tenemos una vista previa (thumbnail)
             */
            if (data != null) {
                /**
                 * En el caso de una vista previa, obtenemos el extra ?data? del intent y
                 * lo mostramos en el ImageView
                 */
                Log.i("Seleccionado", "" + requestCode + TAKE_PICTURE);
                if (data.hasExtra("data")) {
                    ImageView iv = (ImageView)rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                    iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
                    //new Foto().execute("");
                }
                /**
                 * De lo contrario es una imagen completa
                 */
            } else {
                /**
                 * A partir del nombre del archivo ya definido lo buscamos y creamos el bitmap
                 * para el ImageView
                 */
                Log.i("Seleccionado",""+requestCode+TAKE_PICTURE);
                ImageView iv = (ImageView)rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                iv.setImageBitmap(BitmapFactory.decodeFile(name));
                /**
                 * Para guardar la imagen en la galer?a, utilizamos una conexi?n a un MediaScanner
                 */
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    private MediaScannerConnection msc = null; {
                        msc = new MediaScannerConnection(c, this); msc.connect();
                    }
                    public void onMediaScannerConnected() {
                        msc.scanFile(name, null);
                    }
                    public void onScanCompleted(String path, Uri uri) {
                        msc.disconnect();
                    }
                };
                //new Foto().execute("");
            }
            /**
             * Recibimos el URI de la imagen y construimos un Bitmap a partir de un stream de Bytes
             */
        } else if (requestCode == SELECT_PICTURE){
            // OI FILE Manager
            if (data!=null) {
                Uri selectedImage = data.getData();
                String filemanagerstring = selectedImage.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImage);
                Log.i("Imagen", selectedImagePath);
                dir = selectedImagePath;
                InputStream is;
                try {
                    is = c.getContentResolver().openInputStream(selectedImage);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    ImageView iv = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                    iv.setImageBitmap(bitmap);
                    //new Foto().execute("");
                } catch (FileNotFoundException e) {
                }
            }
        }
        if (resultCode == -1)
        {
            new Foto().execute("");
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = c.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private class Foto extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;
        String mensaje;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Cargando foto...", true);
        }

        protected Integer doInBackground(String... urls) {
            foto = f.doFileUploadDetalleDVI(Variables.getIdDetalleDVI(), archivo, dir);
            //s.enviar_foto(aux.getPk(), archivo);
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
            int p = Math.round(100 * valores[0]);
            dialog.setProgress(p);
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            Log.i("foto cargada - ", foto);
            ImageView i = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
            String[] img = foto.split("\\$");
            Log.i("foto cargada", Variables.getDireccion_fotos() + "dvi/" + img[1]);
            new ImageDownloaderTask(i).execute(Variables.getDireccion_fotos() + "dviDetalle/" + img[1]);
            Toast.makeText(c, img[0], Toast.LENGTH_SHORT).show();
            // siguiente_ventana();
        }
    }
    //Cargar Foto

    private class GetDVI extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                NavItms = s.sincronizar_DetalleDVI(Variables.getIdDetalleDVI(), "true");
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
                        EditText monto = (EditText) rootView.findViewById(R.id.editTextMonto);
                        EditText ref = (EditText) rootView.findViewById(R.id.editTextReferencia);
                        EditText obs = (EditText) rootView.findViewById(R.id.editTextDescrip);
                        ImageView i = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                        Log.i("foto cargada", Variables.getDireccion_fotos() + "dviDetalle/" + NavItms.get(0).getMED_FOTO());
                        new ImageDownloaderTask(i).execute(Variables.getDireccion_fotos() + "dviDetalle/" + NavItms.get(0).getMED_FOTO());
                        monto.setText(NavItms.get(0).getMED_MONTO());
                        ref.setText(NavItms.get(0).getMED_REFERENCIA());
                        obs.setText(NavItms.get(0).getMED_FACTURA());
                        id = NavItms.get(0).getMED_DVIFK();
                        idPro = NavItms.get(0).getMED_CLIFK();
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
