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
import tablas.FOTO;

public class ListaFotosAdapter extends BaseAdapter {
    private Context context;
    private List<FOTO> items;

    public ListaFotosAdapter(Context context, List<FOTO> items) {
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
        TextView titulo_itm;
        ImageView icono;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        Fila view;
        LayoutInflater inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FOTO item=items.get(position);
        Log.i("item","posicion "+position+" tam: "+getCount()+ " Nombre ");
        if (convertView == null) {
            view = new Fila();
            convertView = inflator.inflate(R.layout.layout_fotos, null);
            view.icono = (ImageView) convertView.findViewById(R.id.imgMenu);
            ImageLoader.getInstance().init(Variables.cargarImagen(context));
            ImageLoader.getInstance().displayImage(Variables.getDireccion_fotos() + item.getNombre() + "&width=250", view.icono);
            Log.i("item_foto",Variables.getDireccion_fotos() + item.getNombre() + "&width=250");
            convertView.setTag(view);

        } else {
            view = (Fila) convertView.getTag();
            view.icono = (ImageView) convertView.findViewById(R.id.imgMenu);
            ImageLoader.getInstance().init(Variables.cargarImagen(context));
            ImageLoader.getInstance().displayImage(Variables.getDireccion_fotos() + item.getNombre() + "&width=250", view.icono);
            Log.i("item_foto",Variables.getDireccion_fotos() + item.getNombre() + "&width=250");
        }

        //Setear la imagen desde el recurso drawable

        return convertView;
    }

}