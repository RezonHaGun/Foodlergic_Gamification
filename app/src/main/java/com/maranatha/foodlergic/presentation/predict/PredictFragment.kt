package com.maranatha.foodlergic.presentation.predict

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maranatha.foodlergic.databinding.FragmentPredictBinding
import com.maranatha.foodlergic.ml.BestMobilenetv2Model
import com.maranatha.foodlergic.presentation.viewmodel.AllergyViewModel
import com.maranatha.foodlergic.presentation.viewmodel.PredictViewModel
import com.maranatha.foodlergic.utils.ImagePreprocessor
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PredictFragment : Fragment() {
    private var _binding: FragmentPredictBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoUri: Uri
    private lateinit var bitmap: Bitmap
    private val viewModel: AllergyViewModel by viewModels()
    private val predictViewModel: PredictViewModel by viewModels()

    private val predictFragmentArgs: PredictFragmentArgs by navArgs()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] != true
                && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] != true
            ) {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.previewImage.setImageURI(photoUri)
                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoUri)

                context?.let {
                    classifyImage(it, bitmap)
                }
            }
        }

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.previewImage.setImageURI(it)
                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, it)
                photoUri = uri
                context?.let {
                    classifyImage(it, bitmap)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        viewModel.getUserAllergies()
        observeGetAllergiesFromAPI()
        observeUploadingStatus()

        binding.cameraButton.setOnClickListener {
            openCamera()
        }

        binding.galleryButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                requireContext(), "com.maranatha.foodlergic.fileprovider", photoFile
            )
            cameraResultLauncher.launch(photoUri)
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(context, "Failed to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        galleryResultLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context?.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        )
    }

    private fun observeGetAllergiesFromAPI() {
        viewModel.userAllergies.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {

                }

                is Resource.Error -> {
                    // Show error message
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeUploadingStatus() {
        predictViewModel.status.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    val prediction = result.data
                    if (prediction != null) {
                        val action = PredictFragmentDirections
                            .actionPredictFragmentToPredictResultFragment(
                                prediction.predictedAllergen,
                                prediction.hasAllergy,
                                photoUri.toString()
                            )
                        findNavController().navigate(action)
                        predictViewModel.clearStatus()
                    }
                }

                is Resource.Error -> {
                    // Show error message
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun classifyImage(context: Context, bitmap: Bitmap) {
        try {
            val labels = context.assets.open("label.txt").bufferedReader().readLines()
            val inputBuffer = ImagePreprocessor.preprocess(bitmap)

            val model = BestMobilenetv2Model.newInstance(context)
            val outputs = model.process(inputBuffer)
            val output = outputs.outputFeature0AsTensorBuffer.floatArray
            val maxIdx = output.indexOfFirst { it == output.maxOrNull() }
            val label = maxIdx.takeIf { it != -1 }?.let { labels[it] } ?: "Unknown"

            val isAllergic = viewModel.isAllergic(label)
            if (predictFragmentArgs.isAnonymous) {
                val action = PredictFragmentDirections
                    .actionPredictFragmentToPredictResultFragment(
                        label,
                        isAllergic,
                        photoUri.toString(),
                        true
                    )
                findNavController().navigate(action)
            } else {
                predictViewModel.predictAndSave(label, isAllergic)
            }
            model.close()
        } catch (e: Exception) {
            Log.d("PredictFragment", "Prediction failed: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
