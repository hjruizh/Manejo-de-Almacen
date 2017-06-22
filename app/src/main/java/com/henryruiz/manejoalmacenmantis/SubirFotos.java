package com.henryruiz.manejoalmacenmantis;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import clases.Envio;
import clases.ListaFotosAdapter;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.FOTO;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubirFotos extends Fragment {

    View vista;
    Context c;
    Conexion s;
    private static String CARPETA = "/sdcard/DCIM/Camera/";
    private String archivo = "foto.jpg";
    private String dir = "";
    Envio f;
    String foto = "";
    String tipo;
    String numero;
    Boolean eliminar = false;
    ArrayList<FOTO> NavItms = new ArrayList<FOTO>();
    ListView listview;
    Activity activity;
    /**
     * Constantes para identificar la acci?n realizada (tomar una fotograf?a
     * o bien seleccionarla de la galer?a)
     */
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;

    /**
     * Variable que define el nombre para el archivo donde escribiremos
     * la fotograf?a de tama?o completo al tomarla.
    */
    private String name = "";

    public SubirFotos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_subir_fotos, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        f = new Envio(c);
        listview = (ListView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewFoto);

        //Cargar Titulos de Toolbar
        if (!Variables.getCot().equals("")){
            numero = Variables.getCot();
            tipo = "cot";
        }
        if (!Variables.getPed().equals("")){
            numero = Variables.getPed();
            tipo = "ped";
        }
        if (!Variables.getFac().equals("")){
            numero = Variables.getFac();
            tipo = "fac";
        }
        if (!Variables.getCom().equals("")){
            numero = Variables.getCom();
            tipo = "com";
        }
        //Cargar titulos de Toolbar

        //Tomar Foto o seleccionar foto de Galeria
        ImageButton btnAction = (ImageButton) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.fabButton);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tomar o Seleccionar Foto Environment.getExternalStorageDirectory() + "/"
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
                final CharSequence colors[] = new CharSequence[] {"Tomar Foto", "Seleccionar Foto de Galeria"};

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
                        if (!Variables.getCot().equals("")){
                            numero = Variables.getCot();
                            tipo = "cot";
                        }
                        if (!Variables.getPed().equals("")){
                            numero = Variables.getPed();
                            tipo = "ped";
                        }
                        if (!Variables.getFac().equals("")){
                            numero = Variables.getFac();
                            tipo = "fac";
                        }
                        if (!Variables.getCom().equals("")){
                            numero = Variables.getCom();
                            tipo = "com";
                        }
                        startActivityForResult(intent[0], code[0]);
                    }
                });
                builder.show();

                /**
                 * Luego, con todo preparado iniciamos la actividad correspondiente.
                 */

            }
        });
        //Tomar Foto o seleccionar foto de Galeria
        //Eliminar Foto
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(c);
        /*ImageView i = (ImageView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
        i.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i("eliminar foto", "foto: " + foto);
                if (!foto.equals("")){
                    alt_bld.setMessage("Desea Eliminar la foto?")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    tipo = foto;
                                    eliminar = true;
                                    new Foto().execute("");
                                    // finish();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // Aqui ponemos el codigo a ejecutar
                                    // al pulsar el boton �Cancelar�

                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    alert.setTitle("Eliminar Foto"); // Aqui ponemos el titulo de la ventana
                    alert.setIcon(com.henryruiz.manejoalmacenmantis.R.drawable.logo); // Aqui ponemos el icono de la ventana
                    alert.show();
                }
            }
        });*/
        //Eliminar Foto
        Button actualizar = (Button) vista.findViewById(R.id.actualizar);
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ListaFoto().execute();
            }
        });
        new ListaFoto().execute();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final FOTO posActual = NavItms.get(position);
                Log.i("posicion", "posicion " + posActual.getNombre().substring(1, posActual.getNombre().length()));
                alt_bld.setMessage("Desea Eliminar la foto?")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tipo = posActual.getNombre().substring(1, posActual.getNombre().length());
                                eliminar = true;

                                new Foto().execute("");
                                // finish();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Aqui ponemos el codigo a ejecutar
                                // al pulsar el boton �Cancelar�

                            }
                        });
                AlertDialog alert = alt_bld.create();
                alert.setTitle("Eliminar Foto"); // Aqui ponemos el titulo de la ventana
                alert.setIcon(com.henryruiz.manejoalmacenmantis.R.drawable.logo); // Aqui ponemos el icono de la ventana
                alert.show();
                /*Intent intent = new Intent(getActivity(), ViewArticuloActivity.class);
                intent.putExtra("id_art", posActual.getPk());
                intent.putExtra("TituloTipoArt", posActual.getEdicion());
                startActivity(intent);*/
            }
        });
        return vista;
    }

    //Mensaje de Alerta
    private void alerta_cantidad(String mensaje) {

        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setTitle(mensaje);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;

            }
        });
        alertDialog.show();
    }
    //Mensaje de Alerta

    /**
     * Funci?n que se ejecuta cuando concluye el intent en el que se solicita una imagen
     * ya sea de la c?mara o de la galer?a
     */
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
                    /*ImageView iv = (ImageView)vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                    iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));*/
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
                /*ImageView iv = (ImageView)vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                iv.setImageBitmap(BitmapFactory.decodeFile(name));*/
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
                    /*ImageView iv = (ImageView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                    iv.setImageBitmap(bitmap);*/
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

    //Cargar Foto
    private class Foto extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;
        String mensaje;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(c, "",
                    "Registrando...", true);
        }

        protected Integer doInBackground(String... urls) {
            Log.i("NOmbre del archivo", "archivo " + archivo);
            Log.i("NOmbre del Foto", "tipo "+tipo+" Eliminar " + eliminar);
            if (!eliminar) {
                foto = f.doFileUpload(archivo, tipo, numero, eliminar, dir);
            }
            if (eliminar){
                eliminar = false;
                foto = f.eliminar_Foto(tipo);
            }
            NavItms = s.sincronizar_Foto(numero, tipo);
            //s.enviar_foto(aux.getPk(), archivo);
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
            int p = Math.round(100 * valores[0]);
            dialog.setProgress(p);
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            //ImageView i = (ImageView) vista.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
            String[] img = foto.split("\\$");
            /*Log.i("foto cargada", Variables.getDireccion_fotos() + img[1]);*/
            //new ImageDownloaderTask(i).execute(Variables.getDireccion_fotos() + img[1]);
            if (NavItms!=null)
            {
                //NavItms = grupo;
                ListaFotosAdapter adaptadorGrid = new ListaFotosAdapter(c, NavItms);
                listview.setAdapter(adaptadorGrid);
            }
            Toast.makeText(c, img[0], Toast.LENGTH_SHORT).show();
            // siguiente_ventana();
        }
    }
    //Cargar Foto
    //Lista de Fotos
    private class ListaFoto extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;
        //ArrayList<FOTO> grupo = new ArrayList<FOTO>();
        protected void onPreExecute() {		 //Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Fotos...", true);
        }

        protected Integer doInBackground(String... params) {
            NavItms = s.sincronizar_Foto(numero, tipo);
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
                    ListaFotosAdapter adaptadorGrid = new ListaFotosAdapter(c, NavItms);
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

}
