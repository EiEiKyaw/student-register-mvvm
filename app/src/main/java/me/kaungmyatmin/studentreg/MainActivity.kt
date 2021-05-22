package me.kaungmyatmin.studentreg

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StudentItemListener {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private lateinit var studentDao: StudentDao
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var mainViewModel: MainViewModel

    private val CREATE_REQUEST_CODE = 13 * 10
    private val UPDATE_REQUEST_CODE = 13 * 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studentDao = AppDatabase.getDatabase(applicationContext).getStudentDao()
        studentAdapter = StudentAdapter(this)
        mainViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(MainViewModel::class.java)

        rvStudent.adapter = studentAdapter
        rvStudent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainViewModel.findAll()

        mainViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading)
                showLoading()
        })

        mainViewModel.isEmpty.observe(this, Observer { isEmpty ->
            if (isEmpty)
                notifyUser("Data is empty.")
        })

        mainViewModel.data.observe(this, Observer { data ->
            studentAdapter.setNewData(data)
        })

        btnAdd.setOnClickListener {
            val intent = FormActivity.newIntent(this)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getLongExtra(Constants.KEY_ID, -1)
            val student = studentDao.findById(id)
            studentAdapter.appendData(student)
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getLongExtra(Constants.KEY_ID, -1)
            val student = studentDao.findById(id)
            studentAdapter.updateData(student)
        }
    }

    override fun onDeleteClicked(student: Student) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure to delete")
            .setPositiveButton("Ok") { dialog, which ->
                studentDao.delete(student)
                studentAdapter.deleteData(student)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
        dialog.show()

    }

    override fun onEditClicked(student: Student) {
        val intent = FormActivity.newIntent(this, student)
        startActivityForResult(intent, UPDATE_REQUEST_CODE)
    }

    private fun notifyUser(message: String) {
        Snackbar.make(rvStudent, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        Snackbar.make(rvStudent, "Loading..........", Snackbar.LENGTH_SHORT).show()
    }


}