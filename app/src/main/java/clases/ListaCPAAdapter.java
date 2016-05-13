package clases;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.henryruiz.manejoalmacenmantis.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import sincronizacion.Variables;
import tablas.CPA;

/**
 * Created by extre_000 on 13/5/2016.
 */
public class ListaCPAAdapter extends BaseAdapter {
    private Context context;
    private List<CPA> items;

    public ListaCPAAdapter(Context context, List<CPA> items) {
        //super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class Fila
    {
        TextView msg;
        TextView fecha;
        TextView saldo;
        ImageView foto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        Fila view;
        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CPA item=items.get(position);
        Log.i("item","posicion "+position+" tam: "+getCount()+ " Nombre ");
        if (convertView == null) {
            view = new Fila();
            convertView = inflator.inflate(R.layout.layout_cpa, null);
            view.fecha = (TextView) convertView.findViewById(R.id.textViewFecha);
            view.saldo = (TextView) convertView.findViewById(R.id.textViewSaldo);
            view.msg = (TextView) convertView.findViewById(R.id.textViewMsg);
            view.foto = (ImageView) convertView.findViewById(R.id.imageViewFoto);
            view.fecha.setText("Fecha: " + item.getCPA_FECHA());
            view.saldo.setText("Monto Pagado: " + item.getCPA_MONTO());
            view.msg.setText(item.getCPA_MENSAJE());
            ImageLoader.getInstance().init(Variables.cargarImagen(context));
            ImageLoader.getInstance().displayImage(Variables.getDireccion_fotos() + "cpa/" + item.getCPA_FOTO() + "&width=250", view.foto);
            convertView.setTag(view);

        } else {
            view = (Fila) convertView.getTag();
            view.fecha = (TextView) convertView.findViewById(R.id.textViewFecha);
            view.saldo = (TextView) convertView.findViewById(R.id.textViewSaldo);
            view.msg = (TextView) convertView.findViewById(R.id.textViewMsg);
            view.foto = (ImageView) convertView.findViewById(R.id.imageViewFoto);
            view.fecha.setText("Fecha: " + item.getCPA_FECHA());
            view.saldo.setText("Monto Pagado: " + item.getCPA_MONTO());
            view.msg.setText(item.getCPA_MENSAJE());
            ImageLoader.getInstance().init(Variables.cargarImagen(context));
            ImageLoader.getInstance().displayImage(Variables.getDireccion_fotos() + "cpa/" + item.getCPA_FOTO() + "&width=250", view.foto);
        }
        return convertView;
    }


}
