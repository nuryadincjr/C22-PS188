package com.bangkit.capstone.lukaku.ui.detection

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.ActivityDetectionCameraBinding
import com.bangkit.capstone.lukaku.util.createFile
import java.text.DecimalFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DetectionCameraActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetectionCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private var cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var flashFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection_camera)

        binding = ActivityDetectionCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.apply {
            ivCaptureImage.setOnClickListener(this@DetectionCameraActivity)
            ivSwitchCamera.setOnClickListener(this@DetectionCameraActivity)
            ivClose.setOnClickListener(this@DetectionCameraActivity)
            ivFlash.setOnClickListener(this@DetectionCameraActivity)
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
        zoomCamera()
    }

    private fun zoomCamera() {
        binding.seekBarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cameraControl.setLinearZoom(progress / 100.toFloat())
                zoomControl()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_capture_image -> takePhoto()
            R.id.iv_switch_camera -> swichCamera()
            R.id.iv_close -> onBackPressed()
            R.id.iv_flash -> setFlash()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.error_camera_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_OFF).build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                cameraControl = camera.cameraControl
                cameraInfo = camera.cameraInfo

                flashControl()

                val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        val ratio = (cameraInfo.zoomState.value?.zoomRatio)
                        val scale = detector.scaleFactor
                        cameraControl.setZoomRatio(scale * ratio!!)
                        zoomControl()
                        return true
                    }
                }

                val scaleGestureDetector = ScaleGestureDetector(this, listener)

                binding.viewFinder.setOnTouchListener { _, p1 ->
                    when (p1.action) {
                        MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                        MotionEvent.ACTION_UP -> {
                            val factory = binding.viewFinder.meteringPointFactory
                            val point = factory.createPoint(p1.x, p1.y)
                            val action = FocusMeteringAction.Builder(point).build()
                            cameraControl.startFocusAndMetering(action)

                            return@setOnTouchListener true
                        }
                        else -> {
                            scaleGestureDetector.onTouchEvent(p1)

                            return@setOnTouchListener false
                        }
                    }
                }

            } catch (exc: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.error_camera_start),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun zoomControl() {
        val ratio = cameraInfo.zoomState.value?.zoomRatio
        val liner = (cameraInfo.zoomState.value?.linearZoom)
        val dFormat = DecimalFormat("#.##")

        val zoomStatus = "Zoom: ${dFormat.format(ratio)}x"
        val visibility = if (ratio!! >= 1.01) View.VISIBLE else View.INVISIBLE

        binding.apply {
            seekBarZoom.progress = (liner!! * 100).toInt()
            tvZoom.text = zoomStatus
            tvZoom.visibility = visibility
        }
    }

    private fun flashControl() {
        val isVisible = if (cameraInfo.hasFlashUnit()) View.VISIBLE else View.INVISIBLE
        val iconFlash = if (flashFlag) R.drawable.ic_flash_on else R.drawable.ic_flash_off

        cameraControl.enableTorch(flashFlag)
        binding.ivFlash.apply {
            visibility = isVisible
            setImageResource(iconFlash)
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@DetectionCameraActivity,
                        getString(R.string.error_camera_take),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val result = BitmapFactory.decodeFile(photoFile.path)

                    binding.ivOpenGallery.setImageBitmap(result)
                }
            }
        )
    }

    private fun setFlash() {
        flashFlag = !flashFlag
        flashControl()
    }

    private fun swichCamera() {
        cameraSelector = if (cameraSelector == DEFAULT_BACK_CAMERA) DEFAULT_FRONT_CAMERA
        else DEFAULT_BACK_CAMERA
        startCamera()
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}