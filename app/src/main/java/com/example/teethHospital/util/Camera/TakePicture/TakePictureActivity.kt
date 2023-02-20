package com.example.teethHospital.util.Camera.TakePicture

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
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
import com.example.teethHospital.Model.Model
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.R
import java.io.File
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class TakePictureActivity : AppCompatActivity(), TextureView.SurfaceTextureListener, View.OnClickListener {

    lateinit var imageCaptureButton: ImageButton
    lateinit var textTureCapture: TextureView
    lateinit var imageViewBlack: ImageView
    lateinit var imageViewLine: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var presenter: TakePicturePresenter
    lateinit var mCameraDevice: CameraDevice
    private val mCameraOpenCloseLock = Semaphore(1)
    private val mBackgroundHandler: Handler? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    var imageReader: ImageReader? = null
    private lateinit var file: File
    private var backgroundHandler: Handler? = null
    private var captureSession: CameraCaptureSession? = null

    private val STATE_PREVIEW = 0

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private val STATE_WAITING_LOCK = 1

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private val STATE_WAITING_PRECAPTURE = 2

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private val STATE_WAITING_NON_PRECAPTURE = 3

    /**
     * Camera state: Picture was taken.
     */
    private val STATE_PICTURE_TAKEN = 4
    private var state = STATE_PREVIEW

    val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        backgroundHandler?.post(ImageSaver(it.acquireNextImage(), file, this))
    }
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
//            mCameraDevice = null
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
//            mCameraDevice = null
            val activity: Activity? = mainActivity
            activity?.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        supportActionBar!!.hide()

        imageCaptureButton = findViewById(R.id.imageCaptureButton)
        textTureCapture = findViewById(R.id.textTureCapture)
        textTureCapture = findViewById(R.id.textTureCapture)
        imageViewBlack = findViewById(R.id.imageViewBlack)
        imageViewLine = findViewById(R.id.imageViewLine)
        progressBar = findViewById(R.id.progressBar)

        file = File(getExternalFilesDir(null), Model.PIC_FILE_NAME)

        presenter = TakePicturePresenter(this)

        imageCaptureButton.setOnClickListener(this)
        textTureCapture.surfaceTextureListener = this
    }
    private fun openCamera(width: Int, height: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            !== PackageManager.PERMISSION_GRANTED
        ) {
//            requestCameraPermission()
            return
        }
        presenter.setUpCameraOutputs(width, height)
        presenter.configureTransform(width, height)
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(presenter.mCameraId!!, mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        openCamera(width, height)
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
            texture!!.setDefaultBufferSize(presenter.mPreviewSize!!.width, presenter.mPreviewSize!!.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = mCameraDevice!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilder.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(
                listOf(surface, imageReader?.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (mCameraDevice == null) return

                        // When the session is ready, we start displaying the preview.
                        captureSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                            // Finally, we start displaying the camera preview.
                            previewRequest = previewRequestBuilder.build()
                            captureSession?.setRepeatingRequest(previewRequest,
                                captureCallback, backgroundHandler)
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, e.toString())
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                    }
                }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        private fun process(result: CaptureResult) {
            when (state) {
                STATE_PREVIEW -> Unit // Do nothing when the camera preview is working normally.
                STATE_WAITING_LOCK -> {capturePicture(result)}
                STATE_WAITING_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        state = STATE_WAITING_NON_PRECAPTURE
                    }
                }
                STATE_WAITING_NON_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        state = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                }
            }
        }

        private fun capturePicture(result: CaptureResult) {
            val afState = result.get(CaptureResult.CONTROL_AF_STATE)
            if (afState == null) {
                captureStillPicture()
            } else if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                // CONTROL_AE_STATE can be null on some devices
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                    state = STATE_PICTURE_TAKEN
                    captureStillPicture()
                } else {
                    runPrecaptureSequence()
                }
            }
        }

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            process(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            process(result)
        }
    }


    private var backgroundThread: HandlerThread? = null
    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper!!)
    }
    override fun onResume() {
        super.onResume()
        startBackgroundThread()

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
//        if (textureView.isAvailable) {
//            openCamera(textureView.width, textureView.height)
//        } else {
//            textureView.surfaceTextureListener = surfaceTextureListener
//        }
    }

    var flashSupported = false
    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder) {
        if (flashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        }
    }

    private fun captureStillPicture() {
        try {
            if (this == null || mCameraDevice == null) return
            val rotation = windowManager.defaultDisplay.rotation

            val captureBuilder = mCameraDevice?.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
                addTarget(imageReader?.surface!!)

                // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
                // We have to take that into account and rotate JPEG properly.
                // For devices with orientation of 90, we return our mapping from ORIENTATIONS.
                // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
//                set(CaptureRequest.JPEG_ORIENTATION,
//                    (ORIENTATIONS.get(rotation) + sensorOrientation + 180) % 360)

                // Use the same AE and AF modes as the preview.
                set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            }?.also { setAutoFlash(it) }

            val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession,
                                                request: CaptureRequest,
                                                result: TotalCaptureResult) {
                    Log.d(TAG, file.toString())
//                    unlockFocus()
                }
            }

            captureSession?.apply {
                stopRepeating()
//                abortCaptures()
                capture(captureBuilder?.build(), captureCallback, null)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    fun unlockFocus() {
        try {
            // Reset the auto-focus trigger
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
//            setAutoFlash(previewRequestBuilder)
            captureSession?.capture(previewRequestBuilder.build(), captureCallback,
                backgroundHandler)
            // After this, the camera will go back to the normal state of preview.
            state = STATE_PREVIEW
            captureSession?.setRepeatingRequest(previewRequest, captureCallback,
                backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            // Tell #captureCallback to wait for the precapture sequence to be set.
            state = STATE_WAITING_PRECAPTURE
            captureSession?.capture(previewRequestBuilder.build(), captureCallback,
                backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START)
            // Tell #captureCallback to wait for the lock.
            state = STATE_WAITING_LOCK
            captureSession?.capture(previewRequestBuilder.build(), captureCallback,
                backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageCaptureButton -> {
                progressBar.visibility = View.VISIBLE
//                mainActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                lockFocus()
            }
        }
    }
}