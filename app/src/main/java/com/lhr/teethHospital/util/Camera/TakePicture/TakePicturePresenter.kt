/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.lhr.teethHospital.util.Camera.TakePicture

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.*
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.util.Camera.CompareSizesByArea
import java.util.*

class TakePicturePresenter(takePictureActivity: TakePictureActivity) {
    var takePicture2Activity = takePictureActivity
    var textTureCapture = takePictureActivity.textTureCapture

    private val FRAGMENT_DIALOG = "dialog"
    private val REQUEST_CAMERA_PERMISSION = 1
    private var backgroundHandler: Handler? = null

    private val MAX_PREVIEW_WIDTH = 1920
    private val MAX_PREVIEW_HEIGHT = 1080
    private var sensorOrientation = 0
    var mCameraId: String? = null

//    fun requestCameraPermission() {
//        if (PermissionUtils.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CAMERA)) {
//            ConfirmationDialog().show(takePictureFragment.childFragmentManager, FRAGMENT_DIALOG)
//        } else {
//            ActivityCompat.requestPermissions(
//                mainActivity,
//                arrayOf(Manifest.permission.CAMERA),
//                REQUEST_CAMERA_PERMISSION
//            )
//        }
//    }

    var mPreviewSize: Size? = null
    fun setUpCameraOutputs(width: Int, height: Int) {
        val manager = mainActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_FRONT
                ) {
                    continue
                }

                val map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                ) ?: continue

                // For still image captures, we use the largest available size.
                val largest = Collections.max(
                    listOf(*map.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByArea()
                )
                takePicture2Activity.imageReader = ImageReader.newInstance(
                    largest.width, largest.height,
                    ImageFormat.JPEG, /*maxImages*/ 2
                ).apply {
                    setOnImageAvailableListener(takePicture2Activity.onImageAvailableListener, backgroundHandler)
                }

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = mainActivity.windowManager.defaultDisplay.rotation

                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                var swappedDimensions = areDimensionsSwapped(displayRotation)
                val displaySize = Point()
                mainActivity.windowManager.defaultDisplay.getSize(displaySize)
                val rotatedPreviewWidth = if (swappedDimensions) height else width
                val rotatedPreviewHeight = if (swappedDimensions) width else height
                var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(
                    map.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight,
                    maxPreviewWidth, maxPreviewHeight,
                    largest
                )

                // We fit the aspect ratio of TextureView to the size of preview we picked.
//                if (mainActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////                    text.setAspectRatio(rotatedPreviewWidth, rotatedPreviewHeight)
//                    textTureCapture.rotationX = (rotatedPreviewWidth.toFloat())
//                    textTureCapture.rotationY = (rotatedPreviewHeight.toFloat())
//                } else {
////                    textureView.setAspectRatio(previewSize.height, previewSize.width)
//                    textTureCapture.rotationX = (rotatedPreviewHeight.toFloat())
//                    textTureCapture.rotationY = (rotatedPreviewWidth.toFloat())
//                }

                // Check if the flash is supported.
                takePicture2Activity.flashSupported =
                    characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                mCameraId = cameraId

                // We've found a viable camera and finished setting up member variables,
                // so we don't need to iterate through other available cameras.
                return
            }
        } catch (e: CameraAccessException) {
            Log.e(ContentValues.TAG, e.toString())
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
//            ErrorDialog.newInstance(getString(R.string.camera_error))
//                .show(childFragmentManager, FRAGMENT_DIALOG)
        }
    }

    fun chooseOptimalSize(
        choices: Array<Size>,
        textureViewWidth: Int,
        textureViewHeight: Int,
        maxWidth: Int,
        maxHeight: Int,
        aspectRatio: Size
    ): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough = ArrayList<Size>()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.width <= maxWidth && option.height <= maxHeight &&
                option.height == option.width * h / w) {
                if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CompareSizesByArea())
        } else if (notBigEnough.size > 0) {
            return Collections.max(notBigEnough, CompareSizesByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    private fun areDimensionsSwapped(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(ContentValues.TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }

    fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val rotation = mainActivity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, mPreviewSize!!.height.toFloat(), mPreviewSize!!.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale: Float = Math.max(
                viewHeight.toFloat() / mPreviewSize!!.height,
                viewWidth.toFloat() / mPreviewSize!!.width
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textTureCapture.setTransform(matrix)
    }


    fun toDetectResultActivity(){
        takePicture2Activity.finish()
//        val intent = Intent(mainActivity, DetectResultActivity::class.java)
//        intent.putExtra("originalImage", File(mainActivity.getExternalFilesDir(null), Model.PIC_FILE_NAME).toUri())
//        intent.putExtra("detectImage", File(mainActivity.getExternalFilesDir(null), Model.RESULT_FILE_NAME).toUri())
////        mainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        mainActivity.progressBar.visibility = View.INVISIBLE
//        mainActivity.startActivity(intent)
    }
}