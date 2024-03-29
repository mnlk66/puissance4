package com.example.puissance4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements Grid.GridListener {

    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.fab_play)
    FloatingActionButton fabPlay;
    @BindView(R.id.view)
    CoordinatorLayout view;
    @BindView(R.id.btn_info)
    ImageButton btnInfo;
    @BindView(R.id.btn_back)
    ImageButton btnParams;
    @BindView(R.id.toolbar_bottom)
    Toolbar toolbarBottom;
    @BindView(R.id.btn_replay)
    FloatingActionButton btnReplay;


    private Grid adapter;
    private String FIRST_PLAYER;
    private int Niveau;
    private String COLOR_PIECE_USER;
    private AnalyticsDialog analyticsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnReplay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPieceYellow)));
        } else {
            btnReplay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPieceYellow)));
        }
        // INIT DIALOGS

        analyticsDialog = new AnalyticsDialog(getApplicationContext());

        // START INIT ELEMENTS
        Niveau = Const.DEPTH_EASY;
        COLOR_PIECE_USER = Const.YELLOW_PIECE;
        FIRST_PLAYER = Const.PLAYER;


        // INITIALISE GAME
        initOrResetGame();
        
        
    }

    private void replay() {
        if (adapter.gameEnd() || !adapter.gameStart()) {
            initOrResetGame();
        } else {
            Snackbar snackbar = Snackbar.make(view, R.string.do_you_want_replay, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initOrResetGame();
                        }
                    });

            snackbar.setActionTextColor(getResources().getColor(R.color.snackAction));
            snackbar.setDuration(Snackbar.LENGTH_SHORT);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
        }
    }

    @OnClick({R.id.btn_replay,R.id.btn_info,R.id.btn_back})
    public void onClick(View v){

        if (v.getId()==R.id.btn_info){
            analyticsDialog.show(getSupportFragmentManager(), "analyticsDialog");


        }

        if(v.getId()==R.id.btn_replay){
            replay();

        }

        if(v.getId()==R.id.btn_back)
        {
            Intent i = new Intent(MainActivity.this,Home.class);

            startActivity(i);
        }

    }


    @SuppressLint("RestrictedApi")
    private void initOrResetGame() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        adapter = new Grid(this, FIRST_PLAYER, metrics.widthPixels);
        adapter.setColor_piece_user(COLOR_PIECE_USER);
        adapter.setNiveauToIA(this.Niveau);

        if (FIRST_PLAYER.equals(Const.COMPUTER)) {
            fabPlay.setVisibility(View.VISIBLE);
        } else {
            fabPlay.setVisibility(View.GONE);
        }

        gridView.setAdapter(adapter);
    }

    @Override
    public void onBeginComputerLoad() {
        toolbarProgressBar.setVisibility(View.VISIBLE);
        gridView.setEnabled(false);
        btnReplay.setEnabled(false);

        if (Const.COMPUTER.equals(FIRST_PLAYER)) {
            fabPlay.setEnabled(false);
        }
    }

    @OnItemClick(R.id.gridView)
    public void OnItemClick(int position) {
        if (Const.COMPUTER.equals(FIRST_PLAYER) && !adapter.gameStart()) {
            Snackbar.make(view, R.string.touch_play_for_begin, Snackbar.LENGTH_SHORT).show();
        } else {
            adapter.placeGamerPiece(position);
        }
    }

    @Override
    public void onFinishComputerLoad() {
        toolbarProgressBar.setVisibility(View.INVISIBLE);
        gridView.setEnabled(true);
        btnReplay.setEnabled(true);

        if (Const.COMPUTER.equals(FIRST_PLAYER)) {
            fabPlay.setEnabled(true);
        }
    }

    public void showSnackMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }




}
