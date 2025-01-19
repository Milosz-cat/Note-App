package com.example.thenotesapp.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.adapter.ImageAdapter
import com.example.thenotesapp.databinding.FragmentAddNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.utils.BiometricManager
import com.example.thenotesapp.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding: FragmentAddNoteBinding? = null
    private val binding get() = addNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView: View
    private lateinit var biometricManager: BiometricManager
    private lateinit var imageAdapter: ImageAdapter
    private var imageUriList: MutableList<Uri> = mutableListOf()

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        addNoteView = view

        biometricManager = BiometricManager(requireContext(), this)
        setupRecyclerView()

        binding.switchSecureNote.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                biometricManager.authenticate {
                }
            }
        }

        binding.addImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.addDrawingButton.setOnClickListener {
            captureImage()
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(imageUriList)
        binding.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = imageAdapter
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        imageUriList.add(it)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val uri = getImageUriFromBitmap(bitmap)
                    uri?.let {
                        imageUriList.add(it)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "NoteImage_${System.currentTimeMillis()}",
            null
        )
        return Uri.parse(path)
    }

    private fun saveNote() {
        val noteTitle = binding.addNoteTitle.text.toString().trim()
        val noteDesc = binding.addNoteDesc.text.toString().trim()
        val isSecured = binding.switchSecureNote.isChecked
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val imageUris = imageUriList.map { it.toString() }

        if (noteTitle.isNotEmpty()) {
            val note = Note(
                id = 0,
                noteTitle = noteTitle,
                noteDesc = noteDesc,
                isSecured = isSecured,
                isFavorite = false,
                date = date,
                imageUris = imageUris
            )
            notesViewModel.addNote(note)

            Toast.makeText(requireContext(), "Note Saved", Toast.LENGTH_SHORT).show()
            view?.findNavController()?.popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(requireContext(), "Please enter note title", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveNote()
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }
}
