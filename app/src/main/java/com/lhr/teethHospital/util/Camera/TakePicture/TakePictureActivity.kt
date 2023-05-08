package com.lhr.teethHospital.util.Camera.TakePicture
//https://github.com/q1113225201/Camera

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.camera2basic.ImageSaver
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.R
import java.io.File
import java.util.concurrent.Semaphore


class TakePictureActivity : AppCompatActivity(), TextureView.SurfaceTextureListener, View.OnClickListener {

    lateinit var imageCaptureButton: ImageButton
    lateinit var textTureCapture: TextureView
    lateinit var imageViewBlack: ImageView
    lateinit var imageViewLine: ImageView
    lateinit var imageSwitch: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var presenter: TakePicturePresenter
    var cameraDevice: CameraDevice? = null
    private val mCameraOpenCloseLock = Semaphore(1)
    private val mBackgroundHandler: Handler? = null
    private lateinit var previewBuilder: CaptureRequest.Builder
    private var captureBuilder: CaptureRequest.Builder? = null
    var imageReader: ImageReader? = null
    private lateinit var file: File
    private var cameraCaptureSession: CameraCaptureSession? = null
    var currentCameraId = CameraCharacteristics.LENS_FACING_FRONT
    private var previewSize: Size? = null
    private var cameraManager: CameraManager? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        handler?.post(
            ImageSaver(it.acquireNextImage(), file, this)
        )
    }
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            this@TakePictureActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            this@TakePictureActivity.cameraDevice = null
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            this@TakePictureActivity.cameraDevice = null
            val activity: Activity? = mainActivity
            activity?.finish()
        }
    }


    //创建session回调
    private val sessionStateCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                try {
                    cameraCaptureSession = session
                    //自动对焦
                    previewBuilder.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )
                    cameraCaptureSession!!.setRepeatingRequest(previewBuilder.build(), null, handler)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                session.close()
                cameraCaptureSession = null
                cameraDevice!!.close()
                cameraDevice = null
            }
        }

    //拍完照回调
    private val captureCallback: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            super.onCaptureProgressed(session, request, partialResult)
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            try {
                //自动对焦
                captureBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL)
                //重新打开预览
                session.setRepeatingRequest(previewBuilder.build(), null, handler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
            super.onCaptureFailed(session, request, failure)
            cameraCaptureSession!!.close()
            cameraCaptureSession = null
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    private fun initImageReader() {
        imageReader = ImageReader.newInstance(previewSize!!.width, previewSize!!.height, ImageFormat.JPEG, 1)
        //监听ImageReader时间，有图像数据可用时回调，参数就是帧数据
        imageReader!!.setOnImageAvailableListener({ reader ->
            val image: Image = reader.acquireLatestImage()
            presenter.imageSaver(image, file, this)
            image.close()
        }, handler)
    }
    private fun initData() {
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        //Camera2全程异步
        handlerThread = HandlerThread("Camera2")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.getLooper())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        supportActionBar!!.hide()

        imageCaptureButton = findViewById(R.id.imageCaptureButton)
        textTureCapture = findViewById(R.id.textTureCapture)
        imageViewBlack = findViewById(R.id.imageViewBlack)
        imageViewLine = findViewById(R.id.imageViewLine)
        imageSwitch = findViewById(R.id.imageSwitch)
        progressBar = findViewById(R.id.progressBar)

        file = File(getExternalFilesDir(null), Model.PIC_FILE_NAME)

        presenter = TakePicturePresenter(this)
        initData()

        imageCaptureButton.setOnClickListener(this)
        imageSwitch.setOnClickListener(this)
        textTureCapture.surfaceTextureListener = this
    }

    private fun openCamera() {
        try {
            //获取属性CameraDevice属性描述
            val cameraCharacteristics: CameraCharacteristics =
                cameraManager!!.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT.toString())
            //获取摄像头支持的配置属性
            val map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            previewSize = getMaxSize(map!!.getOutputSizes(SurfaceHolder::class.java))
            //第一个参数指定哪个摄像头，第二个参数打开摄像头的状态回调，第三个参数是运行在哪个线程(null是当前线程)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                !== PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            initImageReader()
            cameraManager!!.openCamera(currentCameraId.toString(), mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        openCamera()
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture) = true

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit

    private fun createCameraPreviewSession() {
        try {
            val texture = textTureCapture.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture!!.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            previewBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewBuilder.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDevice!!.createCaptureSession(listOf(surface, imageReader?.surface), sessionStateCallback, handler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun takePhoto() {
        try {
            captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder!!.addTarget(imageReader!!.surface)
            //自动对焦
            captureBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            cameraCaptureSession!!.stopRepeating()
            //拍照
            cameraCaptureSession!!.capture(captureBuilder!!.build(), captureCallback, handler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    var flashSupported = false
    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder) {
        if (flashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        }
    }

    private fun switchCamera() {
        try {
            for (cameraId in cameraManager!!.cameraIdList) {
                val characteristics: CameraCharacteristics = cameraManager!!.getCameraCharacteristics(cameraId)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val maxSize: Size = getMaxSize(map!!.getOutputSizes(SurfaceHolder::class.java))!!
                if ((currentCameraId == CameraCharacteristics.LENS_FACING_BACK) && (characteristics.get(
                        CameraCharacteristics.LENS_FACING
                    ) == CameraCharacteristics.LENS_FACING_FRONT)
                ) {
                    //前置转后置
                    previewSize = maxSize
                    currentCameraId = CameraCharacteristics.LENS_FACING_FRONT
                    cameraDevice!!.close()
                    openCamera()
                    break
                } else if ((currentCameraId == CameraCharacteristics.LENS_FACING_FRONT) && characteristics.get(
                        CameraCharacteristics.LENS_FACING
                    ) == CameraCharacteristics.LENS_FACING_BACK
                ) {
                    //后置转前置
                    previewSize = maxSize
                    currentCameraId = CameraCharacteristics.LENS_FACING_BACK
                    cameraDevice!!.close()
                    openCamera()
                    break
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun getMaxSize(outputSizes: Array<Size>?): Size? {
        var sizeMax: Size? = null
        if (outputSizes != null) {
            sizeMax = outputSizes[0]
            for (size in outputSizes) {
                if (size.width * size.height > sizeMax!!.width * sizeMax.height) {
                    sizeMax = size
                }
            }
        }
        return sizeMax
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageCaptureButton -> {
                progressBar.visibility = View.VISIBLE
//                mainActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                lockFocus()
                takePhoto()
            }
            R.id.imageSwitch -> {
                switchCamera()
            }
        }
    }
}