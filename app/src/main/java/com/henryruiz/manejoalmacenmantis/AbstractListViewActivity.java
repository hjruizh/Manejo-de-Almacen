/*
 * Copyright (C) 2012 Daniel Medina <http://danielme.com>
 * 
 * This file is part of "Android Paginated ListView Demo".
 * 
 * "Android Paginated ListView Demo" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * "Android Paginated ListView Demo" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 3
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 */
package com.henryruiz.manejoalmacenmantis;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import clases.Datasource;
import tablas.INV;

public abstract class AbstractListViewActivity extends Activity {

	protected Datasource datasource;

	protected static final int PAGESIZE = 10;
	protected ListView listView;
	protected TextView textViewDisplaying;
	protected CustomListAdapter la;
	protected View footerView;

	protected boolean loading = false;

	protected class LoadNextPage extends AsyncTask<String, Void, String> {
		private ArrayList<INV> newData = null;

		@Override
		protected String doInBackground(String... arg0) {
			// para que de tiempo a ver el footer ;)
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				Log.e("AbstractListActivity", e.getMessage());
			}
			newData = datasource.getData(la.getCount(), PAGESIZE);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			for (INV value : newData) {
				la.add(value);
			}
			la.notifyDataSetChanged();

			listView.removeFooterView(footerView);
			updateDisplayingTextView();
			loading = false;
		}

	}

	protected void updateDisplayingTextView() {
		textViewDisplaying = (TextView) findViewById(com.henryruiz.manejoalmacenmantis.R.id.displaying);
		String text = getString(com.henryruiz.manejoalmacenmantis.R.string.display);
		text = String.format(text, la.getCount(), datasource.getSize());
		textViewDisplaying.setText(text);
	}

	protected void onListItemClick(int position, String compra) {
		// Toast.makeText(this, getListAdapter().getItem(position) + " " +
		// getString(R.string.selected), Toast.LENGTH_SHORT).show();
		Log.i("la", "position: " + position);
		INV aux = (INV) listView.getAdapter().getItem(position);
		Log.i("la", "pk: " + aux.getPk());
		Intent intent = new Intent(this, DetalleProducto.class);
		intent.putExtra("pk", String.valueOf(aux.getPk()));
		/*intent.putExtra("fechai", "");
		intent.putExtra("fechaf", "");
		intent.putExtra("compra", compra);
		intent.putExtra("diario","");*/
		startActivity(intent);
	}

	protected boolean load(int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		boolean lastItem = firstVisibleItem + visibleItemCount == totalItemCount
				&& listView.getChildAt(visibleItemCount - 1) != null
				&& listView.getChildAt(visibleItemCount - 1).getBottom() <= listView
						.getHeight();
		boolean moreRows = la.getCount() < datasource.getSize();
		return moreRows && lastItem && !loading;

	}

	protected void asignar_adaptador(CustomListAdapter lax) {
		la = lax;
		listView.setAdapter(lax);
	}
}