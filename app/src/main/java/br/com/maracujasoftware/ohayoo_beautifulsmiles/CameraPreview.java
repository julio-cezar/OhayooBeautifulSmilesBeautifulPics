package br.com.maracujasoftware.ohayoo_beautifulsmiles;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

/**
 * Created by julio on 07/09/2016.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String TAG  = "myCamTag";
    Context ctx;
    Activity act;

    int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public CameraPreview(Context context, Camera camera, Activity activity) {
        super(context);
        mCamera = camera;
        this.ctx = context;
        this.act = activity;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            //mCamera.setDisplayOrientation(90);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
       // mCamera.setDisplayOrientation(90);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
      /*  this.getHolder().removeCallback(this);
        mCamera.stopPreview();
        mCamera.release();*/

    }

    public void SwitchCam(int cID){


        if(cID == Camera.CameraInfo.CAMERA_FACING_FRONT){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }else{
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

        mCamera.stopPreview();
        mCamera.release();
        mHolder.removeCallback(this);

        mCamera = Camera.open(currentCameraId);

       // setCameraDisplayOrientation(CameraActivity.this, currentCameraId, camera);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();

    }
    public int getCamId(){
        return currentCameraId;
    }

    public SurfaceHolder getMyHolder(){
        return mHolder;
    }





}
