package com.example.moviequiz;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class Quiz extends Activity {

	final Context context = this;

	TextView timerText;
	CountDownTimer timer;
	long timeLeft;
	boolean hasFinished = false;
	DbAdapter db;

	public static final int STARTTIME = 180000;
	//public static final int STARTTIME = 1000;

	private TextView questionText;
	private RadioButton answer1;
	private RadioButton answer2;
	private RadioButton answer3;
	private RadioButton answer4;

	int correctAnswer;

	Dialog dialog;
	Dialog finished;
	String text;

	QuestionCreator questions;
	Random r = new Random();

	// Stats

	int numCorrectAnswers = 0;
	int numIncorrectAnswers = 0;
	int numberOfQuestions = 0;

	boolean hasQuestion;
	boolean showFinished;
	boolean showDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_quiz);

		db = new DbAdapter(this);
		questions = new QuestionCreator(context);

		timerText = (TextView) findViewById(R.id.timerView);

		questionText = (TextView) findViewById(R.id.question);
		answer1 = (RadioButton) findViewById(R.id.answer1);
		answer2 = (RadioButton) findViewById(R.id.answer2);
		answer3 = (RadioButton) findViewById(R.id.answer3);
		answer4 = (RadioButton) findViewById(R.id.answer4);
		answer1.setOnClickListener(clickHandler);
		answer2.setOnClickListener(clickHandler);
		answer3.setOnClickListener(clickHandler);
		answer4.setOnClickListener(clickHandler);

		setDialog();

	}

	public void setDialog() {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.custom_alert);
		Button next = (Button) dialog.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("quiz", "On click called");
				dialog.dismiss();
				clearAnswers();
				nextQuestion();
				timer = new CountDownTimer(timeLeft, 100) {
					public void onTick(long millisUntilFinished) {
						timeLeft = millisUntilFinished;
						timerText.setText(getTimerString(millisUntilFinished));
					}

					public void onFinish() {
						gameFinished();

					}
				}.start();
			}
		});
	}

	private void gameFinished() {
		Statistics.totalQuizies++;
		Statistics.totalCorrect += numCorrectAnswers;
		Statistics.totalIncorrect += numIncorrectAnswers;
		Statistics.totalTime += STARTTIME;

		Log.d("quiz", "Game over!");
		timerText.setText("0:00");
		hasFinished = true;
		timeLeft = 0;

		finished = new Dialog(context);
		finished.requestWindowFeature(Window.FEATURE_NO_TITLE);
		finished.setCancelable(false);
		finished.setCanceledOnTouchOutside(false);
		finished.setContentView(R.layout.game_over);
		Button mainMenu = (Button) finished.findViewById(R.id.ReturnToMenu);
		Button playAgain = (Button) finished.findViewById(R.id.playAgain);
		
		SharedPreferences preferences  =  PreferenceManager.getDefaultSharedPreferences(this);
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putInt("numCorrectAnswers", preferences.getInt("numCorrectAnswers", 0) + numCorrectAnswers);
	    editor.putInt("numIncorrectAnswers", preferences.getInt("numIncorrectAnswers", 0) + numIncorrectAnswers);
	    editor.putInt("totalQuizies", preferences.getInt("totalQuizies", 0) + 1);
	    editor.commit();


		mainMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		playAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		});

		finished.show();
		String averageTimeString = null;
		if (numberOfQuestions == 0) {
			averageTimeString = "infinity";
		} else {
			averageTimeString = getTimerString(STARTTIME / numberOfQuestions);
		}
		TextView correctAnswers = (TextView) finished
				.findViewById(R.id.correctQuestions);
		TextView incorrectAnswers = (TextView) finished
				.findViewById(R.id.wrongQuestions);
		TextView averageTime = (TextView) finished
				.findViewById(R.id.averageTime);
		correctAnswers.setText("Correct answers: " + numCorrectAnswers);
		incorrectAnswers.setText("Incorrect answers: " + numIncorrectAnswers);
		averageTime.setText("Average time: " + averageTimeString);
		// dialogText.setTextColor(Color.parseColor("#FF0000"));
		// dialogText.setText("Wrong!");

	}

	private void clearAnswers() {
		answer1.setChecked(false);
		answer2.setChecked(false);
		answer3.setChecked(false);
		answer4.setChecked(false);
	}

	private void nextQuestion() {
		String[] question = questions.generateQuestion();

		questionText.setText(question[0]);

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(Integer.valueOf(1));
		list.add(Integer.valueOf(2));
		list.add(Integer.valueOf(3));
		list.add(Integer.valueOf(4));

		int location = r.nextInt(list.size());
		int correctNumber = list.get(location);
		list.remove(location);

		switch (correctNumber) {
		case 1:
			correctAnswer = answer1.getId();
			assignText(1, question[1]);
			break;
		case 2:
			correctAnswer = answer2.getId();
			assignText(2, question[1]);

			break;
		case 3:
			correctAnswer = answer3.getId();
			assignText(3, question[1]);

			break;
		case 4:
			correctAnswer = answer4.getId();
			assignText(4, question[1]);

			break;
		}
		int falseQuestionNumber = 2; // second string in question string array
										// is a false answer
		while (list.size() > 0) {
			int locationFalse = r.nextInt(list.size());
			int answerNumber = list.get(locationFalse);
			list.remove(locationFalse);
			assignText(answerNumber, question[falseQuestionNumber]);
			falseQuestionNumber++;
		}
	}

	public void assignText(int i, String text) {
		switch (i) {
		case 1:
			answer1.setText(text);
			break;
		case 2:
			answer2.setText(text);
			break;
		case 3:
			answer3.setText(text);
			break;
		case 4:
			answer4.setText(text);
			break;
		}
	}

	View.OnClickListener clickHandler = new View.OnClickListener() {
		public void onClick(View v) {
			numberOfQuestions++;

			timer.cancel();

			if (((RadioButton) v).getId() == correctAnswer) {
				numCorrectAnswers++;
				dialog.show();
				TextView dialogText = (TextView) dialog
						.findViewById(R.id.dialogText);
				dialogText.setTextColor(Color.parseColor("#009900"));
				dialogText.setText("Correct!");
			} else {
				numIncorrectAnswers++;
				dialog.show();
				TextView dialogText = (TextView) dialog
						.findViewById(R.id.dialogText);
				dialogText.setTextColor(Color.parseColor("#FF0000"));
				dialogText.setText("Wrong!");
			}
		}
	};

	/*
	 * Takes in the number of milliseconds left on the timer, and converts them
	 * into a displayable string format
	 */
	public static String getTimerString(long millSecondsLeft) {
		int secondsLeft = (int) (millSecondsLeft / 1000);
		int seconds = secondsLeft % 60;
		int minutes = (int) (secondsLeft / 60);
		if (seconds < 10) {
			return Integer.toString(minutes) + ":0" + Integer.toString(seconds);
		} else {
			return Integer.toString(minutes) + ":" + Integer.toString(seconds);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putLong("timeLeft", timeLeft);
		savedInstanceState.putBoolean("hasFinished", hasFinished);
		savedInstanceState.putInt("numCorrectAnswers", numCorrectAnswers);
		savedInstanceState.putInt("numIncorrectAnswers", numIncorrectAnswers);
		savedInstanceState.putInt("numberOfQuestions", numberOfQuestions);

		savedInstanceState.putBoolean("hasQuestion", true);
		savedInstanceState.putInt("correctAnswer", correctAnswer);
		savedInstanceState.putString("question",
				String.valueOf(questionText.getText()));
		if (dialog.isShowing()) {
			savedInstanceState.putString("dialogText", String
					.valueOf(((TextView) dialog.findViewById(R.id.dialogText))
							.getText()));
			savedInstanceState.putBoolean("showDialog", true);
			savedInstanceState
					.putString("dialogTextColor", String
							.valueOf(((TextView) dialog
									.findViewById(R.id.dialogText))));
		} else
			savedInstanceState.putBoolean("showDialog", false);
		
		if (timer != null)
			timer.cancel();
		
		savedInstanceState.putString("clockText", String.valueOf(timerText.getText()));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (timer != null)
			timer.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("quiz", "On resume was called");

		Log.d("quiz", "On create, hasquestion is" + hasQuestion);
		if (!hasQuestion) {
			nextQuestion();
			Log.d("quiz", "Next question was called");
			hasQuestion = true;
		}

		if (showDialog) {
			showDialog = false;
			setDialog();
			((TextView) dialog.findViewById(R.id.dialogText)).setText(text);
			if (text.equals("Wrong!")) {
				((TextView) dialog.findViewById(R.id.dialogText))
						.setTextColor(Color.parseColor("#FF0000"));
			} else {
				((TextView) dialog.findViewById(R.id.dialogText))
						.setTextColor(Color.parseColor("#009900"));

			}
			dialog.show();
		} else {
			if (timeLeft != 0) {
			} else if (!hasFinished) {
				timeLeft = STARTTIME;
			} else {
			}

			timer = new CountDownTimer(timeLeft, 100) {

				public void onTick(long millisUntilFinished) {
					timeLeft = millisUntilFinished;
					timerText.setText(getTimerString(millisUntilFinished));
				}

				public void onFinish() {
					gameFinished();
				}
			}.start();
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d("quiz", "Restore saved instance was called");
		timeLeft = savedInstanceState.getLong("timeLeft");
		Log.d("quiz", "On restore instance, has question is " + hasQuestion);
		hasFinished = savedInstanceState.getBoolean("hasFinished");
		numCorrectAnswers = savedInstanceState.getInt("numCorrectAnswers");
		numIncorrectAnswers = savedInstanceState.getInt("numIncorrectAnswers");
		numberOfQuestions = savedInstanceState.getInt("numberOfQuestions");

		hasQuestion = savedInstanceState.getBoolean("hasQuestion");
		correctAnswer = savedInstanceState.getInt("correctAnswer");
		questionText.setText(savedInstanceState.getString("question"));

		showDialog = savedInstanceState.getBoolean("showDialog");
		showFinished = savedInstanceState.getBoolean("showFinished");
		text = savedInstanceState.getString("dialogText");
		
		timerText.setText(savedInstanceState.getString("clockText"));
		
		

		// if (showFinished)
		// finished.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}
}