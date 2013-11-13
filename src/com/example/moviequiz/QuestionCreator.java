package com.example.moviequiz;

import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuestionCreator {
	DbAdapter dbAdapter;
	SQLiteDatabase db; //laziness that should be fixed
	Random r = new Random();

	public QuestionCreator(Context ctx) {
		dbAdapter = new DbAdapter(ctx);
		db = dbAdapter.mDb;
	}
	
	
	//Question
	//Answer 
	// 3 Wrong answers	
	public String[] generateQuestion() {
		String[] question = null;
		int nextMethod = r.nextInt(10);
		
		switch (nextMethod) {
		case 0:
			question = whoDirectedMovie();
			break;
		case 1:
			question = starsAppearedTogether();
			break;
		case 2:
			question = whenMovieReleased();
			break;
		case 3:
			question = whichStarInMovie();
			break;
		case 4:
			question = whichStarNotInMovie();
			break;
		case 5:
			question = whoDirectedStar();
			break;
		case 6:
			question = didNotDirectStar();
			break;
		case 7:
			question = appearsInMovies();
			break;
		case 8:
			question = appearNotInMovieWithStar();
			break;
		case 9:
			question = directedStarXInY();
			break;
		}
		String[] test = {"Question","answer","fake1","fake2","fake3"};	
		return question;
		
	}
	
	private String[] directedStarXInY() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select first_name, last_name, director, star_id, year from stars_in_movies join movies on stars_in_movies.movie_id = movies.id join stars on stars_in_movies.star_id = stars.id ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		String starName = c.getString(0) + " " + c.getString(1);
		String correct = c.getString(2);
		int starID = c.getInt(3);
		int year = c.getInt(4);
		
		c = db.rawQuery("select distinct director from movies join stars_in_movies on movies.id = stars_in_movies.movie_id join stars on stars.id = stars_in_movies.star_id where NOT (year = " + year + " AND star_id = " + starID + ") ORDER BY RANDOM() LIMIT 3;", null);
		c.moveToFirst();
		String fake1 = c.getString(0);
		c.moveToNext();
		String fake2 = c.getString(0);
		c.moveToNext();
		String fake3 = c.getString(0);
		
		question[0] = "Who directed the star " + starName + " in year " + year +"?";
		question[1] = correct;
		question[2] = fake1;
		question[3] = fake2;
		question[4] = fake3;
		return question;
	}


	private String[] appearNotInMovieWithStar() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select distinct star_id, first_name, last_name from stars_in_movies join stars on stars.id = stars_in_movies.star_id GROUP BY movie_id HAVING count(distinct star_id) > 3 ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		int starID = c.getInt(0);
		String name = c.getString(1) + " " + c.getString(2);
		c = db.rawQuery("select distinct first_name, last_name from stars_in_movies join stars on stars.id = stars_in_movies.star_id  where movie_id in (select movie_id from stars_in_movies where star_id = " + starID + ") AND NOT star_id = " + starID + " ORDER BY RANDOM() LIMIT 3;", null);
		c.moveToFirst();
		Log.d("quiz", "star ID IS " + starID);
		String fake1 = c.getString(0) + " " + c.getString(1);
		c.moveToNext();
		String fake2 = c.getString(0) + " " + c.getString(1);
		c.moveToNext();
		String fake3 = c.getString(0) + " " + c.getString(1);
		c = db.rawQuery("select distinct first_name, last_name from stars_in_movies join stars on stars.id = stars_in_movies.star_id where movie_id NOT in (select movie_id from stars_in_movies where star_id = " + starID + ") AND NOT star_id = " + starID + " ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		String correct = c.getString(0) + " " + c.getString(1);
		
		question[0] = "Which star did NOT appear in the same movie with the star " + name + "?";
		question[1] = correct;
		question[2] = fake1;
		question[3] = fake2;
		question[4] = fake3;
		return question;
	}


	private String[] appearsInMovies() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select first_name, last_name, star_id from stars JOIN stars_in_movies ON stars.id = stars_in_movies.star_id group by star_id HAVING count(distinct movie_id) > 1 ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		String correctName = c.getString(0) + " " + c.getString(1);
		int starID = c.getInt(2);
		c = db.rawQuery("select movie_id, title from stars_in_movies join movies on movies.id = stars_in_movies.movie_id where star_id = " + starID + " ORDER BY RANDOM() LIMIT 2;", null);
		c.moveToFirst();
		int movieID1 = c.getInt(0);
		String movieTitle1 = c.getString(1);
		c.moveToNext();
		int movieID2 = c.getInt(0);
		String movieTitle2 = c.getString(1);
		c = db.rawQuery("select first_name, last_name from stars_in_movies join stars on stars_in_movies.star_id = stars.id where NOT (movie_id = " + movieID1 + " AND movie_id = " + movieID2 + ") AND NOT star_id = " + starID + " ORDER BY RANDOM() LIMIT 3;", null);
		c.moveToFirst();
		String fake1 = c.getString(0) + " " + c.getString(1);
		c.moveToNext();
		String fake2 = c.getString(0) + " " + c.getString(1);
		c.moveToNext();
		String fake3 = c.getString(0) + " " + c.getString(1);

		question[0] = "Which star appears in both " + movieTitle1 + " and " + movieTitle2 + "?";
		question[1] = correctName;
		question[2] = fake1;
		question[3] = fake2;
		question[4] = fake3;
		
		return question;
	}


	private String[] didNotDirectStar() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select star_id, first_name, last_name from stars_in_movies INNER JOIN movies ON movies.id = stars_in_movies.movie_id join stars on stars.id = stars_in_movies.star_id GROUP BY star_id HAVING COUNT(distinct director) > 2 ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		int starID = c.getInt(0);
		String starName = c.getString(1) + " " + c.getString(2);
		c = db.rawQuery("select distinct director from movies inner join stars_in_movies on movies.id = stars_in_movies.movie_id WHERE movies.id IN (select movie_id from stars_in_movies where star_id = " + starID +") GROUP BY director ORDER BY RANDOM() LIMIT 3;",null);
		c.moveToFirst();
		Log.d("quiz","POSSIBLE STAR ID" + starID);
		String fake1 = c.getString(0);
		c.moveToNext();
		String fake2 = c.getString(0);
		c.moveToNext();
		String fake3 = c.getString(0);
		
		c = db.rawQuery("select director from movies inner join stars_in_movies on movies.id = stars_in_movies.movie_id WHERE movies.id NOT IN (select movie_id from stars_in_movies where star_id = " + starID + ") GROUP BY director ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		String correct = c.getString(0);

		question[0] = "Who did NOT direct the star " + starName + "?";
		question[1] = correct;
		question[2] = fake1;
		question[3] = fake2;
		question[4] = fake3;
		return question;
	}


	private String[] whoDirectedStar() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select star_id, first_name, last_name from stars_in_movies join stars on stars.id = stars_in_movies.star_id ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		int starId = c.getInt(0);
		String name = c.getString(1) + " " + c.getString(2);
		c = db.rawQuery("select director from movies inner join stars_in_movies on movies.id = stars_in_movies.movie_id WHERE movies.id IN (select movie_id from stars_in_movies where star_id = " + starId + ") GROUP BY director ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		String correctDirector = c.getString(0);
		c = db.rawQuery("select distinct director from movies inner join stars_in_movies on movies.id = stars_in_movies.movie_id WHERE movies.id NOT IN (select movie_id from stars_in_movies where star_id = " + starId + ") GROUP BY director ORDER BY RANDOM() LIMIT 3;",null);
		c.moveToFirst();
		String fakeOne = c.getString(0);
		c.moveToNext();
		String fakeTwo = c.getString(0);
		c.moveToNext();
		String fakeThree = c.getString(0);
		
		question[0] = "Who directed the star " + name + "?";
		question[1] = correctDirector;
		question[2] = fakeOne;
		question[3] = fakeTwo;
		question[4] = fakeThree;
		
		return question;
	}


	private String[] whichStarNotInMovie() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select movie_id,title from stars_in_movies join movies on stars_in_movies.movie_id = movies.id GROUP BY movie_id HAVING count(distinct star_id) > 2 ORDER BY RANDOM() LIMIT 1;", null);
		c.moveToFirst();
		int movieId = c.getInt(0);
		String title = c.getString(1);
		 c = db.rawQuery("select distinct star_id, first_name, last_name from stars_in_movies JOIN stars ON stars.id = stars_in_movies.star_id where movie_id = " + movieId + " ORDER BY RANDOM() LIMIT 3;", null);
		 c.moveToFirst();
		 String fake1 = c.getString(1) + " " + c.getString(2);
		 c.moveToNext();
		 String fake2 = c.getString(1) + " " + c.getString(2);
		 c.moveToNext();
		 String fake3 = c.getString(1) + " " + c.getString(2);
		 c = db.rawQuery("select star_id,  first_name, last_name from stars_in_movies JOIN stars ON stars.id = stars_in_movies.star_id where star_id NOT IN (select star_id from stars_in_movies where movie_id = " + movieId + ") ORDER BY RANDOM() LIMIT 1;", null);
		 c.moveToFirst();
		 String correctName = c.getString(1) + " " + c.getString(2);

		 question[0] = "Which star was NOT in movie " + title + "?";
		 question[1] = correctName;
		 question[2] = fake1;
		 question[3] = fake2;
		 question[4] = fake3;
		 
		 return question;
	}


	private String[] whichStarInMovie() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select title, movie_id,star_id, first_name, last_name from movies inner join stars_in_movies on stars_in_movies.movie_id = movies.id inner join stars on stars.id = stars_in_movies.star_id ORDER BY RANDOM() Limit 1;", null);
		c.moveToFirst();
		String movieTitle = c.getString(0);
		int movieId = c.getInt(1);
		String name = c.getString(3) + " " + c.getString(4);
		c = db.rawQuery("select distinct star_id,  first_name, last_name from stars_in_movies JOIN stars ON stars.id = stars_in_movies.star_id where star_id NOT IN (select star_id from stars_in_movies where movie_id = " + movieId + ") ORDER BY RANDOM() LIMIT 3;", null);
		c.moveToNext();
		String fake1 = c.getString(1) + " " + c.getString(2);
		c.moveToNext();
		String fake2 = c.getString(1) + " " + c.getString(2);
		c.moveToNext();
		String fake3 = c.getString(1) + " " + c.getString(2);
		
		question[0] = "Which star was in the movie " + movieTitle + "?";
		question[1] = name;
		question[2] = fake1;
		question[3] = fake2;
		question[4] = fake3;
		
		return question;

	}


	private String[] whenMovieReleased() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("SELECT title, year from movies GROUP BY year ORDER BY RANDOM() LIMIT 4;", null);
		c.moveToFirst();
		String title = c.getString(0);
		int correctYear = c.getInt(1);
		c.moveToNext();
		int fake1 = c.getInt(1);
		c.moveToNext();
		int fake2 = c.getInt(1);
		c.moveToNext();
		int fake3 = c.getInt(1);
		
		question[0] = "When was the movie " + title + " released?";
		question[1] = String.valueOf(correctYear);
		question[2] = String.valueOf(fake1);
		question[3] = String.valueOf(fake2);
		question[4] = String.valueOf(fake3);
		
		return question;
	}


	private String[] starsAppearedTogether() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select first_name, last_name, star_id, title from stars_in_movies, movies, stars where stars_in_movies.star_id = stars.id AND movies.id = stars_in_movies.movie_id AND movie_id IN (select movie_id from stars_in_movies JOIN movies on movies.id = stars_in_movies.movie_id GROUP BY movie_id having COUNT(distinct star_id) > 2 ORDER BY RANDOM() LIMIT 1) LIMIT 2;", null);
		c.moveToFirst();
		
		String nameOne = c.getString(0) + " " + c.getString(1);
		int idOne = c.getInt(2);
		String correctAnswerTitle = c.getString(3);
		c.moveToNext();
		String nameTwo = c.getString(0) + " " + c.getString(1);
		int idTwo = c.getInt(2);
		
		c = db.rawQuery("select distinct title from movies where movies.id NOT in (select movies.id from movies JOIN stars_in_movies where movie_id = movies.id AND star_id = " + idOne + " AND movie_id IN (select movie_id from stars_in_movies where star_id = " + idTwo + ")) ORDER BY RANDOM() LIMIT 3;", null);
		c.moveToFirst();
		String fakeOne = c.getString(0);
		c.moveToNext();
		String fakeTwo = c.getString(0);
		c.moveToNext();
		String fakeThree = c.getString(0);
		
		question[0] = "In which movie did the stars " + nameOne + " and " + nameTwo + " appear together?";
		question[1] = correctAnswerTitle;
		question[2] = fakeOne;
		question[3] = fakeTwo;
		question[4] = fakeThree;
		
		return question;
	}


	public String[] whoDirectedMovie() {
		String[] question = new String[5];
		Cursor c = db.rawQuery("select director, title from movies GROUP BY director ORDER BY RANDOM() LIMIT 4;", null);
		
		if(!c.moveToFirst()) {
			Log.d("quiz", "The cursor was empty");
		} else {
			Log.d("quiz", "The cursor has an item");

		}
		String director =  c.getString(0);
		String movie = c.getString(1);
		
		c.moveToNext();
		String fake1 = c.getString(0);
		c.moveToNext();
		String fake2 = c.getString(0);
		c.moveToNext();
		String fake3 = c.getString(0);
		c.close();
		question[0] = "Who directed the movie " + movie + " ?";
		question[1] = director;
		question[2] = fake1;
		question[3] = fake2;
		question[4] = fake3;	
		return question;
	}

}
