package com.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbite.model.LecturaSensor
import com.smartbite.model.LecturaRequest
import com.smartbite.repository.LecturaRepository
import kotlinx.coroutines.launch

class LecturaViewModel : ViewModel() {

    private val lecturaRepository = LecturaRepository()

    val lecturasLiveData = MutableLiveData<List<LecturaSensor>?>()
    val registroExitosoLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String?>()
    val cargandoLiveData = MutableLiveData<Boolean>()

    // ---------- OBTENER TODAS LAS LECTURAS ----------
    fun obtenerLecturas() {
        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val lecturas = lecturaRepository.obtenerLecturas()
                lecturasLiveData.postValue(lecturas)
            } catch (e: Exception) {
                errorLiveData.postValue("Error al obtener lecturas: ${e.message}")
            } finally {
                cargandoLiveData.postValue(false)
            }
        }
    }

    // ---------- REGISTRAR UNA NUEVA LECTURA ----------
    fun registrarLectura(request: LecturaRequest) {
        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val exito = lecturaRepository.registrarLectura(request)
                registroExitosoLiveData.postValue(exito)
            } catch (e: Exception) {
                errorLiveData.postValue("Error al registrar lectura: ${e.message}")
            } finally {
                cargandoLiveData.postValue(false)
            }
        }
    }
}
