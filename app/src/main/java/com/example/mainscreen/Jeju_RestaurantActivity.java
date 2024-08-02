package com.example.mainscreen;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class Jeju_RestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jeju_restaurants);

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
                new Restaurant("Gasibang Noodle", Arrays.asList(
                        R.drawable.jeju_gasiabangnoodle01, R.drawable.jeju_gasiabangnoodle02,
                        R.drawable.jeju_gasiabangnoodle03, R.drawable.jeju_gasiabangnoodle04
                )),

                new Restaurant("Gojipdol RockFish, Jungmun", Arrays.asList(
                        R.drawable.jeju_gojipdol_ureong_jungmun03, R.drawable.jeju_gojipdol_ureong_jungmun01,
                        R.drawable.jeju_gojipdol_ureong_jungmun02, R.drawable.jeju_gojipdol_ureong_jungmun04
                )),

                new Restaurant("Jeju Kitchen Meat Noodles, Jeju Airport", Arrays.asList(
                        R.drawable.jeju_jeju_kitchen_meat_noodles_jeju_airport01, R.drawable.jeju_jeju_kitchen_meat_noodles_jeju_airport02,
                        R.drawable.jeju_jeju_kitchen_meat_noodles_jeju_airport03, R.drawable.jeju_jeju_kitchen_meat_noodles_jeju_airport04
                )),

                new Restaurant("Sukseongdo, Jungmun", Arrays.asList(
                        R.drawable.jeju_sukseongdo_jungmun01, R.drawable.jeju_sukseongdo_jungmun02,
                        R.drawable.jeju_sukseongdo_jungmun03, R.drawable.jeju_sukseongdo_jungmun04
                )),

                new Restaurant("DeokSeongWon", Arrays.asList(
                        R.drawable.jeju_deokseongwon01, R.drawable.jeju_deokseongwon02,
                        R.drawable.jeju_deokseongwon03, R.drawable.jeju_deokseongwon04
                )),

                new Restaurant("Maison Glad, Jeju SamDaJeong", Arrays.asList(
                        R.drawable.jeju_maison_glad_jeju_samdajeong01, R.drawable.jeju_maison_glad_jeju_samdajeong02,
                        R.drawable.jeju_maison_glad_jeju_samdajeong03, R.drawable.jeju_maison_glad_jeju_samdajeong04
                )),

                new Restaurant("Narnia Restaurant", Arrays.asList(
                        R.drawable.jeju_narnia_restaurant01, R.drawable.jeju_narnia_restaurant02,
                        R.drawable.jeju_narnia_restaurant03, R.drawable.jeju_narnia_restaurant04
                )),

                new Restaurant("Western Noodles Shop", Arrays.asList(
                        R.drawable.jeju_western_noodles_shop01, R.drawable.jeju_western_noodles_shop02,
                        R.drawable.jeju_western_noodles_shop03, R.drawable.jeju_western_noodles_shop04
                )),

                new Restaurant("DorDor, Jeju Seogwipo", Arrays.asList(
                        R.drawable.jeju_dordor_jeju_seogwipo01, R.drawable.jeju_dordor_jeju_seogwipo02,
                        R.drawable.jeju_dordor_jeju_seogwipo03, R.drawable.jeju_dordor_jeju_seogwipo04
                )),

                new Restaurant("Haesong Raw Fish Restaurant", Arrays.asList(
                        R.drawable.jeju_haesong_raw_fish_restaurant01, R.drawable.jeju_haesong_raw_fish_restaurant02,
                        R.drawable.jeju_haesong_raw_fish_restaurant03, R.drawable.jeju_haesong_raw_fish_restaurant04
                )),

                new Restaurant("Sungsan Takuma Sushi", Arrays.asList(
                        R.drawable.jeju_sungsan_takuma_sushi01, R.drawable.jeju_sungsan_takuma_sushi02,
                        R.drawable.jeju_sungsan_takuma_sushi03, R.drawable.jeju_sungsan_takuma_sushi04
                )),

                new Restaurant("TongBalSungsan", Arrays.asList(
                        R.drawable.jeju_tongbalsungsan01, R.drawable.jeju_tongbalsungsan02,
                        R.drawable.jeju_tongbalsungsan03, R.drawable.jeju_tongbalsungsan04
                ))
        );
    }
}