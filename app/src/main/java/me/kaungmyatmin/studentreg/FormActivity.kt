package me.kaungmyatmin.studentreg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_form.*

class FormActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, student: Student? = null): Intent {
            val intent = Intent(context, FormActivity::class.java)
            intent.putExtra("student", student)
            return intent
        }
    }

    private fun getStudent(): Student? {
        return intent.getParcelableExtra("student")
    }

    private lateinit var studentDao: StudentDao
    private lateinit var formViewModel: FormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        studentDao = AppDatabase.getDatabase(applicationContext).getStudentDao()
        formViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(FormViewModel::class.java)

        val student = getStudent()
        populateUi(student)

        btnOk.setOnClickListener {
            if (student != null) {
                updateStudent(student)
            } else {
                createStudent()
            }
            startActivity(MainActivity.newIntent(this))
        }
        btnCancel.setOnClickListener {
            finish()
        }

        formViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading)
                showLoading()
        })

        formViewModel.isInsertSuccess.observe(this, Observer {isSuccess ->
            if(isSuccess)
                notifyUser("Saved successfully.")
            else
                notifyUser("Can't save.")
        })

        formViewModel.newStudentId.observe(this, Observer { id ->
            if(id>-1){
                val intent = Intent()
                intent.putExtra(Constants.KEY_ID, id)
                setResult(Activity.RESULT_OK, intent)
            }
        })

        formViewModel.isUpdateSuccess.observe(this, Observer {isSuccess ->
            if(isSuccess)
                notifyUser("Edited successfully.")
            else
                notifyUser("Can't save.")
        })

        formViewModel.updateStudentId.observe(this, Observer { id ->
            if(id>-1){
                val intent = Intent()
                intent.putExtra(Constants.KEY_ID, id)
                setResult(Activity.RESULT_OK, intent)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun createStudent() {

        val name = etName.text.toString()
        val fatherName = etFatherName.text.toString()
        val age = etAge.text.toString().toInt()
        val phone = etPhone.text.toString()

        //todo: check validations
        val student = Student(name = name, fatherName = fatherName, age = age, phone = phone)
        formViewModel.insertStudent(student)

    }

    private fun updateStudent(student: Student) {

        val name = etName.text.toString()
        val fatherName = etFatherName.text.toString()
        val age = etAge.text.toString().toInt()
        val phone = etPhone.text.toString()

        //todo: check validations
        val newStudent = Student(student.id, name, fatherName, age, phone)
        formViewModel.updateStudent(newStudent)
    }


    private fun notifyUser(message: String) {
        Snackbar.make(btnOk, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        Snackbar.make(btnOk, "Loading..........", Snackbar.LENGTH_SHORT).show()
    }

    private fun populateUi(student: Student?) {
        if (student != null) {
            title = "Update"
            etName.setText(student.name)
            etFatherName.setText(student.fatherName)
            etAge.setText(student.age.toString())
            etPhone.setText(student.phone)
            btnOk.text = title
        } else {
            title = "Create"
            btnOk.text = title
        }
    }
}