package com.orion.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

import com.orion.util.Util;
import com.orion.webservice.IAsyncTaskResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckAppUpdateTask extends AsyncTask<String, Integer, Boolean> {

    ProgressDialog progressDialog;
    int status = 0;
    String _fileLocationWithPath;
    String _folderLocation;
    String _fileName;
    ProgressDialog progress;
    String errorMsg = "";
    ProgressDialog mProgressDialog;
    private IAsyncTaskResponse<Boolean> callback;

    public CheckAppUpdateTask() {
    }

    public void setContext(Context context, String fileLocationWithPath, String folderLocation, String fileName, ProgressDialog oProgressDialog, IAsyncTaskResponse<Boolean> cb) {
        this.context = context;
        _fileLocationWithPath = fileLocationWithPath;
        _folderLocation = folderLocation;
        _fileName = fileName;
        mProgressDialog = oProgressDialog;
        callback = cb;
    }

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    public CheckAppUpdateTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        boolean result = false;

        try {
            Thread.sleep(2000);
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                errorMsg = "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();


            //Delete & Recreate Dir
            String FileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + _fileLocationWithPath;
            String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + _folderLocation;

            File file = new File(PATH);
            if (file.exists())
                Util.deleteDirectory(file);

            file.mkdirs();

            File outputFile = new File(file, _fileName);
            if (outputFile.exists()) {
                outputFile.delete();
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(FileLocation);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(outputFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();

            String version = text.toString();
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String versionName = pInfo.versionName;
            int version_code = pInfo.versionCode;

            if (Integer.parseInt(version) > version_code) {
                result = true;
            }

        } catch (java.net.SocketTimeoutException e) {
            errorMsg = "Application Connection Timeout : " + e.getLocalizedMessage();
        }
        catch (Exception e) {
            errorMsg = "Application Update Failed : " + e.getLocalizedMessage();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mWakeLock.release();
        mProgressDialog.dismiss();

        if (status == 1)
            Toast.makeText(context, "File Not Available", Toast.LENGTH_LONG).show();
        else if (errorMsg.length() > 0) {
            final Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT);
            toast.show();

            new CountDownTimer(20000, 1000) {
                public void onTick(long millisUntilFinished) {
                    toast.show();
                }

                public void onFinish() {
                    toast.cancel();
                }
            }.start();
        }

        callback.onTaskComplete(result);
    }
}


