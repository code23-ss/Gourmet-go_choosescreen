package com.example.mainscreen;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class RestaurantDataManager {
    public List<Restaurant> loadRestaurantsFromJson(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("restaurants.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type restaurantListType = new TypeToken<List<Restaurant>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(reader, restaurantListType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}