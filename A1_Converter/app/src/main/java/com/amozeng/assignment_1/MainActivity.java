package com.amozeng.assignment_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText inputText;
    private TextView outputText;
    private TextView inputUnit, outputUnit;
    private TextView convertHistory;

    private static final int MtoK = 0;
    private static final int KtoM = 1;
    private int unitStatus = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind car's to the screen widgets
        inputText = findViewById(R.id.inputText);
        outputText = findViewById(R.id.outputText);
        inputUnit = findViewById(R.id.inputUnit);
        outputUnit = findViewById(R.id.outputUnit);
        convertHistory = findViewById(R.id.convertHistory);

        // This next line is required for proper scrolling behavior
        convertHistory.setMovementMethod(new ScrollingMovementMethod());
    }

    public void buttonConvertClicked(View v) {

        if(inputText.getText().length() != 0) {
            double inputValue = Double.parseDouble(inputText.getText().toString());
            double outputValue;

//            switch (unitStatus) {
//                case MtoK:
//                    outputValue = MtoK(inputValue);
//                    break;
//                case KtoM:
//                    outputValue = KtoM(inputValue);
//                    break;
//                default:
//                    outputValue = 0;
//            }

            if(unitStatus == 0) {  // Miles to Kilometers
                outputValue = MtoK(inputValue);
            }else{ // Kilometers to Miles
                outputValue = KtoM(inputValue);
            }

            outputText.setText(String.format(Locale.getDefault(),"%,.1f", outputValue));

            // history
            String newText = null;
            if(unitStatus == 0) {  // Miles to Kilometers
                newText = "Mi to Km: " + inputText.getText().toString()+ " ==> " + outputText.getText().toString();
            }else{ // Kilometers to Miles
                newText = "Km to Mi: " + inputText.getText().toString()+ " ==> " + outputText.getText().toString();
            }

            String historyText = convertHistory.getText().toString();

            convertHistory.setText(String.format("%s\n%s", newText, historyText));
            inputText.setText("");

        }
    }

    public void radioClicked(View v) {
        String message = "Group 1: You Selected ";
        switch (v.getId()) {
            case R.id.KtoM:
                message += "Kilometer to Miles ";
                Log.d(TAG, "radioClicked: Kilo to Miles");
                inputUnit.setText(R.string.text_kilometersValue);
                outputUnit.setText(R.string.text_milesValue);
                unitStatus = KtoM;
                break;
            case R.id.MtoK:
                message += "Miles to Kilometer";
                Log.d(TAG, "radioClicked: Miles to Kilo");
                inputUnit.setText(R.string.text_milesValue);
                outputUnit.setText(R.string.text_kilometersValue);
                unitStatus = MtoK;

                break;
        }
    }

    public void clearButtonClicked(View v) {
        convertHistory.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("HISTORY", convertHistory.getText().toString());
        outState.putString("INPUT_VALUE", inputText.getText().toString());
        outState.putString("OUTPUT_VALUE", outputText.getText().toString());

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        convertHistory.setText(savedInstanceState.getString("HISTORY"));
        inputText.setText(savedInstanceState.getString("INPUT_VALUE"));
        outputText.setText(savedInstanceState.getString("OUTPUT_VALUE"));

    }


    public double MtoK(double input){
        return input * 1.60934;
    }

    public double KtoM(double input){
        return input * 0.621371;
    }
}