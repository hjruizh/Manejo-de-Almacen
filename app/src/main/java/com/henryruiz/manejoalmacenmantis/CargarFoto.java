package com.henryruiz.manejoalmacenmantis;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import clases.Envio;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.FOTO;


public class CargarFoto extends AppCompatActivity {

    View vista;
    Context c;
    Conexion s;
    private static String CARPETA = "/sdcard/DCIM/Camera/";
    private String archivo = "foto.jpg";
    private String dir = "";
    Envio f = new Envio(this);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.henryruiz.manejoalmacenmantis.R.layout.activity_cargar_foto);
        TextView t1 = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar_nombre);
        TextView t2 = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.toolbar_codigo);
        //Cargar Titulos de Toolbar
        if (!Variables.getCot().equals("")){
            t1.setText("Cotización Nro "+ Variables.getCot().trim());
            t2.setText("Base de Datos " + Variables.getBd());
        }
        if (!Variables.getPed().equals("")){
            t1.setText("Pedido Nro "+ Variables.getPed().trim());
            t2.setText("Base de Datos " + Variables.getBd());
        }
        if (!Variables.getFac().equals("")){
            t1.setText("Factura Nro "+ Variables.getFac().trim());
            t2.setText("Base de Datos " + Variables.getBd());
        }
        if (!Variables.getCom().equals("")){
            t1.setText("Compra Nro "+ Variables.getCom().trim());
            t2.setText("Base de Datos " + Variables.getBd());
        }
        //Cargar titulos de Toolbar
        Fragment fragment = new SubirFotos();
        getSupportFragmentManager().beginTransaction()
                .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment)
                .commit();
    }
}
