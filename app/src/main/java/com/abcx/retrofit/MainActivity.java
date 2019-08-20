package com.abcx.retrofit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDB;

    private TextView textViewResult,textDes,tempDetails;
    private ImageView imageView;
    private AutoCompleteTextView cityName;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private String icon;
    private ImageButton searchButton;
    private Double temp;
    private Example example;
    private String city_Name,iconn,description,tempp,pressure,humidity,minT,maxT;
    private StringBuffer stringBuffer = new StringBuffer();
    private StringBuffer offlineDes = new StringBuffer();
    private ArrayList<String> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);
        Cursor list = myDB.getAllData();
        while(list.moveToNext()){
            cities.add(list.getString(0));
        }

        textViewResult = findViewById(R.id.text_view_result);
        cityName = findViewById(R.id.editText);
        cityName.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cities));
        imageView = findViewById(R.id.imageView);
        searchButton = findViewById(R.id.searchButton);
        textDes = findViewById(R.id.textDes);
        tempDetails = findViewById(R.id.tempDetails);




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        viewAllData();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosts();
                hideKeyboard(v);
                    }
        });
        //getPosts();
        //getComments();
        //createPost();
    }

    public void viewAllData(){
        Cursor result = myDB.getAllData();
        if (result.getCount()==0){
            //Toast.makeText(MainActivity.this,"No Data Found",Toast.LENGTH_SHORT).show();
            showMessage("Error","No Data Found");
            return;
        }

        if (result.moveToLast())

        {
            stringBuffer.append("Temperature " + result.getString(3) + " \n");
            stringBuffer.append("Pressure: " + result.getString(4) + "\n");
            stringBuffer.append("Humidity: " + result.getString(5) + "\n");
            stringBuffer.append("Min. Temp: " + result.getString(6) +"\n");
            stringBuffer.append("Max. Temp: " + result.getString(7) + "\n\n");
        }

        tempDetails.setText(stringBuffer);
        textViewResult.setText(result.getString(0));
        textDes.setText(result.getString(2));

        String iconUrl = "http://openweathermap.org/img/w/" + result.getString(1) + ".png";
        Picasso.get().load(iconUrl).into(imageView);
    }

    public void showMessage(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void getPosts(){
        textViewResult.setText("");
        imageView.setImageDrawable(null);
        textDes.setText("");
        tempDetails.setText("");
        Call<Example> call = jsonPlaceHolderApi.getPosts(cityName.getText().toString(), "53e500a11af5087a55e53a9ea6a43f77");

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if(!response.isSuccessful()){

                    textViewResult.setText("Unknown City");
                    textDes.setText("Details not found!");
                    imageView.setImageResource(R.drawable.ic_action_error);
                    //Toast.makeText(MainActivity.this,"Response is not Successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                example = response.body();
                temp = example.getMain().getTemp() - 273.15;
                tempp =  String.format("%.2f", temp) + " °C";
                pressure = example.getMain().getPressure().toString();
                humidity = example.getMain().getHumidity().toString();
                minT = String.format("%.2f", (example.getMain().getTempMin() - 273.15)) + " °C";
                maxT = String.format("%.2f", (example.getMain().getTempMax() - 273.15)) + " °C ";

                    String content = "";
                    content += "Temperature " + tempp + " \n";
                    content += "Pressure: " + pressure + "\n";
                    content += "Humidity: " + humidity + "\n";
                    content += "Min. Temp: " + minT +"\n";
                    content+= "Max. Temp: " + maxT+ "\n\n";


                    tempDetails.setText(content);
                    city_Name = example.getName();
                textViewResult.setText(city_Name);

                description = example.getWeather().get(0).getDescription();
                textDes.setText(description);

                    icon = example.getWeather().get(0).getIcon();
                String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                Picasso.get().load(iconUrl).into(imageView);

                boolean isInserted = myDB.insertData(
                        city_Name,
                        icon,
                        description,
                        tempp,
                        pressure,
                        humidity,
                        minT,
                        maxT);

                if(isInserted == true){
                    Cursor result = myDB.getAllData();
                    Toast.makeText(MainActivity.this, city_Name +"'s Data is Stored",Toast.LENGTH_SHORT).show();

                }

                else

                    Toast.makeText(MainActivity.this,"Data is not  Stored",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {



                Cursor offline = myDB.getWeather(cityName.getText().toString());
                if(offline.getCount()==1){
                    if (offline.moveToFirst()){
                        offlineDes.append("Temperature " + offline.getString(3) + " \n");
                        offlineDes.append("Pressure: " + offline.getString(4) + "\n");
                        offlineDes.append("Humidity: " + offline.getString(5) + "\n");
                        offlineDes.append("Min. Temp: " + offline.getString(6) +"\n");
                        offlineDes.append("Max. Temp: " + offline.getString(7) + "\n\n");
                    }
                    tempDetails.setText("");
                    tempDetails.clearComposingText();
                    tempDetails.setText(stringBuffer);
                    textViewResult.setText(offline.getString(0));
                    textDes.setText(offline.getString(2));

                    String iconUrl = "http://openweathermap.org/img/w/" + offline.getString(1) + ".png";
                    Picasso.get().load(iconUrl).into(imageView);
//
                }

                else{
                    textViewResult.setText("Details Not Found");
                    textDes.setText("Check your internet connection");
                    imageView.setImageResource(R.drawable.ic_action_error);

                }
            }
        });
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }

    private void getComments(){
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments(3);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Comment> comments = response.body();

                for(Comment comment : comments){
                    String content="";
                    content += "ID: "+comment.getId()+"\n";
                    content += "PostID: " + comment.getPostId()+"\n";
                    content += "Name: " + comment.getName()+"\n";
                    content += "Email: " + comment.getEmail()+"\n";
                    content += "Text: " + comment.getText()+"\n\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

                textViewResult.setText(t.getMessage());
            }
        });
    }


   /* private void createPost() {
        Post post = new Post(23,"New Title","New Text");
        Call<Post> call = jsonPlaceHolderApi.createPost(23,"New Title", "New Text");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()){
                    textViewResult.setText("Code: "+response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "ID: " + postResponse.getId() + "\n";
                content += "User Id: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";

                textViewResult.append(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());

            }
        });
    }*/


}
