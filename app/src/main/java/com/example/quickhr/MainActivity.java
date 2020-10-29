package com.example.quickhr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickhr.Authentication.LoginActivity;
import com.example.quickhr.Cards.Cards;
import com.example.quickhr.Cards.MyArrayAdapter;
import com.example.quickhr.Matches.MatchesActivity;
import com.example.quickhr.Settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cards cardsData[];
    private com.example.quickhr.Cards.MyArrayAdapter MyArrayAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;  // for register swipes to the Database

    ListView listView;
    List<Cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users"); // for register swipes to the Database

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserPreferenceType();

        rowItems = new ArrayList<Cards>();

        MyArrayAdapter = new MyArrayAdapter(this, R.layout.item, rowItems);


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);


        flingContainer.setAdapter(MyArrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                MyArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true); // register swipes to the Database

                isConnectionMatch(userId); // register matches to the Database

               Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                /*View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);*/
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey(); // fot chat
                    usersDb.child(snapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key); // chat
                    usersDb.child(currentUId).child("connections").child("matches").child(snapshot.getKey()).child("ChatId").setValue(key);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*

                }*/

    private String userType;
    private String oppositeUserType;
    public void checkUserPreferenceType(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("type").getValue() != null){
                        userType = snapshot.child("type").getValue().toString();
                        switch (userType){
                            case "HumanResources":
                                oppositeUserType = "Worker";
                                break;
                            case "Worker":
                                oppositeUserType = "HumanResources";
                                break;
                        }
                        getOppositeTypeUsers();
                    }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//
    private String checkLastName, checkPhone, profileImageUrl, checkEmail, checkCountry, checkCity, checkSkills, checkExperience, checkSwitch;
    public void getOppositeTypeUsers(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("type").getValue() != null) {
                    if (snapshot.exists() && !snapshot.child("connections").child("nope").hasChild(currentUId)
                            && !snapshot.child("connections").child("yeps").hasChild(currentUId)
                            && snapshot.child("type").getValue().toString().equals(oppositeUserType)) {


                        getCheckParametersForCards(snapshot);

                        profileImageUrl = "default";
                        if (!snapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        }

                        if(snapshot.child("lastName").getValue() == null){
                            checkLastName = "No Last Name was provided";
                        }else checkLastName = snapshot.child("lastName").getValue().toString();

                        if(snapshot.child("phone").getValue() == null){
                            checkPhone = "No phone was provided";
                        }else checkPhone = snapshot.child("phone").getValue().toString();

                        if(snapshot.child("email").getValue() == null){
                            checkEmail = "No email was provided";
                        }else checkEmail = snapshot.child("email").getValue().toString();

                        if(snapshot.child("country").getValue() == null){
                            checkCountry = "No country was provided";
                        }else checkCountry = snapshot.child("country").getValue().toString();

                        if(snapshot.child("city").getValue() == null){
                            checkCity = "No city was provided";
                        }else checkCity = snapshot.child("city").getValue().toString();

                        if(snapshot.child("skills").getValue() == null){
                            checkSkills = "No skills was provided";
                        }else checkSkills = snapshot.child("skills").getValue().toString();

                        if(snapshot.child("experience").getValue() == null){
                            checkExperience = "No experience was provided";
                        }else checkExperience = snapshot.child("experience").getValue().toString();

                        if(snapshot.child("switch").getValue() == null){
                            checkSwitch = "false";
                        }else checkSwitch = snapshot.child("switch").getValue().toString();


                            Cards item = new Cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profileImageUrl, checkPhone, checkLastName, checkEmail, checkCountry, checkCity, checkSkills, checkExperience, "", checkSwitch );
                            rowItems.add(item);

                        // step 3
                        MyArrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCheckParametersForCards(DataSnapshot snapshot) {
        return "";
    }


    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
    }
}
