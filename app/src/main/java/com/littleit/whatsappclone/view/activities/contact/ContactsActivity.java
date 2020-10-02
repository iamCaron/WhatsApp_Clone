package com.littleit.whatsappclone.view.activities.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Application;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.littleit.whatsappclone.R;
import com.littleit.whatsappclone.adapter.ContactsAdapter;
import com.littleit.whatsappclone.databinding.ActivityContactsBinding;
import com.littleit.whatsappclone.model.Contact;
import com.littleit.whatsappclone.model.user.Users;
import com.littleit.whatsappclone.tools.CountryToPhonePrefix;
import com.littleit.whatsappclone.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";
    private static final int CONTACTS_REQUEST_CODE = 121;
    private ActivityContactsBinding binding;
    private List<Users> list = new ArrayList<>();
    private List<Contact> contactsList = new ArrayList<>();
    private ContactsAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private String[] permissions = {Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!checkIfPermissionGranted()){
            getContactsPermissions();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getContactList();
        }
    }

    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor contacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (contacts.moveToNext()) {
            String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;
            Contact contact = new Contact(name, phone);
            contactsList.add(contact);
            getUserDetails(contact);
            adapter = new ContactsAdapter(list, ContactsActivity.this);
            binding.recyclerView.setAdapter(adapter);
        }


    }

    private void getUserDetails(final Contact contact) {

        // Create a reference to the users collection
        CollectionReference usersRef = firestore.collection("Users");
        // Create a query against the collection.
        usersRef.whereEqualTo("userPhone", contact.getPhone()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String userID = document.getString("userID");
                                String userName = contact.getName();
                                String imageUrl = document.getString("imageProfile");
                                String desc = document.getString("bio");

                                Users user = new Users();
                                user.setUserID(userID);
                                user.setBio(desc);
                                user.setUserName(userName);
                                user.setImageProfile(imageUrl);


                                if (userID != null && !userID.equals(firebaseUser.getUid())) {
                                    list.add(user);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        ;

    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

    private void getContactsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    ContactsActivity.this,
                    permissions,
                    CONTACTS_REQUEST_CODE);
        }else{
            Toast.makeText(this,"Cant Access Contacts",Toast.LENGTH_LONG);
        }
    }

    private boolean checkIfPermissionGranted() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
           return false;
        }
    }


// This function is called when user accept or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CONTACTS_REQUEST_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                getContactsPermissions();
            }

        }
    }
}
