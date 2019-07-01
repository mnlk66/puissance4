package com.example.puissance4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class AnalyticsDialog extends AppCompatDialogFragment {

    @BindView(R.id.winGames)
    TextView winGames;
    @BindView(R.id.looseGames)
    TextView looseGames;
    @BindView(R.id.equalGames)
    TextView equalGames;

    private Unbinder unbinder;

    private Context context;
    private SharedPreferences preferences;

    public AnalyticsDialog(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.dialog_analytics, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        getDialog().setTitle(R.string.action_analytics);

        fillTexViewBySharedPreferences();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fillTexViewBySharedPreferences() {
        final String wins = String.valueOf(preferences.getInt(Const.PREF_WINS, 0));
        final String looses = String.valueOf(preferences.getInt(Const.PREF_LOOSE, 0));
        final String equals = String.valueOf(preferences.getInt(Const.PREF_EQUAL, 0));

        winGames.setText(wins);
        looseGames.setText(looses);
        equalGames.setText(equals);
    }
}
