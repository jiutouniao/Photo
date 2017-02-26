package com.xiren.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @author 写文件的工具类
 */
public class FileUtils {

    public static final String LOG_TAG = FileUtils.class.getSimpleName();

//    public static final String ROOT_DIR = "Android/data/com.eaglesoul.eplatform.english";
    public static final String ROOT_DIR = "aaaa";
    public static final String ICON_DIR = "icon";
    //拍照图片缓存
    public static final String IMAGE_DIR = "image";
    //题目数据缓存
    public static final String EXERCISE_DIR = "exercise";

    //题目数据缓存
    public static final String COMMENT_DIR = "comment";
    private final static String image_tmp_folder =  "esenglish";
    //作业目录
    public final static String HOMEWORK = "homework/";
    //录音目录
    public final static String AUDIO_RECORD_FOLDER = "audiorecord/";




    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean fileIsExistsByPath(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 头像缓存文件在最外面
     */
    public static String getHeadDir() {
        return getDir(ICON_DIR);
    }


    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    private static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     *  根据用户名进行文件命名，处理多用户登录数据缓存
     * @param usrName  用户名文件文件夹
     * @param homeWord_id  homeWork id
     * @param name  根目录文件名称
     * @return
     */
    private static String getDir(String usrName, String homeWord_id, String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        if(usrName!=null && !usrName.isEmpty()){
            sb.append(usrName);
            sb.append(File.separator);
        }
        if(homeWord_id!=null && !homeWord_id.isEmpty()){
            sb.append(homeWord_id);
            sb.append(File.separator);
        }else{
            sb.append("123456");
            sb.append(File.separator);
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 获取SD下的应用目录
     */
    public static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 获取应用的cache目录
     */
    private static String getCachePath() {
        File f = MyApplication.getAppContext().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        Log.e(LOG_TAG, "dir----->" + dirPath);
        isFileADirExists(dirPath);
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 复制文件，可以选择是否删除源文件
     */
    public static boolean copyFile(String srcPath, String destPath,
                                   boolean deleteSrc) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        return copyFile(srcFile, destFile, deleteSrc);
    }

    /**
     * 复制文件，可以选择是否删除源文件
     */
    public static boolean copyFile(File srcFile, File destFile,
                                   boolean deleteSrc) {
        if (!srcFile.exists() || !srcFile.isFile()) {
            Log.e("FileUtils","源文件不存在");
            return false;
        }
        isFileADirExists(destFile);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            if (deleteSrc) {
                srcFile.delete();
            }
        } catch (Exception e) {
            LogUtil.e(e);
            return false;
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }
        return true;
    }
    /**
     * yanshu 2017/2/10
     * 判断文件及文件夹是否存在
     */
    public static void isFileADirExists(File file){
        if (!file.exists()) {
            Log.e("FileUtils","目标文件不存在");
            String filePath =file.getAbsolutePath();
            int index = filePath.lastIndexOf("/");

            String dirPath = filePath.substring(0,index + 1);
            File dir = new File(dirPath);
            //如果文件夹不存在则创建
            if  (!dir .exists()  && !dir .isDirectory()){
                Log.e("FileUtils","目标文件夹不存在");
                dir .mkdir();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("FileUtils","目标文件夹存在");
            }
        }
    }

    /**
     * yanshu 2017/2/10
     * 判断文件及文件夹是否存在
     */
    public static void isFileADirExists(String filePath){
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e("FileUtils","目标文件不存在");
            int index = filePath.lastIndexOf("/");

            String dirPath = filePath.substring(0,index + 1);
            File dir = new File(dirPath);
            //如果文件夹不存在则创建
            if  (!dir .exists()  && !dir .isDirectory()){
                Log.e("FileUtils","目标文件夹不存在");
                dir .mkdir();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("FileUtils","目标文件夹存在");
            }
        }
    }

    /**
     * 判断文件是否可写
     */
    public static boolean isWriteable(String path) {
        try {
            if (StringUtils.isEmpty(path)) {
                return false;
            }
            File f = new File(path);
            return f.exists() && f.canWrite();
        } catch (Exception e) {
            LogUtil.e(e);
            return false;
        }
    }

    /**
     * 修改文件的权限,例如"777"等
     */
    public static void chmod(String path, String mode) {
        try {
            String command = "chmod " + mode + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    /**
     * 把数据写入文件
     *
     * @param is       数据流
     * @param path     文件路径
     * @param recreate 如果文件存在，是否需要删除重建
     * @return 是否写入成功
     */
    public static boolean writeFile(InputStream is, String path,
                                    boolean recreate) {
        boolean res = false;
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            if (recreate && f.exists()) {
                f.delete();
            }
            if (!f.exists() && null != is) {
                File parentFile = new File(f.getParent());
                parentFile.mkdirs();
                int count = -1;
                byte[] buffer = new byte[1024];
                fos = new FileOutputStream(f);
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                res = true;
            }
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     *
     * @param content 需要写入的字符串
     * @param path    文件路径名称
     * @param append  是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(byte[] content, String path, boolean append) {
        boolean res = false;
        File f = new File(path);
        RandomAccessFile raf = null;
        try {
            if (f.exists()) {
                if (!append) {
                    f.delete();
                    f.createNewFile();
                }
            } else {
                f.createNewFile();
            }
            if (f.canWrite()) {
                raf = new RandomAccessFile(f, "rw");
                raf.seek(raf.length());
                raf.write(content);
                res = true;
            }
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            IOUtils.close(raf);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     *
     * @param content 需要写入的字符串
     * @param path    文件路径名称
     * @param append  是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(String content, String path, boolean append) {
        return writeFile(content.getBytes(), path, append);
    }
    
    
    /**
     * @param filePath
     * @return	读取SD中的文件数据
     */
    public static String readFile(String filePath) {
    	StringBuffer sb = new StringBuffer();
		try {
	        File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
            	file.createNewFile();
            }
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";
			while ((readline = br.readLine()) != null) {
				System.out.println("readline:" + readline);
				sb.append(readline);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString();
    }

    /**
     * 把键值对写入文件
     *
     * @param filePath 文件路径
     * @param key      键
     * @param value    值
     * @param comment  该键值对的注释
     */
    public static void writeProperties(String filePath, String key,
                                       String value, String comment) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);// 先读取文件，再把键值对追加到后面
            p.setProperty(key, value);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /**
     * 根据值读取
     */
    public static String readProperties(String filePath, String key,
                                        String defaultValue) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
            return null;
        }
        String value = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            value = p.getProperty(key, defaultValue);
        } catch (IOException e) {
            LogUtil.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return value;
    }

    /**
     * 把字符串键值对的map写入文件
     */
    public static void writeMap(String filePath, Map<String, String> map,
                                boolean append, String comment) {
        if (map == null || map.size() == 0 || StringUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            Properties p = new Properties();
            if (append) {
                fis = new FileInputStream(f);
                p.load(fis);// 先读取文件，再把键值对追加到后面
            }
            p.putAll(map);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /**
     * 把字符串键值对的文件读入map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, String> readMap(String filePath,
                                              String defaultValue) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }
        Map<String, String> map = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            map = new HashMap<String, String>((Map) p);// 因为properties继承了map，所以直接通过p来构造一个map
        } catch (Exception e) {
            LogUtil.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return map;
    }

    /**
     * 改名
     */
    public static boolean copy(String src, String des, boolean delete) {
        File file = new File(src);
        if (!file.exists()) {
            return false;
        }
        File desFile = new File(des);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                out.flush();
            }
        } catch (Exception e) {
            LogUtil.e(e);
            return false;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
        if (delete) {
            file.delete();
        }
        return true;
    }


    /***************************************************************************************************/
    /***************************************************************************************************/
    /************************************根据文件图片的名称将图片写入SDCARD中******************************/
//    public static String SDPATH = Environment.getExternalStorageDirectory()
//            + "/Photo_LJ/";
//
//    public static void saveBitmap(Bitmap bm, String picName) {
//        if (bm == null) {
//            UIUtils.postTaskSafely(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(UIUtils.getContext(), "错误图片", Toast.LENGTH_SHORT);
//                }
//            });
//
//            return;
//        }
//        try {
//            if (!isFileExist("")) {
//                File tempf = createSDDir("");
//            }
//            File f = new File(SDPATH, picName + ".JPEG");
//            if (f.exists()) {
//                f.delete();
//            }
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 当文件的目录不存在的时候，创建文件目录
//     * @param dirName
//     * @return
//     * @throws IOException
//     */
//    public static File createSDDir(String dirName) throws IOException {
//        File dir = new File(SDPATH + dirName);
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED))
//        {
//            dir.getAbsolutePath();
//            dir.mkdir();
//        }
//        return dir;
//    }
//
//    public static boolean isFileExist(String fileName) {
//        File file = new File(SDPATH + fileName);
//        file.isFile();
//        return file.exists();
//    }
//
//    public static void delFile(String fileName) {
//        File file = new File(SDPATH + fileName);
//        if (file.isFile()) {
//            file.delete();
//        }
//        file.exists();
//    }
//
//    public static void deleteDir() {
//        File dir = new File(SDPATH);
//        if (dir == null || !dir.exists() || !dir.isDirectory())
//            return;
//
//        for (File file : dir.listFiles()) {
//            if (file.isFile())
//                file.delete();
//            else if (file.isDirectory())
//                deleteDir();
//        }
//        dir.delete();
//    }
//
//
//
//
//    public static boolean delete(String path){
//        if (!TextUtils.isEmpty(path)) {
//            File file = new File(path);
//            if (file != null && file.exists()) {
//                file.delete();
//            }
//        }
//        return true;
//    }

    /***************************************************************************************************/
    /***************************************************************************************************/
    /************************************根据文件图片的路径将图片写入SDCARD中******************************/

    /**
     * 保存图片的方法 保存到sdcard
     * @throws Exception
     */
    public static void saveBitmapByPath(Bitmap bm, String pathName) {
      if(isSDCardAvailable()) {
          if (bm == null) {
              LogUtil.i("存储图片为空");
              return;
          }
          FileOutputStream fos = null;

          File file = new File(pathName);
          if (file.exists()) {
              file.delete();
          }
          try {
              fos = new FileOutputStream(file);
              if (null != fos) {
                  bm.compress(Bitmap.CompressFormat.PNG, 90, fos);
                  fos.flush();
                  fos.close();
              }
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }

    }

    /**
     * 根据文件路径保存文件
     * @param bm
     * @param pathName
     * @return
     */
    public static File saveBitmap(Bitmap bm, String pathName) {
        if(isSDCardAvailable()) {
            FileOutputStream fos = null;
            File file = new File(pathName);
            if (file.exists()) {
                file.delete();
            }
            try {
                fos = new FileOutputStream(file);
                if (null != fos) {
                    bm.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    public static File saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        BufferedOutputStream os = null;
        File file = null;
        try {
            file = new File(_file);
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
        }
        return file;
    }

    /**
     * 获取SDCard图片
     * @return Bitmap
     */
    public static Bitmap getImageFromSDCardByPath(String pathname) {
         if(pathname != null) {
              if(isSDCardAvailable()) {
                  File file = new File(pathname);
                  if (file.exists()) {
                      Bitmap bm = BitmapFactory.decodeFile(pathname);
                      return bm;
                  }
                  else {
                      LogUtil.e("图片不存在");
                  }
              }
              else {
                  LogUtil.e("没有内存卡");
              }
         }
        return null;
    }

    /**
     * 删除文件
     * @param pathname
     */
    public static void delFileByPath(String pathname) {
        if(pathname != null) {
            File file = new File(pathname);
            if (file.isFile()) {
                file.delete();
            }
            file.exists();
        }
    }

    /**
     * 清除所有的历史图片
     *
     */
    /**
     * 清除系统缓存  清除缓存
     * @param dir 文件目标
     * @param numDays 文件夹新建时间
     * @return
     */
    //	clearCacheFolder(Activity.getCacheDir(), System.currentTimeMillis());
    // clear the cache before time numDays
    public static  int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static File getNewFile(Bitmap bitmap, String fileName) {
        _initTmpFolder();

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream;


        File file = new File(extStorageDirectory + "/" + image_tmp_folder + "/temp_" + fileName);

        if (file.exists()) {
            file.delete();
        }

        try {
            // make a new bitmap from your file
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            LogUtil.e("file", "" + e);
            LogUtil.i(LOG_TAG , "文件未找到 ");
        }
        LogUtil.e("file", "" + file);
        return file;
    }

    private static void _initTmpFolder() {
        File tmpFolder = new File(Environment.getExternalStorageDirectory() + "/" + image_tmp_folder);
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        } else {
            if (tmpFolder.isDirectory()) {
                String[] files = tmpFolder.list();
                // delete all files in tmp folder
                for (int i = 0; i < files.length; i++) {
                    File tmp = new File(tmpFolder, files[i]);
                    LogUtil.i(LOG_TAG,"该目录创建了");
                    if (tmp.exists()) {
                        tmp.delete();
                    }
                }
            } else {
                // cause problem, delete file and create directory
                tmpFolder.delete();
                tmpFolder.mkdirs();
            }
        }
    }

    /**
     * 时间戳
     */
    public static String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp;
    }

    /**
     * 获取UUID，当resourceID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
