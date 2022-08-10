package com.evacupet.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evacupet.R;
import com.evacupet.utility.BaseUtility;
import com.evacupet.utility.CompressImageUtility;
import com.evacupet.utility.ConnectionUtil;
import com.evacupet.utility.ProgressDialogUtil;
import com.evacupet.utility.Validation;
import com.parse.DeleteCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends DashboardActivity implements View.OnClickListener {
    private static final String TAG = ProfileActivity.class.getSimpleName();

    private static final int CAPTURE_IMAGE = 0, SELECT_IMAGE = 1, MULTIPLE_PERMISSION = 005;
    @BindView(R.id.btn_add_user_photo)
    Button btnAddPhoto;


    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;
    @BindView(R.id.ed_username)
    EditText edUsername;
    @BindView(R.id.ed_first_name)
    EditText edFirstName;
    @BindView(R.id.ed_last_name)
    EditText edLastName;
    @BindView(R.id.ed_gate_code)
    EditText edGateCode;
    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ed_phone)
    EditText edPhone;
    @BindView(R.id.ed_address)
    EditText edAddress;
    @BindView(R.id.ed_city)
    EditText edCity;
    @BindView(R.id.ed_zipcode)
    EditText edZipcode;
    @BindView(R.id.ed_note)
    EditText edNote;
    @BindView(R.id.sp_state)
    Spinner spState;
    @BindView(R.id.rg_sign_up_type)
    RadioGroup rgSignUpType;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.rb_yes)
    RadioButton rbYes;
    @BindView(R.id.rb_no)
    RadioButton rbNo;

    @BindView(R.id.btn_deleteaccount)
    Button btn_deleteaccount;

    private String selectedImagePath;
    private Uri outputFileUri;
    boolean doubleBackToExitPressedOnce = false;
    private String state;
    private ProgressDialogUtil progressDialogUtil;
    private boolean signUpType = false;
    private Context context;
    private SharedPreferences.Editor appPrefEditor;
    private SharedPreferences appPref;
    private static final String APP_PREF_NAME = "Evacuet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.fl_content); //Remember this is the FrameLayout area within your activity_main.xml
        titleName.setText(getString(R.string.profile));
        getLayoutInflater().inflate(R.layout.activity_profile, contentFrameLayout);
        ButterKnife.bind(this);
        init();
    }
    private void init(){

        appPref = getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        appPrefEditor = appPref.edit();
        appPrefEditor.apply();





        progressDialogUtil = new ProgressDialogUtil(this);
        btnUpdate.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        setStateAdapter();

        //edPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        com.evacupet.utility.UsPhoneNumberFormatter addLineNumberFormatter = new com.evacupet.utility.UsPhoneNumberFormatter(
                new WeakReference<EditText>(edPhone));
        edPhone.addTextChangedListener(addLineNumberFormatter);



        rgSignUpType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_yes){
                    Log.e(TAG,"yes");
                    signUpType = true;
                }
                else if (checkedId == R.id.rb_no){
                    Log.e(TAG,"no");
                    signUpType = false;
                }
            }
        });
        setProfileData();
        btn_deleteaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDiloge();
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
                attemptUpdate();
                break;
            case R.id.btn_add_user_photo:
                outputFileUri = imageInit();
                // checkpermissons();

                break;
        }
    }

    public void checkpermissons() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            SelectImage();
        } else {
            requestpermission();
        }
    }

    public void requestpermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Camera Permission")
                    .setMessage("Permission is needed For Photos")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfileActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAPTURE_IMAGE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(myAppSettings);
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,}, MULTIPLE_PERMISSION);
        }

    }

    private void SelectImage() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        TextView tv_camera = dialog.findViewById(R.id.camera);
        TextView tv_gallery = dialog.findViewById(R.id.gallery);
        TextView tv_cancel = dialog.findViewById(R.id.cancel);
        tv_camera.setOnClickListener(view -> {
            dialog.dismiss();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
            //captureimageIntent.launch(cameraIntent);
        });
        tv_gallery.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
             startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
           // selectimageIntent.launch(intent);
        });
        tv_cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//
//        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri filePath = data.getData();
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//
//
//            if (checkimagerotate(data) != 0) {
//                Matrix matrix = new Matrix();
//                matrix.postRotate(checkimagerotate(data)); // Degrees calculated in Step 2
//                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                bitmap = rotatedBitmap;
//            }
//
//
//            File capture = CreateFile(bitmap);
//
//            Glide.with(this)
//                    .load(capture.getAbsolutePath())
//                    .error(R.drawable.ic_user)
//                    .dontAnimate()
//                    .into(iv_playerprofile);
//            iv_edit.setVisibility(View.VISIBLE);
//            ImagePath = capture;
//            ImageServerPath = ImagePath.getAbsolutePath();
//
//            //code
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//            Log.e("TAG", "onActivityResult:ImageServerPath SELECT_IMAGE " + ImageServerPath);
//            Log.e("TAG", "onActivityResult:ImagePath SELECT_IMAGE" + ImagePath);
//        } else if (requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            File capture = CreateFile(photo);
//            iv_playerprofile.setImageBitmap(photo);
//            iv_edit.setVisibility(View.VISIBLE);
//            ImagePath = capture;
//            ImageServerPath = ImagePath.getAbsolutePath();
//
//
//            Log.e("TAG", "onActivityResult:ImageServerPath CAPTURE_IMAGE " + ImageServerPath);
//            Log.e("TAG", "onActivityResult:ImagePath CAPTURE_IMAGE" + ImagePath);
//        }
//    }
//


    
    
