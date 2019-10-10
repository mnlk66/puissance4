package com.example.puissance4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
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
    private String SECOND_PLAYER;
    private int Niveau;
    private String COLOR_PIECE_USER;
    private String COLOR_PIECE_USER2;
    private AnalyticsDialog analyticsDialog;
    private boolean player=true;

    private String colorP2="R_P";

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
        COLOR_PIECE_USER2 = Const.RED_PIECE;
        FIRST_PLAYER = Const.PLAYER;
        SECOND_PLAYER = Const.COMPUTER;


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
        Intent int1= getIntent();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        adapter = new Grid(this, FIRST_PLAYER, metrics.widthPixels,int1.getStringExtra("typePartie"));
        adapter.setColor_piece_user(COLOR_PIECE_USER);
        adapter.setNiveauToIA(this.Niveau);

        gridView.setAdapter(adapter);
    }

    @Override
    public void onBeginComputerLoad() {
        toolbarProgressBar.setVisibility(View.VISIBLE);
        gridView.setEnabled(false);
        btnReplay.setEnabled(false);

    }

    @OnItemClick(R.id.gridView)
    public void OnItemClick(int position) {

      if (adapter.getTypePartie().equals("Partie Solo")) {
            adapter.placeGamerPiece(position);
        }

      else {

          if(player) {
              adapter.placeGamerActualpiece(position,COLOR_PIECE_USER,FIRST_PLAYER);
              player=!player;
          }

          else{
              adapter.placeGamerActualpiece(position, COLOR_PIECE_USER2,SECOND_PLAYER);
              player=!player;
          }
      }
    }

    @Override
    public void onFinishComputerLoad() {
        toolbarProgressBar.setVisibility(View.INVISIBLE);
        gridView.setEnabled(true);
        btnReplay.setEnabled(true);

    }

    public void showSnackMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }




}
