package com.bangkit.capstone.lukaku.ui.capture

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.FragmentCaptureBinding
import com.bangkit.capstone.lukaku.ui.capture.CaptureFragmentDirections.actionCaptureFragmentToViewerFragment
import com.bangkit.capstone.lukaku.utils.Constants.IMAGE_TYPE
import com.bangkit.capstone.lukaku.utils.createFile
import com.bangkit.capstone.lukaku.utils.toast
import com.bangkit.capstone.lukaku.utils.uriToFile
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CaptureFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private var cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var flashFlag: Boolean = false

    private val launcherGallery = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri: Uri = it.data?.data as Uri
            val imageFile = uriToFile(imageUri, requireContext())

            onNavigate(imageFile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.apply {
            ivSelectedImage.setOnClickListener(this@CaptureFragment)
            ivSwitchCamera.setOnClickListener(this@CaptureFragment)
            ivClose.setOnClickListener(this@CaptureFragment)
            ivFlash.setOnClickListener(this@CaptureFragment)
            ivOpenGallery.setOnClickListener(this@CaptureFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        setAnimation()
        startCamera()
        zoomCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.iv_selected_image -> takePhoto()
            R.id.iv_switch_camera -> swichCamera()
            R.id.iv_close -> requireActivity().onBackPressed()
            R.id.iv_flash -> setFlash()
            R.id.iv_open_gallery -> startGallery()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                requireActivity().apply {
                    toast(getString(R.string.error_camera_permission))
                    finish()
                }
            }
        }
    }

    private fun setAnimation() {
        requireActivity().window.attributes.rotationAnimation = ROTATION_ANIMATION_CROSSFADE
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = IMAGE_TYPE
        val chooser = Intent.createChooser(intent, getString(R.string.title_gallery))
        launcherGallery.launch(chooser)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            binding.apply {
                val displayMetrics =
                    DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
                val aspectRatio =
                    aspectRatio(displayMetrics.widthPixels, displayMetrics.heightPixels)
                val rotation = viewFinder.display.rotation

                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .setTargetAspectRatio(aspectRatio)
                    .setTargetRotation(rotation)
                    .build()
                    .also { it.setSurfaceProvider(viewFinder.surfaceProvider) }

                imageCapture = Builder()
                    .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetAspectRatio(aspectRatio)
                    .setTargetRotation(rotation)
                    .build()

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        this@CaptureFragment,
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

                    val scaleGestureDetector = ScaleGestureDetector(context, listener)

                    viewFinder.setOnTouchListener { _, p1 ->
                        when (p1.action) {
                            MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                            MotionEvent.ACTION_UP -> {
                                val factory = viewFinder.meteringPointFactory
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
                    requireActivity().toast(getString(R.string.error_camera_start))
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()))
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

    private fun zoomControl() {
        val ratio = cameraInfo.zoomState.value?.zoomRatio
        val liner = (cameraInfo.zoomState.value?.linearZoom)
        val dFormat = DecimalFormat("#.##")

        val zoomStatus = String.format(getString(R.string.zoom_status), dFormat.format(ratio))
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
        val imageFile = createFile(requireActivity().application)
        val outputOptions = OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    requireActivity().toast(getString(R.string.error_camera_take))
                }

                override fun onImageSaved(output: OutputFileResults) = onNavigate(imageFile)
            }
        )
    }

    private fun onNavigate(imageFile: File) {
        val directions = actionCaptureFragmentToViewerFragment(imageFile)
        findNavController().navigate(directions)
    }

    private fun setFlash() {
        flashFlag = !flashFlag
        flashControl()
    }

    private fun swichCamera() {
        cameraSelector =
            if (cameraSelector == DEFAULT_BACK_CAMERA) DEFAULT_FRONT_CAMERA
            else DEFAULT_BACK_CAMERA

        startCamera()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}