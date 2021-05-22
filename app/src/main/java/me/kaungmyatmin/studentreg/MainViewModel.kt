package me.kaungmyatmin.studentreg

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    var isLoading = MutableLiveData<Boolean>()
    var isEmpty = MutableLiveData<Boolean>()
    var data = MutableLiveData<List<Student>>()

    private val studentDao = AppDatabase.getDatabase(application.applicationContext).getStudentDao()

    fun findAll(){
        isLoading.value = true
        viewModelScope.launch {
            val result = studentDao.findAll()
            isLoading.value = false
            if(result.isEmpty()){
                isEmpty.value = true
            }else{
                data.value = result
            }
        }
    }
}