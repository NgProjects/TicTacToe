package tictactoe.angie.com.tictactoe;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import tictactoe.angie.com.tictactoe.TicTacToeGame.GameState;

public class PlayGame extends RxAppCompatActivity {

    private int noOfPlayers = 1;

    @Bind(R.id.game_view)
    protected TicTacToeView ticTacToeView;

    private final TicTacToeGame game = new TicTacToeGame();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_game_activity);

        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            noOfPlayers = bundle.getInt(TicTacToeConstants.NO_OF_PLAYERS);

            String playerOneName = bundle.getString(TicTacToeConstants.PLAYER_ONE_NAME);
            String playerTwoName = bundle.getString(TicTacToeConstants.PLAYER_TWO_NAME);

            if(StringUtils.isNotBlank(playerOneName) && StringUtils.isNotBlank(playerTwoName)){
                ticTacToeView.setPLAYER_ONE_NAME(playerOneName);
                ticTacToeView.setPLAYER_TWO_NAME(playerTwoName);
            }

            refreshGame();

        }

        ticTacToeView.setOnTileClickedListener(new TicTacToeView.OnTileClickListener() {
            @Override
            public void onTileClick(int position) {
                handleMove(position);
            }
        });

        game.setOnGameOverListener(new TicTacToeGame.OnGameOverListener() {
            @Override
            public void onGameOver(@GameState int state, int[] winningIndices) {
                endGame(state, winningIndices);
            }
        });

        if (savedInstanceState != null) {
            ticTacToeView.setEnabled(false);    // So no sneaky quick taps can't happen while restoring...
            game.setGridState(savedInstanceState.getCharArray(TicTacToeConstants.GRID_STATE));
            game.setIsOver(savedInstanceState.getBoolean(TicTacToeConstants.IS_GAME_OVER));
            game.setCurrentPlayer(savedInstanceState.getChar(TicTacToeConstants.CURRENT_PLAYER));
            game.setGameState(savedInstanceState.getInt(TicTacToeConstants.GAME_STATE));
            ticTacToeView.restoreBoard(game.getGridState());

            if (game.isOver()) {
                game.setWinningIndices(savedInstanceState.getIntArray(TicTacToeConstants.WINNING_INDICES));
                game.endGame();
            } else {
                ticTacToeView.setNextPlayer(game.currentPlayer());
            }
        } else {
            ticTacToeView.setNextPlayer(game.currentPlayer());
        }
    }

    private void refreshGame() {
        ScoresDialogFragment dialog = new ScoresDialogFragment();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String scores = ticTacToeView.getPLAYER_ONE_NAME() + " - 0"
                + "\n" + ticTacToeView.getPLAYER_TWO_NAME() + " - 0"
                + "\n" +  "Ties - 0";

       // dialog.isDone = true;
        //dialog.title = title;
        dialog.message = scores;
        dialog.prefs = prefs;
        dialog.refreshScores();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!game.isOver()) {
            startGame();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // If the game got any more complex, I'd probably switch to some form of game serialization
        outState.putCharArray(TicTacToeConstants.GRID_STATE, game.getGridState());
        outState.putBoolean(TicTacToeConstants.IS_GAME_OVER, game.isOver());
        outState.putChar(TicTacToeConstants.CURRENT_PLAYER, game.currentPlayer());
        outState.putInt(TicTacToeConstants.GAME_STATE, game.getGameState());
        outState.putIntArray(TicTacToeConstants.WINNING_INDICES, game.getWinningIndices());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                restart();
                return true;
            case R.id.scores:
                showScoresDialog(TicTacToeGame.CONTINUE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startGame() {
        ticTacToeView.setNextPlayer(game.currentPlayer());
        switch (noOfPlayers){
            case 1:
                handleOnePlayerMove();
                break;
            case 2:
                handleTwoPlayerMove();
                break;
            default:
                handleOnePlayerMove();
        }
    }


    private void handleTwoPlayerMove() {
        ticTacToeView.setEnabled(true);
        if(game.currentPlayer() == TicTacToeGame.PLAYER_ONE){
            Snackbar.make(ticTacToeView, ticTacToeView.getPLAYER_ONE_NAME() + "'s turn" , Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(ticTacToeView, ticTacToeView.getPLAYER_TWO_NAME() + "'s turn" , Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleOnePlayerMove() {
        if (game.currentPlayer() == TicTacToeGame.PLAYER_TWO) {
            simulateCpuMove();
        } else {
            ticTacToeView.setEnabled(true);
            Snackbar.make(ticTacToeView, "Your turn!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleMove(int position) {
        game.makeMove(position);
        char nextPlayer = game.currentPlayer();
        ticTacToeView.setNextPlayer(nextPlayer);

        if (!game.isOver()) {
           startGame();
        }
    }

    /**
     * Simulate a CPU move. This can sometimes take awhile, and we also want it to happen after a
     * delay. To do this without blocking the UI thread, RxJava is my go-to tool for threading.
     * Used in tandem with RxLifecycle, we can do this in a non-leaky and responsive way.
     */
    private void simulateCpuMove() {
        final Snackbar snackbar = Snackbar.make(ticTacToeView, "Thinking...", Snackbar.LENGTH_INDEFINITE);
        game.getCpuMove()
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        ticTacToeView.setEnabled(false);
                        snackbar.show();
                    }
                })
                .delay(1, TimeUnit.SECONDS)                 // Make it look like the computer is "thinking"
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())                 // So it stops if we leave or rotate
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        ticTacToeView.setEnabled(true);
                        ticTacToeView.setTile(game.getNextCpuMove(), TicTacToeGame.PLAYER_TWO);
                        handleMove(game.getNextCpuMove());
                        snackbar.dismiss();
                    }
                });
    }

    private void endGame(@GameState int result, @Nullable int[] winningIndices) {
        ticTacToeView.endGame(winningIndices);
        if (getSupportFragmentManager().findFragmentByTag("scores") == null) {
            showScoresDialog(result);
        }
    }

    private void restart() {
        ticTacToeView.reset();
        game.restart();
        startGame();
    }

    @SuppressLint("CommitPrefEdits")
    private void showScoresDialog(@GameState final int result) {
        final boolean isDone = result != TicTacToeGame.CONTINUE;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String title;
        String scoreToUpdate = null;
        switch (result) {
            case TicTacToeGame.ONE_WINS:
                title = ticTacToeView.getPLAYER_ONE_NAME() + " Wins!";
                scoreToUpdate = TicTacToeConstants.PLAYER_ONE_SCORES;
                break;
            case TicTacToeGame.TWO_WINS:
                title = ticTacToeView.getPLAYER_TWO_NAME() + " Wins!";
                scoreToUpdate = TicTacToeConstants.PLAYER_TWO_SCORES;
                break;
            case TicTacToeGame.TIE:
                title = "It's a tie!";
                scoreToUpdate = TicTacToeConstants.TIES;
                break;
            default:
                title = "History";
        }

        if (scoreToUpdate != null) {
            prefs.edit().putInt(scoreToUpdate, prefs.getInt(scoreToUpdate, 0) + 1).commit();    // Commit synchronously so we're sure they're up to date in the next line
        }

        String scores = ticTacToeView.getPLAYER_ONE_NAME() + " - " +prefs.getInt(TicTacToeConstants.PLAYER_ONE_SCORES, 0)
                + "\n" + ticTacToeView.getPLAYER_TWO_NAME() + " - " + prefs.getInt(TicTacToeConstants.PLAYER_TWO_SCORES, 0)
                + "\n" +  "Ties - " + prefs.getInt(TicTacToeConstants.TIES, 0);

        ScoresDialogFragment dialog = new ScoresDialogFragment();
        dialog.isDone = isDone;
        dialog.title = title;
        dialog.message = scores;
        dialog.prefs = prefs;

        dialog.show(getSupportFragmentManager(), "scores");
    }

    /**
     * These are annoyingly tedious to maintain, but necessary to avoid leaky windows and
     * maintaining the dialog across rotations.
     */
    public static class ScoresDialogFragment extends DialogFragment {

        boolean isDone;
        String message;
        String title;
        SharedPreferences prefs;

        public ScoresDialogFragment() {
            setRetainInstance(true);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MaterialDialog.ButtonCallback buttonCallback = new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    if (isDone) {
                        ((PlayGame) getActivity()).restart();
                    }
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    refreshScores();
                }
            };

            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                    .title(title)
                    .content(message)
                    .positiveText(isDone ? "Restart" : "Done")
                    .callback(buttonCallback);

            if (!isDone) {
                builder.negativeText("Clear scores");
            }

            return builder.build();
        }

        public void refreshScores() {
            prefs.edit()
                    .putInt(TicTacToeConstants.PLAYER_ONE_SCORES, 0)
                    .putInt(TicTacToeConstants.PLAYER_TWO_SCORES, 0)
                    .putInt(TicTacToeConstants.TIES, 0)
                    .apply();
        }

        @Override
        public void onDestroyView() {
            // workaround for a bug causing the dialog to dismissed on rotation
                if (getDialog() != null && getRetainInstance()) {
                    getDialog().setDismissMessage(null);
            }
            super.onDestroyView();
        }
    }
}
