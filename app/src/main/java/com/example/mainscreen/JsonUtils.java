package com.example.mainscreen;


import android.content.Context;
import android.content.res.AssetManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {
    public static List<Restaurant> loadRestaurants(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("restaurants_item.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type restaurantListType = new TypeToken<List<Restaurant>>(){}.getType();
            return gson.fromJson(reader, restaurantListType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}