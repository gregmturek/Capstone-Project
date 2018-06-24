package com.casalasglorias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantMenuData {
    public List<String> boxLabels = new ArrayList<>();
    public Map<String, String> categories = new HashMap<>();
    public Map<String, String> salads = new HashMap<>();
    public Map<String, String> enchiladas = new HashMap<>();
    public Map<String, String> burritosAndWraps = new HashMap<>();
    public Map<String, String> fajitasAndSteaks = new HashMap<>();
    public Map<String, String> specialties = new HashMap<>();

    public RestaurantMenuData() {}
}
