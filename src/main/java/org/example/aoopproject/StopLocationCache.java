package org.example.aoopproject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StopLocationCache {

    private static final String PATH = "src/main/java/org/example/aoopproject/files/stopCache.json";
    private final Map<String, LatLng> cache = new HashMap<>();
    private final Gson gson = new Gson();

    public StopLocationCache(){ load(); }

    private void load(){
        try(FileReader fr = new FileReader(new File(PATH))){
            Type type = new TypeToken<Map<String,LatLng>>(){}.getType();
            Map<String,LatLng> data = gson.fromJson(fr, type);
            if(data!=null) cache.putAll(data);
        }catch(Exception ignored){}
    }

    private void save(){
        try(FileWriter fw = new FileWriter(new File(PATH))){
            gson.toJson(cache, fw);
        }catch(Exception ignored){}
    }

    public LatLng get(String key){ return cache.get(key); }

    public void put(String key, LatLng value){
        cache.put(key, value);
        save(); // SAVE_IMMEDIATE
    }
    public Set<String> stops() {
        return cache.keySet();
    }
}
