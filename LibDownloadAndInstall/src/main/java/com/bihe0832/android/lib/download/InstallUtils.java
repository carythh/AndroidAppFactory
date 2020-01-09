package com.bihe0832.android.lib.download;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by hardyshi on 2017/11/1.
 *
 * 使用InstallUtils的前提是要定义好 lib_bihe0832_install_file_provider 对应的定义
 * 而且安装包需要下载在 context.getString(R.string.lib_bihe0832_install_folder)
 */

public class InstallUtils{

    private static String INSTALL_TYPE = "application/vnd.android.package-archive";

    public static boolean installAPP(Context context, String filePath){
        return realInstallAPP(context,filePath);
    }

    public static boolean realInstallAPP(Context context, String filePath){
        if (DownloadManagerWrapper.INSTANCE.checkFileExist(filePath)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(filePath);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType(Uri.fromFile(file), INSTALL_TYPE);
            } else {
                Uri photoURI = FileProvider.getUriForFile(context, context.getResources().getString(R.string.lib_bihe0832_install_file_provider), file);
                intent.setDataAndType(photoURI, INSTALL_TYPE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            try {
                context.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
