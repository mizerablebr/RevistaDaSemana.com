package com.rds.revistadasemanacom;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    Activity activity;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_about, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        View view = getView();

        if (view != null) {
            TextView developer = (TextView) view.findViewById(R.id.about_developer);

            developer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("OnAttach", "developer Clicked");
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "bruno@miz.com.br"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contato do Aplicativo RevistaDaSemana");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Mensagem enviada do aplicativo Revista da Semana:");
                    startActivity(Intent.createChooser(emailIntent, "Contato com o Desenvolvedor"));
                }
            });
        }


    }
}
