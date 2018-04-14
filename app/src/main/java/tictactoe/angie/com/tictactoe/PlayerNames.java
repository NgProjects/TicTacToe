package tictactoe.angie.com.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayerNames extends AppCompatActivity {

    Button PLAY_BUTTON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_names_activity);

        PLAY_BUTTON = findViewById(R.id.two_player_proceed_button);

        PLAY_BUTTON.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String playerOneName = getPlayerNames(R.id.player_one_name);
                        String playerTwoName = getPlayerNames(R.id.player_two_name);

                        Intent intent = new Intent(PlayerNames.this, PlayGame.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt(TicTacToeConstants.NO_OF_PLAYERS, 2);
                        bundle.putString(TicTacToeConstants.PLAYER_ONE_NAME,playerOneName);
                        bundle.putString(TicTacToeConstants.PLAYER_TWO_NAME,playerTwoName);

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );
    }

    private String getPlayerNames(int id) {
        EditText nameTextView = findViewById(id);
        return nameTextView.getText().toString();
    }
}
