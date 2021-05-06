package com.orion.application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.orion.common.CheckAppUpdateTask;
import com.orion.common.CommonFunction;
import com.orion.webservice.IAsyncTaskResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ApplicationDownloadActivityNew extends AppCompatActivity implements IAsyncTaskResponse<Boolean> {

    private ProgressDialog progress;
    private Context context;
    ProgressDialog mProgressDialog;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.download_page_layout_new);
        context = getApplicationContext();

        String file_Location = BuildConfig.ApplicationFolderTOVersionFileExtensionPath;
        String path = BuildConfig.ApplicationDataDownloadFolderPath;
        String fileName = BuildConfig.VersionFileName;

        mProgressDialog = new ProgressDialog(ApplicationDownloadActivityNew.this);
        mProgressDialog.setMessage("Connecting to server...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        boolean internet = CommonFunction.isInternetOn(this.context);
        if (internet)
            updateCheck(file_Location, path, fileName, mProgressDialog);
        else {
            goToLogin();
        }
    }

    private void updateCheck(String file_Location, String path, String fileName, ProgressDialog progressDialog) {
        try {

            final CheckAppUpdateTask downloadFile = new CheckAppUpdateTask();

            downloadFile.setContext(ApplicationDownloadActivityNew.this, file_Location, path, fileName, progressDialog, this);
            downloadFile.execute(BuildConfig.FileVersionDownloadLink);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(ApplicationDownloadActivityNew.this, LoginPageActivity.class);
        startActivity(intent);
        finish();
    }

    public void downloadUpdate() {

        if (checkPermission()) {
            UpdateApp updateApp = new UpdateApp();
            updateApp.setContext(ApplicationDownloadActivityNew.this);
            updateApp.execute(BuildConfig.ApplicationDownloadLink);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted && cameraAccepted) {
                    UpdateApp updateApp = new UpdateApp();
                    updateApp.setContext(ApplicationDownloadActivityNew.this);
                    updateApp.execute("https://example.com/apks/my_apk.apk");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public class UpdateApp extends AsyncTask<String, Integer, String> {
        private ProgressDialog mPDialog;
        private Context mContext;

        void setContext(Activity context) {
            mContext = context;
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPDialog = new ProgressDialog(mContext);
                    mPDialog.setMessage("Please wait...");
                    mPDialog.setIndeterminate(true);
                    mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mPDialog.setCancelable(false);
                    mPDialog.show();
                }
            });
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                int lengthOfFile = c.getContentLength();

                String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath() + BuildConfig.ApplicationDataDownloadFolderPath;
                File file = new File(PATH);

                boolean isCreate = file.mkdirs();
                File outputFile = new File(file, BuildConfig.ApplicationNameWithExtension);
                if (outputFile.exists()) {
                    boolean isDelete = outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1;
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    fos.write(buffer, 0, len1);
                    publishProgress((int) ((total * 100) / lengthOfFile));
                }
                fos.close();
                is.close();
                if (mPDialog != null)
                    mPDialog.dismiss();
                installApk();
            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mPDialog != null)
                mPDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mPDialog != null) {
                mPDialog.setIndeterminate(false);
                mPDialog.setMax(100);
                mPDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mPDialog != null)
                mPDialog.dismiss();
            if (result != null)
                Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext, "File Downloaded", Toast.LENGTH_SHORT).show();
        }

        private void installApk() {
            try {
                String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
                File file = new File(PATH + BuildConfig.ApplicationFolderTOFileExtensionPath);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                if (Build.VERSION.SDK_INT >= 24) {
                    Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                    List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                    for (ResolveInfo resolveInfo : resInfoList) {
                        mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onDestroy() {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public <T> void onTaskComplete(T result) {
        if ((Boolean) result) {
            downloadUpdate();
        } else {
            goToLogin();
        }
    }
}
