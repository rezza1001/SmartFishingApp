package com.vma.smartfishingapp.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.api.FormPost;
import com.vma.smartfishingapp.api.PostManager;
import com.vma.smartfishingapp.ui.component.EditTextForm;
import com.vma.smartfishingapp.ui.component.OptionChooserImageDialog;
import com.vma.smartfishingapp.ui.component.VmaButton;
import com.vma.smartfishingapp.ui.component.find.FindDialog;
import com.vma.smartfishingapp.database.table.AccountDB;
import com.vma.smartfishingapp.libs.FileProcessing;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.ui.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RegisterActivity extends MyActivity {

    EditTextForm edtx_username,edtx_password,
            edtx_owner,edtx_shipName,edtx_phone,edtx_address,edtx_gt,edtx_sipi,
            edtx_port,edtx_institution;

    VmaButton bbtn_login;
    RoundedImageView imvw_photo;

    ArrayList<Bundle> listPort = new ArrayList<>();
    ArrayList<Bundle> listInstitution = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initLayout() {
        findViewById(R.id.imvw_close).setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            mActivity.finish();
        });

        edtx_username = findViewById(R.id.edtx_username);
        edtx_username.create(getResources().getString(R.string.user_id));
        edtx_username.setRequired();

        edtx_password = findViewById(R.id.edtx_password);
        edtx_password.create(getResources().getString(R.string.password));
        edtx_password.setRequired();
        edtx_password.setType(EditTextForm.Type.PASSWORD);

        edtx_owner = findViewById(R.id.edtx_owner);
        edtx_owner.create(getResources().getString(R.string.ownerName));
        edtx_owner.setRequired();

        edtx_shipName = findViewById(R.id.edtx_shipName);
        edtx_shipName.create(getResources().getString(R.string.ship_name));
        edtx_shipName.setRequired();

        edtx_phone = findViewById(R.id.edtx_phone);
        edtx_phone.create(getResources().getString(R.string.phone));
        edtx_phone.setRequired();
        edtx_phone.setType(EditTextForm.Type.PHONE);

        edtx_address = findViewById(R.id.edtx_address);
        edtx_address.create(getResources().getString(R.string.address));
        edtx_address.setType(EditTextForm.Type.ADDRESS);
        edtx_address.setRequired();

        edtx_gt = findViewById(R.id.edtx_gt);
        edtx_gt.create("GT");
        edtx_gt.setType(EditTextForm.Type.NUMBER);

        edtx_sipi = findViewById(R.id.edtx_sipi);
        edtx_sipi.create("No. Sipi");
        edtx_sipi.setType(EditTextForm.Type.NUMBER_DECIMAL);

        edtx_port = findViewById(R.id.edtx_port);
        edtx_port.setType(EditTextForm.Type.SELECT);
        edtx_port.create("Pelbuhan Perikanan");
        edtx_port.setRequired();

        edtx_institution = findViewById(R.id.edtx_institution);
        edtx_institution.setType(EditTextForm.Type.SELECT);
        edtx_institution.create("Koperasi");
        edtx_institution.setRequired();

        bbtn_login = findViewById(R.id.bbtn_login);
        bbtn_login.setButtonType(VmaButton.ButtonType.STANDARD);
        bbtn_login.create(getResources().getString(R.string.register),0);

        imvw_photo = findViewById(R.id.imvw_photo);
    }

    @Override
    protected void initData() {
        FileProcessing.clearImage(mActivity,"/"+FileProcessing.ROOT+"/"+FileProcessing.TEMP);
        loadPort();
    }

    @Override
    protected void initListener() {

        bbtn_login.setOnActionListener(view -> register());
        edtx_port.setOnSelectListener(this::showPort);
        edtx_institution.setOnSelectListener(this::showInstitution);
        imvw_photo.setOnClickListener(view -> showOptionImage());

    }

    private void showOptionImage(){
        OptionChooserImageDialog dialog = new OptionChooserImageDialog(mActivity);
        dialog.show();
        dialog.setOnSelectedListener(option -> {
            if (option == OptionChooserImageDialog.Option.CAMERA){
                openCamera();
            }
            else {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        }

        FileProcessing.createFolder(this,FileProcessing.ROOT+"/"+FileProcessing.TEMP);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent.createChooser(intent, "Choose File to Upload..");
        mActivity.startActivityForResult(intent,FileProcessing.REQUEST_OPEN_GALLERY);

    }
    private void openCamera() {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        FileProcessing.createFolder(this,FileProcessing.ROOT+"/"+FileProcessing.TEMP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        }

        String mediaPath = FileProcessing.getMainPath(this).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+FileProcessing.TEMP;
        String file =mediaPath+"/shiptmp.jpg";
        File newfile = new File(file);
        try {
            if (newfile.exists()){
                boolean deleted =  newfile.delete();
            }
            boolean created = newfile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Uri outputFileUri = FileProcessing.getUriFormFile(this, newfile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mActivity.startActivityForResult(intent, 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            String mediaPath = FileProcessing.getMainPath(this).getAbsolutePath() + "/" + FileProcessing.ROOT + "/" + FileProcessing.TEMP;
            String file = mediaPath + "/shiptmp.jpg";
            Glide.with(mActivity).load(new File(file))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imvw_photo);
            imvw_photo.setTag(file);
//            Uri uri = Uri.fromFile(new File(file));

        } else {
            if (data == null) {
                Toast.makeText(this, "Data is error", Toast.LENGTH_LONG).show();
                return;
            }
            if (resultCode != RESULT_OK) {
                return;
            }
            FileProcessing fileProcessing = new FileProcessing();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                String pathFile = "/"+FileProcessing.ROOT+"/"+FileProcessing.TEMP;
                fileProcessing.saveToTmpReal(mActivity,bitmap, pathFile,"shiptmp1.jpg");
                fileProcessing.setOnSavedListener((path, name) -> {
                    String newFile = FileProcessing.getMainPath(mActivity).getAbsolutePath() +path+name;
                    Glide.with(mActivity).load(new File(newFile))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imvw_photo);
                    imvw_photo.setTag(newFile);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void register(){
        if (!isValid()){
            return;
        }

        PostManager post = new PostManager(mActivity, ApiConfig.REGISTER);
        post.addParam("userName", edtx_username.getValue());
        post.addParam("password", edtx_password.getValue());
        post.addParam("shipName", edtx_shipName.getValue());
        post.addParam("sipi", edtx_sipi.getValue());
        post.addParam("gt", edtx_gt.getValue());
        int port = Integer.parseInt(edtx_port.getKey());
        post.addParam("port", port);

        try {
            JSONObject owner = new JSONObject();
            owner.put("name",edtx_owner.getValue());
            owner.put("phone",edtx_phone.getValue());
            owner.put("address",edtx_address.getValue());
            owner.put("identityNumber","");
            post.addParam("owner", owner);

            JSONObject address = new JSONObject();
            address.put("province",32);
            address.put("regency",3202);
            address.put("districts",3202021);
            address.put("village",0);
            address.put("boatyard","");
            post.addParam("address", address);

            JSONObject project = new JSONObject();
            project.put("project",22);
            project.put("buyer",21);
            int institution = Integer.parseInt(edtx_institution.getKey());
            project.put("institution",edtx_institution.getValue());
            project.put("institutionId",institution);
            post.addParam("project", project);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                login(edtx_username.getValue(), edtx_password.getValue());
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    private void login(String username, String password){
        PostManager post = new PostManager(mActivity, ApiConfig.LOGIN);
        post.addParam("userName", username);
        post.addParam("password", password);
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                insertToDB(obj);
            }
            else {
                Utility.showToastError(mActivity,"Login Failed !");
            }
        });
    }

    private void insertToDB(JSONObject obj)  {
        try {
            JSONObject data  = obj.getJSONObject("data");

            AccountDB db = new AccountDB();
            db.username = data.getString("username");
            db.password = edtx_password.getValue();
            db.imei = data.getString("imei");
            db.mKey = data.getLong("mKey");
            db.shipName = data.getString("ship_name");
            db.owner = data.getJSONObject("owner").getString("name");
            db.phone = data.getJSONObject("owner").getString("phone");
            db.sipi = data.getString("sipi");
            db.gt = data.getString("gt");
            db.image = data.getString("image");
            db.insert(mActivity);

            if (imvw_photo.getTag() != null){
                uploadPhoto(db);
            }
            else {
                Utility.showToastSuccess(mActivity,"Login Success !");
                setResult(RESULT_OK);
                mActivity.finish();
            }


        } catch (JSONException e) {
            Utility.showToastError(mActivity,"Login Failed ! "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPort(){
        listPort.clear();
        PostManager post = new PostManager(mActivity, ApiConfig.PELABUHAN);
        post.exGet();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (!success){
                return;
            }
            try {
                JSONArray ja = obj.getJSONArray("data");
                for (int i=0; i<ja.length(); i++){
                    Bundle bundle = new Bundle();
                    bundle.putString("key",ja.getJSONObject(i).getString("id"));
                    bundle.putString("value",ja.getJSONObject(i).getString("name"));
                    listPort.add(bundle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            loadInstitution();
        });
    }

    private void loadInstitution(){
        listInstitution.clear();
        PostManager post = new PostManager(mActivity, ApiConfig.KOPERASI);
        post.exGet();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (!success){
                return;
            }
            try {
                JSONArray ja = obj.getJSONArray("data");
                for (int i=0; i<ja.length(); i++){
                    Bundle bundle = new Bundle();
                    bundle.putString("key",ja.getJSONObject(i).getString("id"));
                    bundle.putString("value",ja.getJSONObject(i).getString("name"));
                    listInstitution.add(bundle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void showPort(){
        FindDialog dialog = new FindDialog(mActivity);
        dialog.show("Pelabuhan Perikanan");
        dialog.setData(listPort);
        dialog.setOnSelectedListener(bundle -> {
            String value = bundle.getString("value");
            String key = bundle.getString("key");
            edtx_port.setValue(key, value);
        });
    }
    private void showInstitution(){
        FindDialog dialog = new FindDialog(mActivity);
        dialog.show("Koperasi");
        dialog.setData(listInstitution);
        dialog.setOnSelectedListener(bundle -> {
            String value = bundle.getString("value");
            String key = bundle.getString("key");
            edtx_institution.setValue(key, value);
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean isValid(){
        if (!edtx_username.isValid()){
            return false;
        }
        if (!edtx_password.isValid()){
            return false;
        }
        if (!edtx_shipName.isValid()){
            return false;
        }
        if (!edtx_owner.isValid()){
            return false;
        }
        if (!edtx_phone.isValid()){
            return false;
        }
        if (!edtx_port.isValid()){
            return false;
        }
        if (!edtx_institution.isValid()){
            return false;
        }
        return edtx_address.isValid();
    }

    private void uploadPhoto(AccountDB accountDB){
        FormPost post = new FormPost(mActivity,ApiConfig.UPLOAD_FILE);
        post.addParam("collection","ref_kapal");
        post.addParam("reffId",""+accountDB.mKey);
        post.addImage("file",imvw_photo.getTag().toString());
        post.execute();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                accountDB.image = "kapal/"+accountDB.mKey+".jpg";
                accountDB.insert(mActivity);
            }

            Utility.showToastSuccess(mActivity,"Login Success !");
            setResult(RESULT_OK);
            mActivity.finish();
        });
    }
}
