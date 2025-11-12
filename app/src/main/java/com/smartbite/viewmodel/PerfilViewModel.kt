package com.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbite.model.PerfilUsuario
import com.smartbite.repository.PerfilRepository
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val repository = PerfilRepository()

    val perfilLiveData = MutableLiveData<PerfilUsuario?>()
    val cargandoLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String?>()

    /**
     * Obtiene el perfil del usuario desde el servidor usando el token.
     */
    fun obtenerPerfil(token: String) {
        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val perfil = repository.obtenerPerfil(token)
                perfilLiveData.postValue(perfil)
            } catch (e: Exception) {
                errorLiveData.postValue("Error al obtener perfil: ${e.message}")
            } finally {
                cargandoLiveData.postValue(false)
            }
        }
    }

    /**
     * Actualiza el perfil en el servidor y luego refresca los datos.
     */
    fun actualizarPerfil(token: String, perfil: PerfilUsuario) {
        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val exito = repository.actualizarPerfil(token, perfil)
                if (exito) {
                    // Vuelve a obtener el perfil actualizado desde el servidor
                    val perfilActualizado = repository.obtenerPerfil(token)
                    perfilLiveData.postValue(perfilActualizado)
                } else {
                    errorLiveData.postValue("No se pudo actualizar el perfil.")
                }
            } catch (e: Exception) {
                errorLiveData.postValue("Error al actualizar perfil: ${e.message}")
            } finally {
                cargandoLiveData.postValue(false)
            }
        }
    }
}
