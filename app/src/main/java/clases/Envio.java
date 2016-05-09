package clases;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import sincronizacion.Conexion;
import sincronizacion.Post;
import sincronizacion.Variables;

//import com.android.dataframework.DataFramework;
//import com.android.dataframework.Entity;

public class Envio extends Conexion {

	private int proxpk;

	public Envio(Context mContext) {
		super(mContext);

	}



	public String doFileUpload(String pk, String archivo, String dir) {

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String existingFileName = "";
		String datos="";
		if (dir.equals("")) {
			existingFileName = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/DCIM/Camera/" + archivo;
		}
		else {
			existingFileName = dir;
		}
		if (isOnline()) {
			ArrayList par = new ArrayList();
			Post post = new Post();
			par.add("pk");
			par.add("");
			datos = post.getServerDataStringImagen(par, existingFileName, Variables.getDireccion()
					+ "Servicio.svc/Imagen/"+archivo+"/"+pk);

			return (datos.replace("\"",""));

		} else {
			Log.i("sin conexion", "Inventario/Inv.aspx");
		}
		return datos;
		/*String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String responseFromServer = "";
		String urlString = "";
		if (!dir.equals(""))
			urlString = "http://192.168.1.250:8080/catalogo_smith/imagen/catalogo/foto/upload.php?nombreImagen="+archivo;
		else
			urlString = "http://192.168.1.250:8080/catalogo_smith/imagen/catalogo/foto/upload.php?nombreImagen=";
		/*urlString = Variables.getDireccion()
				+ "Servicio.svc/Imagen/"+archivo;*
		try {

			// ------------------ CLIENT REQUEST
			FileInputStream fileInputStream = new FileInputStream(new File(
					existingFileName));
			// open a URL connection to the Servlet
			URL url = new URL(urlString);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ existingFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {

				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// close streams
			Log.e("Debug", "File is written");
			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		} catch (IOException ioe) {
			Log.e("Debug", "error: " + ioe.getMessage(), ioe);
		}

		// ------------------ read the SERVER RESPONSE
		try {

			inStream = new DataInputStream(conn.getInputStream());
			String str;
			String mensaje = "";
			while ((str = inStream.readLine()) != null) {

				Log.e("Debug", "Server Response " + str);
				mensaje += str;
			}

			inStream.close();
			return mensaje;

		} catch (IOException ioex) {
			Log.e("Debug", "error: " + ioex.getMessage(), ioex);
			return ioex.getLocalizedMessage();
		}*/
	}
	public String doFileUpload(String archivo,String tipo, String numero,Boolean eliminar, String dir) {

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String existingFileName = "";
		String datos="";
		if (dir.equals(""))
			existingFileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DCIM/Camera/" + archivo;
		else{
			existingFileName = dir;
		}
		if (isOnline()) {
			ArrayList par = new ArrayList();
			Post post = new Post();
			par.add("pk");
			par.add("");
			datos = post.getServerDataStringImagen(par, existingFileName, Variables.getDireccion()
					+ "Servicio.svc/ImagenFacPedCot/"+tipo+"/"+numero);

			return (datos.replace("\"",""));

		} else {
			Log.i("sin conexion", "Inventario/Inv.aspx");
		}
		return datos;
		/*String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String responseFromServer = "";
		String urlString = "";
		if (eliminar)
			urlString = "http://192.168.1.250:8080/catalogo_smith/imagen/catalogo/foto/delete.php?foto="+tipo;
		else
			urlString = "http://192.168.1.250:8080/catalogo_smith/imagen/catalogo/foto/upload.php?tipo="+tipo+"&numero="+numero;
		//String urlString = "http://192.168.1.120:3030/odbc/update.php?tipo="+tipo+"&numero="+numero;
		try {

			// ------------------ CLIENT REQUEST
			FileInputStream fileInputStream = new FileInputStream(new File(
					existingFileName));
			// open a URL connection to the Servlet
			URL url = new URL(urlString);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ existingFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {

				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// close streams
			Log.e("Debug", "File is written");
			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException ex) {
			Log.e("Debug", "error1: " + ex.getMessage(), ex);
		} catch (IOException ioe) {
			Log.e("Debug", "error2: " + ioe.getMessage(), ioe);
		}

		// ------------------ read the SERVER RESPONSE
		String foto = "";
		try {

			inStream = new DataInputStream(conn.getInputStream());
			String str = "";
			int i = 0;
			while ((str = inStream.readLine()) != null) {

				Log.e("Debug", "Server Response 3 " + str);
				if (i==0)
					foto = str;
				i++;
			}
			inStream.close();
			

		} catch (IOException ioex) {
			Log.e("Debug", "error4: " + ioex.getMessage(), ioex);
		}
		Log.i("foto", foto);
		return foto;*/
	}
}
