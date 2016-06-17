package com.sportus.sportus.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.sportus.sportus.R;
import com.sportus.sportus.data.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileFragment extends BaseFragment {
    private static final String TAG = EditProfileFragment.class.getSimpleName();

    private int PICK_IMAGE_REQUEST = 1;

    View view;

    DatabaseReference readUserRef;
    DatabaseReference updateUserRef;
    List<String> checkeds;

    GridLayout parentLayout;

    EditText inputNameMyProfile;
    EditText inputEmailMyProfile;
    EditText inputLocalMyProfile;
    EditText inputAgeMyProfile;
    ImageView profileImage;

    String checkboxs[];
    CheckBox checkBox;
    List<String> userInterests;
    String[] userInterestsArray;

    User user;
    String currentUserId;
    FirebaseUser currentUser;
    String photoUrlString;


    private Button buttonChoose;
    Button cancelEditProfile;
    Button saveEditProfile;
    private Bitmap bitmap;
    Uri downloadUrl;

    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference profileImageRef;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changeToolbar("Editar Perfil");
        view = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        setupUI(view);

        parentLayout = (GridLayout) view.findViewById(R.id.gridInterests);

        inputNameMyProfile = (EditText) view.findViewById(R.id.inputNameMyProfile);
        inputEmailMyProfile = (EditText) view.findViewById(R.id.inputEmailMyProfile);
        inputLocalMyProfile = (EditText) view.findViewById(R.id.inputLocalMyProfile);
        inputAgeMyProfile = (EditText) view.findViewById(R.id.inputAgeMyProfile);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        buttonChoose = (Button) view.findViewById(R.id.changeProfilePhoto);
        cancelEditProfile = (Button) view.findViewById(R.id.cancelEditProfile);
        saveEditProfile = (Button) view.findViewById(R.id.saveEditProfile);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();
        readUserRef = database.getReference("users").child(currentUserId);
        updateUserRef = database.getReference();

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://sport-us.appspot.com");
        profileImageRef = storageRef.child("profile-images");

        checkboxs = getResources().getStringArray(R.array.event_types);

        readUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        fillInputs(user);
                        userInterests = user.getInterests();
                        if (userInterests != null) {
                            userInterestsArray = userInterests.toArray(new String[userInterests.size()]);
                            createCheckbox(userInterestsArray);
                        } else {
                            createCheckbox(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        buttonChoose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        cancelEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment(new ProfileFragment(), currentUserId);
            }
        });

        saveEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                String name = inputNameMyProfile.getText().toString();
                String email = inputEmailMyProfile.getText().toString();
                String local = inputLocalMyProfile.getText().toString();
                String age = inputAgeMyProfile.getText().toString();
                List<String> interests = checkeds;
                showDialog("Salvando os dados");

                updateUser(userId, name, email, local, age, interests);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        openProfileFragment(new ProfileFragment(), currentUserId);
                    }
                }, 600);
            }
        });

        return view;
    }

    private void createCheckbox(String[] userInterestsArray) {
        checkeds = new ArrayList<String>();

        for (int i = 0; i < checkboxs.length; i++) {
            final int index = i;
            checkBox = new CheckBox(getActivity());
            checkBox.setText(checkboxs[i]);
            checkBox.setButtonTintList(getContext().getResources().getColorStateList(R.color.colorWhite));
            checkBox.setTextColor(getResources().getColor(R.color.colorWhite));
            checkBox.setTextSize(16);
            checkBox.setId(i);
            String checkboxText = (String) checkBox.getText();
            if (userInterests != null) {
                if (Arrays.asList(this.userInterestsArray).contains(checkboxText)) {
                    checkBox.setChecked(true);
                    checkeds.add(checkboxs[index]);
                }
            }
            parentLayout.addView(checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked) {
                        checkeds.add(checkboxs[index]);
                    } else {
                        checkeds.remove(checkboxs[index]);
                    }
                }
            });
        }
    }

    private void fillInputs(User user) {
        inputNameMyProfile.setText((user.getName() == null) ? "" : user.getName());
        inputEmailMyProfile.setText((user.getEmail() == null) ? "" : user.getEmail());
        inputLocalMyProfile.setText((user.getLocal() == null) ? "" : user.getLocal());
        inputAgeMyProfile.setText((user.getAge() == null) ? "" : user.getAge());
        Picasso.with(getActivity().getApplicationContext())
                .load(user.getPhoto())
                .placeholder(R.drawable.profile)
                .into(profileImage);
    }

    private void updateUser(String userId, String name, String email, String local, String age, List<String> interests) {
        User user = new User(name, email, local, age, interests);
        Map<String, Object> userValue = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userId, userValue);

        updateUserRef.updateChildren(childUpdates);
    }

    private void updateUserImage(Uri photo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photo)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.rounded_corners_profile));
                profileImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Carregando...", "Só um minutinho...", false, false);
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        final Bitmap bitmap = profileImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileImageRef.child(currentUserId).putBytes(data);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                loading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                photoUrlString = downloadUrl.toString();
                updateUserRef.child("users").child(currentUserId).child("photo").setValue(photoUrlString);
                updateUserImage(downloadUrl);
                loading.dismiss();
            }
        });
    }
}
