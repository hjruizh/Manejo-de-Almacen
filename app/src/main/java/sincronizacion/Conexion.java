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

import tablas.FOTO;
import tablas.INV;
import tablas.MSG;
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
                Log.i("sin error", "inv_busq2.php");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "inv_busq2.php");
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
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/detalle");
                Log.i("buscado", valor);
                Log.i("sin error", "inv_busq2.php");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "inv_busq2.php");
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
                Log.i("sin error", "inv_busq2.php");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "inv_busq2.php");
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
            parametros.add("inv_pk");
            parametros.add(String.valueOf(pk));
            parametros.add("impresos");
            parametros.add(impreso);
            parametros.add("cantidad");
            parametros.add(cant);
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
    //Inventario Mensual Diario y General
    public ArrayList<INV> sincronizar_INV_CDI(String fechaI, String fechaF, String buscar) {
        try {
            if (isOnline()) {
                ArrayList parametros = new ArrayList();
                Post post = new Post();
                parametros.add("fechai");
                parametros.add(fechaI);
                parametros.add("fechaf");
                parametros.add(fechaF);
                parametros.add("buscar");
                parametros.add(buscar);
                String datos = post.getServerDataString(parametros, direccion
                        + "Servicio.svc/Inventario");

                Log.i("sin error", direccion + "Inventario/Inventario");
                return parseJSONdata_INV(datos);
            } else {
                Log.i("sin conexion", "inv_busq.php");
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
            parametros.add("vnm_invfk");
            parametros.add(pk);
            parametros.add("inventario");
            parametros.add(Variables.getAudi());
            parametros.add("contados");
            parametros.add(contados);
            parametros.add("existencia");
            parametros.add(existencia);
            String datos = post.getServerDataString(parametros, direccion
                    + "Servicio.svc/Inv.aspx");

            Log.i("sin error", direccion + "Inventario/Inv.aspx");
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
    public String eliminar_Foto(String foto) {
        if (isOnline()) {
            ArrayList parametros = new ArrayList();
            Post post = new Post();
            parametros.add("foto");
            parametros.add("foto");
            String datos = post.getServerDataString(parametros,
                    "http://192.168.1.250:8080/catalogo_smith/imagen/catalogo/foto/delete.php?foto="+foto);

            Log.i("sin error",  "http://192.168.1.250:8080/catalogo_smith/imagen/catalogo/foto/delete.php?foto="+foto);
            return datos;

        } else {
            Log.i("sin conexion", "usr_busq.php");
        }
        return "";
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
                Log.i("sin conexion", "inv_busq2.php");
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
                Log.i("sin conexion", "inv_busq2.php");
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
                Log.i("sin conexion", "inv_busq2.php");
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
            Log.i("sin conexion", "inv_busq2.php");
        }
    }
    //Actualizar Estado del Mensaje
}
