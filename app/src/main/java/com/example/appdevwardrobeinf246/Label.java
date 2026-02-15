package com.example.appdevwardrobeinf246;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Label extends AppCompatActivity {

    private ImageView imgPreview;
    private EditText etName, etDescription, etMaxWear;
    private Spinner spinnerArea, spinnerType;
    private Button btnSave;

    private Uri imageUri;
    private int userId;
    private String uploadedImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        imgPreview = findViewById(R.id.imgPreview);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etMaxWear = findViewById(R.id.etMaxWear);
        spinnerArea = findViewById(R.id.spinnerArea);
        spinnerType = findViewById(R.id.spinnerType);
        btnSave = findViewById(R.id.btnSave);

        String imageUriString = getIntent().getStringExtra("imageUri");
        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (imageUriString != null && !imageUriString.isEmpty()) {
            imageUri = Uri.parse(imageUriString);
            loadImageSafely(imageUri);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] areas = {"Top", "Bottom", "Headwear", "Footwear", "Accessory"};
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTypeSpinner(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSave.setOnClickListener(v -> saveItemToServer());
    }

    private void loadImageSafely(Uri uri) {
        try {
            if (uri.toString().startsWith("file://")) {
                String filePath = uri.getPath();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imgPreview.setImageBitmap(bitmap);
                    } else {
                        imgPreview.setImageResource(R.drawable.ic_clothing);
                    }
                } else {
                    imgPreview.setImageResource(R.drawable.ic_clothing);
                }
            } else {
                try {
                    imgPreview.setImageURI(uri);
                } catch (Exception e) {
                    imgPreview.setImageResource(R.drawable.ic_clothing);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            imgPreview.setImageResource(R.drawable.ic_clothing);
        }
    }

    private void updateTypeSpinner(String area) {
        String[] types;
        switch (area) {
            case "Top":
                types = new String[]{"Polo", "T-Shirt", "Long Sleeve", "Jacket"};
                break;
            case "Bottom":
                types = new String[]{"Jeans", "Shorts", "Slacks"};
                break;
            case "Headwear":
                types = new String[]{"Cap", "Beanie", "Hat"};
                break;
            case "Footwear":
                types = new String[]{"Sneakers", "Sandals", "Shoes", "Boots"};
                break;
            case "Accessory":
                types = new String[]{"Watch", "Bracelet", "Necklace", "Glasses"};
                break;
            default:
                types = new String[]{"Other"};
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, types);
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);
    }

    private void saveItemToServer() {
        if (spinnerArea.getSelectedItem() == null || spinnerType.getSelectedItem() == null) {
            Toast.makeText(this, "Please select area and type", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        final String area = spinnerArea.getSelectedItem().toString();
        final String type = spinnerType.getSelectedItem().toString();
        final String description = etDescription.getText().toString().trim();

        final Integer maxWear;
        String maxWearStr = etMaxWear.getText().toString().trim();
        if (maxWearStr.isEmpty()) {
            maxWear = null;
        } else {
            try {
                int value = Integer.parseInt(maxWearStr);
                if (value < 0) {
                    etMaxWear.setError("Max wear count cannot be negative");
                    return;
                }
                maxWear = value;
            } catch (NumberFormatException e) {
                etMaxWear.setError("Invalid number");
                return;
            }
        }

        if (imageUri == null) {
            Toast.makeText(this, "Image not found. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }


        btnSave.setEnabled(false);
        btnSave.setText("Uploading...");


        File file = new File(imageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.ImageUploadResponse> uploadCall = apiInterface.uploadImage(body);
        uploadCall.enqueue(new Callback<ApiService.ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ApiService.ImageUploadResponse> call, Response<ApiService.ImageUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ImageUploadResponse uploadResponse = response.body();
                    if ("success".equals(uploadResponse.getStatus())) {
                        uploadedImageUrl = uploadResponse.getImageUrl();

                        createClothingItem(name, area, type, description, maxWear);
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Save");
                        Toast.makeText(Label.this, "Upload failed: " + uploadResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save");
                    Toast.makeText(Label.this, "Server error during upload", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ImageUploadResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                Toast.makeText(Label.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createClothingItem(String name, String area, String type, String description, Integer maxWear) {
        ApiService.AddClothRequest request = new ApiService.AddClothRequest(
                userId,
                name,
                type,
                area,
                description,
                uploadedImageUrl,
                maxWear
        );

        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.ApiResponse> call = apiInterface.addCloth(request);
        call.enqueue(new Callback<ApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse> call, Response<ApiService.ApiResponse> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        Toast.makeText(Label.this, "Item saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Label.this, "Error: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Label.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                Toast.makeText(Label.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}