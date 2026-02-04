package com.example.appdevwardrobeinf246;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class itemdetail extends AppCompatActivity {

    private int itemIndex = -1;
    private clothitem item;

    private ImageView imgDetail;

    private EditText etDetailName, etDetailDescription;
    private Spinner spinnerAreaDetail, spinnerTypeDetail;

    private Button btnEdit, btnSave, btnCancel, btnDelete;

    private String oldName, oldDesc, oldArea, oldType;

    private final String[] areas = {"Top", "Bottom", "Headwear", "Footwear", "Accessory"};

    private boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdetail);

        itemIndex = getIntent().getIntExtra("itemIndex", -1);

        imgDetail = findViewById(R.id.imgDetail);

        etDetailName = findViewById(R.id.etDetailName);
        etDetailDescription = findViewById(R.id.etDetailDescription);

        spinnerAreaDetail = findViewById(R.id.spinnerAreaDetail);
        spinnerTypeDetail = findViewById(R.id.spinnerTypeDetail);

        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);

        if (itemIndex >= 0 && itemIndex < tempdb.items.size()) {
            item = tempdb.items.get(itemIndex);
        }

        setupAreaSpinner();

        if (item != null) {
            imgDetail.setImageURI(Uri.parse(item.imageUri));

            etDetailName.setText(item.name);
            etDetailDescription.setText(item.description);

            setSpinnerSelection(spinnerAreaDetail, item.area);

            imgDetail.setOnClickListener(v -> showFullScreenImageDialog(item.imageUri));
        }

        enableEditing(false);

        btnEdit.setOnClickListener(v -> enableEditing(true));

        btnCancel.setOnClickListener(v -> {
            restoreOldValues();
            enableEditing(false);
        });

        btnSave.setOnClickListener(v -> {
            saveChanges();
            enableEditing(false);
        });

        btnDelete.setOnClickListener(v -> {

            if (item == null) return;

            new AlertDialog.Builder(this)
                    .setTitle("Delete item")
                    .setMessage("Are you sure you want to delete this clothing item?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        if (itemIndex >= 0 && itemIndex < tempdb.items.size()) {
                            tempdb.items.remove(itemIndex);
                        }

                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupAreaSpinner() {

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                areas
        );

        areaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerAreaDetail.setAdapter(areaAdapter);

        spinnerAreaDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedArea = parent.getItemAtPosition(position).toString();

                updateTypeSpinner(selectedArea);

                if (firstLoad && item != null) {
                    spinnerTypeDetail.post(() -> {
                        setSpinnerSelection(spinnerTypeDetail, item.type);
                        firstLoad = false;
                    });
                }
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

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                types
        );

        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTypeDetail.setAdapter(typeAdapter);
    }

    private void enableEditing(boolean enabled) {

        if (enabled) {
            oldName = etDetailName.getText().toString();
            oldDesc = etDetailDescription.getText().toString();

            oldArea = spinnerAreaDetail.getSelectedItem() != null
                    ? spinnerAreaDetail.getSelectedItem().toString()
                    : "";

            oldType = spinnerTypeDetail.getSelectedItem() != null
                    ? spinnerTypeDetail.getSelectedItem().toString()
                    : "";
        }

        etDetailName.setEnabled(enabled);
        etDetailDescription.setEnabled(enabled);

        spinnerAreaDetail.setEnabled(enabled);
        spinnerTypeDetail.setEnabled(enabled);

        btnEdit.setVisibility(enabled ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(enabled ? View.VISIBLE : View.GONE);
        btnCancel.setVisibility(enabled ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(enabled ? View.GONE : View.VISIBLE);

        if (enabled) {
            etDetailName.requestFocus();
            etDetailName.setSelection(etDetailName.getText().length());
        }
    }

    private void restoreOldValues() {
        etDetailName.setText(oldName);
        etDetailDescription.setText(oldDesc);

        setSpinnerSelection(spinnerAreaDetail, oldArea);

        updateTypeSpinner(oldArea);

        spinnerTypeDetail.post(() -> setSpinnerSelection(spinnerTypeDetail, oldType));
    }

    private void saveChanges() {
        if (item == null) return;

        String newName = etDetailName.getText().toString().trim();
        String newDesc = etDetailDescription.getText().toString().trim();

        if (newName.isEmpty()) {
            etDetailName.setError("Name required");
            return;
        }

        if (spinnerAreaDetail.getSelectedItem() == null || spinnerTypeDetail.getSelectedItem() == null) {
            return;
        }

        String newArea = spinnerAreaDetail.getSelectedItem().toString();
        String newType = spinnerTypeDetail.getSelectedItem().toString();

        item.name = newName;
        item.description = newDesc;
        item.area = newArea;
        item.type = newType;

        tempdb.items.set(itemIndex, item);
    }

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
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fullscreen);

        ImageView fullscreenImageView = dialog.findViewById(R.id.fullscreenImageView);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);

        fullscreenImageView.setImageURI(Uri.parse(imageUriString));

        fullscreenImageView.setOnClickListener(v -> dialog.dismiss());
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
