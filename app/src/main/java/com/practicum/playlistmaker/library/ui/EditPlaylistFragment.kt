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
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentEditPlaylistFormBinding
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistFormBinding
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.library.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream

class EditPlaylistFragment: Fragment() {

    private lateinit var binding: FragmentEditPlaylistFormBinding
    private lateinit var oldCoverUri: Uri
    private lateinit var newCoverUri: Uri

    private var textWatcher: TextWatcher? = null
    private var isImageSet = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPlaylistFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = requireArguments().getSerializable(ARGS_PLAYLIST) as Playlist
        val viewModel by viewModel<EditPlaylistViewModel> { parametersOf(playlist) }

        oldCoverUri = playlist.playlistCoverUri.toUri()
        newCoverUri = oldCoverUri

        binding.submitButton.isEnabled = true

        binding.fieldName.editText?.setText(playlist.playlistName)

        if (playlist.playlistDescription.isNotEmpty()) {
            binding.fieldDescription.editText?.setText(playlist.playlistDescription)
        }

        Glide.with(requireActivity())
            .load(playlist.playlistCoverUri)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop()
            )
            .into(binding.playlistCover)

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
                    binding.playlistCover.setImageURI(uri)
                    isImageSet = true
                    newCoverUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.menuButton.setOnClickListener{
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener{

            if (newCoverUri != oldCoverUri) {
                newCoverUri?.let {
                    saveImageToPrivateStorage(it)
                }
            }

            viewModel.editPlaylist(
                name = binding.fieldName.editText!!.text.toString(),
                description = binding.fieldDescription.editText?.text.toString(),
                coverUri = newCoverUri.toString()
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

        newCoverUri = file.toUri()

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    companion object {
        private const val ARGS_PLAYLIST = "playlist"

        fun createArgs(playlist: Playlist): Bundle = bundleOf(ARGS_PLAYLIST to playlist)
    }
}