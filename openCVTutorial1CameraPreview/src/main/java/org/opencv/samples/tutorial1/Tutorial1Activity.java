package org.opencv.samples.tutorial1;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG2;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.video.Video.createBackgroundSubtractorMOG2;

public class Tutorial1Activity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;




    private BackgroundSubtractor sub;
   // private Mat mGray= new Mat();
     private Mat mRgb;
    private Mat mFGMask;
    private Mat mRgba;
    private  Mat[] sub1;
     private Mat[] history;
    private  Mat histogrammask;
    private  Mat noisefreemask;
    private int count=0;
    private Mat finalResult;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Tutorial1Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial1_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(320,400);
      //  sub= createBackgroundSubtractorMOG2(3, 4, false);
      //  mFGMask = new Mat();
      //  mRgb = new Mat();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {


        sub= createBackgroundSubtractorMOG2(300,0,false);
        mFGMask = new Mat(300,600,CvType.CV_8UC4);
        mRgb = new Mat(300,600,CvType.CV_8UC4);
        mRgba = new Mat(300,600,CvType.CV_8UC4);
        history= new Mat[1000];
        noisefreemask = new Mat(300,600,CvType.CV_8UC4);
        histogrammask = new Mat(300,600,CvType.CV_8UC4);
        sub1 = new Mat[10];
        finalResult = new Mat(300,600,CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
//        mFGMask.release();
  //      mRgb.release();
          mFGMask.release();
          mRgb.release();
          mRgba.release();
        noisefreemask.release();
        histogrammask.release();
         // history[1].release();
        finalResult.release();
    }


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();

        //Size size = mRgba.size();
        history[count] = new Mat(300,600,CvType.CV_8UC4 );
        //history[count] = new Mat(size, CvType.CV_8UC1);
     //  buf[i] = Mat.zeros(size, CvType.CV_8UC1);
        Imgproc.cvtColor(mRgba, mRgb, Imgproc.COLOR_RGBA2RGB);

        mRgba.copyTo(history[count]);

            if(count>10) {


                sub.apply(mRgb, mFGMask);


                Imgproc.equalizeHist(mFGMask, histogrammask);
                Imgproc.medianBlur(histogrammask, noisefreemask, 5);

                history[count-10].copyTo(mRgba, noisefreemask);

                return mRgba;
            }
        count ++;

        return null;





    }
    void processFrame(Mat frame){

    }
}
