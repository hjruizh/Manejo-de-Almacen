package com.henryruiz.manejoalmacenmantis;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import clases.BitmapRe;
import clases.Envio;
import clases.Fecha;
import observablescrollview.ObservableScrollView;
import observablescrollview.ObservableScrollViewCallbacks;
import observablescrollview.ScrollState;
import observablescrollview.ScrollUtils;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.INV;


public class DetalleProducto extends BaseActivity implements ObservableScrollViewCallbacks {

    Envio f = new Envio(this);
    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private Toolbar toolbar;
    private String pk = "";
    static ArrayList<INV> invent = new ArrayList<INV>();
    INV aux = null;
    String codigoChat = "";
    Conexion s = new Conexion(this);
    Context c;
    private static String CARPETA = "/sdcard/DCIM/Camera/";
    private String archivo = "";
    private String dir = "";
    public static final int PICK_CONTACT_REQUEST = 1 ;
    private Uri contactUri;
    String cantSMS = "";

    /**
     * Constantes para identificar la acci?n realizada (tomar una fotograf?a
     * o bien seleccionarla de la galer?a)
     */
    private static int TAKE_PICTURE = 3;
    private static int SELECT_PICTURE = 2;

    /**
     * Variable que define el nombre para el archivo donde escribiremos
     * la fotograf?a de tama?o completo al tomarla.
     */
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.henryruiz.manejoalmacenmantis.R.layout.activity_detalle_producto);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setSupportActionBar((Toolbar) findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar));
        toolbar = (Toolbar) findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar);
        toolbar.setTitle("Detalle de Productos");
        setSupportActionBar(toolbar);

        mImageView = findViewById(com.henryruiz.manejoalmacenmantis.R.id.image);
        mToolbarView = findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(com.henryruiz.manejoalmacenmantis.R.color.list_row_pressed_bg)));

        mScrollView = (ObservableScrollView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(com.henryruiz.manejoalmacenmantis.R.dimen.parallax_image_height);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pk = extras.getString("pk");
        }
        if (!pk.equals(""))
            new Detalle().execute("");
        //Ocultar o Mostrar Contados por Compra o Auditoria
        CardView audi = (CardView)findViewById(com.henryruiz.manejoalmacenmantis.R.id.card_view_contados);
        if(!Variables.getCom().equals("") || !Variables.getInventario().equals(""))
            audi.setVisibility(View.VISIBLE);
        else
            audi.setVisibility(View.GONE);
        //Ocultar o Mostrar Contados por Compra o Auditoria
        //Tomar Foto o seleccionar foto de Galeria
        ImageButton btnAction = (ImageButton)findViewById(com.henryruiz.manejoalmacenmantis.R.id.fabButton);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(DetalleProducto.this);
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

                /**
                 * Luego, con todo preparado iniciamos la actividad correspondiente.
                 */

            }
        });
        //Tomar Foto o seleccionar foto de Galeria
        //Ubicaciones
        final EditText u1 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi1);
        final EditText u2 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi2);
        final EditText u3 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi3);
        final EditText u4 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi4);
        final EditText u5 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi5);
        final EditText u6 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi6);
        //Ubicaciones
        //Imprimir Etiquetas Peque�as
        final EditText cantidad = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCantidad);
        ImageButton boton_impp = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonEtiquetaP);
        final EditText cont = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCantidad);
        boton_impp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String ncant = cantidad.getText().toString().trim();
                //String cantcont = cont.getText().toString().trim();
                Log.i("cantidad", "cantidad: " + ncant);
                if (ncant.equalsIgnoreCase("")) {
                    alerta_cantidad("Debe indicar la cantidad");
                } else {
                    if ((!String.valueOf(u1.getText()).trim().equals("")
                            || !String.valueOf(u2.getText()).trim().equals("")
                            || !String.valueOf(u3.getText()).trim().equals("")
                            || !String.valueOf(u4.getText()).trim().equals("")
                            || !String.valueOf(u5.getText()).trim().equals("")
                            || !String.valueOf(u6.getText()).trim().equals(""))
                        //&& !aux.getFoto().equals("")
							/*|| ((!compra.equals("") || !Variables.getAudi().equals("")) && cantcont.equalsIgnoreCase("") && Integer.getInteger(cantcont)>0)*/){
                        new imprimir().execute("1");
                    }
                    else{
                        String mensaje = "";
						/*if ((!compra.equals("") || !Variables.getAudi().equals(""))  && !cantcont.equalsIgnoreCase("")  && Integer.getInteger(cantcont)<=0){
							mensaje += "Debe indicar la cantidad contada";
						}
						if (aux.getFoto().equals("")){
							mensaje += "Asigne una foto\n";
						}*/
                        if(!Variables.getCom().equals("")) {
                            mensaje += "Debe indicar la cantidad contada";
                        }
                        if (String.valueOf(u1.getText()).trim().equals("")
                                && String.valueOf(u2.getText()).trim().equals("")
                                && String.valueOf(u3.getText()).trim().equals("")
                                && String.valueOf(u4.getText()).trim().equals("")
                                && String.valueOf(u5.getText()).trim().equals("")
                                && String.valueOf(u6.getText()).trim().equals("")){
                            mensaje += "Ingrese Primero una Ubicacion\n";
                        }
                        alerta_cantidad(mensaje);
                    }
                }
            }
        });
        //Imprimir Etiquetas Peque�as
        //Imprimir Etiquetas Grandes
        ImageButton boton_impg = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonEtiquetaG);
        boton_impg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String ncant = cantidad.getText().toString().trim();
                String cantcont = cont.getText().toString().trim();
                Log.i("cantidad", "cantidad: " + ncant);
                if (ncant.equalsIgnoreCase("")) {
                    alerta_cantidad("Debe indicar la cantidad");
                } else {
                    if ((!String.valueOf(u1.getText()).trim().equals("")
                            || !String.valueOf(u2.getText()).trim().equals("")
                            || !String.valueOf(u3.getText()).trim().equals("")
                            || !String.valueOf(u4.getText()).trim().equals("")
                            || !String.valueOf(u5.getText()).trim().equals("")
                            || !String.valueOf(u6.getText()).trim().equals(""))
							/*&& !aux.getFoto().equals("")*/){
                        new imprimir().execute("2");
                    }
                    else{
                        String mensaje = "";
						/*if (aux.getFoto().equals("")){
							mensaje += "Asigne una foto\n";
						}*/
                        if (String.valueOf(u1.getText()).trim().equals("")
                                && String.valueOf(u2.getText()).trim().equals("")
                                && String.valueOf(u3.getText()).trim().equals("")
                                && String.valueOf(u4.getText()).trim().equals("")
                                && String.valueOf(u5.getText()).trim().equals("")
                                && String.valueOf(u6.getText()).trim().equals("")){
                            mensaje += "Ingrese Primero una Ubicacion\n";
                        }
                        alerta_cantidad(mensaje);
                    }
                }
            }
        });
        //Imprimir Etiquetas Grandes
        //Guardar Informacion
        final EditText contados = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextContados);
        ImageButton boton_comprar = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonGuardar);
        boton_comprar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!Variables.getCom().equals("") || !Variables.getInventario().equals(""))
                    if (!contados.getText().toString().equals(""))
                        new Actualizar().execute("");
                    else
                        alerta_cantidad("Ingrese La Cantidad Contada");
                else
                    new Actualizar().execute("");
            }
        });
        //Guardar Informacion
        //Informacion de productos solicitados por Chat
        SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
        codigoChat = settings.getString("codigo", null);
        final ImageButton NoHay = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.fabButtonListo);
        NoHay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new updateMsg().execute("4");
                }
            });
        final ImageButton Entregado = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.fabButtonCancelar);
        Entregado.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {new updateMsg().execute("3");
                }
            });
        //Informacion de productos solicitados por Chat
        //Enviar Etiqueta
        final ImageButton sms = (ImageButton) findViewById(com.henryruiz.manejoalmacenmantis.R.id.fabButtonSMS);
        sms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetalleProducto.this);
                builder.setTitle("Cantidad de Productos");
                // Set up the input
                final EditText input = new EditText(DetalleProducto.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                // Set up the buttons
                builder.setNeutralButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cantSMS = input.getText().toString();
                        enviarSms();
                    }
                });
                builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cantSMS = input.getText().toString();
                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(i, PICK_CONTACT_REQUEST);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        //Enviar Etiqueta
    }
    //Envio de SMS
    public void enviarSms(Uri uri){
        try{
            //EditText cant = (EditText) findViewById(R.id.editTextCantPed);04249005033
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(getPhone(uri), null, "Traer " + cantSMS.toString().trim() + " del el producto: " + aux.getCodigo() + " - " + aux.getNombre(), null, null);
            Log.i("listo","04249005033 - " + getPhone(uri));
            Toast.makeText(c, "Mensaje Enciado Con Exito al Numero: " + getPhone(uri), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("error","error sms");
        }
        cantSMS = "";
    }
    public void enviarSms(){
        try{
            //EditText cant = (EditText) findViewById(R.id.editTextCantPed);04249005033
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("04249005033", null, "Traer " + cantSMS.toString().trim() + " del el producto: " + aux.getCodigo() + " - " + aux.getNombre(), null, null);
            Log.i("listo","04249005033");
            Toast.makeText(c, "Mensaje Enciado Con Exito al Numero: 04249005033", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("error","error sms");
        }
        cantSMS = "";
    }
    //Envio de SMS
    //Seleccionar Contacto
    public void initPickContacts(View v){
        /*
        Crear un intent para seleccionar un contacto del dispositivo
         */
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        /*
        Iniciar la actividad esperando respuesta a través
        del canal PICK_CONTACT_REQUEST
         */
        startActivityForResult(i, PICK_CONTACT_REQUEST);
    }
    private String getPhone(Uri uri) {
        /*
        Variables temporales para el id y el telÃ©fono
         */
        String id = null;
        String phone = null;

        /************* PRIMERA CONSULTA ************/
        /*
        Obtener el _ID del contacto
         */
        Cursor contactCursor = getContentResolver().query(
                uri,
                new String[]{ContactsContract.Contacts._ID},
                null,
                null,
                null);


        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0);
        }
        contactCursor.close();

        /************* SEGUNDA CONSULTA ************/
        /*
        Sentencia WHERE para especificar que solo deseamos
        nÃºmeros de telefonÃ­a mÃ³vil
         */
        String selectionArgs =
                Phone.CONTACT_ID + " = ? AND " +
                        Phone.TYPE+"= " +
                        Phone.TYPE_MOBILE;

        /*
        Obtener el nÃºmero telefÃ³nico
         */
        Cursor phoneCursor = getContentResolver().query(
                Phone.CONTENT_URI,
                new String[] { Phone.NUMBER },
                selectionArgs,
                new String[] { id },
                null
        );
        if (phoneCursor.moveToFirst()) {
            phone = phoneCursor.getString(0);
        }
        phoneCursor.close();

        return phone;
    }

    //Seleccionar Contacto
    //Mensaje de Alerta
    private void alerta_cantidad(String mensaje) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

    /**
     * Funci?n que se ejecuta cuando concluye el intent en el que se solicita una imagen
     * ya sea de la c?mara o de la galer?a
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Seleccionar Contacto
        Log.i("requestCode", "valor " + requestCode + "-PICK_CONTACT_REQUEST-" + PICK_CONTACT_REQUEST + "-TAKE_PICTURE-" + TAKE_PICTURE);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                /*
                Capturar el valor de la Uri
                 */
                contactUri = data.getData();
                /*
                Procesar la Uri
                 */
                enviarSms(contactUri);
            }
        }
        else {
            //Seleccionar Contacto
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
                        ImageView iv = (ImageView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.image);
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
                    Log.i("Seleccionado", "" + requestCode + TAKE_PICTURE);
                    File f = new File(name);
                    Bitmap bitmap = BitmapRe.decodeFile(f,800,600);
                    ImageView iv = (ImageView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.image);
                    iv.setImageBitmap(bitmap);
                    /**
                     * Para guardar la imagen en la galer?a, utilizamos una conexi?n a un MediaScanner
                     */
                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        private MediaScannerConnection msc = null;

                        {
                            msc = new MediaScannerConnection(getApplicationContext(), this);
                            msc.connect();
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
            } else if (requestCode == SELECT_PICTURE) {
                // OI FILE Manager
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String filemanagerstring = selectedImage.getPath();

                    // MEDIA GALLERY
                    String selectedImagePath = getPath(selectedImage);
                    Log.i("Imagen", selectedImagePath);
                    dir = selectedImagePath;
                    InputStream is;
                    try {
                        is = getContentResolver().openInputStream(selectedImage);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        File f = new File(selectedImagePath);
                        Bitmap bitmap = BitmapRe.decodeFile(f,800,600);//BitmapFactory.decodeStream(bis);
                        ImageView iv = (ImageView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.image);
                        iv.setImageBitmap(bitmap);
                        //new Foto().execute("");
                    } catch (FileNotFoundException e) {
                    }
                }
            }
            if (resultCode == RESULT_OK) {
                new Foto().execute("");
            }
        }
    }



    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
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
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(com.henryruiz.manejoalmacenmantis.R.color.list_row_pressed_bg);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private class Detalle extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(DetalleProducto.this, "",
                    "Cargando...", true);
        }

        protected Integer doInBackground(String... urls) {
            try {
                /*if ((!fechai.equals("") && !fechaf.equals("")) || !diario.equals("")){
                    invent = s.sincronizar_INV_pk(id_seleccionado, fechai, fechaf);
                }
                else if (!compra.equals("")){
                    invent = s.sincronizar_INV_pk(id_seleccionado, compra);
                }
                else if (!Variables.audi.equals("Sin Auditoria")){
                    invent = s.sincronizar_INV(id_seleccionado);
                }*/
                if (!Variables.getCom().equals("")){
                    invent = s.sincronizar_INV_pk(pk, Variables.getCom());
                }
                else{
                    invent = s.sincronizar_INV_pk(pk);
                }
                aux = invent.get(0);
                archivo = aux.getCodigo() + ".jpg";
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            if (aux != null) {
                TextView t1 = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar_nombre);
                TextView t2 = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar_codigo);
                ImageView i = (ImageView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.image);
                EditText u1 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi1);
                EditText u2 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi2);
                EditText u3 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi3);
                EditText u4 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi4);
                EditText u5 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi5);
                EditText u6 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi6);
                TextView ex = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.textViewTitulo5);
                t1.setText(aux.getNombre());
                t2.setText(aux.getCodigo());
                u1.setText(aux.getubi1());
                u2.setText(aux.getubi2());
                u3.setText(aux.getubi3());
                u4.setText(aux.getubi4());
                u5.setText(aux.getubi5());
                u6.setText(aux.getubi6());
                if(Variables.getInventario().equals("")) {
                    ex.setText(ex.getText() + String.valueOf(aux.getExistencia()));
                }
                archivo = aux.getCodigo() + ".jpg";
                if (!Variables.getCom().equals("")){
                    EditText con = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextContados);
                    con.setText(aux.getcontados());
                }
                //ex.setText(String.valueOf(aux.getExistencia()));
                SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
                String codigo = settings.getString("codigo", null);
                String pkMsg = settings.getString("pk_mensaje", null);
                /*TableRow chat = (TableRow) findViewById(R.id.tableRow7);
                if (codigo!=null && codigo.equals(aux.getCodigo())){
                    chat.setVisibility(View.VISIBLE);
                    s.sincronizar_Mensaje_guardar("1", pkMsg);
                }
                else{
                    chat.setVisibility(View.GONE);
                }
                if ((!fechai.equals("") && !fechaf.equals("")) || !Variables.audi.equals("Sin Auditoria") || !diario.equals("")){
                    EditText con = (EditText) findViewById(R.id.editTextCont);
                    TextView eticon = (TextView) findViewById(R.id.TextView03);
                    try{
                        if (!aux.getcontados().equals(""))
                            con.setText(aux.getcontados());
                        Log.i("Mensaje", aux.getcontados());
                    }
                    catch(Exception e){
                        Log.i("Mensaje", e.getMessage());
                    }
                    LinearLayout contados = (LinearLayout) findViewById(R.id.cont);
                    contados.setVisibility(View.VISIBLE);
                    con.setVisibility(View.VISIBLE);
                    eticon.setVisibility(View.VISIBLE);
                }
                else{
                    EditText con = (EditText) findViewById(R.id.editTextCont);
                    TextView eticon = (TextView) findViewById(R.id.TextView03);
                    if (!compra.equals("")){
                        con.setText(aux.getcontados());
                        LinearLayout contados = (LinearLayout) findViewById(R.id.cont);
                        contados.setVisibility(View.VISIBLE);
                        con.setVisibility(View.VISIBLE);
                        eticon.setVisibility(View.VISIBLE);
                    }
                    else{
                        LinearLayout contados = (LinearLayout) findViewById(R.id.cont);
                        con.setVisibility(View.GONE);
                        eticon.setVisibility(View.GONE);
                        contados.setVisibility(View.GONE);
                    }
                }*/
                Log.i("foto", aux.getFoto());
                /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();*/
                ImageLoader.getInstance().init(Variables.cargarImagen(getApplicationContext()));
                ImageLoader.getInstance().displayImage(aux.getFoto(), i);

                //new ImageDownloaderTask(i).execute(aux.getFoto());
				/*
				 * i.setOnClickListener(new View.OnClickListener() { public void
				 * onClick(View view) { Intent intent = new
				 * Intent(VistaProducto.this,ProductoFoto.class);
				 * intent.putExtra("url",aux.getFoto()); startActivity(intent);
				 * } });
				 *
				 * Button boton_comprar = (Button) findViewById(R.id.button1);
				 * TextView tienda = (TextView)
				 * findViewById(R.id.textView_tienda); EditText cantidad=
				 * (EditText) findViewById(R.id.editText1); if
				 * (aux.getTienda()==1) { boton_comprar.setEnabled(true);
				 * tienda.setVisibility(4); cantidad.setEnabled(true); } else {
				 * boton_comprar.setEnabled(false);
				 * boton_comprar.setBackgroundColor(-7829368);
				 * tienda.setVisibility(0); cantidad.setEnabled(false); }
				 */
                if(codigoChat!=null && aux.getCodigo().trim().equals(codigoChat)) {

                }
                else{
                    CardView chat = (CardView)findViewById(com.henryruiz.manejoalmacenmantis.R.id.card_view_chat);
                    chat.setVisibility(View.GONE);
                }
            }
        }
    }
    //Subir Foto
    private class Foto extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;
        String mensaje;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(DetalleProducto.this, "En progreso", "Cargando...");
        }

        protected Integer doInBackground(String... urls) {
            Log.i("NOmbre del archivo", "archivo "+archivo);
            mensaje = f.doFileUpload(String.valueOf(aux.getPk()),archivo, dir);
            Log.i("mensaje", mensaje);
            //s.enviar_foto(aux.getPk(), archivo);
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
            int p = Math.round(100 * valores[0]);
            dialog.setProgress(p);
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            //Toast.makeText(c, "Foto Cargada", Toast.LENGTH_SHORT).show();
            // siguiente_ventana();

            alerta_cantidad(mensaje);
        }
    }
    //Subir Foto
    //Imprimir Etiquetas
    private class imprimir extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;
        String mensaje;

        protected void onPreExecute() { // Mostramos antes de comenzar
            //dialog = ProgressDialog.show(DetalleProducto.this, "","Registrando...", true);
        }

        protected Integer doInBackground(String... urls) {
            try {
                EditText cant = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextCantidad);
                mensaje = "Etiqueta: "+s.enviar_etiqueta(aux.getPk(), urls[0], cant.getText().toString())+"\n";
                /*if (!compra.equals("") && urls[0].equals("1")){
                    EditText cont = (EditText) findViewById(R.id.editTextCont);
                    mensaje += "Auditar: " + s.enviar_etiqueta_comp(compra,aux.getPk(),
                            cant.getText().toString(), cont.getText().toString());
                }*/
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
        }

        protected void onPostExecute(Integer bytes) {
            //dialog.dismiss();
            //Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();
            alerta_cantidad(mensaje);
        }
    }
    //Imprimir Etiquetas
    //Actualizar Datos de Productos
    private class Actualizar extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;
        String mensaje;

        protected void onPreExecute() { // Mostramos antes de comenzar
            //dialog = ProgressDialog.show(DetalleProducto.this, "","Registrando...", true);
        }

        protected Integer doInBackground(String... urls) {
            try {
                EditText u1 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi1);
                EditText u2 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi2);
                EditText u3 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi3);
                EditText u4 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi4);
                EditText u5 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi5);
                EditText u6 = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextUbi6);
                mensaje = "Ubicacion: "+s.enviar_ubicacion(aux.getainPk(), u1.getText()
                        .toString(), u2.getText().toString(), u3.getText()
                        .toString(), u4.getText().toString(), u5.getText()
                        .toString(), u6.getText().toString())+"\n";
                if (!Variables.getMes().equals("") && !Variables.getAnio().equals("")){
                    EditText cont = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextContados);
                    String mes = Variables.agregarCero(Integer.parseInt(Variables.getMes()) + 1);
                    String fechaf = String.valueOf(Variables.getAnio().toString()) + mes + Fecha.diasDelMes(Integer.parseInt(Variables.getMes()), Variables.getAnio().toString());
                    mensaje += "Contados: "+s.enviar_conatdos(String.valueOf(aux.getPk()), fechaf,
                            cont.getText().toString(), String.valueOf(aux.getExistencia()));
                    Log.i("Inventario","-"+(Variables.getInventario()));
                    Log.i("Inventario","-"+(Variables.getInventario().equals("Diario")));
                }
                if(Variables.getInventario().equals("Diario")){
                    EditText cont = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextContados);
                    mensaje += "Contados: "+s.enviar_conatdos(String.valueOf(aux.getPk()), "",
                            cont.getText().toString(), String.valueOf(aux.getExistencia()));
                    Log.i("Inventario","-"+(Variables.getInventario()));
                    Log.i("Inventario","-"+(Variables.getInventario().equals("Diario")));
                }
                if (!Variables.getAudi().equals("")){
                    EditText cont = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextContados);
                    mensaje += "Contados: "+s.enviar_conatdos(String.valueOf(aux.getPk()),
                            cont.getText().toString(), String.valueOf(aux.getExistencia()));
                }
                if (!Variables.getCom().equals("")){
                    EditText cont = (EditText) findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextContados);
                    mensaje += "Auditar: " + s.enviar_etiqueta_comp(Variables.getCom(),aux.getPk(),
                            "0", cont.getText().toString());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.i("Contados","-"+Variables.getCom());
                mensaje += e.getMessage();
                e.printStackTrace();
            }
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
        }

        protected void onPostExecute(Integer bytes) {
            /*dialog.dismiss();
            Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();*/
            alerta_cantidad(mensaje);
        }
    }
    //Actualizar Datos de Productos
    //Actualizar Datos de Chat
    private class updateMsg extends AsyncTask<String, Float, Integer> {

        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(DetalleProducto.this, "",
                    "Actualizando Mensaje...", true);
        }

        protected Integer doInBackground(String... urls) {
            //Log.i("NOmbre del archivo", "archivo "+archivo);
            SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
            String pkMsg = settings.getString("pk_mensaje", null);
            s.sincronizar_Mensaje_guardar(urls[0].trim(),pkMsg);
            if (!urls[0].trim().equals("1")){
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("codigo");
                editor.remove("pk_mensaje");
                editor.commit();
                Variables.setMensaje_pk("");
            }
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
            int p = Math.round(100 * valores[0]);
            dialog.setProgress(p);
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            //Toast.makeText(c, "Mensaje Actualizado", Toast.LENGTH_SHORT).show();
            // siguiente_ventana();
        }
    }
    //Actualizar Datos de Chat
}
