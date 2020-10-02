package com.littleit.whatsappclone.view.activities.display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.littleit.whatsappclone.R;
import com.littleit.whatsappclone.common.Common;
import com.littleit.whatsappclone.databinding.ActivityViewImageBinding;
import com.littleit.whatsappclone.databinding.ActivityViewProfileImageBinding;

public class ViewProfileImageActivity extends AppCompatActivity {

    private ActivityViewProfileImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_profile_image);

        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);
    }
}
