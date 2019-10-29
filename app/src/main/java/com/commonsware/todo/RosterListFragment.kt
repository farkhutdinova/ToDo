package com.commonsware.todo

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.todo_roster.*
import kotlinx.android.synthetic.main.todo_roster.view.*

class RosterListFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RosterAdapter(
            inflater = layoutInflater,
            onCheckboxToggle = { model ->
                ToDoRepository.save(model.copy(isCompleted = !model.isCompleted))
            },
            onRowClick = { model -> display(model) })
        view.items.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        adapter.submitList(ToDoRepository.items)
        empty.visibility = if (ToDoRepository.items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.todo_roster, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.actions_roster, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add -> {
                add(); return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun display(model: ToDoModel) {
        findNavController().navigate(RosterListFragmentDirections.displayModel(model.id))
    }

    private fun add() {
        findNavController().navigate(RosterListFragmentDirections.createModel())
    }
}