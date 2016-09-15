package br.com.maracujasoftware.ohayoo_beautifulsmiles;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.aviary.internal.headless.utils.MegaPixels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String TAG = "myCamTag";


    private static final int IMG_CODE_EDIT = 263;
    private ImageView ivImg;
    private EditText etUri;

    private ImageView mEditedImageView;
    Uri imageUri;

    String mCurrentPhotoPath;

    private Camera mCamera;
    private CameraPreview mPreview;

    Uri picUril;

    FrameLayout preview;

    static  int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        mCamera = getCameraInstance();
       setCameraDisplayOrientation(MainActivity.this, 0, mCamera);
        // Create our Preview view and set it as the content of our activity.
         main = new MainActivity();
        mPreview = new CameraPreview(this, mCamera, main);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


       /* ivImg = (ImageView) findViewById(R.id.iv_img);
        etUri = (EditText) findViewById(R.id.et_uri);

        Intent intent = AviaryIntent.createCdsInitIntent(this);
        startService(intent);*/

        mEditedImageView = (ImageView) findViewById(R.id.editedImageView);

         /* 1) Make a new Uri object (Replace this with a real image on your device) */
        //Uri imageUri = Uri.parse("content://media/external/images/media/####");
        //Uri imageUri = Uri.parse("content://storage/emulated/0/DCIM/Camera/image1.jpg");
        imageUri = Uri.parse("https://static.cineclick.com.br/sites/adm/uploads/banco_imagens/31/602x0_1439644246.jpg");

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    14);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

               /* case 1:

                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    Drawable d = new BitmapDrawable(yourSelectedImage);



                    // Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    //  mEditedImageView.setImageURI(editedImageUri);
                    mEditedImageView.setBackground(d);

                    break;*/
                case 1:
                    Uri uri = data.getData();

                    Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                            .setData(uri)
                            .build();
                    startActivityForResult(imageEditorIntent, 2);
                    break;
                case 2:
                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    // Toast.makeText(MainActivity.this, ""+editedImageUri, Toast.LENGTH_LONG).show();
                    Log.d("paths ", "" + editedImageUri);

                    String editedPath = "file:" + getRealPathFromURI(this, editedImageUri);
                    // galleryAddPic(Uri.parse(editedPath));
                    Log.d("paths ", "" + Uri.parse(editedPath));
                   // mEditedImageView.setImageURI(Uri.parse(editedPath));
                    Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();

                    break;
                case 3:
                    Uri picUril = Uri.parse(mCurrentPhotoPath);
                    // Toast.makeText(MainActivity.this, ""+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
                    Log.d("paths ", "" + picUril);

                    Intent picIntent = new AdobeImageIntent.Builder(this)
                            .setData(picUril)
                            .build();
                    startActivityForResult(picIntent, 2);
                    // Toast.makeText(MainActivity.this, ""+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();

                    /*
                    Uri pathUri = data.getData();
                    Bitmap myImg = BitmapFactory.decodeFile(pathUri.getPath());
                    mEditedImageView.setImageBitmap(myImg);
                    */
                   /*Bundle extras = data.getExtras();//thumbnail image
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mEditedImageView.setImageBitmap(imageBitmap);*/
                    break;
            }
        }
    }

    public void callGallery(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    public void callCameraPic(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "br.com.maracujasoftware.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 3);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic(Uri editedImageUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = editedImageUri;
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);

            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(currentCameraId); // attempt to get a Camera instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        if(currentCameraId==Camera.CameraInfo.CAMERA_FACING_BACK){
            mtx.setRotate(90);
        } else {
            mtx.setRotate(270);
        }

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: " /*+
                        e.getMessage()*/);
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                if(currentCameraId==Camera.CameraInfo.CAMERA_FACING_FRONT){
                    // Bitmap newImage = null;
                    // if (data != null) {
                    //cameraBitmap = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
                    // if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // use matrix to reverse image data and keep it normal
                    Matrix mtx = new Matrix();
                    //this will prevent mirror effect
                    mtx.preScale(-1.0f, 1.0f);
                    // Setting post rotate to 90 because image will be possibly in landscape
                    mtx.postRotate(180.f);
                    // Rotating Bitmap , create real image that we want
                    realImage = Bitmap.createBitmap(realImage, 0, 0, realImage.getWidth(), realImage.getHeight(), mtx, true);
                   /* }else{// LANDSCAPE MODE
                        //No need to reverse width and height
                        newImage = Bitmap.createScaledBitmap(realImage, screenWidth, screenHeight, true);
                        cameraBitmap = newImage;
                    }*/
                    // }
                }

                ExifInterface exif=new ExifInterface(pictureFile.toString());
                Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                    realImage= rotate(realImage, 90);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                    realImage= rotate(realImage, 270);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                    realImage= rotate(realImage, 180);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                    realImage= rotate(realImage, 90);
                }

                boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.d("Info", bo + "");

                fos.write(data);
                fos.close();
                mCurrentPhotoPath = "file:" + pictureFile.getAbsolutePath();
                Log.d("paths", "" + mCurrentPhotoPath);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

        }
    };

    public void takePic(View v) {
       // mCamera = getCameraInstance();
       // mCamera.open(currentCameraId);
//     mCamera.startPreview();

        mCamera.takePicture(null, null, mPicture);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                //buttons[inew][jnew].setBackgroundColor(Color.BLACK);
              //  Toast.makeText(MainActivity.this, "" + mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
                picUril = Uri.parse(mCurrentPhotoPath);
                Log.d("paths ", "" + picUril);
                Intent picIntent = new AdobeImageIntent.Builder(getApplicationContext())
                        .setData(picUril)
                        .build();
                startActivityForResult(picIntent, 2);
            }
        }, 2000);
    }


    private File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "OhayooBeaultifulSmiles");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //releaseMediaRecorder();       // if you are using MediaRecorder, release it first
      //  releaseCamera();              // release the camera immediately on pause event
    }

  /*  private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
        preview.removeView(mPreview);

    }*/

    /*public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }*/

   public void SwitchCam(View v) {
       // if (inPreview) {
            mCamera.stopPreview();
       // }
//NB: if you don't release the current camera before switching, you app will crash
        mCamera.release();

//swap the id of the camera to be used
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
       preview.removeView(mPreview);
        mCamera = getCameraInstance();
       // Create our Preview view and set it as the content of our activity.
       mPreview = null;
       mPreview = new CameraPreview(this, mCamera, main);

       preview.addView(mPreview);

        //setCameraDisplayOrientation(MainActivity., currentCameraId, camera);
        try {
            mCamera.setPreviewDisplay(mPreview.getMyHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
           setCameraDisplayOrientation(MainActivity.this, 0, mCamera);

       mCamera.startPreview();
    }

  /* public void SwitchCamChanged(View v) {
        mPreview.SwitchCam(currentCameraId);
        currentCameraId =  mPreview.getCamId();

    }*/
  public static void setCameraDisplayOrientation(Activity activity,
                                                 int cameraId, android.hardware.Camera camera) {
      android.hardware.Camera.CameraInfo info =
              new android.hardware.Camera.CameraInfo();
      android.hardware.Camera.getCameraInfo(cameraId, info);
      int rotation = activity.getWindowManager().getDefaultDisplay()
              .getRotation();
      int degrees = 0;
      switch (rotation) {
          case Surface.ROTATION_0: degrees = 0; break;
          case Surface.ROTATION_90: degrees = 90; break;
          case Surface.ROTATION_180: degrees = 180; break;
          case Surface.ROTATION_270: degrees = 270; break;
      }

      int result;
      if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
          result = (info.orientation + degrees) % 360;
          result = (360 - result) % 360;  // compensate the mirror
      } else {  // back-facing
          result = (info.orientation - degrees + 360) % 360;
      }
      camera.setDisplayOrientation(result);
  }

}
