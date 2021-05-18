package net.syntessense.app.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.util.SparseBooleanArray
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.syntessense.app.myapplication.databinding.ActivityMainBinding
import net.syntessense.app.myapplication.model.TodoDB
import net.syntessense.app.myapplication.model.TodoEntity

class MainActivity : AppCompatActivity() {

    // automatically generated class as the name of layout class
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // init binding
        setContentView(binding.root)

        val TODOS = Room.databaseBuilder(
            applicationContext,
            TodoDB::class.java, "todo-list.db"
        ).build().todoDao()

        var todoList = arrayListOf<String>()
        var todoIdList = arrayListOf<Int>()
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, todoList)
        binding.listView.adapter = adapter

        fun refresh() {
            val todos = TODOS.getAll()
            todoList.clear()
            todoIdList.clear()
            for (todo in todos) {
                todoList.add(todo.content)
                todoIdList.add(todo.id)
            }
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                adapter.notifyDataSetChanged()
            })
        }

        GlobalScope.launch {
            refresh()
        }

        binding.add.setOnClickListener {
            val txt = binding.editText.text.toString();
            binding.editText.text.clear()
            GlobalScope.launch{
                TODOS.add(TodoEntity(0, txt))
                refresh()
            }
        }

        binding.clear.setOnClickListener {
            GlobalScope.launch {
                TODOS.clear()
                refresh()
            }
        }

        binding.delete.setOnClickListener {
            GlobalScope.launch {
                val position: SparseBooleanArray = binding.listView.checkedItemPositions
                val count = binding.listView.count
                var i = count - 1
                while (i >= 0) {
                    if (position.get(i)) {
                        TODOS.remove(todoIdList[i])
                    }
                    i--
                }
                position.clear()
                refresh()
            }
        }

        binding.listView.setOnItemClickListener { adapterView, view, i, l ->
            android.widget.Toast.makeText(
                    this,
                    "You Selected the item --> " + todoIdList[i].toString(), android.widget.Toast.LENGTH_SHORT
            ).show()
        }

    }
}