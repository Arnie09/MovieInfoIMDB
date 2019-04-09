package com.example.arnab.movieinfoimdb;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
        import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
        import org.jsoup.select.Elements;
        
        import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String url ="";
    TextView searchContent;
    TextView RatingBox;
    TextView MovieNameBox;
    TextView MovieGenreBox;
    TextView MovieSynopsis;
    TextView MovieDirector;

    Button favouriteButton;
    Button todoButton;
    LinearLayout layout_one;

    String words ="";
    String info = "";
    String Genre = "";
    String name = "";
    String rating_movie = "";
    String synopsis = "";
    String director  = "";

    FirebaseAuth firebaseauthenticator;
    FirebaseUser user;
    FirebaseFirestore db;
    String User_ID;
    Map<String,Object> movie_data = new HashMap<>();

    String User_name = "";

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
            //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
            new GoogleSearch().execute();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RatingBox = (TextView)findViewById(R.id.MovieRatingView);
        MovieNameBox = (TextView) findViewById(R.id.MovieNameView);
        MovieGenreBox = (TextView) findViewById(R.id.MovieGenreView);
        MovieSynopsis = (TextView) findViewById(R.id.SynopsisView);
        MovieDirector = (TextView) findViewById(R.id.MovieDirectorView);
        favouriteButton = findViewById(R.id.favouriteButton);
        todoButton = findViewById(R.id.todoButton);
        favouriteButton.setVisibility(View.INVISIBLE);
        todoButton.setVisibility(View.INVISIBLE);
        layout_one = findViewById(R.id.linearLayout);
        layout_one.setVisibility(View.INVISIBLE);

        //final TextView user_name_to_show = findViewById(R.id.userName);
        firebaseauthenticator = FirebaseAuth.getInstance();
        user = firebaseauthenticator.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        User_ID = user.getUid();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getBackground().setAlpha(250);
        navigationView.setNavigationItemSelectedListener(this);

        db.collection("UserDatabase").document(User_ID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            User_name = documentSnapshot.getString("Name");
                            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View vi = inflater.inflate(R.layout.nav_header_main,null);
                            TextView tv = findViewById(R.id.userName);
                            tv.setText("Welcome "+User_name);
                            Log.i("SearchActivity :",User_name);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }
        else if(id == R.id.logout){
            firebaseauthenticator.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(),LogInActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



    public void favouriteFunction(View view){
        if(movie_data.isEmpty()){
            return;
        }
        db.collection("UserFavourites").document(User_ID).collection("Favourites").document(name)
                .set(movie_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SearchActivity.this, "Added to favourites!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void toWatch(View view){
        if(movie_data.isEmpty()){
            return;
        }
        db.collection("UserToWatchList").document(User_ID).collection("ToWatch").document(name)
                .set(movie_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SearchActivity.this, "Added to toWatch!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.favourites) {
            //open favourites aactivity!
            startActivity(new Intent(getApplicationContext(),FavouriteActivity.class));
        }
        else if(id == R.id.History){
            //open history activity
            startActivity((new Intent(getApplicationContext(),HistoryActivity.class)));
        }
        else if(id == R.id.ToWatch){
            //open history activity
            startActivity((new Intent(getApplicationContext(),ToWatchActivity.class)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GoogleSearch extends AsyncTask<Void, Void, Void> {



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
                            favouriteButton.setVisibility(View.VISIBLE);
                            todoButton.setVisibility(View.VISIBLE);
                            MovieDirector.setText("Director: "+director);
                            MovieNameBox.setText("Name: "+name);
                            MovieSynopsis.setText("Synopsis: "+synopsis);
                            RatingBox.setText("Rating: "+rating_movie);
                            MovieGenreBox.setText("Genre: "+Genre);

                            new AddtoHistory().execute();
                        }
                    });

                }
                else{
                    Toast.makeText(SearchActivity.this, "The Data you searched for was not a movie or Tv Series!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchActivity.this, "Sorry doesnot exist!", Toast.LENGTH_SHORT).show();

                    }
                });
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class AddtoHistory extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            movie_data.put("Name",name);
            movie_data.put("Rating",rating_movie);
            movie_data.put("Genre",Genre);
            movie_data.put("Director",director);
            movie_data.put("Synopsis",synopsis);

            db.collection("UserHistory").document(User_ID).collection("History").document(name)
                    .set(movie_data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SearchActivity.this, "Added to history!", Toast.LENGTH_SHORT).show();
                        }
                    });
            return null;
        }
    }

}
