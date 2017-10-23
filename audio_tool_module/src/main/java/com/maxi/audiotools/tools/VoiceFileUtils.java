package com.maxi.audiotools.tools;

import android.os.Environment;
import android.util.Log;

import com.maxi.audiotools.IMAudioManager;
import com.maxi.audiotools.apis.DeleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.util.List;

/**
 * 文件处理工具类
 */
public class VoiceFileUtils {

    private File file; // 文件根目录
    private File tempFile; // 临时文件,保存下载的文件,每次下载过后,清空

    /**
     *
     */
    public VoiceFileUtils() {
        // 构造文件根目录
        file = new File(getSdcardPath());
        if (!file.exists()) { // 判断文件夹是否存在
            // 不存在，则创建文件夹
            file.mkdirs();
        }
    }

    /**
     * 判断本地是否有指定url音频文件的缓存文件
     *
     * @param url 音频文件url
     * @return 缓存文件路径, 如果不存在则返回null
     */
    public String exists(String url) throws IOException {
        // 截取地址, 获取缓存音频文件的名称
        String fileName;
        if (url.startsWith("http://")) {
            fileName = getMediaID(url) == null ? url.substring(url.lastIndexOf("/")) : getMediaID(url);
            // 构建文件
            tempFile = new File(file, fileName);
            Log.i("AUDIO_TOOLS", "缓存路径 = " + tempFile.getAbsolutePath());
            // 如果文件存在,则返回文件路径,如果文件不存在则返回null并且创建文件
            if (tempFile.exists()) {
                return tempFile.getAbsolutePath();
            } else {
                tempFile.createNewFile(); // 创建音频文件
                return null;
            }
        } else {
            fileName = url;
            tempFile = new File(fileName);
            // 如果文件存在,则返回文件路径,如果文件不存在则返回null并且创建文件
            if (tempFile.exists()) {
                return tempFile.getAbsolutePath();
            } else {
                tempFile.createNewFile(); // 创建音频文件
                return null;
            }
        }
    }

    private String getMediaID(String url) {
//        String mediaID = url.split("/media/")[1];
//        return mediaID.split("/")[0];
        String mediaID = url.replaceAll("\\.", "").replaceAll("/", "").replaceAll(":", "") + ".aud";
        Log.i("AUDIO_TOOLS", "缓存id = " + mediaID);
        return mediaID;
    }

    /**
     * 保存音频文件
     *
     * @param is InputStream
     */
    public void saveFile(InputStream is) throws IOException {
        // 构建文件输出流
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] b = new byte[1024]; // 构建缓冲区
        // 循环读取数据
        int byteCount; // 每次读取的长度
        while ((byteCount = is.read(b)) != -1) {
            fos.write(b, 0, byteCount); // 将每次读取的数据保存到文件当中
        }
        fos.close(); // 关闭文件输出流
        is.close(); // 关闭输入流
    }

    /**
     * 删除文件(多个文件)
     *
     * @param fileNames 文件名的集合
     */
    public void deleteFiles(List<String> fileNames) {
        if (fileNames != null && fileNames.size() > 0) {
            // 循环依次删除每一个文件
            for (String fileName : fileNames) {
                // 构建文件
                File f = new File(file, fileName);
                if (f.exists()) { // 文件存在
                    f.delete(); // 删除文件
                }
            }
        }
    }

    /**
     * 合并所有的音频文件为一个音频文件(后缀名为.amr)
     * 由于.amr的文件的头文件大小固定都是6个字节
     * 所以合并的时候,只需要把除去第一个文件外的头文件(6个字节)然后拼接在一起
     *
     * @param fileNames 文件名称集合
     * @return 合并后的文件File
     */
    public File mergeAudioFiles(List<String> fileNames) throws IOException {
        // 创建临时文件保存合并后的文件
        tempFile = new File(file, System.currentTimeMillis() + "_temp.amr");
        if (tempFile.exists()) { // 如果文件存在,则删除文件
            tempFile.delete(); // 删除文件
        }
        tempFile.createNewFile(); // 重新创建新文件
        // 构建文件输出流,用来写数据到文件中
        FileOutputStream os = new FileOutputStream(tempFile);
        RandomAccessFile raf;
        // 循环依次读取每一个文件的音频信息
        for (int i = 0; i < fileNames.size(); i++) {
            // 以只读模式打开文件流
            raf = new RandomAccessFile(new File(file, fileNames.get(i)), "r");
            // 如果不是第一个文件,则跳过文件头,直接读取音频帧信息
            if (i != 0) {
                raf.seek(6); // 跳过文件头,.amr文件头为固定的6个字节
            }
            // 构建临时缓存数组
            byte[] bytes = new byte[1024];
            int len; // 保存每次读取的数据长度
            // 循环依次读取数据
            while ((len = raf.read(bytes)) != -1) {
                // 将读取的数据写入文件
                os.write(bytes, 0, len);
            }
            raf.close(); // 关闭流
        }
        // 数据写入完成后
        os.close(); // 关闭流
        return tempFile;
    }

    /**
     * 获取SDCARD的绝对路径
     *
     * @return Environment.getExternalStorageDirectory().getAbsolutePath()
     */
    private String getSdcardPath() {
        return getExternDir(IMAudioManager.instance().audioFileCache);
    }

    /**
     * 获取保存文件的根目录
     *
     * @return String file.getAbsolutePath()
     */
    public String getFileDirector() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public String getExternDir(String dir) {
        String path = IMAudioManager.instance().mContext.getCacheDir().getPath();
        if (dir != null) {
            path += dir;
        }

        return path;
    }

    public void recursionDeleteFile(DeleteListener mDeleteListener) {
        File file = new File(getSdcardPath());
        if (file.isFile()) {
            mDeleteListener.failed("error : the path is a file");
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                mDeleteListener.failed("the directory is already empty");
                return;
            }
            for (File f : childFile) {
                if (f.exists()) { // 文件存在
                    f.delete(); // 删除文件
                }
            }
        }
        mDeleteListener.success();
    }

}
