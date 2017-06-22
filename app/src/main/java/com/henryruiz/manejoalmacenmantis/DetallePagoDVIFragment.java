package com.henryruiz.manejoalmacenmantis;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.util.ArrayList;

import clases.ListaDetalleDVIAdapter;
import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.MED;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetallePagoDVIFragment extends Fragment {

    View rootView;
    Context c;
    Conexion s;
    ArrayList<MED> NavItms = new ArrayList<MED>();
    ListView listview;
    String id;
    String idPro;

    @SuppressLint("ValidFragment")
    public DetallePagoDVIFragment(String pk, String proPk) {
        // Required empty public constructor
        id = pk;
        idPro = proPk;
    }

    public DetallePagoDVIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_detalle_pago_dvi, container, false);
        c = (Context)getActivity();
        s = new Conexion(c);
        Variables.setEmailCliN("");
        Variables.setTituloVentana("ListaDetalleDVI");
        listview = (ListView) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.listViewGrupo);
        new DetalleDVI().execute("");
        ImageButton btnAction = (ImageButton) rootView.findViewById(R.id.fabButtonAdd);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.setIdDetalleDVI("");
                CrearDetalleDVIFragment fragment2 = new CrearDetalleDVIFragment(id, idPro);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                fragmentTransaction.commit();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final MED posActual = NavItms.get(position);
                Variables.setIdDetalleDVI(posActual.getMED_PK());
                CrearDetalleDVIFragment fragment2 = new CrearDetalleDVIFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }

    private class DetalleDVI extends AsyncTask<String, Float, Integer> {
        ProgressDialog dialog;

        protected void onPreExecute() { // Mostramos antes de comenzar
            dialog = ProgressDialog.show(getActivity(), "", "Consultando Clientes...", true);
        }

        protected Integer doInBackground(String... params) {
            try {
                String pk = Variables.getGruPK();
                if (params[0].equals(""))
                    NavItms = s.sincronizar_DetalleDVI(id, "false");
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
                        ListaDetalleDVIAdapter adaptadorGrid = new ListaDetalleDVIAdapter(c, NavItms);
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
}
