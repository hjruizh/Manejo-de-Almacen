package com.henryruiz.manejoalmacenmantis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import sincronizacion.Variables;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrimInsetsFrameLayout sifl;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView ndList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sifl = (ScrimInsetsFrameLayout)findViewById(R.id.scrimInsetsFrameLayout);

        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);

        //Inicio de sesion
        SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
        Variables.setPk(settings.getString("pk", null));
        Variables.setUser(settings.getString("alias", null));
        Variables.setMensaje_pk(settings.getString("pk_mensaje", null));
        try {
            Variables.setAsunto(settings.getString("asunto", ""));
            Variables.setMsg(settings.getString("msg", ""));
        }
        catch (Exception x){}

        if(Variables.getPk()!=null) {
            //Menu del Navigation Drawer
            //startService(new Intent(MainActivity.this, Servicio.class));
            Fragment fragment = new Principal();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            ndList = (ListView) findViewById(R.id.navdrawerlist);

            final String[] opciones = new String[]{"Buscar Productos", "Buscar Productos Por Compra", "Relaizar Inventario",
                    "Tomar Fotos Cot, Ped, Fac, Com","Cuentas por Cobrar", "Cuentas Pagadas"};

            ArrayAdapter<String> ndMenuAdapter =
                    new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_activated_1, opciones);

            ndList.setAdapter(ndMenuAdapter);
            final RelativeLayout principal = (RelativeLayout) findViewById(R.id.principal);
            ndList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    Fragment fragment = null;

                    switch (pos) {
                        case 0:
                            Variables.setTituloVentana("BuscarProductosPCF");
                            fragment = new BuscarProductosPCF();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 1:
                            Variables.setTituloVentana("BuscarProductosCompras");
                            fragment = new BuscarProductosCompras();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 2:
                            Variables.setTituloVentana("SeleccionarInventario");
                            fragment = new SeleccionarInventario();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 3:
                            Variables.setTituloVentana("FotosFacPedCotCom");
                            fragment = new FotosFacPedCotCom();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 4:
                            fragment = new GrupoCuentasXCobrar();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 5:
                            Variables.setTituloVentana("GrupoCuentasPagadas");
                            fragment = new GrupoCuentasPagadas();
                            //principal.setVisibility(View.GONE);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();

                    ndList.setItemChecked(pos, true);

                    getSupportActionBar().setTitle(opciones[pos]);

                    drawerLayout.closeDrawer(sifl);
                }
            });

            //Drawer Layout
        }
        else {
            Fragment fragment = new IniciarSesion();
            getSupportActionBar().setTitle("Iniciar Sesión");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary_dark));

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        ActionBarDrawerToggle mDrawerToggle= new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final CharSequence colors[] = new CharSequence[] {"Mantis Principal", "Mantis Respaldo", "Remoto Principal", "Remoto Respaldo"};
            SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
            Variables.setUser(settings.getString("alias", null));
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Seleccionar Base de Datos");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //colors[which];
                    switch (which) {
                        case 0:
                            Variables.setPuerto("4043");
                            Variables.setBd("Principal");
                            Variables.setUrl("192.168.1.250");
                            break;
                        case 1:
                            Variables.setPuerto("4044");
                            Variables.setBd("Respaldo");
                            Variables.setUrl("192.168.1.250");
                            break;
                        case 2:
                            if (Variables.getUser().equals("MANTIS") ||
                                    Variables.getUser().equals("SMITH")) {
                                Variables.setPuerto("4043");
                                Variables.setBd("Remoto P");
                                Variables.setUrl("rptoscoreanos.myq-see.com");
                            }
                            break;
                        case 3:
                            if (Variables.getUser().equals("MANTIS") ||
                                    Variables.getUser().equals("SMITH")) {
                                Variables.setPuerto("4044");
                                Variables.setBd("Remoto R");
                                Variables.setUrl("rptoscoreanos.myq-see.com");
                            }
                            break;
                    }
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){

        if (Variables.getTituloVentana().equals("GrupoCuentasXCobrar")) {
            Principal fragment2 = new Principal();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment2)
                    .commit();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.content_frame, fragment2);
        }
        if (Variables.getTituloVentana().equals("ListaClientes")) {
            GrupoCuentasXCobrar fragment2 = new GrupoCuentasXCobrar();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment2)
                    .commit();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.content_frame, fragment2);
        }
        if (Variables.getTituloVentana().equals("CuentasPorCobrar")) {
            ListaClientes fragment2 = new ListaClientes();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment2)
                    .commit();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.content_frame, fragment2);
        }
        if (Variables.getTituloVentana().equals("ConsultaListaPrecios")) {
            CuentasPorCobrar fragment2 = new CuentasPorCobrar();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment2)
                    .commit();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.content_frame, fragment2);
        }
        // super.onBackPressed();
    }
}
