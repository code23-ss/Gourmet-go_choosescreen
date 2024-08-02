// GuideActivity.java
package com.example.mainscreen;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    private RecyclerView recyclerKoreaFood;
    private GuideAdapter KoreaFoodAdapter;
    private List<Guide> koreaFoodList;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.guide);

        recyclerKoreaFood = findViewById(R.id.recycler_korea_food);

        layoutManager = new LinearLayoutManager(this);
        recyclerKoreaFood.setLayoutManager(layoutManager);

        koreaFoodList = new ArrayList<>();


        // Sample data for Korea Food
        //button 입력 부분 **수정사항**
        koreaFoodList.add(new Guide(R.drawable.tteokbokki, "Tteokbokki(Stir-fried Rice Cake)", "Sliced rice cake bar (garaetteok) or thin rice cake sticks (Tteokbokkitteok) stir-fried in a spicy gochujang sauce with vegetables and fish cakes"));
        koreaFoodList.add(new Guide(R.drawable.porknoodles, "Pork Noodles", "Noodles served in soup made by simmering pork bones until a cloudy broth forms. A local specialty of Jeju Island, this noodle dish is eaten on special occasions"));
        koreaFoodList.add(new Guide(R.drawable.guide_grilled_pork_belly, "Grilled Pork Belly", "Fat-streaked pork belly grilled and dipped in salt or ssamjang (red soybean paste dip). Also eaten wrapped in lettuce leaves"));


        KoreaFoodAdapter = new GuideAdapter(koreaFoodList);

        recyclerKoreaFood.setAdapter(KoreaFoodAdapter);
    }
}
