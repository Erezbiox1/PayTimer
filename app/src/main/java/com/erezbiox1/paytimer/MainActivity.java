package com.erezbiox1.paytimer;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import com.erezbiox1.paytimer.EditShift.EditShiftActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import static com.erezbiox1.paytimer.EditShift.EditShiftActivity.*;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ImageView anchor, startSymbol;
    private Animation rotatingAnimation;

    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        anchor = findViewById(R.id.ic_anchor);
        startButton = findViewById(R.id.start_shift);
        startSymbol = findViewById(R.id.start_symbol);

        rotatingAnimation = AnimationUtils.loadAnimation(this, R.anim.rotating);
        final AnimatedVectorDrawable startingAnimation = (AnimatedVectorDrawable) startSymbol.getDrawable();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning){
                    anchor.startAnimation(rotatingAnimation);
                    startingAnimation.start();
                    startButton.setText(R.string.stop_now);
                } else {
                    anchor.clearAnimation();
                    startingAnimation.reset();
                    startButton.setText(R.string.start_now);

                    Intent intent = new Intent(MainActivity.this, EditShiftActivity.class);
                    startActivity(intent);
                }

                isRunning = !isRunning;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
