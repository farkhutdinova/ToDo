package com.commonsware.todo

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.commonsware.todo.databinding.TodoEditBinding
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import org.koin.android.ext.android.inject

class EditFragment : Fragment() {

    private val repo: ToDoRepository by inject()
    private lateinit var binding: TodoEditBinding
    private val args: EditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TodoEditBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = repo.find(args.modelId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.actions_edit, menu)
        menu?.findItem(R.id.delete)?.isVisible = args.modelId != null

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.save -> {
                save(); return true
            }
            R.id.delete -> {
                delete(); return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        val edited = if (binding.model == null) {
            ToDoModel(
                description = binding.desc.text.toString(),
                isCompleted = binding.isCompleted.isChecked,
                notes = binding.notes.text.toString()
            )
        } else {
            binding.model?.copy(
                description = binding.desc.text.toString(),
                isCompleted = binding.isCompleted.isChecked,
                notes = binding.notes.text.toString()
            )
        }

        edited?.let { repo.save(it) }
        navToDisplay()
    }

    private fun delete() {
        binding.model?.let { repo.delete(it.id) }
        navToList()
    }

    private fun navToDisplay() {
        hideKeyboard()
        findNavController().popBackStack()
    }

    private fun navToList() {
        hideKeyboard()
        findNavController().popBackStack(R.id.rosterListFragment, false)
    }

    // from https://stackoverflow.com/questions/21573586/hidesoftinputfromwindow-not-working/21574135#21574135
    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}