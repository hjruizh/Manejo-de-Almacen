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
        setContentView(com.henryruiz.manejoalmacenmantis.R.layout.activity_main);

        sifl = (ScrimInsetsFrameLayout)findViewById(com.henryruiz.manejoalmacenmantis.R.id.scrimInsetsFrameLayout);

        //Toolbar

        toolbar = (Toolbar) findViewById(com.henryruiz.manejoalmacenmantis.R.id.appbar);
        toolbar.setNavigationIcon(com.henryruiz.manejoalmacenmantis.R.drawable.logo);
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
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment)
                    .commit();

            ndList = (ListView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.navdrawerlist);

            final String[] opciones = new String[]{"Buscar Productos", "Buscar Productos Por Compra", "Relaizar Inventario",
                    "Tomar Fotos Cot, Ped, Fac, Com","Cuentas por Cobrar", "Cuentas Pagadas", "Clientes a Credito", "Clientes Nuevos", "Proveedores DVI", "Configuración"};

            ArrayAdapter<String> ndMenuAdapter =
                    new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_activated_1, opciones);

            ndList.setAdapter(ndMenuAdapter);
            final RelativeLayout principal = (RelativeLayout) findViewById(com.henryruiz.manejoalmacenmantis.R.id.principal);
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
                        case 6:
                            Variables.setTituloVentana("ClientesCredito");
                            Variables.setGruPK("0");
                            fragment = new ListaClientes();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 7:
                            Variables.setGruPK("0");
                            Variables.setTituloVentana("ListaClientesNuevos");
                            fragment = new ListaClientesNuevosFragment();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 8:
                            Variables.setTituloVentana("ListaProveedores");
                            fragment = new ProveedorDVIFragment();
                            //principal.setVisibility(View.GONE);
                            break;
                        case 9:
                            Variables.setTituloVentana("Configuraciones");
                            fragment = new ConfMsgFragment();
                            //principal.setVisibility(View.GONE);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment)
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
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment)
                    .commit();
        }
        drawerLayout = (DrawerLayout)findViewById(com.henryruiz.manejoalmacenmantis.R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(com.henryruiz.manejoalmacenmantis.R.color.color_primary_dark));

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, com.henryruiz.manejoalmacenmantis.R.string.openDrawer, com.henryruiz.manejoalmacenmantis.R.string.closeDrawer){

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
        ActionBarDrawerToggle mDrawerToggle= new ActionBarDrawerToggle(this, drawerLayout,toolbar, com.henryruiz.manejoalmacenmantis.R.string.app_name, com.henryruiz.manejoalmacenmantis.R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.henryruiz.manejoalmacenmantis.R.menu.menu_main, menu);
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
                            /*if (Variables.getUser().equals("MANTIS") ||
                                    Variables.getUser().equals("SMITH")) {*/
                            Variables.setPuerto("4043");
                            Variables.setBd("Remoto P");
                            Variables.setUrl("rptoscoreanos.myq-see.com");
                            //}
                            break;
                        case 3:
                            /*if (Variables.getUser().equals("MANTIS") ||
                                    Variables.getUser().equals("SMITH")) {*/
                            Variables.setPuerto("4044");
                            Variables.setBd("Remoto R");
                            Variables.setUrl("rptoscoreanos.myq-see.com");
                            //}
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
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("ListaClientes")) {
            GrupoCuentasXCobrar fragment2 = new GrupoCuentasXCobrar();
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("CuentasPorCobrar")) {
            ListaClientes fragment2 = new ListaClientes();
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("ConsultaListaPrecios")) {
            if(!Variables.getGruPK().equals("0")) {
                CuentasPorCobrar fragment2 = new CuentasPorCobrar();
                getSupportFragmentManager().beginTransaction()
                        .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                        .commit();
            }
            else{
                if (Variables.getEmailCliN().equals("")) {
                    ListaClientes fragment2 = new ListaClientes();
                    getSupportFragmentManager().beginTransaction()
                            .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                            .commit();
                }
                else{
                    Variables.setEmailCliN("");
                    ListaClientesNuevosFragment fragment2 = new ListaClientesNuevosFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                            .commit();
                }
            }
        }
        if (Variables.getTituloVentana().equals("ClientesNuevos")) {
            ListaClientesNuevosFragment fragment2 = new ListaClientesNuevosFragment();
            Variables.setGruPK("0");
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("ListaClientesNuevos")) {
            Principal fragment2 = new Principal();
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("CrearDVI")) {
            ProveedorDVIFragment fragment2 = new ProveedorDVIFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("ListaDetalleDVI")) {
            ProveedorDVIFragment fragment2 = new ProveedorDVIFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        if (Variables.getTituloVentana().equals("CrearDetalleDVI")) {
            ProveedorDVIFragment fragment2 = new ProveedorDVIFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(com.henryruiz.manejoalmacenmantis.R.id.content_frame, fragment2)
                    .commit();
        }
        // super.onBackPressed();
    }
}
