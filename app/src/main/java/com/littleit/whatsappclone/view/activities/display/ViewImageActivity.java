package com.littleit.whatsappclone.view.activities.display;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.littleit.whatsappclone.R;
import com.littleit.whatsappclone.common.Common;
import com.littleit.whatsappclone.databinding.ActivityViewImageBinding;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;
    private FirebaseFirestore firestore;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        firestore=FirebaseFirestore.getInstance();

        //getting the name of the sender of the photo from firstore if from chat
        String sender=getIntent().getStringExtra("senderId");
        if(sender!=null) {
            firestore.collection("Users").document(sender).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    title = documentSnapshot.getString("userName");
                    binding.tvTitle.setText(title);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    title = "Photo";
                    binding.tvTitle.setText(title);

                }
            });
        }else {
            //getting the user name
            String title=getIntent().getStringExtra("title");
            if(title!=null) {
                binding.tvTitle.setText(title);
            }
        }

        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
