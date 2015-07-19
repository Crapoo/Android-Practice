package me.taroli.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matt on 19/07/15.
 */
@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "me.taroli.criminalIntent.photo_filename";

    /* TODO Camera deprecated */
    private Camera camera;
    private SurfaceView surfaceView;
    private View progressContainer;

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            progressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filename = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream os = null;
            boolean success = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch(Exception e ){
                Log.e(TAG, "Error saving picture " + filename, e);
                success = false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (Exception e){
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
            }

            if (success) {
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        Button takePicBtn = (Button) v.findViewById(R.id.crime_camera_takePictureBtn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(shutterCallback, null, jpegCallback);
            }
        });

        surfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (camera == null) {
                    return;
                }

                Camera.Parameters parameters = camera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width, s.height);
                camera.setParameters(parameters);
                try {
                    camera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    camera.release();
                    camera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.stopPreview();
                }
            }
        });

        progressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);

        return v;
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(0);
        } else {
            camera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size size : sizes) {
            int area = size.width * size.height;
            if (area > largestArea) {
                bestSize = size;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
