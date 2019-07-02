package com.example.licentatest;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Finance extends AppCompatActivity {

    TextView mLeft;
    TextView mCurrCycleLeft;
    EditText mCurrCycleBudget;
    TextView mCurrCycleSpent;
    TextView mLastCycleLeft;

    //Change color/ Only visual impact
    TextView RonText1;
    TextView RonText2;
    TextView RonText3;
    TextView RonText4;
    TextView RonText5;

    Button mDetails;
    Button mSwitchNextCycle;
    Button mReset;

    private final String RESET = "RESET";

    //Save budget
    int currBudget;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private final String BUDGET = "BUDGET";
    private final String LASTCYCLELEFT = "LASTCYCLELEFT";
    public static final String CURRCYCLESPENT = "CURRCYCLESPENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        LocalBroadcastManager.getInstance(this).registerReceiver(FinanceDetailsActivity.mDoReset, new IntentFilter(RESET));

        mLeft = findViewById(R.id.totalWoPastView);
        mCurrCycleLeft = findViewById(R.id.totalLeftView);
        mCurrCycleBudget = findViewById(R.id.budgetView);
        mCurrCycleSpent = findViewById(R.id.totalSpentView);
        mLastCycleLeft = findViewById(R.id.totalLeftPastView);

        RonText1 = findViewById(R.id.RonText1);
        RonText2 = findViewById(R.id.RonText2);
        RonText3 = findViewById(R.id.RonText3);
        RonText4 = findViewById(R.id.RonText4);
        RonText5 = findViewById(R.id.RonText5);

        mDetails = findViewById(R.id.financeDetailsButton);
        mSwitchNextCycle = findViewById(R.id.nextCycleButton);
        mReset = findViewById(R.id.resetFinanceButton);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        currBudget = Integer.parseInt(mCurrCycleBudget.getText().toString());

        mLeft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Integer.parseInt(mLeft.getText().toString()) > 0) {
                    mLeft.setTextColor(ContextCompat.getColor(Finance.this, R.color.blue));
                    RonText1.setTextColor(ContextCompat.getColor(Finance.this, R.color.blue));
                }
                else {
                    mLeft.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                    RonText1.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                }
            }
        });

        mCurrCycleLeft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Integer.parseInt(mCurrCycleLeft.getText().toString()) > 0) {
                    mCurrCycleLeft.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                    RonText2.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                }
                else {
                    mCurrCycleLeft.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                    RonText2.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCurrCycleBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!mCurrCycleBudget.getText().toString().isEmpty() &&
                mCurrCycleBudget.getText().toString().matches("[0-9]+")) {
                    int budget = Integer.parseInt(mCurrCycleBudget.getText().toString());

                    currBudget = budget;

                    mEditor.putInt(BUDGET, budget);
                    mEditor.apply();

                    mCurrCycleLeft.setText(Integer.toString(budget -
                            Integer.parseInt(mCurrCycleSpent.getText().toString())));
                    mLeft.setText(Integer.toString(Integer.parseInt(mCurrCycleLeft.getText().toString()) +
                            Integer.parseInt(mLastCycleLeft.getText().toString())));

                    if (budget > 0) {
                        mCurrCycleBudget.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                        RonText3.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                    } else {
                        mCurrCycleBudget.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                        RonText3.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                    }
                }
            }
        });

        mCurrCycleSpent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int budget = Integer.parseInt(mCurrCycleBudget.getText().toString());
                int currSpent = Integer.parseInt(mCurrCycleSpent.getText().toString());
                mCurrCycleLeft.setText(Integer.toString(budget - currSpent));
                mLeft.setText(Integer.toString(Integer.parseInt(mCurrCycleLeft.getText().toString()) -
                        Integer.parseInt(mLastCycleLeft.getText().toString())));

                if(budget - currSpent > 0) {
                    mCurrCycleSpent.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                    RonText4.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                }
                else {
                    mCurrCycleSpent.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                    RonText4.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                }
            }
        });

        mLastCycleLeft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int sum = Integer.parseInt(mLastCycleLeft.getText().toString());

                mEditor.putInt(LASTCYCLELEFT, sum);
                mEditor.apply();

                if(sum > 0) {
                    mLastCycleLeft.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                    RonText5.setTextColor(ContextCompat.getColor(Finance.this, R.color.green));
                }
                else {
                    mLastCycleLeft.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                    RonText5.setTextColor(ContextCompat.getColor(Finance.this, R.color.red));
                }
            }
        });

        mDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Finance.this, FinanceDetailsActivity.class));
            }
        });

        mSwitchNextCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastCycleLeft.setText(Integer.toString(Integer.parseInt(mCurrCycleLeft.getText().toString())));
                mCurrCycleSpent.setText("0");
                mCurrCycleBudget.setText(Integer.toString(currBudget));

                Intent intent = new Intent(RESET);
                LocalBroadcastManager.getInstance(Finance.this).sendBroadcast(intent);
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Finance.this, "Long press to reset", Toast.LENGTH_SHORT).show();
            }
        });

        mReset.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String tmp = Integer.toString(currBudget);
                mCurrCycleBudget.setText(tmp);
                mCurrCycleLeft.setText(tmp);
                mCurrCycleSpent.setText("0");
                mLastCycleLeft.setText("0");
                mLeft.setText(tmp);

                Intent intent = new Intent(RESET);
                LocalBroadcastManager.getInstance(Finance.this).sendBroadcast(intent);
                return false;
            }
        });

        mCurrCycleBudget.setText(Integer.toString(mPreferences.getInt(BUDGET, 0)));
        mCurrCycleSpent.setText(Integer.toString(mPreferences.getInt(CURRCYCLESPENT, 0)));
        mLastCycleLeft.setText(Integer.toString(mPreferences.getInt(LASTCYCLELEFT, 0)));
    }
}
