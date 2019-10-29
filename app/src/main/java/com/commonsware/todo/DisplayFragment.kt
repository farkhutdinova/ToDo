package com.commonsware.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.commonsware.todo.databinding.TodoDisplayBinding

class DisplayFragment: Fragment() {

    private val args: DisplayFragmentArgs by navArgs()

    private lateinit var binding: TodoDisplayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TodoDisplayBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = ToDoRepository.find(args.modelId)
    }
}