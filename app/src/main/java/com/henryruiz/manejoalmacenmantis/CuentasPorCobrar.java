package com.henryruiz.manejoalmacenmantis;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import clases.ListaCxcAdapter;
import sincronizacion.Conexion;
import sincronizacion.Post;
import sincronizacion.Variables;
import tablas.CXC;


public class CuentasPorCobrar extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    ArrayList<CXC> NavItms = new ArrayList<CXC>();
    ListView listview;
    private static String CARPETA = "/sdcard/DCIM/Camera/";
    private String dir = "";
    private String archivo = "foto.jpg";
    private String name = "";
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String pagado = "";
    private double monto = 0;
    NumberFormat formatter = new DecimalFormat("#0.00");
    EditText montoTotal;
    TextView msg;
    Uri selectedImage;


    public CuentasPorCobrar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_cuentas_por_cobrar, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        listview = (ListView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewFac);
        montoTotal = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextMonto);
        msg = (TextView)  rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextMensaje);
        Variables.setTituloVentana("CuentasPorCobrar");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Log.i("posicion", "posicion " + position);
                final CXC posActual = NavItms.get(position);
                Variables.setCliPk(String.valueOf(posActual.getPk()));
                EditText msg = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextMensaje);
                String str1 = String.valueOf(msg.getText());
                int wantedPosition = position; // Whatever position you're looking for
                int firstPosition = listview.getFirstVisiblePosition() - listview.getHeaderViewsCount(); // This is the same as child #0
                int wantedChild = wantedPosition - firstPosition;
                if (str1.toUpperCase().contains(NavItms.get(position).getFactura().toUpperCase())){
                    msg.setText(str1.replace("\r\n Factura Nro. " + NavItms.get(position).getFactura().toUpperCase() + " Monto " + NavItms.get(position).getSaldo().toUpperCase(),""));
                    Log.i("posicion nueva", "pos " + wantedChild);
                    listview.getChildAt(wantedChild).setBackgroundColor(Color.WHITE);
                    pagado.replace(NavItms.get(position).getFactura().toUpperCase() + " ","");
                    monto = monto - Double.valueOf(NavItms.get(position).getSaldo().toUpperCase().replace(",","."));
                    if (monto<0){
                        monto = 0;
                    }
                    montoTotal.setText(String.valueOf(formatter.format(monto)));
                }
                else {
                    msg.setText(str1 + "\r\n Factura Nro. " + NavItms.get(position).getFactura().toUpperCase() + " Monto " + NavItms.get(position).getSaldo().toUpperCase());
                    Log.i("posicion nueva", "pos " + wantedChild);
                    pagado += NavItms.get(position).getFactura().toUpperCase() + " ";
                    listview.getChildAt(wantedChild).setBackgroundColor(Color.GRAY);
                    monto = monto + Double.valueOf(NavItms.get(position).getSaldo().toUpperCase().replace(",","."));
                    montoTotal.setText(String.valueOf(formatter.format(monto)));
                }
            }
        });

        ImageButton enviar = (ImageButton) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.buttonEnviar);

        enviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                name = CARPETA + archivo;
                final Intent[] intent = {new Intent(MediaStore.ACTION_IMAGE_CAPTURE)};
                final int[] code = {TAKE_PICTURE};
                final CharSequence colors[] = new CharSequence[] {"Tomar Foto", "Seleccionar Foto de Galeria", "Sin Foto", "Enviar cuentas por cobrar", "Lista de precios"};
                final boolean[] band = {true};
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Tomar Foto");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Uri output = Uri.fromFile(new File(name));
                                intent[0].putExtra(MediaStore.EXTRA_OUTPUT, output);
                                break;
                            case 1:
                                intent[0] = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                code[0] = SELECT_PICTURE;
                                break;
                            case 2:
                                /*TextView msg = (TextView)  rootView.findViewById(R.id.editTextMensaje);
                                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                emailIntent.setType("application/image");
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{NavItms.get(0).getEmail(),"administracion@rptoscoreanos.com"});
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Comprobante de pago");
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.getText() + "\r\nTotal Pagado: " + montoTotal.getText());
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));*/
                                new EnviarPago().execute(msg.getText().toString(), montoTotal.getText().toString(), "No");
                                band[0] = false;
                                break;
                            case 3:
                                new EnviarCuentasxCobrar().execute(Variables.getCliPk().toString());
                                band[0] = false;
                                break;
                            case 4:
                                ConsultaListaPrecios fragment2 = new ConsultaListaPrecios();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                                fragmentTransaction.commit();
                                band[0] = false;
                                break;
                        }
                        if(band[0])
                            getActivity().startActivityFromFragment(CuentasPorCobrar.this,intent[0], code[0]);
                    }
                });
                builder.show();
            }
        });

        new CuentasxCobrar().execute("");

        return rootView;
    }

    //envio de imagen y correo

    //Mensaje de Alerta
    private void alerta_cantidad(String mensaje) {

        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            selectedImage = data.getData();
        }
        catch (Exception x) {
            File f = new File(name);
            selectedImage = Uri.fromFile(f);
        }
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
                        /*ImageView iv = (ImageView) findViewById(R.id.image);
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
                    Log.i("Seleccionado", "" + requestCode + TAKE_PICTURE);
                    /*ImageView iv = (ImageView) findViewById(R.id.image);
                    iv.setImageBitmap(BitmapFactory.decodeFile(name));*/
                    /**
                     * Para guardar la imagen en la galer?a, utilizamos una conexi?n a un MediaScanner
                     */
                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        private MediaScannerConnection msc = null;

                        {
                            msc = new MediaScannerConnection(c, this);
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
                    String filemanagerstring = selectedImage.getPath();

                    // MEDIA GALLERY
                    String selectedImagePath = getPath(selectedImage);
                    Log.i("Imagen", selectedImagePath);
                    dir = selectedImagePath;
                    /*InputStream is;
                    try {
                        is = getContentResolver().openInputStream(selectedImage);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        Bitmap bitmap = BitmapFactory.decodeStream(bis);
                        ImageView iv = (ImageView) findViewById(R.id.image);
                        iv.setImageBitmap(bitmap);
                        //new Foto().execute("");
                    } catch (FileNotFoundException e) {
                    }*/
                }
            }
            if (resultCode == Activity.RESULT_OK) {
                //new Foto().execute("");
                /*final EditText montoTotal = (EditText) rootView.findViewById(R.id.editTextMonto);
                String selectedImagePath = getPath(selectedImage);
                TextView msg = (TextView)  rootView.findViewById(R.id.editTextMensaje);
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/image");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{NavItms.get(0).getEmail(),"administracion@rptoscoreanos.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Comprobante de pago");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.getText() + "\r\nTotal Pagado: " + montoTotal.getText());
                emailIntent.putExtra(Intent.EXTRA_STREAM, selectedImage);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));*/
                new EnviarPago().execute(msg.getText().toString(), montoTotal.getText().toString(), "Si");
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


    //envio de imagen y correo

    private class CuentasxCobrar extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Cuentas por Cobrar...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                NavItms = s.sincronizar_CXC();
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
                        ListaCxcAdapter adaptadorGrid = new ListaCxcAdapter(c, NavItms);
                        listview.setAdapter(adaptadorGrid);
                        TextView Nombre = (TextView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.textViewNombre);
                        TextView Codigo = (TextView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.textViewCodigo);
                        Nombre.setText(NavItms.get(0).getNombre());
                        Codigo.setText(NavItms.get(0).getCodigo());
                    }
                } catch (Exception e) {
                    //Log.i("error", e.getMessage());
                }
            }
            else {
                //Log.i("error","Sin Grupo");
            }
        }

    }

    private class EnviarPago extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Enviando pagos al servidor...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                if (params!=null) {
                    Post p = new Post();
                    String foto = s.enviar_pago(NavItms.get(0).getCliPk(),params[1],params[0]);
                    Log.i("Foto", "-" + foto);
                    ArrayList parametros = new ArrayList();
                    String selectedImagePath = getPath(selectedImage);
                    if (selectedImagePath!=null)
                        p.getServerDataStringImagen(parametros,selectedImagePath,Variables.getDireccion() + "Servicio.svc/CPA_FOTO/" + foto.replace("\n",""));
                    else
                        p.getServerDataStringImagen(parametros,name,Variables.getDireccion() + "Servicio.svc/CPA_FOTO/" + foto.replace("\n",""));
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("application/image");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{NavItms.get(0).getEmail(), "administracion@rptoscoreanos.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Comprobante de pago");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, params[0] + "\r\nTotal Pagado: " + params[1]);
                    if (params[2]=="Si")
                        emailIntent.putExtra(Intent.EXTRA_STREAM, selectedImage);
                    startActivity(Intent.createChooser(emailIntent, "Enviar correo..."));
                }
            } catch (Exception e) {
                Log.i("error_grupo", "-"+e.getLocalizedMessage());
                e.printStackTrace();
                return 0;
            }
            return 1;
        }

        protected void onProgressUpdate(Float... valores) {
            //dialog.dismiss();
            /*if (!verificar_internet()) {

            }*/
        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            if (bytes==1) {
                try {

                } catch (Exception e) {
                    //Log.i("error", e.getMessage());
                }
            }
            else {
                //Log.i("error","Sin Grupo");
            }
        }

    }

    private class EnviarCuentasxCobrar extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;
        String msg;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Cuentas por Cobrar...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                msg = s.enviar_CXC(params[0]);
                if (msg != null)
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
                    alerta_cantidad(msg);
                } catch (Exception e) {
                    //Log.i("error", e.getMessage());
                }
            }
            else {
                //Log.i("error","Sin Grupo");
            }
        }

    }
}
