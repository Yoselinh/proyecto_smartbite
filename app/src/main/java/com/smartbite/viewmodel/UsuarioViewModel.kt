package com.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbite.model.Usuario
import com.smartbite.model.LoginRequest
import com.smartbite.model.LoginResponse
import com.smartbite.model.RegistroRequest
import com.smartbite.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository()

    val usuarioLiveData = MutableLiveData<LoginResponse?>()
    val errorLiveData = MutableLiveData<String?>()
    val cargandoLiveData = MutableLiveData<Boolean>()

    // ---------- LOGIN ----------
    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            errorLiveData.postValue("Debe ingresar correo y password.")
            return
        }

        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val request = LoginRequest(correo, password)
                val resultado = usuarioRepository.login(request)

                if (resultado != null) {
                    usuarioLiveData.postValue(resultado)
                } else {
                    errorLiveData.postValue("Credenciales incorrectas.")
                }

            } catch (e: Exception) {
                errorLiveData.postValue("Error al iniciar sesión: ${e.message}")
            } finally {
                cargandoLiveData.postValue(false)
            }
        }
    }

    // ---------- REGISTRO ----------
    fun registrar(nombre: String, correo: String, password: String, rol: String = "USER") {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            errorLiveData.postValue("Debe completar todos los campos.")
            return
        }

        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val request = RegistroRequest(nombre, correo, password, rol)
                val resultado = usuarioRepository.registrar(request)

                if (resultado != null) {
                    usuarioLiveData.postValue(resultado)
                } else {
                    errorLiveData.postValue("Error al registrar usuario.")
                }

            } catch (e: Exception) {
                errorLiveData.postValue("Error al registrar: ${e.message}")
            } finally {
                cargandoLiveData.postValue(false)
            }
        }
    }
}
