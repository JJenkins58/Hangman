package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //declare variables
    TextView txtWordToBeGuessed;
    String wordToBeGuessed;
    String wordDisplayedString;
    char[] wordDisplayedCharArray;
    ArrayList<String> myListOfWords;
    EditText edtInput;
    TextView txtLettersTried;
    String lettersTried;
    final String MESSAGE = "Letters tried: ";
    TextView txtTriesLeft;
    String triesLeft;
    final String WIN = "You won!";
    final String LOSE = "You lose!";
    Animation rotateAnimation;
    Animation scaleAnimation;
    Animation scaleAndRotateAnimation;

    void revealLetterInWord(char letter) {
        int indexOfLetter = wordToBeGuessed.indexOf(letter);

        while(indexOfLetter >= 0) {
            wordDisplayedCharArray[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
        }

        //update the string
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
    }

    void displayWordOnScreen() {
        String formattedString = "";
        for(char character : wordDisplayedCharArray) {
            formattedString += character + " ";
        }
        txtWordToBeGuessed.setText(formattedString);
    }

    void initializeGame(){
        //randomly shuffle array list and get first element then remove it
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);

        //initialize char array
        wordDisplayedCharArray = wordToBeGuessed.toCharArray();

        //add underscores
        for(int i = 1; i < wordDisplayedCharArray.length - 1; i++) {
            wordDisplayedCharArray[i] = '_';
        }

        //reveal spaces in the word
        for (int i = 0; i < wordDisplayedCharArray.length; i++) {
            if(wordToBeGuessed.charAt(i) == ' ') {
                wordDisplayedCharArray[i] = ' ';
            }
        }

        //reveal all occurrences of first char
        revealLetterInWord(wordDisplayedCharArray[0]);

        //reveal all occurrences of last char
        revealLetterInWord(wordDisplayedCharArray[wordDisplayedCharArray.length - 1]);

        //initialize a string from this char array
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);

        //display word
        displayWordOnScreen();

        //clear input field
        edtInput.setText("");

        //letters tried
        lettersTried = " ";

        //Display on screen
        txtLettersTried.setText(MESSAGE);

        //tries left
        triesLeft = " X X X X X";
        txtTriesLeft.setText(triesLeft);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize variables
        myListOfWords = new ArrayList<String>();
        txtWordToBeGuessed = findViewById(R.id.txtWordToBeGuessed);
        edtInput = findViewById(R.id.edtInput);
        txtLettersTried = findViewById(R.id.txtLettersTried);
        txtTriesLeft = findViewById(R.id.txtTriesLeft);
        AppCompatButton btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        //use database file and add names to array list
        InputStream myInputStream = null;
        Scanner in = null;
        String aWord = "";

        try {
            myInputStream = getAssets().open("database.txt");
            in = new Scanner(myInputStream);
            while (in.hasNextLine()){
                aWord = in.nextLine();
                myListOfWords.add(aWord);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            //close scanner
            if(in != null) {
                in.close();
            }

            //close InputStream
            try {
                if(myInputStream != null) {
                    myInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        initializeGame();

        //setup the text changed listener for the edit text
        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                //if letter is on input field
                if(s.length() != 0) {
                    checkIfLetterIsInWord(s.charAt(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void checkIfLetterIsInWord(char letter) {
        //check if letter was in word
        if(wordToBeGuessed.indexOf(letter) >= 0) {
            //if letter not displayed
            if(wordDisplayedString.indexOf(letter) < 0) {
                //replace underscores with letter
                revealLetterInWord(letter);

                //update changes on screen
                displayWordOnScreen();

                //check if won
                if(!wordDisplayedString.contains("_")) {
                    txtTriesLeft.setText(WIN);
                }
            }
        }
        //else letter not in word
        else {
            //decrease num of tries and update screen
            decreaseAndDisplayTriesLeft();

            //check if lost
            if(triesLeft.isEmpty()){
                txtTriesLeft.setText(LOSE);
                txtWordToBeGuessed.setText(wordToBeGuessed);
            }
        }

        //display the letter tried
        if(lettersTried.indexOf(letter) < 0) {
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE + lettersTried;
            txtLettersTried.setText(messageToBeDisplayed);
        }
    }

    void decreaseAndDisplayTriesLeft() {
        //if there are still tries
        if(!triesLeft.isEmpty()){
            //take out last 2 character from string
            triesLeft = triesLeft.substring(0, triesLeft.length() - 2);
            txtTriesLeft.setText(triesLeft);
        }
    }

    void resetGame() {
        try {
            //setup new game
            initializeGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}