//    // selectimage Intent
//    ActivityResultLauncher<Intent> selectimageIntent  = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // selelct image get
//                        Intent data = result.getData();
//                        Uri filePath = data.getData();
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        if (checkimagerotate(data) != 0) {
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(checkimagerotate(data)); // Degrees calculated in Step 2
//                            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                            bitmap = rotatedBitmap;
//                        }
//
//
//                        File capture = CreateFile(bitmap);
//
//                        Glide.with(Account_Edit.this)
//                                .load(capture.getAbsolutePath())
//                                .error(R.drawable.ic_user)
//                                .dontAnimate()
//                                .into(iv_playerprofile);
//                        iv_edit.setVisibility(View.VISIBLE);
//                        ImagePath = capture;
//                        ImageServerPath = ImagePath.getAbsolutePath();
//                        Log.e("TAG", "onActivityResult:ImageServerPath SELECT_IMAGE " + ImageServerPath);
//                        Log.e("TAG", "onActivityResult:ImagePath SELECT_IMAGE" + ImagePath);
//                        YoYo.with(Techniques.Tada)
//                                .duration(700)
//                                .repeat(1)
//                                .playOn(iv_playerprofile);
//                    }
//                }
//            });
//
//
//    // Capture image intent
//    ActivityResultLauncher<Intent> captureimageIntent = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // get image from intent
//                        Intent data = result.getData();
//                        Bitmap photo = (Bitmap) data.getExtras().get("data");
//                        File capture = CreateFile(photo);
//                        iv_playerprofile.setImageBitmap(photo);
//                        iv_edit.setVisibility(View.VISIBLE);
//                        ImagePath = capture;
//                        ImageServerPath = ImagePath.getAbsolutePath();
//                        YoYo.with(Techniques.FadeIn)
//                                .duration(700)
//                                .repeat(1)
//                                .playOn(iv_playerprofile);
//                        Log.e("TAG", "onActivityResult:ImageServerPath CAPTURE_IMAGE " + ImageServerPath);
//                        Log.e("TAG", "onActivityResult:ImagePath CAPTURE_IMAGE" + ImagePath);
//                    }
//                }
//            });









    private void showDeleteDiloge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.diloge_deleteaccount, null);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        TextView btn_delete = view.findViewById(R.id.btn_delete);
        builder.setView(view);

        final AlertDialog dialog = builder.show();
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                ParseUser user = ParseUser.getCurrentUser();
                user.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            //user deleted

                            Toast.makeText(ProfileActivity.this, "User Deleted Successfully", Toast.LENGTH_SHORT).show();
                            appPrefEditor.clear();
                            appPrefEditor.commit();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });



            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
    }
    private void sentToLoginScreen() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    void logoutser(){

        //logout From application
        ParseUser.logOutInBackground(new LogOutCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // logOutSuccessful();
                    appPrefEditor.clear();
                    appPrefEditor.commit();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    //  somethingWentWrong();
                    Log.e(TAG, "notdone:"+e.getMessage() );
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

  

    @Override
    public void onImageSuccess(Intent data) {
        final boolean isCamera;
        Log.e(TAG, "onImageSuccess:1 "+data.getData() );
        if (data.getDataString() == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            if (action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }
        Uri selectedImageUri;
        String path;
        if (isCamera) {


            selectedImageUri = data.getData();
            Log.e(TAG, "onImageSuccess: "+selectedImageUri );
            selectedImageUri = outputFileUri;
            selectedImagePath = new CompressImageUtility().compressImage(this, selectedImageUri.getPath());
               selectedImagePath = selectedImageUri.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
            //rotate if any

            if (checkimagerotate(data) != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(checkimagerotate(data)); // Degrees calculated in Step 2
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap = rotatedBitmap;
            }

            ivUserImage.setVisibility(View.VISIBLE);
            ivUserImage.setImageBitmap(bitmap);

        } else {
            selectedImageUri = data == null ? null : data.getData();
            path = new BaseUtility().getPath(this,selectedImageUri);
              selectedImagePath = path;

            Log.e(TAG, "onImageSuccess: "+data.getData() );
            selectedImagePath = new  CompressImageUtility().compressImage(this, path);
            Log.e(TAG, "onImageSuccess:2 "+selectedImagePath );
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);

            //rotate if any

            if (checkimagerotate(data) != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(checkimagerotate(data)); // Degrees calculated in Step 2
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap = rotatedBitmap;
            }


            ivUserImage.setVisibility(View.VISIBLE);
            ivUserImage.setImageURI(selectedImageUri);
        }
    }

    public int checkimagerotate(Intent intent) {
        int rotationDegrees = 0;
        try {
            InputStream inputStream = getContentResolver().openInputStream(intent.getData()); //intent is parameter from onActivityResult()
            ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exif = new ExifInterface(inputStream);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                inputStream.close();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotationDegrees = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotationDegrees = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotationDegrees = 270;
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotationDegrees;
    }



    private void attemptUpdate() {
        edUsername.setError(null);
        edEmail.setError(null);
        edPhone.setError(null);

        boolean cancel = false;
        View focusView = null;

        String username = edUsername.getText().toString().trim();
        String phone = edPhone.getText().toString().trim();
        String email = edEmail.getText().toString().trim();

        phone = phone.replaceAll("[^\\d]", "");


        // Check for a valid mobile address.
        if (TextUtils.isEmpty(phone)) {
            edPhone.setError(getString(R.string.error_field_required));
            focusView = edPhone;
            cancel = true;
        } else if (!Validation.isValidMobile(phone)) {
            edPhone.setError(getString(R.string.error_invalid_mobile));
            focusView = edPhone;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edEmail.setError(getString(R.string.error_field_required));
            focusView = edEmail;
            cancel = true;
        } else if (!Validation.isValidEmail(email)) {
            edEmail.setError(getString(R.string.error_invalid_email));
            focusView = edEmail;
            cancel = true;
        }
        // Check for a valid mobile address.
        if (TextUtils.isEmpty(username)) {
            edUsername.setError(getString(R.string.error_field_required));
            focusView = edUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (ConnectionUtil.isInternetOn(this)) {
                updateApi();
            } else {

                Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void setStateAdapter(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, getResources().getStringArray(R.array.state_list));
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spState.setAdapter(adapter);
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = adapter.getItem(position);
                Log.e(TAG,state+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    private void updateApi(){
        progressDialogUtil.showDialog();
        ParseUser user = ParseUser.getCurrentUser();

        user.setUsername(edUsername.getText().toString().toLowerCase());
        user.setEmail(edEmail.getText().toString());

        user.put("Address",edAddress.getText().toString());
        user.put("MobileNumber",edPhone.getText().toString());
        user.put("City",edCity.getText().toString());
        user.put("ZipCode",edZipcode.getText().toString());
        user.put("FirstName",edFirstName.getText().toString());
        user.put("LastName",edLastName.getText().toString());
        user.put("Volunteer",signUpType);
//        user.put("GateCode",edGateCode.getText().toString());
        if (!state.equals("Select state")) {
            user.put("State", state);
        }
        user.put("PropertyNotes",edNote.getText().toString());
        if (selectedImagePath != null) {
            Object imageObject = null;
            try {

                imageObject = new BaseUtility().readInFile(selectedImagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Create the ParseFile
            ParseFile file = new ParseFile("Image", (byte[]) imageObject);
            // Upload the image into Parse Cloud
            file.saveInBackground();
            try {
                file.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            user.put("Image",file);
        }

       user.saveInBackground(new SaveCallback() {
           @Override
           public void done(ParseException e) {
               progressDialogUtil.dismissDialog();

               if (e == null) {
                   Toast.makeText(ProfileActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();

               } else {
                   Log.e(TAG,"error = "+e.getMessage());
                   //Register Fail
                   //get error by calling e.getMessage()
               }

           }
       });

    }
    private void setProfileData(){
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            edAddress.setText(user.getString("Address"));
            edCity.setText(user.getString("City"));
            edPhone.setText(user.getString("MobileNumber"));
            edZipcode.setText(user.getString("ZipCode"));
            edFirstName.setText(user.getString("FirstName"));
            edLastName.setText(user.getString("LastName"));
            edNote.setText(user.getString("PropertyNotes"));
            edUsername.setText(user.getUsername());
            edEmail.setText(user.getEmail());
            signUpType = user.getBoolean("Volunteer");
            edGateCode.setText(user.getString("GateCode"));

            if (signUpType){
                rbYes.setChecked(true);
            }else {
                rbNo.setChecked(true);
            }
            if (!TextUtils.isEmpty(user.getString("State"))){
                state = user.getString("State").trim();
                setState(state);
            }

            if (user.getParseFile("Image") != null){
                byte[] profile_image_file = new byte[0];
                try {
                    profile_image_file = user.getParseFile("Image").getData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Bitmap profile_image_bitmap = BitmapFactory.decodeByteArray(profile_image_file , 0, profile_image_file.length);
                ivUserImage.setImageBitmap(profile_image_bitmap);
                ivUserImage.setVisibility(View.VISIBLE);
            }

        }
    }
    private void setState(String state){
        for (int i = 0; i<getResources().getStringArray(R.array.state_list).length; i++){
            if (state.equals(getResources().getStringArray(R.array.state_list)[i])){
                spState.setSelection(i);
            }
        }
    }
}