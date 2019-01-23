package com.example.arnab.movieinfoimdb;

import android.content.Intent;
import android.os.AsyncTask;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
        import android.widget.Toast;

        import org.jsoup.Jsoup;
        import org.jsoup.select.Elements;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.concurrent.ExecutionException;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    String url ="";
    TextView searchContent;
    TextView RatingBox;
    TextView MovieNameBox;
    TextView MovieGenreBox;
    TextView MovieSynopsis;
    TextView MovieDirector;

    TextView MovieNameHeader;
    TextView MovieRatingHeader;
    TextView MovieGenreHeader;
    TextView MovieDirectorHeader;
    TextView MovieSynopsisHeader;
    Button backButton;
    LinearLayout layout_one;
    LinearLayout layout_two;

    public void searchFunction(View view) throws ExecutionException, InterruptedException {
        searchContent = findViewById(R.id.InputBox);
        String search = searchContent.getText().toString();
        if (search.equals("Name of movie/tv series") || (search.equals(""))){
            Toast.makeText(this, "Please enter the name of the movie to be searched!!!", Toast.LENGTH_SHORT).show();
        }
        else{
            String url1 = "https://www.google.co.in/search?&q=";
            String url2 = "&ie=UTF-8&oe=UTF-8";
            url = url1 + search + "+imdb" + url2;
            Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
            new GoogleSearch().execute();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RatingBox = (TextView)findViewById(R.id.MovieRatingView);
        MovieNameBox = (TextView) findViewById(R.id.MovieNameView);
        MovieGenreBox = (TextView) findViewById(R.id.MovieGenreView);
        MovieSynopsis = (TextView) findViewById(R.id.SynopsisView);
        MovieDirector = (TextView) findViewById(R.id.MovieDirectorView);
        MovieNameHeader = findViewById(R.id.MovieTitle);
        MovieRatingHeader = findViewById(R.id.RatingTitle);
        MovieGenreHeader = findViewById(R.id.GenreTitle);
        MovieDirectorHeader = findViewById(R.id.DirectorTitle);
        MovieSynopsisHeader = findViewById(R.id.SynopsisTitle);
        backButton = findViewById(R.id.BackButton);
        layout_one = findViewById(R.id.linearLayout);
        layout_one.setVisibility(View.INVISIBLE);
        layout_two = findViewById(R.id.linearLayout2);
        layout_two.setVisibility(View.INVISIBLE);
    }

    public void backButtonFunction(View view){
        finish();
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
    }

    public class GoogleSearch extends AsyncTask<Void, Void, Void> {
        String words ="";
        String info = "";
        String Genre = "";
        String name = "";
        String rating_movie = "";
        String synopsis = "";
        String director  = "";


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                words =  doc.text();
                Pattern url_pattern = Pattern.compile("https://www.imdb.com/title/(.*?)/");
                Matcher url_matcher = url_pattern.matcher(words);
                ArrayList<String> ListOfUrls = new ArrayList<>();
                while(url_matcher.find()){
                    ListOfUrls.add(url_matcher.group());
                }

                int length_of_array_list = ListOfUrls.size();
                for(int i = 0;i < length_of_array_list;i++){
                    Log.i("The urls of imdb scrapped are : ", ListOfUrls.get(i));
                }

                if(ListOfUrls.size()>0) {

                    String imdbUrl = ListOfUrls.get(0);
                    Log.i("UrlOfMovie", imdbUrl);
                    String Doc2 = Jsoup.connect(imdbUrl).get().html();
                    org.jsoup.nodes.Document Doc3 = Jsoup.parse(Doc2);//parses the html document

                    //To get rating of Movie
                    final Elements rating = ((org.jsoup.nodes.Document) Doc3).select("div.imdbRating span");
                    rating_movie = rating.get(0).text();

                    //To get Name of Movie
                    Elements names = Doc3.select("div.title_wrapper h1");
                    name = names.get(0).text();

                    //To get Genre
                    final Elements genre = Doc3.select("div.subtext a");
                    int n = genre.size();
                    for (int i = 0; i < n - 1; i++) {
                        if (i != n - 2) {
                            Genre += genre.get(i).text() + ",";
                        } else
                            Genre += genre.get(i).text();
                    }


                    Elements Director = Doc3.select("div.plot_summary_wrapper div.plot_summary div.credit_summary_item a[href]");
                    director = Director.get(0).text();

                    Elements Synopsis = Doc3.select("div.plot_summary  div.summary_text");
                    synopsis = Synopsis.get(0).text();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout_one.setVisibility(View.VISIBLE);
                            layout_two.setVisibility(View.VISIBLE);
                            MovieNameHeader.setText("NAME :");
                            MovieRatingHeader.setText("RATING :");
                            MovieGenreHeader.setText("GENRE :");
                            MovieDirectorHeader.setText("DIRECTOR :");
                            MovieSynopsisHeader.setText("SYNOPSIS :");
                            MovieDirector.setText(director);
                            MovieNameBox.setText(name);
                            MovieSynopsis.setText(synopsis);
                            RatingBox.setText(rating_movie);
                            MovieGenreBox.setText(Genre);
                        }
                    });

                }
                else{
                    Toast.makeText(SearchActivity.this, "The Data you searched for was not a movie or Tv Series!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
