package com.henryruiz.manejoalmacenmantis;


import android.annotation.TargetApi;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import clases.Envio;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.DVI;
import tablas.PRO;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrearDVIFragment extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    int pro = -1;
    static ArrayList<PRO> NavItms = new ArrayList<PRO>();
    static ArrayList<DVI> NavItmsDVI = new ArrayList<DVI>();
    String msg = "";
    private static String CARPETA = "/sdcard/DCIM/Camera/";
    private String archivo = "foto.jpg";
    private String dir = "";
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String name = "";
    Envio f;
    String foto = "";

    public CrearDVIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_crear_dvi, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        f = new Envio(c);
        Variables.setTituloVentana("CrearDVI");

        new Provedores().execute("");
        Button save = (Button) rootView.findViewById(R.id.buttonGuardar);
        final EditText montoD = (EditText) rootView.findViewById(R.id.editTextMontoD);
        final EditText montoB = (EditText) rootView.findViewById(R.id.editTextMontoB);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (pro!=-1 && !montoB.getText().toString().trim().equals("") && !montoD.getText().toString().trim().equals("")) {
                    new SetDVI().execute(montoD.getText().toString().trim(),montoB.getText().toString().trim());
                    if (!Variables.getIdDVI().equals("")) {
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
        return rootView;
    }

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

    private class Provedores extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                if (params[0].equals("")) {
                    NavItms = s.sincronizar_ProAll();
                    NavItmsDVI = s.sincronizar_DVI(Variables.getIdDVI());
                }
                else {
                    //NavItms = s.sincronizar_cli(params[0]);
                }
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
                        if (NavItmsDVI.size()>0){
                            EditText montod = (EditText) rootView.findViewById(R.id.editTextMontoD);
                            EditText montob = (EditText) rootView.findViewById(R.id.editTextMontoB);
                            montob.setText(NavItmsDVI.get(0).getDVI_MONTOB());
                            montod.setText(NavItmsDVI.get(0).getDVI_MONTOD());
                            ImageView i = (ImageView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.imagenTomada);
                            new ImageDownloaderTask(i).execute(Variables.getDireccion_fotos() + "dvi/" + NavItmsDVI.get(0).getDVI_FOTO());
                        }
                        String array_spinner[]=new String[NavItms.size()+1];
                        array_spinner[0] = "Seleccione Proveedor";
                        int j = 0;
                        for (int i = 0; i<NavItms.size(); i++){
                            array_spinner[i+1] = NavItms.get(i).getNombre();
                            Log.i("valor", "con"+NavItms.get(i).getNombre());
                            if (NavItmsDVI.size()>0 && NavItms.get(i).getPk() == Integer.parseInt(NavItmsDVI.get(0).getDVI_PROFK())){
                                j = i+1;
                            }
                        }
                        final Spinner auditoria = (Spinner) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.spinnerProveedor);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, array_spinner);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        auditoria.setAdapter(adapter);
                        auditoria.setSelection(j);
                        auditoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                try {
                                    pro = NavItms.get(auditoria.getSelectedItemPosition() - 1).getPk();
                                    alerta_cantidad(NavItms.get(auditoria.getSelectedItemPosition() - 1).getNombre());
                                }catch (Exception x){}
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub

                            }
                        });
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

    private class SetDVI extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                if (!params[0].equals("") && !params[1].equals(""))
                    msg = s.save_DVI(String.valueOf(pro),params[0],params[1]);
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
    private class Foto extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;
        String mensaje;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Cargando foto...", true);
        }

        protected Integer doInBackground(String... urls) {
            foto = f.doFileUploadDVI(Variables.getIdDVI(), archivo, dir);
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
            new ImageDownloaderTask(i).execute(Variables.getDireccion_fotos() + "dvi/" + img[1]);
            Toast.makeText(c, img[0], Toast.LENGTH_SHORT).show();
            // siguiente_ventana();
        }
    }
    //Cargar Foto
}
