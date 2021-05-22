package me.kaungmyatmin.studentreg

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FormViewModel(
    application: Application
) : AndroidViewModel(application) {
    val isLoading = MutableLiveData<Boolean>()
    val isInsertSuccess = MutableLiveData<Boolean>()
    val isUpdateSuccess = MutableLiveData<Boolean>()
    val newStudentId = MutableLiveData<Long>()
    val updateStudentId = MutableLiveData<Long>()

    private val studentDao = AppDatabase.getDatabase(application.applicationContext).getStudentDao()
    fun insertStudent(data: Student) {
        isLoading.value = true
        viewModelScope.launch {
            val id = studentDao.insert(data)
            isLoading.value = false
            newStudentId.value = id
            isInsertSuccess.value = id > -1
        }
    }

    fun updateStudent(data: Student) {
        viewModelScope.launch {
            val rowCount = studentDao.update(data)
            isUpdateSuccess.value = rowCount > 0
            updateStudentId.value = data.id ?: -1
        }
    }
}