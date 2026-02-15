package com.example.appdevwardrobeinf246;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class itemdetail extends AppCompatActivity {

    private int itemId = -1;
    private int userId = -1;
    private clothitem item;

    private ImageView imgDetail;
    private EditText etDetailName, etDetailDescription, etMaxWearEdit;
    private TextView tvCurrentWear, tvMaxWearDisplay;
    private Spinner spinnerAreaDetail, spinnerTypeDetail;
    private Button btnEdit, btnDelete, btnWear, btnChangeImage;
    private Button btnSave, btnCancel;
    private LinearLayout viewModeButtons, editModeButtons;

    private String oldName, oldDesc, oldArea, oldType, oldMaxWearStr;
    private String originalImageUri;
    private String newImageUrl = null;
    private boolean imageChanged = false; // true if a new image has been uploaded

    private final String[] areas = {"Top", "Bottom", "Headwear", "Footwear", "Accessory"};

    // Image change related
    private File currentPhotoFile;
    private boolean isUploading = false;

    // Camera launcher
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && currentPhotoFile != null) {
                    Uri savedUri = saveImageToAppStorage(currentPhotoFile);
                    if (savedUri != null) {
                        uploadImage(savedUri);
                    }
                }
            }
    );

    // Gallery launcher
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        Uri savedUri = saveGalleryImageToAppStorage(selectedUri);
                        if (savedUri != null) {
                            uploadImage(savedUri);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdetail);

        itemId = getIntent().getIntExtra("item_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        if (itemId == -1 || userId == -1) {
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imgDetail = findViewById(R.id.imgDetail);
        etDetailName = findViewById(R.id.etDetailName);
        etDetailDescription = findViewById(R.id.etDetailDescription);
        tvCurrentWear = findViewById(R.id.tvCurrentWear);
        tvMaxWearDisplay = findViewById(R.id.tvMaxWearDisplay);
        etMaxWearEdit = findViewById(R.id.etMaxWearEdit);
        spinnerAreaDetail = findViewById(R.id.spinnerAreaDetail);
        spinnerTypeDetail = findViewById(R.id.spinnerTypeDetail);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnWear = findViewById(R.id.btnWear);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        viewModeButtons = findViewById(R.id.viewModeButtons);
        editModeButtons = findViewById(R.id.editModeButtons);

        setupAreaSpinner();
        loadItemFromDatabase();
        setEditingMode(false);

        btnEdit.setOnClickListener(v -> setEditingMode(true));
        btnCancel.setOnClickListener(v -> cancelEdit());
        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> deleteItem());
        btnWear.setOnClickListener(v -> wearItem());
        btnChangeImage.setOnClickListener(v -> showImageSourceDialog());
    }

    // ------------------------------------------------------------
    // Mode switching
    // ------------------------------------------------------------
    private void setEditingMode(boolean editing) {
        if (editing) {
            // Save current values for cancel
            oldName = etDetailName.getText().toString();
            oldDesc = etDetailDescription.getText().toString();
            oldArea = spinnerAreaDetail.getSelectedItem().toString();
            oldType = spinnerTypeDetail.getSelectedItem().toString();
            oldMaxWearStr = etMaxWearEdit.getText().toString();
            originalImageUri = item.imageUri;
            newImageUrl = null;
            imageChanged = false;

            // Enable editing
            etDetailName.setFocusable(true);
            etDetailName.setFocusableInTouchMode(true);
            etDetailName.setClickable(true);
            etDetailName.setCursorVisible(true);

            etDetailDescription.setFocusable(true);
            etDetailDescription.setFocusableInTouchMode(true);
            etDetailDescription.setClickable(true);
            etDetailDescription.setCursorVisible(true);

            spinnerAreaDetail.setEnabled(true);
            spinnerTypeDetail.setEnabled(true);

            tvMaxWearDisplay.setVisibility(View.GONE);
            etMaxWearEdit.setVisibility(View.VISIBLE);

            viewModeButtons.setVisibility(View.GONE);
            editModeButtons.setVisibility(View.VISIBLE);

            etDetailName.requestFocus();
        } else {
            // Disable editing
            etDetailName.setFocusable(false);
            etDetailName.setFocusableInTouchMode(false);
            etDetailName.setClickable(false);
            etDetailName.setCursorVisible(false);

            etDetailDescription.setFocusable(false);
            etDetailDescription.setFocusableInTouchMode(false);
            etDetailDescription.setClickable(false);
            etDetailDescription.setCursorVisible(false);

            spinnerAreaDetail.setEnabled(false);
            spinnerTypeDetail.setEnabled(false);

            tvMaxWearDisplay.setVisibility(View.VISIBLE);
            etMaxWearEdit.setVisibility(View.GONE);

            viewModeButtons.setVisibility(View.VISIBLE);
            editModeButtons.setVisibility(View.GONE);
        }
    }

    // ------------------------------------------------------------
    // Cancel edit
    // ------------------------------------------------------------
    private void cancelEdit() {
        if (isUploading) {
            Toast.makeText(this, "Please wait, image upload in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        // If a new image was uploaded, delete it from server
        if (imageChanged && newImageUrl != null) {
            deleteUploadedImage(newImageUrl);
        }

        // Revert UI to original values
        performLocalRevert();
    }

    private void performLocalRevert() {
        etDetailName.setText(oldName);
        etDetailDescription.setText(oldDesc);
        setSpinnerSelection(spinnerAreaDetail, oldArea);
        updateTypeSpinner(oldArea);
        spinnerTypeDetail.postDelayed(() -> setSpinnerSelection(spinnerTypeDetail, oldType), 100);
        etMaxWearEdit.setText(oldMaxWearStr);
        // Revert image
        if (originalImageUri != null) {
            Glide.with(this)
                    .load(originalImageUri)
                    .placeholder(R.drawable.ic_clothing)
                    .error(R.drawable.ic_clothing)
                    .into(imgDetail);
            item.imageUri = originalImageUri;
        }
        setEditingMode(false);
    }

    private void deleteUploadedImage(String imageUrl) {
        Map<String, String> body = new HashMap<>();
        body.put("image_url", imageUrl);
        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.SimpleResponse> call = apiInterface.deleteImage(body);
        call.enqueue(new Callback<ApiService.SimpleResponse>() {
            @Override
            public void onResponse(Call<ApiService.SimpleResponse> call, Response<ApiService.SimpleResponse> response) {
                // Ignore result, just log
            }

            @Override
            public void onFailure(Call<ApiService.SimpleResponse> call, Throwable t) {
                // Ignore
            }
        });
    }

    // ------------------------------------------------------------
    // Load item from server
    // ------------------------------------------------------------
    private void loadItemFromDatabase() {
        ApiService.GetItemRequest request = new ApiService.GetItemRequest(itemId, userId);
        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.GetItemResponse> call = apiInterface.getItem(request);

        call.enqueue(new Callback<ApiService.GetItemResponse>() {
            @Override
            public void onResponse(Call<ApiService.GetItemResponse> call, Response<ApiService.GetItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.GetItemResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        item = apiResponse.getItem();
                        if (item != null) {
                            updateUIWithItem();
                        } else {
                            Toast.makeText(itemdetail.this, "Item not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(itemdetail.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(itemdetail.this, "Server error", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiService.GetItemResponse> call, Throwable t) {
                Toast.makeText(itemdetail.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUIWithItem() {
        if (item == null) return;

        if (item.imageUri != null && !item.imageUri.isEmpty()) {
            Glide.with(this)
                    .load(item.imageUri)
                    .placeholder(R.drawable.ic_clothing)
                    .error(R.drawable.ic_clothing)
                    .into(imgDetail);
            imgDetail.setOnClickListener(v -> showFullScreenImageDialog(item.imageUri));
        } else {
            imgDetail.setImageResource(R.drawable.ic_clothing);
        }

        etDetailName.setText(item.name);
        etDetailDescription.setText(item.description != null ? item.description : "");
        updateWearDisplay();

        setSpinnerSelection(spinnerAreaDetail, item.area);
        updateTypeSpinner(item.area);
        spinnerTypeDetail.postDelayed(() -> setSpinnerSelection(spinnerTypeDetail, item.type), 100);
    }

    private void updateWearDisplay() {
        tvCurrentWear.setText("Current wears: " + item.current_wear_count);
        if (item.max_wear_count != null) {
            tvMaxWearDisplay.setText("Max wears: " + item.max_wear_count);
            etMaxWearEdit.setText(String.valueOf(item.max_wear_count));
        } else {
            tvMaxWearDisplay.setText("Max wears: No limit");
            etMaxWearEdit.setText("");
        }
    }

    // ------------------------------------------------------------
    // Spinner setup
    // ------------------------------------------------------------
    private void setupAreaSpinner() {
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerAreaDetail.setAdapter(areaAdapter);

        spinnerAreaDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArea = parent.getItemAtPosition(position).toString();
                updateTypeSpinner(selectedArea);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
            case "Footwear":
                types = new String[]{"Sneakers", "Sandals", "Shoes"};
                break;
            case "Headwear":
                types = new String[]{"Hat", "Cap", "Beanie"};
                break;
            case "Accessory":
                types = new String[]{"Belt", "Watch", "Necklace"};
                break;
            default:
                types = new String[]{"Other"};
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, types);
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTypeDetail.setAdapter(typeAdapter);
    }

    // ------------------------------------------------------------
    // Save changes (including image if changed)
    // ------------------------------------------------------------
    private void saveChanges() {
        if (item == null) return;

        String newName = etDetailName.getText().toString().trim();
        String newDesc = etDetailDescription.getText().toString().trim();

        if (newName.isEmpty()) {
            etDetailName.setError("Name required");
            return;
        }

        String newArea = spinnerAreaDetail.getSelectedItem().toString();
        String newType = spinnerTypeDetail.getSelectedItem().toString();

        Integer newMaxWear = null;
        String maxWearStr = etMaxWearEdit.getText().toString().trim();
        if (!maxWearStr.isEmpty()) {
            try {
                int value = Integer.parseInt(maxWearStr);
                if (value < 0) {
                    Toast.makeText(this, "Max wear cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }
                newMaxWear = value;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid max wear number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update local item
        item.name = newName;
        item.description = newDesc;
        item.area = newArea;
        item.type = newType;
        item.max_wear_count = newMaxWear;
        if (imageChanged && newImageUrl != null) {
            item.imageUri = newImageUrl;
        }

        // Send to server
        ApiService.UpdateClothRequest request = new ApiService.UpdateClothRequest();
        request.id = itemId;
        request.user_id = userId;
        request.name = newName;
        request.description = newDesc;
        request.area = newArea;
        request.type = newType;
        request.image_uri = item.imageUri;
        request.max_wear_count = item.max_wear_count;
        request.current_wear_count = item.current_wear_count;

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.ApiResponse> call = apiInterface.updateCloth(request);

        call.enqueue(new Callback<ApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse> call, Response<ApiService.ApiResponse> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        Toast.makeText(itemdetail.this, "Item updated", Toast.LENGTH_SHORT).show();
                        setEditingMode(false);
                        updateUIWithItem();
                    } else {
                        Toast.makeText(itemdetail.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(itemdetail.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                Toast.makeText(itemdetail.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------------------------------------------------
    // Wear item (increment wear count)
    // ------------------------------------------------------------
    private void wearItem() {
        if (isUploading) return;
        item.current_wear_count++;

        ApiService.UpdateClothRequest request = new ApiService.UpdateClothRequest();
        request.id = itemId;
        request.user_id = userId;
        request.name = item.name;
        request.description = item.description;
        request.area = item.area;
        request.type = item.type;
        request.image_uri = item.imageUri;
        request.max_wear_count = item.max_wear_count;
        request.current_wear_count = item.current_wear_count;

        btnWear.setEnabled(false);
        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.ApiResponse> call = apiInterface.updateCloth(request);
        call.enqueue(new Callback<ApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse> call, Response<ApiService.ApiResponse> response) {
                btnWear.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        updateWearDisplay();
                        Toast.makeText(itemdetail.this, "Wearing Recorded!", Toast.LENGTH_SHORT).show();
                    } else {
                        item.current_wear_count--;
                        Toast.makeText(itemdetail.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    item.current_wear_count--;
                    Toast.makeText(itemdetail.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse> call, Throwable t) {
                btnWear.setEnabled(true);
                item.current_wear_count--;
                Toast.makeText(itemdetail.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------------------------------------------------
    // Delete item
    // ------------------------------------------------------------
    private void deleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete item")
                .setMessage("Are you sure you want to delete this clothing item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ApiService.DeleteClothRequest request = new ApiService.DeleteClothRequest(itemId, userId);
                    ApiService.ApiInterface apiInterface = retrofitclient.getClient();
                    Call<ApiService.ApiResponse> call = apiInterface.deleteCloth(request);
                    call.enqueue(new Callback<ApiService.ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiService.ApiResponse> call, Response<ApiService.ApiResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiService.ApiResponse apiResponse = response.body();
                                if ("success".equals(apiResponse.getStatus())) {
                                    Toast.makeText(itemdetail.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(itemdetail.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiService.ApiResponse> call, Throwable t) {
                            Toast.makeText(itemdetail.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ------------------------------------------------------------
    // Image upload (temporary, does not update item yet)
    // ------------------------------------------------------------
    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Change Image")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .show();
    }

    private void openCamera() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            currentPhotoFile = File.createTempFile(imageFileName, ".jpg", storageDir);

            Uri photoURI = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider",
                    currentPhotoFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(cameraIntent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private Uri saveImageToAppStorage(File imageFile) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "wardrobe_" + timeStamp + ".jpg";
            File privateStorageDir = getFilesDir();
            File destinationFile = new File(privateStorageDir, fileName);
            try (InputStream in = new FileInputStream(imageFile);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            return destinationFile.exists() ? Uri.fromFile(destinationFile) : null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private Uri saveGalleryImageToAppStorage(Uri galleryUri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "wardrobe_gallery_" + timeStamp + ".jpg";
            File privateStorageDir = getFilesDir();
            File destinationFile = new File(privateStorageDir, fileName);
            try (InputStream in = getContentResolver().openInputStream(galleryUri);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                if (in == null) return null;
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            return destinationFile.exists() ? Uri.fromFile(destinationFile) : null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImage(Uri imageUri) {
        isUploading = true;
        btnChangeImage.setEnabled(false);
        btnChangeImage.setText("Uploading...");

        File file = new File(imageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ApiService.ApiInterface apiInterface = retrofitclient.getClient();
        Call<ApiService.ImageUploadResponse> call = apiInterface.uploadImage(body);
        call.enqueue(new Callback<ApiService.ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ApiService.ImageUploadResponse> call, Response<ApiService.ImageUploadResponse> response) {
                isUploading = false;
                btnChangeImage.setEnabled(true);
                btnChangeImage.setText("Change Image");

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ImageUploadResponse uploadResponse = response.body();
                    if ("success".equals(uploadResponse.getStatus())) {
                        // If there was a previous temporary image, delete it
                        if (imageChanged && newImageUrl != null) {
                            deleteUploadedImage(newImageUrl);
                        }
                        newImageUrl = uploadResponse.getImageUrl();
                        imageChanged = true;
                        // Update UI with new image
                        Glide.with(itemdetail.this)
                                .load(newImageUrl)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.ic_clothing)
                                .error(R.drawable.ic_clothing)
                                .into(imgDetail);
                        // Do NOT update server yet
                    } else {
                        Toast.makeText(itemdetail.this, "Upload failed: " + uploadResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(itemdetail.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ImageUploadResponse> call, Throwable t) {
                isUploading = false;
                btnChangeImage.setEnabled(true);
                btnChangeImage.setText("Change Image");
                Toast.makeText(itemdetail.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------
    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (value.equals(adapter.getItem(i).toString())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void showFullScreenImageDialog(String imageUriString) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fullscreen);
        ImageView fullscreenImageView = dialog.findViewById(R.id.fullscreenImageView);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        Glide.with(this).load(imageUriString).into(fullscreenImageView);
        fullscreenImageView.setOnClickListener(v -> dialog.dismiss());
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}