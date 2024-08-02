package com.example.mainscreen;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class RestaurantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // RecyclerView 찾기
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // LayoutManager 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        RestaurantAdapter adapter = new RestaurantAdapter(getRestaurantList());
        recyclerView.setAdapter(adapter);
    }

    // 예시 레스토랑 리스트 메소드
    private List<Restaurant> getRestaurantList() {
        // 예시 데이터 생성
        return Arrays.asList(
                new Restaurant("Buddha's Belly", Arrays.asList(
                        R.drawable.buddhasbelly, R.drawable.buddhasbelly_2, R.drawable.buddhasbelly_3, R.drawable.buddhasbelly_4, R.drawable.buddhasbelly_5, R.drawable.buddhasbelly_6)),

                new Restaurant("The Margaux Grill", Arrays.asList(
                        R.drawable.the_margaux_grill, R.drawable.the_magaux_grill_2,
                        R.drawable.the_magaux_grill_3, R.drawable.the_magaux_grill_4,
                        R.drawable.the_magaux_grill_5
                ))
        );
    }
}
//~~ 그 뒤로 쭉 나열