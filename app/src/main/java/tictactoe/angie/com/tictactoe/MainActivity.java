package tictactoe.angie.com.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import tictactoe.angie.com.tictactoe.TicTacToeGame;

public class MainActivity extends AppCompatActivity {

    Button ONE_PLAYER_BUTTON, TWO_PLAYER_BUTTON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ONE_PLAYER_BUTTON = findViewById(R.id.one_player_button);
        TWO_PLAYER_BUTTON = findViewById(R.id.two_player_button);

        ONE_PLAYER_BUTTON.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PlayGame.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt("NO_OF_PLAYERS", 1);

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );

        TWO_PLAYER_BUTTON.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PlayerNames.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
