package com.example.quickhr.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quickhr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText mNameField, mPhoneField, mLastName, mEmail, mCity, mSkills, mExperience;
    private Button mBack, mConfirm;
    private ImageView mProfileImage;
    private Spinner mCountrySpinner;
    private Switch mSwitch;

    //skills
    private EditText mDisplaySkills;
    private SearchView mSearchViewSkills;
    private ListView mListViewSkills;
    List<String> list;
    ArrayAdapter<String> adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String userId, name, phone, profileImageUrl, userType, lastName, email, country, city, skills, experience, switch1;

    private Uri resultUri;

    //pdf
    private EditText editPDFName;
    private Button mBtnUpload;
    private StorageReference storageReference;
    //private DatabaseReference databaseReference;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mLastName = (EditText) findViewById(R.id.lastName);
        mEmail = (EditText) findViewById(R.id.email);
        mCity = (EditText) findViewById(R.id.city);
        mExperience = (EditText) findViewById(R.id.experience);
        mSwitch = (Switch) findViewById(R.id.switch1);
        mCountrySpinner = (Spinner) findViewById(R.id.spinner_country); // add spinner

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        //skills
        mSearchViewSkills = (SearchView) findViewById(R.id.searchView_skills);
        mListViewSkills = (ListView) findViewById(R.id.listView_skills);
        mDisplaySkills = (EditText) findViewById(R.id.display_skills);

        //pdf
        editPDFName = (EditText) findViewById(R.id.txt_pdfName);
        mBtnUpload = (Button) findViewById(R.id.btn_upload);
        storageReference = FirebaseStorage.getInstance().getReference();
        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFilePDF();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        getAdapterSpinnerCountry();

        addListViewSkillsItems();

        /*mSearchViewSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }


        });*/


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //switch

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Map<String, Object> newSwitch = new HashMap<String, Object>();
                String result;
                if(isChecked){
                    result = "true";
                }else {
                    result = "false";
                }
                newSwitch.put("switch", result);
                mUserDatabase.updateChildren(newSwitch);
                }

        });
    }

    private void addListViewSkillsItems() {
        list = new ArrayList<String>();

        list.add("java");
        list.add("Spring");
        list.add("C#");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        mSearchViewSkills.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                mListViewSkills.setAdapter(adapter);
                adapter.getFilter().filter(s);

                //adapter.getFilter();

                return false;
            }
        });

        mListViewSkills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String sNumber = adapterView.getItemAtPosition(position).toString();
                mDisplaySkills.append(sNumber.toUpperCase() + "  ");

            }
        });



    }

    //pdf
    private void selectFilePDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"), 1);
    }

    //pdf , I need to try to change databaseReference and mUserDatabase
    private void uploadPDFFile(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("upload/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete());
                        Uri url = uri.getResult();

                        UploadPDF uploadPDF = new UploadPDF(editPDFName.getText().toString(), url.toString());
                        mUserDatabase.child("pdf").setValue(uploadPDF);
                        Toast.makeText(SettingsActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded: " + (int)progress + "%");
            }
        });
    }


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if (map.get("type") != null) {
                        userType = map.get("type").toString();
                    }
                    if (map.get("lastName") != null){
                        lastName = map.get("lastName").toString();
                        mLastName.setText(lastName);
                    }
                    if (map.get("email") != null){
                        email = map.get("email").toString();
                        mEmail.setText(email);
                    }
                    if (map.get("country") != null){
                        country = map.get("country").toString();
                        mCountrySpinner.getSelectedItem().toString();
                    }
                    if (map.get("city") != null){
                        city = map.get("city").toString();
                        mCity.setText(city);
                    }
                    if (map.get("skills") != null){
                        skills = map.get("skills").toString();
                        mDisplaySkills.setText(skills);
                    }
                    if (map.get("experience") != null){
                        experience = map.get("experience").toString();
                        mExperience.setText(experience);
                    }

                    if(map.get("switch") != null){
                        if(Objects.equals(map.get("switch"), "true")) {
                            mSwitch.setChecked(true);
                        }else {
                            mSwitch.setChecked(false);
                        }
                    }


                    //Glide.clear(mProfileImage);
                    if (map.get("profileImageUrl") != null) {  // Display profile image with Glide
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl) {
                            case "default":
                                // mProfileImage.setImageResource(R.mipmap.ic_launcher);
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUserInformation() {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();
        lastName = mLastName.getText().toString();
        email = mEmail.getText().toString();
        country = mCountrySpinner.getSelectedItem().toString();
        city = mCity.getText().toString();
        skills = mDisplaySkills.getText().toString();
        experience = mExperience.getText().toString();


        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);  // to add name in map
        userInfo.put("phone", phone); // to add phone in map
        userInfo.put("lastName", lastName);
        userInfo.put("email", email);
        userInfo.put("country", country);
        userInfo.put("city", city);
        userInfo.put("skills", skills);
        userInfo.put("experience", experience);
        mUserDatabase.updateChildren(userInfo);  // Save map with name and phone

        //image
        if (resultUri != null) {   // save image in FirebaseStorage
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });

        } else {
            finish();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // method for result code in method startActivityForResult(intent, 1);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //&& resultCode == Activity.RESULT_OK
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);

        }

        //pdf
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uploadPDFFile(data.getData());
        }
    }

    private void getAdapterSpinnerCountry() {   // adapter for spinner

        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.country, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountrySpinner.setAdapter(adapterCountry);
        mCountrySpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {  //  method for spinner
        String choice = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), choice, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {             // method fpr spinner

    }
}