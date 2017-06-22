package com.henryruiz.manejoalmacenmantis;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import clases.ListaProveedorAdapter;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.PRO;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProveedorDVIFragment extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    ArrayList<PRO> NavItms = new ArrayList<PRO>();
    ListView listview;
    String msg = "";

    public ProveedorDVIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_proveedor_dvi, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        Variables.setEmailCliN("");
        Variables.setTituloVentana("ListaProveedores");
        listview = (ListView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewGrupo);
        new Provedores().execute("");
        ImageButton btnAction = (ImageButton) rootView.findViewById(R.id.fabButtonAdd);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.setIdDVI("");
                CrearDVIFragment fragment2 = new CrearDVIFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                fragmentTransaction.commit();
            }
        });
        listview = (ListView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewGrupo);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Log.i("posicion", "posicion " + position);
                final PRO posActual = NavItms.get(position);
                final CharSequence colors[] = new CharSequence[]{"Editar", "Detalles de pago", "Enviar Correo"};

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Proveedor " + posActual.getNombre());
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //colors[which];
                        switch (which) {
                            case 0:
                                Variables.setIdDVI(posActual.getDviFk());
                                CrearDVIFragment fragment2 = new CrearDVIFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                DetallePagoDVIFragment fragment1 = new DetallePagoDVIFragment(posActual.getDviFk(), String.valueOf(posActual.getPk()));
                                FragmentManager fragmentManager1 = getFragmentManager();
                                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                                fragmentTransaction1.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment1);
                                fragmentTransaction1.commit();
                                break;
                            case 2:
                                Variables.setIdDVI(posActual.getDviFk());
                                new Correo().execute("");
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        return rootView;
    }

    private class Provedores extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                if (params[0].equals(""))
                    NavItms = s.sincronizar_Pro();
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
                        ListaProveedorAdapter adaptadorGrid = new ListaProveedorAdapter(c, NavItms);
                        listview.setAdapter(adaptadorGrid);
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

    private class Correo extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Enviando correo...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                if (params[0].equals(""))
                    msg = s.save_CorreoDVI();
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
                    if (msg!="")
                    {
                        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
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
