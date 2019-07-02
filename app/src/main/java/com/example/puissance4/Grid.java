package com.example.puissance4;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Grid extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final int screenWidth;
    private final IA mon_IA;
    private String[][] mPiecesPlayed = new String[7][6]; // [column][line]
    private int[] nbPiecesByColumn = new int[7];
    private int[] mThumbs = new int[42];
    private String nextPlayer;
    private String color_piece_user;
    private String typePartie;

    // partages de preferences 
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;


    //Utilisation de l'interface pour envoyer les evenements
    private GridListener mListener;

    public Grid(final Activity activity, final String firstPlayer, final int screenWidth,final String type) {
        this.context = activity.getApplicationContext();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.nextPlayer = firstPlayer;
        this.screenWidth = screenWidth;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferencesEditor = this.preferences.edit();
        this.typePartie=type;
        mon_IA = new IA();


        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GridAdapterListener so we can send events to the host
            mListener = (GridListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener");
        }

        cleanGrid();
    }

    public String getTypePartie() {
        return typePartie;
    }

    public void setTypePartie(String typePartie) {
        this.typePartie = typePartie;
    }

    public interface GridListener {
        void onBeginComputerLoad();

        void onFinishComputerLoad();

        void showSnackMessage(String message);
    }

    public void setNiveauToIA(int niveau) {
        mon_IA.setNiveau(niveau);
    }

    public void setColor_piece_user(String color_piece_user) {
        this.color_piece_user = color_piece_user;
    }

    public boolean gameStart() {


        boolean b = false;

        for(int i = 0; i<=6; i++) //vérif  par colonnes
        {
            if(this.nbPiecesByColumn[i] > 0)
            {
                b = true;
            }
        }

        return b;
    }

    static class ViewHolder {
        @BindView(R.id.imageViewPion)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return mThumbs.length;
    }

    @Override
    public Integer getItem(int position) {
        return mThumbs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_item_pion, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.image.getLayoutParams().height = screenWidth / 7;
        holder.image.getLayoutParams().width = screenWidth / 7;
        holder.image.requestLayout();

        holder.image.setImageResource(mThumbs[position]);

        return view;
    }

    private void cleanGrid() {
        for(int i = 0; i<=41; i++) {
            mThumbs[i] = R.drawable.ic_vide;
        }

        for(int i = 0; i<=6; i++) {
            nbPiecesByColumn[i] = 0;
        }

        for(int i = 0; i<=6; i++) {
            for(int z = 0; z<=5; z++) {
                mPiecesPlayed[i][z] = null;
            }
        }
    }

    public void placeGamerPiece(int position) {
        if(gameEnd()) {
            showMessage(context.getString(R.string.game_over));
        } else {
            final int column = position % 7;

            if (nbPiecesByColumn[column] < 6) {
                int li = 5;
                boolean b = false;

                do {
                    if (mPiecesPlayed[column][li] == null) {
                        b = true;

                        nbPiecesByColumn[column] = nbPiecesByColumn[column] + 1;
                        mPiecesPlayed[column][li] = Const.PLAYER;

                        int positionAjouer = column + (li * 7);

                        if (Const.YELLOW_PIECE.equals(color_piece_user)) {
                            mThumbs[positionAjouer] = R.drawable.ic_orange;
                        } else if (Const.RED_PIECE.equals(color_piece_user)) {
                            mThumbs[positionAjouer] = R.drawable.ic_rouge;
                        }

                        notifyDataSetChanged();

                        if (!mon_IA.playerWin(mPiecesPlayed, Const.PLAYER)) {
                            if(stillPlay()) {
                                    nextPlayer = Const.COMPUTER;
                                    placeIAPiece();
                            }
                            else {
                                increaseAnalytics(Const.PREF_EQUAL);
                                showMessage(context.getString(R.string.equal_game));
                            }
                        } else {

                            Log.d("WIN", "placeGamerPiece:WINN ");
                            increaseAnalytics(Const.PREF_WINS);
                            showMessage(context.getString(R.string.you_win));
                        }
                    } else {
                        li--;
                    }
                } while (!b);
            } else {
                showMessage(context.getString(R.string.no_more_space));
            }
        }
    }
    public void placeIAPiece() {
        new AsyncTask<Void, Void, Integer>() {
            private double startTime;

            @Override
            protected void onPreExecute() {
                startTime = System.nanoTime();
                mListener.onBeginComputerLoad();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                int column = -1;

                if (gameEnd()) {
                    showMessage(context.getString(R.string.game_over));
                } else {
                    column = mon_IA.getColumn(mPiecesPlayed);

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(5);

                    final double exectime = (System.nanoTime() - startTime) / 1000000000;
                    Log.i("Execution time", exectime + "s.");

                    final long restTime = Double.valueOf((0.5 - exectime) * 1000).longValue();

                    if (restTime > 0) {
                        Log.i("Rest time", restTime + "ms.");
                        try {
                            Thread.sleep(restTime);
                        } catch (InterruptedException e) {
                            Log.e("Err", e.getMessage());
                        }
                    }
                }

                return column;
            }

            @Override
            protected void onPostExecute(Integer column) {
                if (column != -1) {
                    final int positionAjouer = ((5 - nbPiecesByColumn[column]) * 7) + column;
                    final int ligne = (int) Math.floor(positionAjouer / 7);

                    nbPiecesByColumn[column] = nbPiecesByColumn[column] + 1;
                    mPiecesPlayed[column][ligne] = Const.COMPUTER;

                    if (Const.YELLOW_PIECE.equals(color_piece_user)) {
                        mThumbs[positionAjouer] = R.drawable.ic_rouge;
                    } else if (Const.RED_PIECE.equals(color_piece_user)) {
                        mThumbs[positionAjouer] = R.drawable.ic_orange;
                    }

                    notifyDataSetChanged();

                    if (!mon_IA.playerWin(mPiecesPlayed, nextPlayer)) {
                        if(stillPlay()){
                            nextPlayer = Const.PLAYER;
                        }
                        else {
                            increaseAnalytics(Const.PREF_EQUAL);
                            showMessage(context.getString(R.string.equal_game));
                        }
                    } else {
                        increaseAnalytics(Const.PREF_LOOSE);
                        showMessage(context.getString(R.string.you_lose));
                    }
                }

                mListener.onFinishComputerLoad();
            }
        }.execute();

    }

    public void placeGamerActualpiece(int position,String color_piece_user,String nextPlayer){
        if(gameEnd()) {
            showMessage(context.getString(R.string.game_over));
        } else {
            final int column = position % 7;

            if (nbPiecesByColumn[column] < 6) {
                int li = 5;
                boolean b = false;

                do {
                    if (mPiecesPlayed[column][li] == null) {

                        if (!mon_IA.playerWin(mPiecesPlayed, nextPlayer)) {
                            if(stillPlay()) {
                        Log.d("mpiecesPlayed", "placeGamerActualpiece: ICI ");
                        b = true;

                        nbPiecesByColumn[column] = nbPiecesByColumn[column] + 1;
                        if (nextPlayer==Const.PLAYER) {
                            mPiecesPlayed[column][li] = Const.PLAYER;

                        }

                        else if (nextPlayer==Const.COMPUTER){
                            mPiecesPlayed[column][li]= Const.COMPUTER;
                        }

                        int positionAjouer = column + (li * 7);

                        if (Const.YELLOW_PIECE.equals(color_piece_user)) {
                            mThumbs[positionAjouer] = R.drawable.ic_orange;
                        } else if (Const.RED_PIECE.equals(color_piece_user)) {
                            mThumbs[positionAjouer] = R.drawable.ic_rouge;
                        }

                        notifyDataSetChanged();
                            }
                            else {
                                increaseAnalytics(Const.PREF_EQUAL);
                                showMessage(context.getString(R.string.equal_game));
                            }
                        }
                        Log.d("iswin ?", "placeGamerPiece: "+!mon_IA.playerWin(mPiecesPlayed, Const.PLAYER));
                        if (mon_IA.playerWin(mPiecesPlayed, Const.PLAYER)){
                            Log.d("WIN", "placeGamerActualpiece: WINER ");
                            increaseAnalytics(Const.PREF_WINS);
                            showMessage(context.getString(R.string.you_win));
                        }
                    } else {
                        li--;
                    }
                } while (!b);
            } else {
                showMessage(context.getString(R.string.no_more_space));
            }
        }
    }
    public boolean gameEnd() {
        return mon_IA.playerWin(mPiecesPlayed, Const.COMPUTER) || mon_IA.playerWin(mPiecesPlayed, Const.PLAYER) || !stillPlay();
    }

    private boolean stillPlay() {
        boolean b = false;

        for(int i = 0; i<=6; i++) {
            if(this.nbPiecesByColumn[i] < 6) {
                b = true;
            }
        }

        return b;
    }

    private void showMessage(final String m) {
        mListener.showSnackMessage(m);
    }

    private void increaseAnalytics(final String key) {
        preferencesEditor.putInt(key, preferences.getInt(key, 0) + 1);
        preferencesEditor.commit();
    }
}
