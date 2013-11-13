package com.example.moviequiz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class Statistics extends Activity {

	public static int totalQuizies = 0;
	public static int totalCorrect = 0;
	public static int totalIncorrect = 0;
	public static int totalTime = 0;

	TextView numQuizzes;
	TextView numCorrect;
	TextView numIncorrect;
	TextView averageTime;
	
	TextView lifeNumQuizzes;
	TextView lifeNumCorrect;
	TextView lifeNumIncorrect;
	TextView lifeAverageTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_statistics);

		numQuizzes = (TextView) findViewById(R.id.quizzesTaken);
		numCorrect = (TextView) findViewById(R.id.numberCorrectStats);
		numIncorrect = (TextView) findViewById(R.id.numberIncorrectStats);
		averageTime = (TextView) findViewById(R.id.averageTimeStats);
		
		lifeNumQuizzes = (TextView) findViewById(R.id.quizLifeTime);
		lifeNumCorrect = (TextView) findViewById(R.id.correctLifeTime);
		lifeNumIncorrect = (TextView) findViewById(R.id.incorrectLifeTime);
		lifeAverageTime = (TextView) findViewById(R.id.averageLifeTime);
		
		SharedPreferences preferences  =  PreferenceManager.getDefaultSharedPreferences(this);
		
	
		int totQuizies =  preferences.getInt("totalQuizies", -1);
		int lifeCorrect = preferences.getInt("numCorrectAnswers", -1);
		int lifeIncorrect = preferences.getInt("numIncorrectAnswers", -1);
		Log.d("quiz", "life inCorrect" + lifeIncorrect);

		lifeNumQuizzes.setText("Quizzes: " + totQuizies);
		lifeNumCorrect.setText("Correct: " + lifeCorrect);
		lifeNumIncorrect.setText("Incorrect: " + lifeIncorrect);
		String averageTimeStringLife = null;
		if (totQuizies == 0 || ((lifeIncorrect + lifeCorrect) == 0)) {
			averageTimeStringLife = "infinity";
		} else {
			averageTimeStringLife = Quiz.getTimerString((totQuizies*Quiz.STARTTIME) / (lifeCorrect + lifeIncorrect));
		}
		lifeAverageTime.setText("Average Time: " + averageTimeStringLife);
		
		
		
		numQuizzes.setText("Number of Quizzes: " + totalQuizies);
		numCorrect.setText("Number Correct: " + totalCorrect);
		numIncorrect.setText("Number Incorrect: " + totalIncorrect);
		String averageTimeString = null;
		if (totalQuizies == 0) {
			averageTimeString = "infinity";
		} else {
			averageTimeString = Quiz.getTimerString(totalTime / (totalCorrect + totalIncorrect));
		}
		averageTime.setText("Average Time: " + averageTimeString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics, menu);
		return true;
	}

}