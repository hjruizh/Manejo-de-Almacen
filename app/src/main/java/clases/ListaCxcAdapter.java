package clases;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.henryruiz.manejoalmacenmantis.R;

import java.util.List;

import tablas.CXC;

/**
 * Created by extre_000 on 20/4/2016.
 */
public class ListaCxcAdapter extends BaseAdapter {
    private Context context;
    private List<CXC> items;

    public ListaCxcAdapter(Context context, List<CXC> items) {
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
        TextView nro;
        TextView fecha;
        TextView vence;
        TextView saldo;
        private CheckBox select;

        public CheckBox getSelect() {
            return select;
        }

        public void setSelect(CheckBox select) {
            this.select = select;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        Fila view;
        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CXC item=items.get(position);
        Log.i("item","posicion "+position+" tam: "+getCount()+ " Nombre ");
        if (convertView == null) {
            view = new Fila();
            convertView = inflator.inflate(R.layout.layout_cxc, null);
            view.nro = (TextView) convertView.findViewById(R.id.textViewNro);
            view.fecha = (TextView) convertView.findViewById(R.id.textViewFecha);
            view.vence = (TextView) convertView.findViewById(R.id.textViewVence);
            view.saldo = (TextView) convertView.findViewById(R.id.textViewSaldo);
            view.nro.setText(item.getFactura());
            view.fecha.setText(item.getFecha());
            view.vence.setText(item.getVence());
            view.saldo.setText(item.getSaldo());
            convertView.setTag(view);

        } else {
            view = (Fila) convertView.getTag();
            view.nro = (TextView) convertView.findViewById(R.id.textViewNro);
            view.fecha = (TextView) convertView.findViewById(R.id.textViewFecha);
            view.vence = (TextView) convertView.findViewById(R.id.textViewVence);
            view.saldo = (TextView) convertView.findViewById(R.id.textViewSaldo);
            view.nro.setText(item.getFactura());
            view.fecha.setText(item.getFecha());
            view.vence.setText(item.getVence());
            view.saldo.setText(item.getSaldo());
        }
        //view.fondo = (LinearLayout) convertView.findViewById(R.id.fondo);

        //Setear la imagen desde el recurso drawable

        return convertView;
    }
}
