package com.vma.smartfishingapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.DownloadFileFromURL;
import com.vma.smartfishingapp.component.BottomMenuView;
import com.vma.smartfishingapp.component.Loading;
import com.vma.smartfishingapp.dialog.GlobalConfirmDialog;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.fragment.MapPluginFragment;
import com.vma.smartfishingapp.libs.FileProcessing;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaNetwork;
import com.vma.smartfishingapp.libs.VmaPreferences;
import com.vma.smartfishingapp.libs.ZipManager;
import com.vma.smartfishingapp.libs.downloader.DownloadService;
import com.vma.smartfishingapp.master.MyActivity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainMapActivity extends MyActivity {

    FrameLayout frame_body;
    BottomMenuView menu_bottom;

    long newVersion = 0;
    String newName = "";


    @Override
    protected int setLayout() {
        return R.layout.fishmap_activity_main;
    }

    @Override
    protected void initLayout() {
        frame_body = findViewById(R.id.frame_body);
        menu_bottom = findViewById(R.id.menu_bottom);

        registerReceiver(receiver, new IntentFilter(VmaConstants.SHOW_TRACK));

    }

    @Override
    protected void initData() {
        Loading.showLoading(mActivity,getResources().getString(R.string.check_map_version));
        getVersionMap();
    }

    @Override
    protected void initListener() {
        menu_bottom.setOnSelectedListener(this::gotToPage);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void gotToPage(int page){
        Fragment fragment = MapPluginFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (page == 2){
//            fragment = DpiFragment.newInstance();
        }
        else if (page == 3){
//            fragment = TestMapFragment.newInstance();
        }
        else if (page == 4){
//            fragment = TrackListFragment.newInstance();
        }

        fragmentTransaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out);
        fragmentTransaction.replace(frame_body.getId(), fragment,"page_"+page);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.SHOW_TRACK)){
                menu_bottom.setSelected(0);
                new Handler().postDelayed(() -> {
                    Intent intent1 = new Intent(VmaConstants.SHOW_TRACK_MAP);
                    intent1.putExtra("path",intent.getStringExtra("file_path"));
                    sendBroadcast(intent1);
                },500);
            }
        }
    };

    private void checkLib(){
        String rootPath = FileProcessing.getMainPath(mActivity).getAbsolutePath()+"/"+FileProcessing.ROOT;
        if (new File((rootPath+"/plugin")).exists()){
            Loading.cancelLoading();
            gotToPage(1);
            return;
        }
        String pathPlugin = rootPath +"/map.zip";
        String downloadPath = FileProcessing.getDownloadDir(mActivity).getAbsolutePath()+"/map.zip";
        if (!new File(pathPlugin).exists()){
            if (!new File(downloadPath).exists()){
                VmaPreferences.delete(mActivity,VmaConstants.MAP_VERSION_ID);
                loadVersion();
            }
            else {
                copyFromDownload(downloadPath, rootPath);
            }
        }
        else {
            FileProcessing.init(mActivity);
            ZipManager zipManager = new ZipManager(mActivity);
            zipManager.extractPluginMaps();
            zipManager.setOnFinishListener(path -> checkLib());
        }
    }

    private void download(String link){
        Intent intent = new Intent();
        intent.putExtra(DownloadService.URL, link);
        intent.putExtra(DownloadService.DESCRIPTION, "Download file configuration map. Please wait...");
        intent.putExtra(DownloadService.TITLE, "Downloading...");
        intent.putExtra(DownloadService.FILE_NAME, "map.zip");
        DownloadFileFromURL service = new DownloadFileFromURL(mActivity,intent);
        service.startDownload();
        service.setOnFinishDownloadListener(new DownloadFileFromURL.OnFinishDownloadListener() {
            @Override
            public void onSuccess() {
                Utility.showToastSuccess(mActivity,"Download Success");
                VmaPreferences.save(mActivity, VmaConstants.MAP_VERSION_ID, newVersion);
                VmaPreferences.save(mActivity, VmaConstants.MAP_VERSION_NAME, newName);
                FileProcessing.DeleteFile(FileProcessing.getMainPath(mActivity).getAbsolutePath()+"/"+FileProcessing.ROOT,"map.zip");
                FileProcessing.DeleteFolder(FileProcessing.getDownloadDir(mActivity).getAbsolutePath()+"/"+FileProcessing.ROOT+"/plugin");
                FileProcessing.DeleteFile(FileProcessing.getDownloadDir(mActivity).getAbsolutePath()+"/"+FileProcessing.ROOT,"map.mmpk");

                checkLib();
            }

            @Override
            public void onFailed() {
                Utility.showToastError(mActivity,"Download Failed");
                FileProcessing.DeleteFile(FileProcessing.getDownloadDir(mActivity).getAbsolutePath(),"map.zip");
            }
        });
    }

    private void copyFromDownload(String downloadPath, String mainPath){
        File fromFile = new File(downloadPath);
        File toFile = new File(mainPath+"/map.zip");
        if (!toFile.exists()){
            try {
                boolean x = toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileProcessing.Copy(fromFile, toFile);
            checkLib();
        } catch (IOException e) {
            e.printStackTrace();
            Utility.showToastError(mActivity,"Failed to copy");
            mActivity.finish();
        }
    }

    private void getVersionMap(){
        Loading.showLoading(mActivity,getResources().getString(R.string.check_map_version));
        VmaNetwork network = new VmaNetwork(mActivity);
        if (!network.isNetworkConnected()){
            checkLib();
            return;
        }
        loadVersion();
    }

    private void loadVersion(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("STATIC")
                .document("MAP")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null){
                            newVersion = doc.get("version") == null ? 0 : (long) doc.get("version");
                            newName = doc.getString("version_name");
                            String link = doc.getString("link");
                            Date updateAt = doc.getDate("updated_at");
                            long currentVersion = VmaPreferences.getLong(mActivity, VmaConstants.MAP_VERSION_ID);
                            if (newVersion > currentVersion){
                                needUpdate(link,updateAt);
                            }
                            else {
                                checkLib();
                            }

                        }
                    }
                });
    }

    private void needUpdate(String link, Date updateAt){
        GlobalConfirmDialog dialog = new GlobalConfirmDialog(mActivity);
        dialog.show("Pembaharuan MAP","Terdapat pembaharuan map versi "+ newName+" pada tanggal "+Utility.getDateString(updateAt,"dd MMM yyyy")+
                "\nUntuk mengoptimalkan map saat ini,Silahkan melakukan pembaharuan");
        dialog.setAction("Update Sekarang","Update Nanti");
        dialog.setOnActionListener(action -> {
            if (action == GlobalConfirmDialog.Action.YES){
                download(link);
            }
        });
    }

}
