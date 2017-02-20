package servicio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.henryruiz.manejoalmacenmantis.ListadoDeInventario;
import com.henryruiz.manejoalmacenmantis.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sincronizacion.Conexion;
import sincronizacion.Variables;
import tablas.MSG;

public class Servicio extends Service {
    
	private Timer timer;
	SharedPreferences settings;
	String codigo = "";
	String veri = "";
    static ArrayList<MSG> mensaje = new ArrayList<MSG>();
	Conexion s = new Conexion(this);
    
    @Override
    public void onCreate() {
          Toast.makeText(this, "Servicio creado",
				  Toast.LENGTH_SHORT).show();
          timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
          Toast.makeText(this, "Servicio arrancado " + idArranque,
				  Toast.LENGTH_SHORT).show();
          //reproductor.start();
          timer.scheduleAtFixedRate(new TimerTask()
	       {
	           @Override
	           public void run() 
	           {
	        	   settings = getSharedPreferences("perfil", MODE_PRIVATE);
	     	       codigo = settings.getString("codigo", null);
	        	   handler.sendEmptyMessage(0);
	           }
	       }, 0, 500);
          return START_STICKY;
    }
    
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			new MiTarea().execute("");
		}
	};

    @Override
    public void onDestroy() {
          Toast.makeText(this, "Servicio detenido",
				  Toast.LENGTH_SHORT).show();
          
    }

    @Override
    public IBinder onBind(Intent intencion) {
          return null;
    }
    
    private void notificacion(){
    	if (codigo!=null && codigo!=""){
	    	String ns = Context.NOTIFICATION_SERVICE;
	        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	        int icon = R.drawable.logo_g;
	        CharSequence tickerText = "Nuevo Producto Asignado Cod. "+ codigo;
	        long when = System.currentTimeMillis();
	
	        final Notification notification = new Notification(icon, tickerText, when);
	        
	        final Context context = getApplicationContext();
	        final CharSequence contentTitle = "Nuevo Producto";
	        final CharSequence contentText = "Nuevo Producto Asignado Cod. "+ codigo;
	        /*Intent notificationIntent = new Intent(this, ListadoDeInventario.class);
	        //notificationIntent.putExtra("msg","true");
	        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);*/
			//notification.setContentIntent(contentIntent);
			PendingIntent intencionPendiente = PendingIntent.getActivity(
					this, 0, new Intent(this, ListadoDeInventario.class), 0);

			/*notification.setLatestEventInfo(this, contentTitle,
					contentText, intencionPendiente);*/
	        //Le a?ade sonido
			notification.defaults |= Notification.DEFAULT_SOUND;
			//Le a?ade vibraci?n
			notification.defaults |= Notification.DEFAULT_VIBRATE;

			//Le a?ade luz mediante LED
			notification.defaults |= Notification.DEFAULT_LIGHTS;

			//La notificaci?n se detendr? cuando el usuario pulse en ella
			notification.flags = Notification.FLAG_AUTO_CANCEL;

			// Insistente
			// Son tan intrusivas que han decidido no a?adir un m?todo para esto en el Builder
			// Se hace igual para todos los niveles de API
			notification.flags = notification.flags | Notification.FLAG_INSISTENT;
			 
			// En curso
			// -- API 10 o menor
			notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
			// -- API 11 o mayor
			//mNotificationManager.setOngoing(true);
	        
	        final int HELLO_ID = 1;
	
	        mNotificationManager.notify(HELLO_ID, notification);
    	}
    }
    
    private class MiTarea extends AsyncTask<String, Float, Integer> {
		//ProgressDialog dialog;

		protected void onPreExecute() { // Mostramos antes de comenzar
			//dialog = ProgressDialog.show(Servicio.this, "", "Cargando...",
					//true);
		}

		protected Integer doInBackground(String... params) {
			if (verificar_internet()) {
				try {

					//String nivel = "1";

					//publishProgress();
					if ((codigo==null || codigo == "") && Variables.getPk()!=""){
						mensaje = s.sincronizar_Mensaje();
					}
					if ((codigo!=null && codigo != "") && Variables.getPk()!=""){
						veri = s.buscar_Mensaje();
					}

				} catch (Exception e) {

					//e.printStackTrace();
					return 0;
				}
			}
			return 1;
		}
		
		public boolean isOnline() {
			try {
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				return cm.getActiveNetworkInfo().isConnectedOrConnecting();
			} catch (Exception e) {
				//Log.i("error conexion", "conex: " + e.getMessage());
				e.printStackTrace();
				return false;
			}

		}
		
		public boolean verificar_internet() {
			if (!isOnline()) {
				Toast.makeText(Servicio.this, "No posee conexion a internet",
						Toast.LENGTH_SHORT).show();
				return false;
			} else
				return true;

		}

		protected void onProgressUpdate(Float... valores) {
			if (!verificar_internet()) {
				//dialog.dismiss();
			}
		}

		protected void onPostExecute(Integer bytes) {
			//dialog.dismiss();
			try{
				//Log.i("Codigo 1","- "+codigo);
				if ((bytes == 1) && (mensaje != null) && (codigo==null || codigo == "") && Variables.getPk()!="") {
					if (codigo != mensaje.get(0).get_codigo()){
						SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
		          		SharedPreferences.Editor editor = settings.edit();
		          		editor.putString("codigo", String.valueOf(mensaje.get(0).get_codigo()));
		          		editor.putString("pk_mensaje", String.valueOf(mensaje.get(0).get_pk()));
		        		editor.commit();
		        		Variables.setMensaje_pk(mensaje.get(0).get_pk());
		        		codigo = String.valueOf(mensaje.get(0).get_codigo());
		        		notificacion();
					}
				}
				if (veri.trim().equals("False")){
					SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.remove("codigo");
					editor.remove("pk_mensaje");
					editor.commit();
					Variables.setMensaje_pk("");
					veri = "";
					codigo = "";
					//Log.i("codigo verificar", "Borrar");
				}
			}catch (Exception e) {
				Toast.makeText(Servicio.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}

	}
    
}
