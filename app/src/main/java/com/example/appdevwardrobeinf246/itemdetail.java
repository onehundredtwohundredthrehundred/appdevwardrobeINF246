package com.example.appdevwardrobeinf246;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class itemdetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdetail);

        int itemIndex = getIntent().getIntExtra("itemIndex", -1);

        if (itemIndex >= 0 && itemIndex < tempdb.items.size()) {
            final clothitem item = tempdb.items.get(itemIndex);

            ImageView imgDetail = findViewById(R.id.imgDetail);
            TextView tvDetailName = findViewById(R.id.tvDetailName);
            TextView tvDetailArea = findViewById(R.id.tvDetailArea);
            TextView tvDetailType = findViewById(R.id.tvDetailType);
            TextView tvDetailDescription = findViewById(R.id.tvDetailDescription);

            Uri imageUri = Uri.parse(item.imageUri);
            imgDetail.setImageURI(imageUri);
            tvDetailName.setText(item.name);
            tvDetailArea.setText(item.area);
            tvDetailType.setText(item.type);
            tvDetailDescription.setText(item.description);

            imgDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFullScreenImageDialog(item.imageUri);
                }
            });
        }
    }

    private void showFullScreenImageDialog(String imageUriString) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fullscreen);

        ImageView fullscreenImageView = dialog.findViewById(R.id.fullscreenImageView);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);

        fullscreenImageView.setImageURI(Uri.parse(imageUriString));

        fullscreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}