package com.commonsware.todo.ui.roster

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.commonsware.todo.R
import com.commonsware.todo.repo.FilterMode
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.ui.util.EventObserver
import kotlinx.android.synthetic.main.todo_roster.*
import kotlinx.android.synthetic.main.todo_roster.view.*
import org.koin.android.ext.android.inject

private const val REQUEST_SAVE = 1337

class RosterListFragment : Fragment() {

    private val motor: RosterMotor by inject()
    private val menuMap = mutableMapOf<FilterMode, MenuItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RosterAdapter(
            inflater = layoutInflater,
            onCheckboxToggle = { model ->
                motor.save(model.copy(isCompleted = !model.isCompleted))
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
        motor.states.observe(this, Observer { state ->
            adapter.submitList(state.items)
            when {
                state.items.isEmpty() && state.filterMode == FilterMode.ALL -> {
                    empty.visibility = View.VISIBLE
                    empty.setText(R.string.msg_empty)
                }
                state.items.isEmpty() -> {
                    empty.visibility = View.VISIBLE
                    empty.setText(R.string.msg_empty_filtered)
                }
                else -> empty.visibility = View.GONE
            }
            loading.visibility = View.GONE
            menuMap[state.filterMode]?.isChecked = true
        })

        motor.navEvents.observe(this, EventObserver { nav ->
            when(nav) {
                is Nav.ViewReport -> viewReport(nav.doc)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SAVE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { motor.saveReport(it) }
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.actions_roster, menu)

        menuMap.apply {
            put(FilterMode.ALL, menu.findItem(R.id.all))
            put(FilterMode.COMPLETED, menu.findItem(R.id.completed))
            put(FilterMode.OUTSTANDING, menu.findItem(R.id.outstanding))
        }

        motor.states.value?.let { menuMap[it.filterMode]?.isChecked = true }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                add()
                return true
            }
            R.id.all -> {
                item.isChecked = true
                motor.load(FilterMode.ALL)
                return true
            }
            R.id.completed -> {
                item.isChecked = true
                motor.load(FilterMode.COMPLETED)
                return true
            }
            R.id.outstanding -> {
                item.isChecked = true
                motor.load(FilterMode.OUTSTANDING)
                return true
            }
            R.id.save -> {
                saveReport()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun display(model: ToDoModel) {
        findNavController().navigate(
            RosterListFragmentDirections.displayModel(
                model.id
            )
        )
    }

    private fun add() {
        findNavController().navigate(RosterListFragmentDirections.createModel())
    }

    private fun saveReport() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("text/html")
        startActivityForResult(intent, REQUEST_SAVE)
    }

    private fun viewReport(uri: Uri) {
        val i = Intent(Intent.ACTION_VIEW, uri).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.msg_saved, Toast.LENGTH_LONG).show()
        }
    }
}