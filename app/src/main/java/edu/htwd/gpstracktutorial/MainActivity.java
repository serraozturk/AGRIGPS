package edu.htwd.gpstracktutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //create dropdow
    private Spinner dropdown;
    //Buton to start activitys
    private Button move;

    //Variable to know the activity to do
    Integer titlea = 0;



    //List of activitys for the dropdown
    String[] activitysitems = new String[]{"Plant the crops", "Collect the crops", "Prepare the Earth"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get the spinner from the xml.
        dropdown = (Spinner)findViewById(R.id.spinneractivitys);

        //adapter for the correct work of the dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activitysitems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //set the adapter in the spinner
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);


        move = findViewById(R.id.Move);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(adapter);
                String message = activitysitems[titlea];
                Intent intent=new Intent(MainActivity.this, GPSView.class);
                intent.putExtra("farmactivity", message);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                titlea = 0;
                break;
            case 1:
                titlea = 1;
                break;
            case 2:
                titlea = 2;
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

}