package com.lhr.teethHospital.ui.camera.takePicture
//https://github.com/q1113225201/Camera

import android.Manifest
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
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.ActivityTakePictureBinding
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.ui.base.BaseViewModel
import java.io.File
import java.util.concurrent.Semaphore


class TakePictureActivity : BaseActivity(), TextureView.SurfaceTextureListener, View.OnClickListener {

    override val viewModel: TakePictureViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }
    lateinit var binding: ActivityTakePictureBinding
    lateinit var takePictureActivity: TakePictureActivity
    var cameraDevice: CameraDevice? = null
    private val mCameraOpenCloseLock = Semaphore(1)
    private val mBackgroundHandler: Handler? = null
    private lateinit var previewBuilder: CaptureRequest.Builder
    private var captureBuilder: CaptureRequest.Builder? = null
    private var imageReader: ImageReader? = null
    private lateinit var file: File
    private var cameraCaptureSession: CameraCaptureSession? = null
    var currentCameraId = CameraCharacteristics.LENS_FACING_FRONT
    private var previewSize: Size? = null
    private var cameraManager: CameraManager? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

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
            finish()
        }
    }


    //創建session回调
    private val sessionStateCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                try {
                    cameraCaptureSession = session
                    //自動對焦
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

    //拍完照回傳
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
                //自動對焦
                captureBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL)
                //重新打開預覽
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
        //監聽ImageReader
        imageReader!!.setOnImageAvailableListener({ reader ->
            val image: Image = reader.acquireLatestImage()
            viewModel.imageSaver(image, file, this)
            image.close()
        }, handler)
    }
    private fun initData() {
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        //Camera2全程異步
        handlerThread = HandlerThread("Camera2")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_take_picture)
        takePictureActivity = this
        file = File(getExternalFilesDir(null), Model.RESULT_FILE_NAME)

        initData()

        binding.imageCaptureButton.setOnClickListener(this)
        binding.imageSwitch.setOnClickListener(this)
        binding.textTureCapture.surfaceTextureListener = this
    }

    private fun openCamera() {
        try {
            //獲取CameraDevice屬性
            val cameraCharacteristics: CameraCharacteristics =
                cameraManager!!.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT.toString())
            //獲取相機的配置
            val map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            previewSize = getMaxSize(map!!.getOutputSizes(SurfaceHolder::class.java))
            //權限判斷
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                !== PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            initImageReader()
            cameraManager!!.openCamera(currentCameraId.toString(), mStateCallback, mBackgroundHandler)
            // 切換鏡頭按鈕恢復可點擊狀態
            binding.imageSwitch.isEnabled = true
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
            val texture = binding.textTureCapture.surfaceTexture

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
            //自動對焦
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
                    //前鏡頭轉後鏡頭
                    previewSize = maxSize
                    currentCameraId = CameraCharacteristics.LENS_FACING_FRONT
                    cameraDevice!!.close()
                    openCamera()
                    break
                } else if ((currentCameraId == CameraCharacteristics.LENS_FACING_FRONT) && characteristics.get(
                        CameraCharacteristics.LENS_FACING
                    ) == CameraCharacteristics.LENS_FACING_BACK
                ) {
                    //後鏡頭轉前鏡頭
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
                binding.progressBar.visibility = View.VISIBLE
                takePhoto()
            }
            R.id.imageSwitch -> {
                // 切換鏡頭按鈕不可點擊狀態
                binding.imageSwitch.isEnabled = false
                switchCamera()
            }
        }
    }
}