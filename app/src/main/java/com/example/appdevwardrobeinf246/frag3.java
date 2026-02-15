package com.example.appdevwardrobeinf246;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag3 extends Fragment {

    private LinearLayout containerLaundry;
    private ProgressBar progressBar;
    private TextView tvSelectedCount;
    private Button btnWash;
    private MaterialButtonToggleGroup toggleGroup;
    private LinearLayout bottomWashBar;
    private List<clothitem> allItems = new ArrayList<>();
    private Set<Integer> selectedIds = new HashSet<>();

    private Map<String, List<clothitem>> itemsByCategory = new HashMap<>();
    private String currentCategory = "fresh";

    private TextView tvCategoryTitle;
    private TextView tvCategoryCount;

    private SharedPreferences sharedPreferences;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);
        tvCategoryCount = view.findViewById(R.id.tvCategoryCount);
        bottomWashBar = view.findViewById(R.id.bottomWashBar);
        containerLaundry = view.findViewById(R.id.containerLaundry);
        progressBar = view.findViewById(R.id.progressBar);
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount);
        btnWash = view.findViewById(R.id.btnWash);
        toggleGroup = view.findViewById(R.id.toggleGroup);

        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_SHORT).show();
            return;
        }

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnFresh) {
                    currentCategory = "fresh";
                } else if (checkedId == R.id.btnUsed) {
                    currentCategory = "used";
                } else if (checkedId == R.id.btnDirty) {
                    currentCategory = "dirty";
                }
                displayCurrentCategory();
            }
        });


        toggleGroup.check(R.id.btnFresh);

        btnWash.setOnClickListener(v -> showWashConfirmation());

        loadLaundryItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLaundryItems();
    }

    private void loadLaundryItems() {
        showLoading(true);

        ApiService.GetClothesRequest request = new ApiService.GetClothesRequest(
                userId, "", "all", "all", "all"
        );

        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.ApiResponse> call = apiInterface.getClothes(request);
        call.enqueue(new Callback<ApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse> call, Response<ApiService.ApiResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        allItems = apiResponse.getClothes();
                        if (allItems == null) allItems = new ArrayList<>();
                        groupItemsByCategory();
                        displayCurrentCategory();
                    } else {
                        Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void groupItemsByCategory() {
        itemsByCategory.clear();
        itemsByCategory.put("fresh", new ArrayList<>());
        itemsByCategory.put("used", new ArrayList<>());
        itemsByCategory.put("dirty", new ArrayList<>());

        for (clothitem item : allItems) {
            String status = getStatus(item);
            itemsByCategory.get(status).add(item);
        }
    }

    private String getStatus(clothitem item) {
        int current = item.current_wear_count;
        Integer max = item.max_wear_count;
        if (current == 0) return "fresh";
        if (max != null && current >= max) return "dirty";
        return "used";
    }

    private void displayCurrentCategory() {
        containerLaundry.removeAllViews();

        List<clothitem> items = itemsByCategory.get(currentCategory);
        int count = items == null ? 0 : items.size();


        String title = currentCategory.substring(0, 1).toUpperCase() + currentCategory.substring(1);
        tvCategoryTitle.setText(title);
        tvCategoryCount.setText("(" + count + ")");

        if (items == null || items.isEmpty()) {
            showEmptyMessage();
            return;
        }

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        grid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        for (clothitem item : items) {
            addItemToGrid(item, grid);
        }
        containerLaundry.addView(grid);
    }

    private void addItemToGrid(clothitem item, GridLayout grid) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View itemView = inflater.inflate(R.layout.griditemlaundry, grid, false);

        ImageView imgItem = itemView.findViewById(R.id.imgItem);
        TextView tvName = itemView.findViewById(R.id.tvItemName);
        TextView tvWear = itemView.findViewById(R.id.tvItemWear);
        ImageView ivSelected = itemView.findViewById(R.id.ivSelected);
        CardView card = itemView.findViewById(R.id.cardItem);

        tvName.setText(item.name);
        String wearText = "Worn: " + item.current_wear_count;
        if (item.max_wear_count != null) {
            wearText += "/" + item.max_wear_count;
        }
        tvWear.setText(wearText);

        if (item.imageUri != null && !item.imageUri.isEmpty()) {
            Glide.with(requireContext())
                    .load(item.imageUri)
                    .placeholder(R.drawable.ic_clothing)
                    .error(R.drawable.ic_clothing)
                    .into(imgItem);
        } else {
            imgItem.setImageResource(R.drawable.ic_clothing);
        }

        updateItemSelection(itemView, selectedIds.contains(item.id));

        itemView.setOnClickListener(v -> {
            if (selectedIds.contains(item.id)) {
                selectedIds.remove(item.id);
                updateItemSelection(itemView, false);
            } else {
                selectedIds.add(item.id);
                updateItemSelection(itemView, true);
            }
            updateWashButton();
        });

        itemView.setOnLongClickListener(v -> {
            Intent intent = new Intent(requireActivity(), itemdetail.class);
            intent.putExtra("item_id", item.id);
            intent.putExtra("user_id", item.user_id);
            startActivity(intent);
            return true;
        });

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        itemView.setLayoutParams(params);

        grid.addView(itemView);
    }

    private void updateItemSelection(View itemView, boolean selected) {
        ImageView ivSelected = itemView.findViewById(R.id.ivSelected);
        ivSelected.setVisibility(selected ? View.VISIBLE : View.GONE);
        itemView.findViewById(R.id.cardItem).setAlpha(selected ? 0.8f : 1.0f);
    }

    private void updateWashButton() {
        int count = selectedIds.size();
        tvSelectedCount.setText(count + " clothes selected");
        btnWash.setEnabled(count > 0);
        if (count > 0) {
            bottomWashBar.setVisibility(View.VISIBLE);
        } else {
            bottomWashBar.setVisibility(View.GONE);
        }
    }

    private void showEmptyMessage() {
        TextView empty = new TextView(requireContext());
        empty.setText("No clothes in this category.");
        empty.setTextColor(0xffffffff);
        empty.setPadding(0, 64, 0, 0);
        empty.setGravity(1);
        containerLaundry.addView(empty);
    }

    private void showWashConfirmation() {
        if (selectedIds.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Wash clothes")
                .setMessage("Are you sure you want to wash " + selectedIds.size() + " selected items?")
                .setPositiveButton("Wash", (dialog, which) -> washSelectedItems())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void washSelectedItems() {
        List<Integer> ids = new ArrayList<>(selectedIds);
        ApiService.WashClothesRequest request = new ApiService.WashClothesRequest(userId, ids);

        btnWash.setEnabled(false);
        btnWash.setText("Washing...");

        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.SimpleResponse> call = apiInterface.washClothes(request);
        call.enqueue(new Callback<ApiService.SimpleResponse>() {
            @Override
            public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                btnWash.setEnabled(true);
                btnWash.setText("Wash");
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.SimpleResponse res = response.body();
                    if ("success".equals(res.getStatus())) {
                        Toast.makeText(requireContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                        selectedIds.clear();
                        loadLaundryItems(); // refresh
                    } else {
                        Toast.makeText(requireContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                btnWash.setEnabled(true);
                btnWash.setText("Wash");
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        containerLaundry.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private int dpToPx(int dp) {
        return (int) (dp * requireContext().getResources().getDisplayMetrics().density);
    }
}