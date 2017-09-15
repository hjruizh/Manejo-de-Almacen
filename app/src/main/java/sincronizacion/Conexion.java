package sincronizacion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import tablas.CLI;
import tablas.CPA;
import tablas.CXC;
import tablas.DVI;
import tablas.FOTO;
import tablas.GCL;
import tablas.GRU;
import tablas.INV;
import tablas.MED;
import tablas.MSG;
import tablas.PRO;
import tablas.USR;
import tablas.VN1;

/**
 * Created by extre_000 on 05-06-2015.
 */
public class Conexion {
    Context mContext;
    String direccion = Variables.getDireccion();
    public Conexion(Context mContext) {
        this.mContext = mContext;

    }
    //verifica la conexion de wifi
    public boolean isOnline() {
        try {
            ConnectivityManager cm;
            cm = (ConnectivityManager)
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo tresG = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mWifi.isConnected() || tresG.isConnected())
                {
                    return true;
                }
            }
            else
            {
                return false;
            }
        } catch (Exception e) {

            Log.i("error conexion", "conex: " +e.getMessage());
            e.printStackTrace();

        }
        return false;

    }
    //Correlativos de factura cotizacion o pedido
    public String buscarUltimoCorrelativo(String tipo) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(tipo);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/correlativo");
            Log.i("buscado", tipo);
            Log.i("sin error", datos);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "Ultimo Correlativo");
        }
        return "";
    }
    //json parse tabla VN1
    private ArrayList<VN1> parseJSONdataVN1_busq(String data)
            throws JSONException {

        Log.i("data", data);
        ArrayList<VN1> prod = new ArrayList<VN1>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            VN1 producto = new VN1();
            //JSONObject userObj = jsonArray.getJSONObject(i);
            try{
                //String userStr = userObj.getString("singular");
                JSONObject item = jsonArray.getJSONObject(i);
                producto.setPk(item.getString("VN1_PK"));
                producto.setFecha(item.getString("VN1_FECHA").trim());
                producto.setNombre(item.getString("VN1_NOMBRE").trim());
            }
            catch (JSONException e) {

                Log.i("error", e.getMessage());
                //e.printStackTrace();
                producto.setMensaje(data.replace("\"",""));
            }
            prod.add(i, producto);
        }
        Log.i("prod", String.valueOf(prod.size()));
        return prod;
    }
    //Array tabla VN1
    public ArrayList<VN1> sincronizar_VN1() {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/InvenatrioGeneral");
                return parseJSONdataVN1_busq(datos);
            } else {
                Log.i("sin conexion", "Tabla VN1");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    //Jsong parse Inventario
    private ArrayList<INV> parseJSONdata_INV(String data)
            throws JSONException, UnsupportedEncodingException {

        Log.i("data", data);
        ArrayList<INV> prod = new ArrayList<INV>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            INV producto = new INV();
            producto.setPk(item.getInt("INV_PK"));
            producto.setCodigo(item.getString("INV_CODIGO").trim());
            producto.setNombre(new String(item.getString("INV_NOMBRE").getBytes("ISO-8859-1"), "UTF-8"));
            producto.setainPk(item.getString("AIN_PK").trim());
            producto.setalmacen(item.getString("AIN_ALMFK").trim());
            producto.setubi1(item.getString("AIN_UBI1").trim());
            producto.setubi2(item.getString("AIN_UBI2").trim());
            producto.setubi3(item.getString("AIN_UBI3").trim());
            producto.setubi4(item.getString("AIN_UBI4").trim());
            producto.setubi5(item.getString("AIN_UBI5").trim());
            producto.setubi6(item.getString("AIN_UBI6").trim());
            try{
                if (item.getString("CONTADOS").trim()!="null"){
                    producto.setcontados(item.getString("CONTADOS").trim());
                }
            }
            catch (JSONException e) {

                Log.i("error", e.getMessage());
                e.printStackTrace();
            }
            try{
                if (item.getString("EXISTENCIA_ACTUAL").trim()!="null") {
                    producto.setexistencia_actual(item.getString("EXISTENCIA_ACTUAL").trim());
                }
            }
            catch (JSONException e) {

                Log.i("error", e.getMessage());
                e.printStackTrace();
            }

            producto.setExistencia(Float.valueOf(item.getString(
                    "INV_EXISTENCIA").trim()));
            // producto.setPrecio(Float.valueOf(item.getString("PRECIO").trim()));
            if (!(item.getString("INV_FOTO").equals("")))
                producto.setFoto(Variables.getDireccion_fotos()
                        + item.getString("INV_FOTO").trim() + "&width=250");
            prod.add(i, producto);
            Log.i("msj", "pk " + item.getInt("INV_PK"));
            Log.i("foto", Variables.getDireccion_fotos() + item.getString("INV_FOTO").trim()
                    + "&width=250");

        }
        return prod;
    }
    //Buscar Inventario
    public ArrayList<INV> buscar_INV(String valor) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("valor");
                parametros.add(valor);
                parametros.add("user");
                parametros.add(Variables.getPk());
                if (!Variables.getInventario().equals("")){
                    parametros.add("valor_inv");
                    parametros.add(Variables.getInventario());
                }
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/buscar");
                Log.i("buscado", valor);
                Log.i("sin error", "Busqueda de Inventario");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "Busqueda de Inventario");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<INV> buscar_INV_GRU(String valor, String gru, String clase) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("valor");
                parametros.add(valor);
                parametros.add("user");
                parametros.add(Variables.getPk());
                parametros.add("clase");
                parametros.add(clase);
                if (!Variables.getInventario().equals("")){
                    parametros.add("valor_inv");
                    parametros.add(Variables.getInventario());
                }
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/ProductosGru/" + gru);
                Log.i("buscado", valor);
                Log.i("sin error", "Busqueda de Inventario");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "Busqueda de Inventario");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Buscar Inventario Pedido Cotizacion Factura
    public ArrayList<INV> sincronizar_INV_pedido(String valor,String campo, String bus) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("valor");
                parametros.add(valor);
                parametros.add("tipo");
                parametros.add(campo);
                parametros.add("busqueda");
                parametros.add(bus);
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/PedFacCot");
                Log.i("buscado", valor);
                Log.i("sin error", "-");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Buscar Inventario Pedido Cotizacion Factura
    public ArrayList<INV> sincronizar_INV_tarslado(String valor) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("valor");
                parametros.add(valor);
                parametros.add("user");
                parametros.add(Variables.getUser());
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/traslado");
                Log.i("buscado", valor);
                Log.i("sin error", "-");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Buscar Producto por PK
    public ArrayList<INV> sincronizar_INV_pk(String valor) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("valor");
                parametros.add(valor);
                parametros.add("user");
                parametros.add(Variables.getPk());
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/detalle");
                Log.i("buscado", valor);
                Log.i("sin error", "-");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Buscar Detalle de Productos por Compra
    public ArrayList<INV> sincronizar_INV_pk(String valor,String compra) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("compra");
                parametros.add(compra);
                parametros.add("buscar");
                parametros.add(valor);
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/CompraPk");
                Log.i("buscado", valor);
                Log.i("sin error", "-");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Guardar Foto en el Servidor
    public String enviar_foto(int pk, String foto) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("imp_invfk");
            parametros.add(String.valueOf(pk));
            parametros.add("foto");
            parametros.add(foto);
            String datos = post.getServerDataString(parametros, direccion
                    + "Inventario/Inv.aspx");

            Log.i("sin error", direccion + "Inventario/Inv.aspx");

            try {
                return (parseJSONdataActualizarAlm(datos));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "Error de conexión";
    }
    //Actualizar Almacen
    private String parseJSONdataActualizarAlm(String data) throws JSONException {

        Log.i("data", data);
        //ArrayList<PED> prod = new ArrayList<PED>();
        JSONArray jsonArray = new JSONArray(data);
        String mensaje = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            mensaje = item.getString("mensaje").trim();
        }
        return mensaje;
    }
    //Buscar Fotos
    private ArrayList<FOTO> parseJSONdataBuscarFoto(String data) throws JSONException {

        Log.i("data", data);
        ArrayList<FOTO> prod = new ArrayList<FOTO>();
        JSONArray jsonArray = new JSONArray(data);
        String mensaje = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            FOTO producto = new FOTO();
            producto.setNombre(item.getString("IMP_FOTO"));
            prod.add(i, producto);
        }
        return prod;
    }
    //Imprimri Etiquetas
    public String enviar_etiqueta(int pk, String tamano, String cant) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("pk");
            parametros.add(String.valueOf(pk));
            parametros.add("tamanio");
            parametros.add(tamano);
            parametros.add("cantidad");
            parametros.add(cant);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Imprimir");

            Log.i("sin error", direccion + "Inventario/Inv.aspx");
            return (datos.replace("\"",""));

        } else {
            Log.i("sin conexion", "Inventario/Inv.aspx");
        }
        return "Error de conexión";
    }
    //Actualizar Datos de Producto
    public String enviar_ubicacion(String pk, String ubi1, String ubi2,
                                   String ubi3, String ubi4, String ubi5, String ubi6) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("AIN_PK");
            parametros.add(pk);
            parametros.add("AIN_UBI1");
            parametros.add(ubi1);
            parametros.add("AIN_UBI2");
            parametros.add(ubi2);
            parametros.add("AIN_UBI3");
            parametros.add(ubi3);
            parametros.add("AIN_UBI4");
            parametros.add(ubi4);
            parametros.add("AIN_UBI5");
            parametros.add(ubi5);
            parametros.add("AIN_UBI6");
            parametros.add(ubi6);
            parametros.add("user");
            parametros.add(Variables.getPk());
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/ActUbi");

            Log.i("sin error", direccion + "Servicio.svc/ActUbi");
            Log.i("ain_pk", pk);
            return datos.replace("\"","");

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "Error de conexión nueva";
    }
    //Productos por compra
    public ArrayList<INV> sincronizar_INV_comp(String comp, String bus) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("compra");
                parametros.add(comp);
                parametros.add("buscar");
                parametros.add(bus);
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/Compra");

                Log.i("sin error", direccion + "Inventario/Inv.aspx");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "Productos Por Comppra");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Etiquetas por Compra
    public String enviar_etiqueta_comp(String comp, int pk, String impreso, String cant) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("compra");
            parametros.add(comp);
            parametros.add("buscar");
            parametros.add(String.valueOf(pk));
            parametros.add("impresos");
            parametros.add(impreso);
            parametros.add("cantidad");
            parametros.add(cant);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GuardarCompra");

            Log.i("sin error", direccion + "Inventario/Inv.aspx");

            return datos.replace("\"","");

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "Error de conexión";
    }
    //Inventario Mensual Diario y General
    public ArrayList<INV> sincronizar_INV_CDI(String fechaI, String fechaF, String buscar) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                if (fechaI!=null) {
                    parametros.add("fechai");
                    parametros.add(fechaI);
                }
                parametros.add("fechaf");
                parametros.add(fechaF);
                parametros.add("buscar");
                parametros.add(buscar);
                parametros.add("user");
                parametros.add(Variables.getPk());
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/Inventario");

                Log.i("sin error", direccion + "Servicio.svc/Inventario");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
    //Contados en Auditoria de Inventario
    public String enviar_conatdos(String pk, String contados,
                                  String existencia) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("VN1_INVFK");
            parametros.add(pk);
            parametros.add("NOMBRE");
            parametros.add(Variables.getAudi());
            parametros.add("CONTADOS");
            parametros.add(contados);
            parametros.add("EXISTENCIA");
            parametros.add(existencia);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GuardarInventario");

            Log.i("sin error", direccion + "Inventario/GuardarInventario");
            Log.i("ain_pk", Variables.getAudi());

            try {
                return (parseJSONdataActualizarAlm(datos));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "Error de conexión";
    }
    public String enviar_conatdos(String pk, String fecha, String contados,
                                  String existencia) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("VN1_INVFK");
            parametros.add(pk);
            parametros.add("VN1_FECHA");
            parametros.add(fecha);
            parametros.add("CONTADOS");
            parametros.add(contados);
            parametros.add("EXISTENCIA");
            parametros.add(existencia);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GuardarInventario");

            Log.i("enviar_conatdos", direccion + "Inventario/Inv.aspx");
            Log.i("fecha final", "fechaf "+fecha);
            Log.i("pk_inv", "vnm_invfk "+pk);
            return datos.replace("\"","");

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "Error de conexión";
    }
    public ArrayList<FOTO> sincronizar_Foto(String numero, String tipo) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            Log.i("Numero y tipo", "Numero " + numero + " tipo " + tipo);
            parametros.add("IMP_NUMERO");
            parametros.add(numero);
            parametros.add("IMP_TIPO");
            parametros.add(tipo);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Foto");

            Log.i("sin error", direccion + "Servicio.svc/Foto");
            try {
                return (parseJSONdataBuscarFoto(datos));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return null;
    }
    //Eliminar Foto
    public String eliminar_Foto(String tipo) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion +
                    "Servicio.svc/EliminarImagenFacPedCot/" + tipo);

            Log.i("sin error", direccion + "Servicio.svc/EliminarImagenFacPedCot/" + tipo);
            return datos.replace("\"","");

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "Error al eliminar la imagen";
    }
    //Eliminar Foto
    //Datos de Usuarios de Mantis
    private ArrayList<USR> parseJSONdataUser_busq(String data)
            throws JSONException {

        Log.i("data", data);
        ArrayList<USR> usr = new ArrayList<USR>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            Log.i("item", "-" + item);
            USR usuario = new USR();
            usuario.setPk(Integer.parseInt(item.getString("USR_PK")));
            usuario.setAlias(item.getString("USR_LANID").trim());
            usr.add(i, usuario);
        }
        Log.i("prod", String.valueOf(usr.size()));
        return usr;
    }
    public ArrayList<USR> sincronizar_User() {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/User");
                Log.i("data", "datos" + datos);
                return  parseJSONdataUser_busq(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    //Datos de Usuarios de Mantis
    //Buscar Mensajes
    private ArrayList<MSG> parseJSONdataMsg_busq(String data)
            throws JSONException {

        //Log.i("data", data);
        ArrayList<MSG> usr = new ArrayList<MSG>();
        JSONObject jsonObj = new JSONObject(data);
        String strData = jsonObj.getString("plural");
        JSONArray jsonArray = new JSONArray(strData);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userObj = jsonArray.getJSONObject(i);
            String userStr = userObj.getString("singular");
            JSONObject item = new JSONObject(userStr);
            MSG mensaje = new MSG();
            mensaje.set_codigo(item.getString("MSG_INVCODIGO"));
            mensaje.set_pk(item.getString("MSG_PK"));
            usr.add(i, mensaje);
        }
        Log.i("prod", String.valueOf(usr.size()));
        return usr;
    }
    public ArrayList<MSG> sincronizar_Mensaje() {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("pk_usuario");
                parametros.add(Variables.getPk());
                String datos = post.getServerDataString(parametros, direccion
                        + "Users/Chat.aspx");
                return  parseJSONdataMsg_busq(datos);
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    private String parseJSONdataMsg_veri(String data)
            throws JSONException {

        JSONObject jsonObj = new JSONObject(data);
        String strData = jsonObj.getString("plural");
        JSONArray jsonArray = new JSONArray(strData);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userObj = jsonArray.getJSONObject(i);
            String userStr = userObj.getString("singular");
            JSONObject item = new JSONObject(userStr);
            return item.getString("mensaje").trim();
        }
        return "";
    }
    public String  buscar_Mensaje() {
        try {
            if (isOnline()) {
                if (Variables.getMensaje_pk()!=""){
                    ArrayList parametros = new ArrayList();
                    Post post = new Post();
                    parametros.add("pk_mensaje");
                    parametros.add(Variables.getMensaje_pk());
                    String datos = post.getServerDataString(parametros, direccion
                            + "Users/Chat.aspx");
                    return parseJSONdataMsg_veri(datos);
                }
            } else {
                Log.i("sin conexion", "-");
            }
        } catch (JSONException e) {

            Log.i("error", e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    //Buscar Mensajes
    //Actualizar Estado del Mensaje
    public void sincronizar_Mensaje_guardar(String estado, String mensaje) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("pk_mensaje");
            parametros.add(mensaje);
            parametros.add("leido");
            parametros.add(estado);
            String datos = post.getServerDataString(parametros, direccion
                    + "Users/Chat.aspx");
            //Log.i("sincronizar_Mensaje_guardar","Estado: " + estado + datos);
        } else {
            Log.i("sin conexion", "-");
        }
    }
    //Actualizar Estado del Mensaje
    //Sincronizar Grupos clientes cuentas por cobrar
    private ArrayList<GCL> parseJSONdataGrupoCli(String data)
            throws JSONException {
        ArrayList<GCL> gru = new ArrayList<GCL>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            GCL grupo = new GCL();
            grupo.setPk(Integer.parseInt(item.getString("GCL_PK")));
            grupo.setCodigo(item.getString("GCL_CODIGO").trim());
            grupo.setNombre(item.getString("GCL_NOMBRE").trim());
            gru.add(i, grupo);
        }
        return gru;
    }
    public ArrayList<GCL> sincronizar_GRUPO_cli() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GrupoxCliente");
            try {
                return parseJSONdataGrupoCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<GCL>();
    }
    //Sincronizar Grupos clientes cuentas por cobrar
    //Sincronizar Clientes
    private ArrayList<CLI> parseJSONdataCli(String data)
            throws JSONException {
        ArrayList<CLI> cli = new ArrayList<CLI>();
        JSONArray jsonArray = new JSONArray(data);
        String pk = "";
        int j = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            CLI cliente = new CLI();
            if(!pk.equals(item.getString("CLI_PK").trim())) {
                cliente.setPk(Integer.parseInt(item.getString("CLI_PK")));
                cliente.setCodigo(item.getString("CLI_CODIGO").trim());
                cliente.setNombre(item.getString("CLI_NOMBRE").trim());
                if (!Variables.getGruPK().equals("0"))
                    cliente.setSaldo(item.getString("CLI_SALDO").trim());
                else
                    try {
                        cliente.setEmail(item.getString("CLI_EMAIL").trim());
                    }
                    catch (Exception x){}
                cli.add(j, cliente);
                j++;
                pk = item.getString("CLI_PK").trim();
            }
            else{
                cli.get(j-1).setSaldo(Float.toString(Float.parseFloat(cli.get(j-1).getSaldo().replace(",",".")) + Float.parseFloat(item.getString("CLI_SALDO").trim().replace(",","."))));
            }
        }
        return cli;
    }
    public ArrayList<CLI> sincronizar_cli(String pk) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Cliente/" + pk);
            Log.i("sin conexion", direccion
                    + "Servicio.svc/Cliente/" + pk + datos);
            try {
                return parseJSONdataCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CLI>();
    }
    public ArrayList<CLI> sincronizar_cli(String pk, String buscar) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(buscar);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Cliente/" + pk);
            try {
                return parseJSONdataCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CLI>();
    }

    public ArrayList<CLI> sincronizar_cli_nuevos(String pk) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/ClienteN/" + pk);
            Log.i("sin conexion", direccion
                    + "Servicio.svc/ClienteN/" + pk + datos);
            try {
                return parseJSONdataCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CLI>();
    }
    public ArrayList<CLI> sincronizar_cli_nuevos(String pk, String buscar) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(buscar);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/ClienteN/" + pk);
            try {
                return parseJSONdataCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CLI>();
    }

    public String guardar_cli_nuevo(String rif,String nom,
                                    String tel,String dir,
                                    String obs,String mail) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("CLN_RIF");
            parametros.add(rif);
            parametros.add("CLN_NOMBRE");
            parametros.add(nom);
            parametros.add("CLN_GCLFK");
            parametros.add(Variables.getGruPK());
            parametros.add("CLN_TEL");
            parametros.add(tel);
            parametros.add("CLN_DIR");
            parametros.add(dir);
            parametros.add("CLN_OBS");
            parametros.add(obs);
            parametros.add("CLN_EMAIL");
            parametros.add(mail);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GuardarNuevoCliente");
            Log.i("sin conexion", direccion
                    + "Servicio.svc/GuardarNuevoCliente" + datos);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "-");
        }
        return "";
    }
    //Sincronizar Clientes
    //Detalle cuentas por cobrar
    private ArrayList<CXC> parseJSONdataCxc(String data)
            throws JSONException {
        ArrayList<CXC> cxc = new ArrayList<CXC>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            CXC cuantasxcobrar = new CXC();
            cuantasxcobrar.setPk(Integer.parseInt(item.getString("CXC_PK")));
            cuantasxcobrar.setCliPk(Integer.parseInt(item.getString("CXC_CLIPK")));
            cuantasxcobrar.setCodigo(item.getString("CXC_CODIGO").trim());
            cuantasxcobrar.setNombre(item.getString("CXC_NOMBRE").trim());
            cuantasxcobrar.setEmail(item.getString("CXC_EMAIL").trim());
            cuantasxcobrar.setSaldo(item.getString("CXC_SALDO").trim());
            cuantasxcobrar.setFecha(item.getString("CXC_FECHA").trim());
            cuantasxcobrar.setFactura(item.getString("CXC_FACTURA").trim());
            cuantasxcobrar.setGrupo(item.getString("CXC_GCLFK").trim());
            cuantasxcobrar.setVence(item.getString("CXC_VENCE").trim());
            cxc.add(i, cuantasxcobrar);
        }
        return cxc;
    }
    public ArrayList<CXC> sincronizar_CXC() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/CXC/" + Variables.getCliPk());
            try {
                return parseJSONdataCxc(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CXC>();
    }
    //Detalle cuentas por cobrar
    //Enviar pago
    public String enviar_pago(int pk, String saldo, String mensaje) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("CPA_CLIFK");
            parametros.add(String.valueOf(pk));
            parametros.add("CPA_MENSAJE");
            parametros.add(mensaje);
            parametros.add("CPA_MONTO");
            parametros.add(saldo);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/CuentasPagadas");
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "-");
        }
        return "";
    }
    //Enviar pago
    //Grupo Cuentas Pagadas
    public ArrayList<GCL> sincronizar_GRUPO_cpa() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GrupoxClienteCPA");
            try {
                return parseJSONdataGrupoCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<GCL>();
    }
    public ArrayList<CLI> sincronizar_cli_cpa(String pk) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/ClienteCPA/" + pk);
            try {
                return parseJSONdataCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CLI>();
    }
    public ArrayList<CLI> sincronizar_cli_cpa(String pk, String buscar) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(buscar);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/ClienteCPA/" + pk);
            try {
                return parseJSONdataCli(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CLI>();
    }

    private ArrayList<CPA> parseJSONdataCpa(String data)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<CPA> cpa = new ArrayList<CPA>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            CPA cuentaspagadas = new CPA();
            cuentaspagadas.setCPA_PK(item.getString("CPA_PK"));
            cuentaspagadas.setCPA_CLIFK(item.getString("CPA_CLIFK"));
            cuentaspagadas.setCPA_FECHA(item.getString("CPA_FECHA").trim());
            cuentaspagadas.setCPA_FOTO(item.getString("CPA_FOTO").trim());
            cuentaspagadas.setCPA_MENSAJE(new String(item.getString("CPA_MENSAJE").getBytes("ISO-8859-1"), "UTF-8"));
            cuentaspagadas.setCPA_MONTO(item.getString("CPA_MONTO").trim());
            cuentaspagadas.setCPA_NOMBRE(item.getString("CPA_NOMBRE").trim());
            cuentaspagadas.setCPA_RIF(item.getString("CPA_RIF").trim());
            cpa.add(i, cuentaspagadas);
        }
        return cpa;
    }

    public ArrayList<CPA> sincronizar_CPA() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/DetalleCPA/" + Variables.getCliPk());
            try {
                return parseJSONdataCpa(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<CPA>();
    }
    //Grupo Cuentas Pagadas
    //Enviar cuentas pagadas
    public String enviar_CPA(String pk) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/EnviarCPA/" + pk);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "-");
        }
        return "";
    }
    //Enviar cuentas pagadas
    // Eliminar cuentas pagadas
    public String eliminar_CPA(String pk) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/EliminarCPA/" + pk);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "-");
        }
        return "";
    }
    //Eliminar cuentas pagadas
    //Enviar cuentas por cobrar
    public String enviar_CXC(String pk) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/EnviarCXC/" + pk);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "-");
        }
        return "";
    }
    //Enviar cuentas por cobrar

    //Detalle Grupo de productos
    private ArrayList<GRU> parseJSONdataGru(String data)
            throws JSONException {
        ArrayList<GRU> cxc = new ArrayList<GRU>();
        JSONArray jsonArray = new JSONArray(data);
        GRU temp = new GRU();
        temp.setNombre("Seleccione una marca");
        temp.setPk(0);
        cxc.add(0,temp);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            GRU cuantasxcobrar = new GRU();
            cuantasxcobrar.setPk(Integer.parseInt(item.getString("GRU_PK")));
            cuantasxcobrar.setNombre(item.getString("GRU_NOMBRE").trim());
            cxc.add(i+1, cuantasxcobrar);
        }
        return cxc;
    }
    public ArrayList<GRU> sincronizar_GRU() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Grupos");
            try {
                Log.i("Grupos", datos);
                    return parseJSONdataGru(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "-");
        }
        return new ArrayList<GRU>();
    }
    //Detalle grupo de productos

    //Enviar lista de precios
    public String enviar_ListaPrecios(String valor, String gru, String porcen, String email, String clase) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(valor);
            parametros.add("pocentaje");
            parametros.add(porcen);
            parametros.add("cliente");
            if (email.equals(""))
                parametros.add(Variables.getCliPk());
            else
                parametros.add(email);
            parametros.add("asunto");
            parametros.add(Variables.getAsunto());
            parametros.add("msg");
            parametros.add(Variables.getMsg());
            parametros.add("user");
            parametros.add(Variables.getPk());
            parametros.add("clase");
            parametros.add(clase);
            if (Variables.getMasVendido()){
                parametros.add("masVendido");
                parametros.add("True");
            }
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/EnviarCorreo/" + gru);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "-");
        }
        return "";
    }
    //Enviar lista de precios
    //Sincronizar Proveedores DVI
    private ArrayList<PRO> parseJSONdataPro(String data)
            throws JSONException {
        ArrayList<PRO> gru = new ArrayList<PRO>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            PRO grupo = new PRO();
            grupo.setPk(Integer.parseInt(item.getString("PRO_PK")));
            grupo.setCodigo(item.getString("PRO_CODIGO").trim());
            grupo.setNombre(item.getString("PRO_NOMBRE").trim());
            grupo.setEmail(item.getString("PRO_EMAIL").trim());
            grupo.setSaldoB(item.getString("PRO_SALDOB").trim());
            grupo.setSaldoD(item.getString("PRO_SALDOD").trim());
            grupo.setDviFk(item.getString("PRO_DVIPK").trim());
            grupo.setPagado(item.getString("PRO_PAGADO").trim());
            gru.add(i, grupo);
        }
        return gru;
    }
    public ArrayList<PRO> sincronizar_Pro() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/ProveedoresDVI");
            Log.i("sin conexion", "Servicio.svc/ProveedoresDVI" + datos);
            try {
                return parseJSONdataPro(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "Servicio.svc/ProveedoresDVI");
        }
        return new ArrayList<PRO>();
    }
    public ArrayList<PRO> sincronizar_ProAll() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Proveedores");
            Log.i("sin conexion", "Servicio.svc/Proveedores" + datos);
            try {
                return parseJSONdataPro(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "Servicio.svc/Proveedores");
        }
        return new ArrayList<PRO>();
    }
    //Sincronizar Proveedores DVI
    //Guardar DVI
    public String save_DVI(String id, String montod, String monotb) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("DVI_PROFK");
            parametros.add(id);
            parametros.add("DVI_MONTOD");
            parametros.add(montod);
            parametros.add("DVI_MONTOB");
            parametros.add(monotb);
            if(!Variables.getIdDVI().equals("")){
                parametros.add("DVI_PK");
                parametros.add(Variables.getIdDVI());
            }
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/SetDivisas");
            Log.i("sin conexion", "Servicio.svc/SetDivisas" + datos);
            String[] temp = datos.replace("\"","").split("-");
            Variables.setIdDVI(temp[1].replace("\n",""));
            return temp[0];
        } else {
            Log.i("sin conexion", "Servicio.svc/SetDivisas");
            return "Sin conexion a la red";
        }
    }
    //Guardar DVI
    //Sincronizar Detalle DVI
    private ArrayList<MED> parseJSONdataDetalleDVI(String data)
            throws JSONException {
        ArrayList<MED> gru = new ArrayList<MED>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            MED grupo = new MED();
            grupo.setMED_PK(item.getString("MED_PK"));
            grupo.setMED_CLIFK(item.getString("MED_CLIFK").trim());
            grupo.setMED_DVIFK(item.getString("MED_DVIFK").trim());
            grupo.setMED_FACTURA(item.getString("MED_FACTURA").trim());
            grupo.setMED_FECHA(item.getString("MED_FECHA").trim());
            grupo.setMED_FOTO(item.getString("MED_FOTO").trim());
            grupo.setMED_MONTO(item.getString("MED_MONTO").trim());
            grupo.setMED_REFERENCIA(item.getString("MED_REFERENCIA").trim());
            gru.add(i, grupo);
        }
        return gru;
    }
    public ArrayList<MED> sincronizar_DetalleDVI(String id, String detalle) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/GetDivisasDetalle/" + id + "/" + detalle);
            Log.i("sin conexion", "Servicio.svc/GetDivisasDetalle/"+ id + datos);
            try {
                return parseJSONdataDetalleDVI(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "Servicio.svc/GetDivisasDetalle/"+ id);
        }
        return new ArrayList<MED>();
    }
    //Sincronizar Proveedores DVI
    //Sincronizar DVI
    private ArrayList<DVI> parseJSONdataDVI(String data)
            throws JSONException {
        ArrayList<DVI> gru = new ArrayList<DVI>();
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            DVI grupo = new DVI();
            grupo.setDVI_PK(item.getString("DVI_PK"));
            grupo.setDVI_FECHA(item.getString("DVI_FECHA").trim());
            grupo.setDVI_MONTOB(item.getString("DVI_MONTOB").trim());
            grupo.setDVI_MONTOD(item.getString("DVI_MONTOD").trim());
            grupo.setDVI_PROFK(item.getString("DVI_PROFK").trim());
            grupo.setDVI_FOTO(item.getString("DVI_FOTO").trim());
            gru.add(i, grupo);
        }
        return gru;
    }
    public ArrayList<DVI> sincronizar_DVI(String id) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/DivisasPC/"+ id);
            Log.i("sin conexion", "Servicio.svc/DivisasPC/"+ id + datos);
            try {
                return parseJSONdataDVI(datos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("sin conexion", "Servicio.svc/DivisasPC/"+ id);
        }
        return new ArrayList<DVI>();
    }
    //Sincronizar DVI
    //Guardar Detalle DVI
    public String save_DetalleDVI(String id, String idPro, String monto, String ref, String obs, String fecha) throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("MED_CLIFK");
            parametros.add(idPro);
            parametros.add("MED_MONTO");
            parametros.add(monto);
            parametros.add("MED_REFERENCIA");
            parametros.add(ref);
            parametros.add("MED_FACTURA");
            parametros.add(obs);
            parametros.add("MED_FECHA");
            parametros.add(fecha);
            parametros.add("MED_DVIFK");
            parametros.add(id);
            if(!Variables.getIdDetalleDVI().equals("")){
                parametros.add("MED_PK");
                parametros.add(Variables.getIdDetalleDVI());
            }
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/SetDivisasPagos");
            Log.i("sin conexion", "Servicio.svc/SetDivisasPagos" + datos);
            String[] temp = datos.replace("\"","").split("-");
            Variables.setIdDetalleDVI(temp[1].replace("\n",""));
            return temp[0];
        } else {
            Log.i("sin conexion", "Servicio.svc/SetDivisas");
            return "Sin conexion a la red";
        }
    }
    //Guardar DVI
    //Correo Detalle DVI
    public String save_CorreoDVI() throws JSONException {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/CorreoDVI/" + Variables.getIdDVI());
            Log.i("sin conexion", "Servicio.svc/CorreoDVI/" + Variables.getIdDVI() + datos);
            return datos.replace("\"","");
        } else {
            Log.i("sin conexion", "Servicio.svc/CorreoDVI/" + Variables.getIdDVI());
            return "Sin conexion a la red";
        }
    }
    //Verificar Usuario de traslado
    public String get_user_traslado(String numero) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(numero);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/trasladoAutorizado");

            Log.i("sin error", direccion + "Inventario/Inv.aspx");
            return (datos.replace("\"",""));

        } else {
            Log.i("sin conexion", "Inventario/Inv.aspx");
        }
        return "Error de conexión";
    }
    //Set Usuario de traslado
    public String set_user_traslado(String numero, String user) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("valor");
            parametros.add(numero);
            parametros.add("user");
            parametros.add(user);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/trasladoSetAutorizado");

            Log.i("sin error", direccion + "Inventario/Inv.aspx");
            return (datos.replace("\"",""));

        } else {
            Log.i("sin conexion", "Inventario/Inv.aspx");
        }
        return "Error de conexión";
    }
}
