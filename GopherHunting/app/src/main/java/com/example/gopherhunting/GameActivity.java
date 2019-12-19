package com.example.gopherhunting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity {

    //Setting constants

    //Game Actors
    int GOPHER_POSITION;
    final int GOPHER = 0;
    final static int PLAYER1 = 1;
    final static int PLAYER2 = 2;

    //Move Outcomes
    final int SUCCESS = 1;
    final int NEARMISS = 2;
    final int CLOSEGUESS  = 3;
    final int COMPLETEMISS = 4;
    final int DISASTER = 5;

    //Game Modes
    final int GUESSMODE = 0;
    final int CONTINUOUS = 1;

    //Hole Array corresponds to the text views occupying each of the position from 0 to 99 on the grid
    final int[] holeArray = {
            R.id.txt1,R.id.txt2,R.id.txt3,R.id.txt4,R.id.txt5,R.id.txt6,R.id.txt7,R.id.txt8,R.id.txt9,R.id.txt10,
            R.id.txt11, R.id.txt12, R.id.txt13, R.id.txt14, R.id.txt15, R.id.txt16, R.id.txt17, R.id.txt18, R.id.txt19, R.id.txt20,
            R.id.txt21, R.id.txt22, R.id.txt23, R.id.txt24, R.id.txt25, R.id.txt26, R.id.txt27, R.id.txt28, R.id.txt29, R.id.txt30,
            R.id.txt31, R.id.txt32, R.id.txt33, R.id.txt34, R.id.txt35, R.id.txt36, R.id.txt37, R.id.txt38, R.id.txt39, R.id.txt40,
            R.id.txt41, R.id.txt42, R.id.txt43, R.id.txt44, R.id.txt45, R.id.txt46, R.id.txt47, R.id.txt48, R.id.txt49, R.id.txt50,
            R.id.txt51, R.id.txt52, R.id.txt53, R.id.txt54, R.id.txt55, R.id.txt56, R.id.txt57, R.id.txt58, R.id.txt59, R.id.txt60,
            R.id.txt61, R.id.txt62, R.id.txt63, R.id.txt64, R.id.txt65, R.id.txt66, R.id.txt67, R.id.txt68, R.id.txt69, R.id.txt70,
            R.id.txt71, R.id.txt72, R.id.txt73, R.id.txt74, R.id.txt75, R.id.txt76, R.id.txt77, R.id.txt78, R.id.txt79, R.id.txt80,
            R.id.txt81, R.id.txt82, R.id.txt83, R.id.txt84, R.id.txt85, R.id.txt86, R.id.txt87, R.id.txt88, R.id.txt89, R.id.txt90,
            R.id.txt91, R.id.txt92, R.id.txt93, R.id.txt94, R.id.txt95, R.id.txt96, R.id.txt97, R.id.txt98, R.id.txt99, R.id.txt100
    };

    //Array to check whether a particular position in the grid has been occupied and if yes then by which item
    int[] holeOccupancy = new int[100];

    //runnable handler
    private Handler rHandler = new Handler() ;
    //message handler
    private Handler mHandler;
    boolean isGopherFound = false;

    TextView player1Status;
    TextView player2Status;
    TextView gameStatus;
    Button modeBtn;
    final String[] outcomes = {"","Success","Near Miss", "Close Guess", "Complete Miss", "Disaster"};
    boolean player1Move = true;
    int gameMode = CONTINUOUS; //Intial game mode is Continuous

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Initialize hole occupancy array to -1 i.e all holes are unoccupied
        Arrays.fill(holeOccupancy,-1);

        //Set random position of GOPHER and place it at the position generated
        GOPHER_POSITION = (int)(Math.random() * 100)%100;
        placeItem(GOPHER_POSITION, GOPHER);

        player1Status = findViewById(R.id.player1Status);
        player2Status = findViewById(R.id.player2Status);
        gameStatus = findViewById(R.id.gameStatus);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                Log.i("Message Handler","Received "+msg.what + " " + msg.arg1 + " " + msg.arg2);
                int what = msg.what ;
                switch (what) {
                    case PLAYER1:
                        placeItem(msg.arg1,PLAYER1);
                        player1Status.setText(outcomes[msg.arg2]);
                        if(msg.arg2 == SUCCESS)
                            gameStatus.setText("Player 1 won!");
                        break;
                    case PLAYER2:
                        placeItem(msg.arg1,PLAYER2);
                        player2Status.setText(outcomes[msg.arg2]);
                        if(msg.arg2 == SUCCESS)
                            gameStatus.setText("Player 2 won!");
                        break;
                }
            }
        }	;

        Thread t1 = new Thread(new Player1Runnable());
        t1.start();
        Thread t2 = new Thread(new Player2Runnable());
        t2.start();

        modeBtn = findViewById(R.id.modeButton);
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameMode == CONTINUOUS) {
                    gameMode = GUESSMODE;
                    Toast.makeText(getApplicationContext(),"Guess By Guess Mode Enabled",Toast.LENGTH_SHORT).show();
                }
                else {
                    gameMode = CONTINUOUS;
                    Toast.makeText(getApplicationContext(),"Continuous Mode Enabled",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //Place the provided item at the position mentioned
    public void placeItem(int pos, int item){
        TextView t;
        switch(item){
            case GOPHER: {
                t = findViewById(holeArray[pos]);
                t.setBackgroundResource(R.drawable.image4);
                holeOccupancy[pos] = GOPHER;
                break;
            }
            case PLAYER1: {
                t = findViewById(holeArray[pos]);
                t.setBackgroundResource(R.drawable.image1);
                holeOccupancy[pos] = PLAYER1;
                break;
            }
            case PLAYER2: {
                t = findViewById(holeArray[pos]);
                t.setBackgroundResource(R.drawable.image2);
                holeOccupancy[pos] = PLAYER2;
                break;
            }
        }
    }

    //Check position given by the player and take corresponding decision
    public int calcGopherProximity(int pos) {
        int playerRow = pos/10, playerCol = pos%10, gopherRow = GOPHER_POSITION/10, gopherCol = GOPHER_POSITION%10;

        if(holeOccupancy[pos] != -1 && holeOccupancy[pos] != 0)
            return DISASTER;
        else {
            if (pos == GOPHER_POSITION) {
                return SUCCESS;
            } else if ((playerRow == gopherRow && Math.abs(playerCol - gopherCol) == 1) || (playerCol == gopherCol && Math.abs(playerRow - gopherRow) == 1) || (Math.abs(playerCol - gopherCol) == 1 && Math.abs(playerRow - gopherRow) == 1)) {
                return NEARMISS;
            } else if ((playerRow == gopherRow && Math.abs(playerCol - gopherCol) == 2) || (playerCol == gopherCol && Math.abs(playerRow - gopherRow) == 2) || (Math.abs(playerCol - gopherCol) == 2 && Math.abs(playerRow - gopherRow) == 2)) {
                return CLOSEGUESS;
            } else
                return COMPLETEMISS;
        }
    }

    class Player1Runnable implements Runnable{
        @Override
        public void run() {
            while(!isGopherFound) {

                if(gameMode == GUESSMODE)
                    if(!player1Move)
                        continue;
                    else
                        player1Move = false;


                final int myPos = (int)(Math.random() * 100)%100;
                final int outcome = calcGopherProximity(myPos);
                Log.i("Player1","Player 1 :" + outcome);
                if(outcome != DISASTER){

                    //Send runnable to UI Thread Handler to place item for PLayer 1
                    rHandler.post(new Runnable() {
                        public void run() {
                            placeItem(myPos,PLAYER1);
                            player1Status.setText(outcomes[outcome]);
                        }
                    });
                    if(outcome == SUCCESS){
                        isGopherFound =true;
                        rHandler.post(new Runnable() {
                            public void run() {
                                gameStatus.setText("Player 1 won!");
                            }
                        });
                        break;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class Player2Runnable implements Runnable{
        @Override
        public void run() {
            int counter=0;

            while(!isGopherFound && counter < 100){

                if(gameMode == GUESSMODE)
                    if(player1Move)
                        continue;
                    else
                        player1Move = true;

                final int outcome = calcGopherProximity(counter);
                Log.i("Player2","Player 2 :" + outcome);
                if(outcome != DISASTER){
                    final int myPos = counter;

                    //Send message to UI Thread handler to place item for PLayer 2
                    Message msg = mHandler.obtainMessage(GameActivity.PLAYER2) ;
                    msg.arg1 = myPos ;
                    msg.arg2 = outcome;
                    mHandler.sendMessage(msg);

                    if(outcome == SUCCESS){
                        isGopherFound =true;
                        break;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
            }
        }
    }
}