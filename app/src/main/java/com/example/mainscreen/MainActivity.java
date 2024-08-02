package com.example.mainscreen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private LinearLayout contentLayout;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Apply window insets to adjust padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Initialize spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.city_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Set default selection to "Seoul"
        spinner.setSelection(adapter.getPosition("Seoul"));

        // Initialize content layout
        contentLayout = findViewById(R.id.contentLayout);

        // Initialize executor service
        executorService = Executors.newFixedThreadPool(4);

        // Set up category buttons
        setupCategoryButton(R.id.korean, new int[]{
                R.drawable.ajju,
                R.drawable.tosokchon,
                R.drawable.sintoburi,
                R.drawable.daol_charcoal_grill,
                R.drawable.sangdo_tripes
        }, new String[]{
                "Ajju",
                "Tosokchon Samgyetang",
                "Sintoburi Rice Cake Boki",
                "Daol Charcoal Grill",
                "Sangdo Tripes"
        }, "#E24443");

        setupCategoryButton(R.id.chinese, new int[]{
                R.drawable.haidilao,
                R.drawable.the_round,
                R.drawable.songhwa_lamb_skewers,
                R.drawable.jonny_dumpling,
                R.drawable.pf_chang
        }, new String[]{
                "Haidilao Hotpot",
                "The Round",
                "Songhwa Lamb Skewers",
                "Jonny Dumpling",
                "P.F Chang"
        }, "#E24443");

        setupCategoryButton(R.id.italian, new int[]{
                R.drawable.rosso_1924,
                R.drawable.made_in_chicago_pizza,
                R.drawable.downtowner,
                R.drawable.slowpark,
                R.drawable.schedule_cheongdam
        }, new String[]{
                "Rosso 1924",
                "Made In Chicago Pizza",
                "Downtowner",
                "Slow Park",
                "Schedule Cheongdam"
        }, "#E24443");

        setupCategoryButton(R.id.japanese, new int[]{
                R.drawable.kandasoba,
                R.drawable.tamayura,
                R.drawable.ran_susa,
                R.drawable.izakayakyum,
                R.drawable.sushitano
        }, new String[]{
                "Kanda Soba",
                "Tamayura",
                "Ran Susa",
                "Izakaya Kyum",
                "Sushi Tano"
        }, "#E24443");

        setupCategoryButton(R.id.fushion, new int[]{
                R.drawable.tokkijung,
                R.drawable.vatos,
                R.drawable.bar,
                R.drawable.sanok,
                R.drawable.yoongong_korea_bistro
        }, new String[]{
                "Tokkijung",
                "Vatos",
                "5412",
                "Sanok",
                "Yoongong Korea Bistro"
        }, "#E24443");

        setupCategoryButton(R.id.asian, new int[]{
                R.drawable.camouflage,
                R.drawable.khaosan,
                R.drawable.new_delhi,
                R.drawable.flavour_town,
                R.drawable.asian_table
        }, new String[]{
                "Camouflage",
                "KHAOSAN",
                "New Delhi",
                "Flavor Town",
                "Asian Table"
        }, "#E24443");

        setupCategoryButton(R.id.viewmore, new int[]{
                R.drawable.the_halal_guys,
                R.drawable.mexicali,
                R.drawable.dubai_restaurant,
                R.drawable.durga,
                R.drawable.couscous
        }, new String[]{
                "The Halal Guys",
                "Mexicali",
                "Dubai Restaurant",
                "Durga",
                "CousCous"
        }, "#E24443");

        // Initialize image click listeners
        initializeImageClickListeners();
    }

    private void setupCategoryButton(int buttonId, int[] imageResIds, String[] texts, String colorHex) {
        Button button = findViewById(buttonId);

        button.setOnClickListener(v -> {
            handleButtonClick(button, colorHex);
            executorService.submit(() -> loadContent(imageResIds, texts));
        });
    }

    private void handleButtonClick(Button button, String colorHex) {
        int color = Color.parseColor(colorHex);
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.round);
        drawable.setColor(color);
        button.setBackground(drawable);
        resetOtherButtons(button);
    }

    private void resetOtherButtons(Button clickedButton) {
        Button[] buttons = {
                findViewById(R.id.korean),
                findViewById(R.id.chinese),
                findViewById(R.id.italian),
                findViewById(R.id.japanese),
                findViewById(R.id.fushion),
                findViewById(R.id.asian),
                findViewById(R.id.viewmore)
        };
        for (Button button : buttons) {
            if (button != clickedButton) {
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_design));
            }
        }
    }

    private void loadContent(int[] imageResIds, String[] texts) {
        runOnUiThread(() -> {
            contentLayout.removeAllViews(); // Clear existing views

            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            for (int i = 0; i < imageResIds.length; i++) {
                View itemView = inflater.inflate(R.layout.button_item, contentLayout, false);

                ImageView imageView = itemView.findViewById(R.id.button_image);
                Glide.with(MainActivity.this)
                        .load(imageResIds[i])
                        .apply(new RequestOptions().override(250, 200)) // Image size adjustment
                        .into(imageView);

                TextView textView = itemView.findViewById(R.id.button_text);
                textView.setText(texts[i]);

                Typeface typeface = Typeface.create("casual", Typeface.NORMAL);
                textView.setTypeface(typeface);

                contentLayout.addView(itemView);
            }
        });
    }

    private void initializeImageClickListeners() {
        setupClickListener(R.id.book, RestaurantsActivity.class);
        setupClickListener(R.id.wait, WaitActivity.class);
        setupClickListener(R.id.hotdeals, HotdealsActivity.class);
        setupClickListener(R.id.guide, GuideActivity.class);
        setupClickListener(R.id.search, SearchActivity.class);
        setupClickListener(R.id.profile, ProfileActivity.class);
        setupClickListener(R.id.home, null); // Special case for "home" button
    }

    private void setupClickListener(int imageViewId, Class<?> RestaurantsActivity) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setOnClickListener(view -> {
            if (RestaurantsActivity != null) {
                Intent intent = new Intent(getApplicationContext(), RestaurantsActivity);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "You are already on the home screen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear any images loaded by Glide from contentLayout
        Glide.with(this).clear(contentLayout);
        executorService.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button color to default
        resetButtonColors();
        // Set spinner selection to "Seoul"
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        spinner.setSelection(adapter.getPosition("Seoul"));
    }

    private void resetButtonColors() {
        // You can implement custom button color reset logic here if needed.
        // For example, resetting all buttons to a default color or drawable.
        Button[] buttons = {
                findViewById(R.id.korean),
                findViewById(R.id.chinese),
                findViewById(R.id.italian),
                findViewById(R.id.japanese),
                findViewById(R.id.fushion),
                findViewById(R.id.asian),
                findViewById(R.id.viewmore)
        };
        for (Button button : buttons) {
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_design));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCity = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, "Selected city: " + selectedCity, Toast.LENGTH_SHORT).show();

        Intent intent = null;
        switch (selectedCity) {
            case "Seoul":
                return; // Do nothing if "Seoul" is selected
            case "Jeju":
                intent = new Intent(this, JejuActivity.class);
                break;
            case "Busan":
                intent = new Intent(this, BusanActivity.class);
                break;
            // Optionally add more cases if needed
            default:
                Toast.makeText(this, "City not recognized", Toast.LENGTH_SHORT).show();
                return; // Exit if the city is not recognized
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed
    }
}
