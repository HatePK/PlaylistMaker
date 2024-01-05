package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistFormBinding
import com.practicum.playlistmaker.library.presentation.AddNewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddNewPlaylistFragment: Fragment() {

    private val addNewPlaylistViewModel by viewModel<AddNewPlaylistViewModel>()

    private lateinit var binding: FragmentNewPlaylistFormBinding
    private var textWatcher: TextWatcher? = null
    private var isImageSet = false
    private var coverUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.isEnabled = false

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.submitButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        textWatcher?.let { binding.fieldName.editText?.addTextChangedListener(it) }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.trackCover.setImageURI(uri)
                    isImageSet = true
                    coverUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.trackCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Нет") { dialog, which ->
            }.setPositiveButton("Завершить") { dialog, which ->
                findNavController().popBackStack()
            }

        binding.menuButton.setOnClickListener{
            if (binding.fieldName.editText!!.text.isNotEmpty() || binding.fieldDescription.editText!!.text.isNotEmpty() || isImageSet) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.fieldName.editText!!.text.isNotEmpty()
                || binding.fieldDescription.editText!!.text.isNotEmpty()
                || isImageSet
            ) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.submitButton.setOnClickListener{

            coverUri?.let {
                saveImageToPrivateStorage(it)
            }

            savePlaylist(
                binding.fieldName.editText!!.text.toString(),
                binding.fieldDescription.editText?.text.toString()
            )

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.fieldName.editText?.removeTextChangedListener(it) }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playListCovers")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val file = File(filePath, "playlistCover.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        coverUri = file.toUri()

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun savePlaylist(name: String, description: String) {
        addNewPlaylistViewModel.addPlaylist(
            name = name,
            description = description,
            coverUri = coverUri.toString()
        )

        Toast.makeText(requireContext(), "Плейлист $name создан", Toast.LENGTH_SHORT).show()
    }
}