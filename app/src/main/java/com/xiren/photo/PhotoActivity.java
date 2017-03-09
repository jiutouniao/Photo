package com.xiren.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述：设置照相机拍照
 * 作者：shaobing
 * 时间： 2017/2/26 19:34
 */
public class PhotoActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    public static final String FILEPATH="filepath";
    public static final String TAG="PhotoActivity";

    //预览界面
    @Bind(R.id.cameraView)
    SurfaceView cameraView;
    private SurfaceHolder surfaceHolder;
    //照相机布局
    @Bind(R.id.rlyt_camera)
    RelativeLayout rlytCamera;
    //开始拍照界面
    @Bind(R.id.iv_start)
    ImageView ivStart;
    //点击使用界面
    @Bind(R.id.iv_use)
    ImageView ivUse;
    //取消拍照接卖弄
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    //
    @Bind(R.id.RelativeLayout1)
    RelativeLayout RelativeLayout1;
    //文件路径
    private String filePath = "";
    //权限判断
    private boolean isPermisson;
    //照相机
    private Camera mCamera;
    private Camera.Size mCameraSize;
    private int mRotation;

    //判断横竖屏
    MyOrientationDetector myOrientationDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        filePath=getIntent().getStringExtra(FILEPATH);
        initData();
    }


    private void initData(){
        float width = DisplayMetricsUtil.getDisplayWidth(PhotoActivity.this);
        float height = DisplayMetricsUtil.getDisplayHeight(PhotoActivity.this);
        mCamera =  CameraManager.getInstance().init(PhotoActivity.this, (int)height, (int)width, new CameraManager.OnCameraListener() {
            @Override
            public void OnPermisson(boolean flag) {
                ToastUtil.showLongToast(PhotoActivity.this,"摄像头权限拒绝");
                isPermisson = true;
            }
            @Override
            public void OnCameraSize(Camera.Size size) {
                LogUtil.d("OnCameraSize  "+size.width+"  "+size.height );
                mCameraSize =size;
            }
            @Override
            public void OnCameraRotation(int rotation) {
                mRotation = rotation;
            }
        });

        surfaceHolder = cameraView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if(mCameraSize!=null){
            surfaceHolder.setFixedSize(mCameraSize.width, mCameraSize.height);
        }
        surfaceHolder.addCallback(this);
        try {
            if(null==filePath || filePath.isEmpty()){
                filePath= Environment.getExternalStorageDirectory()+"/aaaaaa/"+System.currentTimeMillis() + ".jpg";
            }
            File file = new File(filePath);
            LogUtil.d(file.getAbsolutePath());
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            filePath =file.getAbsolutePath();
        }catch (Exception e){
            LogUtil.d("videoFilePath ERROR: ",e);
        }
        handleView(0);
    }

    /**
     * 设置界面
     * @param state   0 默认界面  1.点击拍照之后的界面
     */
    private void handleView(int state){
        switch (state){
            case 0:
                ivStart.setVisibility(View.VISIBLE);
                ivUse.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                break;
            case 1:
                ivStart.setVisibility(View.GONE);
                ivUse.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.VISIBLE);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        myOrientationDetector = new MyOrientationDetector(this);
        myOrientationDetector.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myOrientationDetector.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInstance().stopPreview();
    }

    @OnClick({R.id.cameraView, R.id.iv_start, R.id.iv_use, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_start:
                //判断权限
                if(isPermisson){return;}
//                CameraManager.getInstance().doTakePicture(pictureCallback);
                CameraManager.getInstance().stop();
                handleView(1);
                break;
            case R.id.iv_use:
                break;
            case R.id.tv_cancel:
                CameraManager.getInstance().start();
                handleView(0);
                break;
        }
    }

    /**
     * 图片回调
     */
    Camera.PictureCallback pictureCallback  = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            LogUtil.d("onPictureTaken    " +data);
            if(data==null){
                return;
            }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            Matrix matrix=new Matrix();
//            //设置缩放
//            matrix.postScale(0.5f, 0.5f);
//            bitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
////            File file = new File(Environment.getExternalStorageDirectory(),
////                    System.currentTimeMillis() + ".jpg");
//            try {
//                FileOutputStream outStream = new FileOutputStream(filePath);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//                outStream.close();
//                camera.startPreview();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            CameraManager.getInstance().stop();
        }
    };



    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    Camera.PictureCallback mRawCallback = new Camera.PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };



    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if(null != data){
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
            }
//            //保存图片到sdcard
//            if(null != b)
//            {
//                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
//                //图片竟然不能旋转了，故这里要旋转下
//                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
//                FileUtil.saveBitmap(rotaBitmap);
//            }
            //再次进入预览
            mCamera.startPreview();
        }
    };


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraManager.getInstance().setPreview(holder);
        CameraManager.getInstance().start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraManager.getInstance().stop();
    }
}
