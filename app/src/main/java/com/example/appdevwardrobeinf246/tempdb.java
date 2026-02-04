package com.example.appdevwardrobeinf246;

import java.util.ArrayList;

public class tempdb {
    public static ArrayList<clothitem> items = new ArrayList<>();
    public static ArrayList<outfit> outfits = new ArrayList<>();
    public static ArrayList<washsched> washSchedules = new ArrayList<>();

    public static void add(clothitem item) {
        items.add(item);
    }

    public static void addOutfit(outfit outfit) {
        outfits.add(outfit);
    }

    public static void addWashSchedule(washsched schedule) {
        washSchedules.add(schedule);
    }

    public static void removeOutfit(int index) {
        if (index >= 0 && index < outfits.size()) {
            outfits.remove(index);
        }
    }

    public static void removeWashSchedule(int index) {
        if (index >= 0 && index < washSchedules.size()) {
            washSchedules.remove(index);
        }
    }

    public static void markAllItemsAsWashed() {
        for (clothitem item : items) {
            item.wash();
        }

        for (washsched schedule : washSchedules) {
            if (schedule.isRecurring && schedule.nextWashDate <= System.currentTimeMillis()) {
                schedule.nextWashDate = System.currentTimeMillis() +
                        (schedule.recurrenceDays * 24 * 60 * 60 * 1000L);
            }
        }
    }
}