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

public class JejuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private LinearLayout contentLayout;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jeju);

        // Apply window insets to adjust padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_jeju), (v, insets) -> {
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

        // Set default selection to "Jeju" **수정사항**
        spinner.setSelection(adapter.getPosition("Jeju"));

        // Initialize content layout
        contentLayout = findViewById(R.id.contentLayout1);

        // Initialize executor service
        executorService = Executors.newFixedThreadPool(4);

        // Set up category buttons
        setupCategoryButton(R.id.korean, new int[]{
                R.drawable.jeju_gasiabangnoodle01,
                R.drawable.jeju_gojipdol_ureong_jungmun03,
                R.drawable.jeju_jeju_kitchen_meat_noodles_jeju_airport03,
                R.drawable.jeju_sukseongdo_jungmun01,
                R.drawable.jeju_yang_daegam01
        }, new String[]{
                "Gasiabang Noodle",
                "Gojipdol RockFish, Jungmun",
                "Jeju Kitchen Meat Noodles, Jeju Airport",
                "Sukseongdo, Jungmun",
                "YangDaegam"
        }, "#E24443");

        setupCategoryButton(R.id.chinese, new int[]{
                R.drawable.jeju_deokseongwon01,
                R.drawable.jeju_hamdeok_chinese_restaurant01,
                R.drawable.jeju_hyeopjae_myeon_chalong01,
                R.drawable.jeju_oiljangbanjeom01,
                R.drawable.jeju_sungsan_haenyeo_jjamppong01,
                R.drawable.jeju_yeontaeman01,
        }, new String[]{
                "DeokSeongWon",
                "Hamdeok Chinese Restaurant",
                "Hyeopjae Myeon Cha-Long",
                "OiljangBanjeom",
                "Sungsan HaeNyeo Jjamppong",
                "YeonTaeMan"
        }, "#E24443");

        setupCategoryButton(R.id.italian, new int[]{
                R.drawable.jeju_maison_glad_jeju_samdajeong01,
                R.drawable.jeju_narnia_restaurant03,
                R.drawable.jeju_western_noodles_shop01,
        }, new String[]{
                "Masion Glad Jeju SamDaJeong",
                "Narnia Restaurant",
                "Western Noodles Shop",
        }, "#E24443");

        setupCategoryButton(R.id.japanese, new int[]{
                R.drawable.jeju_dordor_jeju_seogwipo01,
                R.drawable.jeju_haesong_raw_fish_restaurant01,
                R.drawable.jeju_sungsan_takuma_sushi01,
                R.drawable.jeju_tongbalsungsan01,
        }, new String[]{
                "DorDor, Jeju Seogwipo",
                "Haesong Raw Fish Restaurant",
                "Sungsan Takuma Sushi",
                "TongBalSungsan",
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

            LayoutInflater inflater = LayoutInflater.from(JejuActivity.this);
            for (int i = 0; i < imageResIds.length; i++) {
                View itemView = inflater.inflate(R.layout.button_item, contentLayout, false);

                ImageView imageView = itemView.findViewById(R.id.button_image);
                Glide.with(JejuActivity.this)
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
        setupClickListener(R.id.book_jeju, Jeju_RestaurantActivity.class);
        setupClickListener(R.id.wait_jeju, WaitActivity.class);
        setupClickListener(R.id.hotdeals_jeju, HotdealsActivity.class);
        setupClickListener(R.id.guide_jeju, GuideActivity.class);
    }

    private void setupClickListener(int imageViewId, Class<?> RestaurantsActivity) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setOnClickListener(view -> {
            if (RestaurantsActivity != null) {
                Intent intent = new Intent(getApplicationContext(), RestaurantsActivity);
                startActivity(intent);
            } else {
                Toast.makeText(JejuActivity.this, "You are already on the home screen", Toast.LENGTH_SHORT).show();
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
        //ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        //spinner.setSelection(adapter.getPosition("Seoul"));
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
        //**변경사항
        switch (selectedCity) {
            case "Seoul":
                intent = new Intent(this, MainActivity.class);
                break;
            case "Jeju":
                return; // Do nothing if "Jeju" is selected
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
