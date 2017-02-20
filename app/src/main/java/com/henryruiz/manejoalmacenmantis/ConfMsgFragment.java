package com.henryruiz.manejoalmacenmantis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import sincronizacion.Variables;


public class ConfMsgFragment extends Fragment {
    View rootView;
    Context c;
    public ConfMsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.henryruiz.manejoalmacenmantis.R.layout.fragment_conf_msg, container, false);
        c = (Context)getActivity();
        final EditText asunto = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextAsunto);
        final EditText msg = (EditText) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.editTextMsg);
        Button salvar = (Button) rootView.findViewById(com.henryruiz.manejoalmacenmantis.R.id.button);

        salvar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedPreferences sp = c.getSharedPreferences("perfil",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("asunto", asunto.getText().toString());
                editor.putString("msg", msg.getText().toString());
                editor.commit();
            }
        });

        asunto.setText(Variables.getAsunto());
        msg.setText(Variables.getMsg());

        return rootView;
    }
}
