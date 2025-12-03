package com.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbite.model.PerfilUsuario
import com.smartbite.repository.PerfilRepository
import kotlinx.coroutines.launch
import android.content.Context
class PerfilViewModel : ViewModel() {

    // METAS DIARIAS
    val metaProteinaLiveData = MutableLiveData<Int>()
    val metaCarboLiveData = MutableLiveData<Int>()
    val metaVegetalLiveData = MutableLiveData<Int>()

    val objetivoLiveData = MutableLiveData<String>() // subir / bajar / mantener

    private val repository = PerfilRepository()

    val perfilLiveData = MutableLiveData<PerfilUsuario?>()
    val cargandoLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String?>()


    // =====================================================
    // OBTENER PERFIL DEL SERVIDOR
    // =====================================================
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


    // =====================================================
    // ACTUALIZAR PERFIL
    // =====================================================
    fun actualizarPerfil(token: String, perfil: PerfilUsuario) {
        cargandoLiveData.postValue(true)
        viewModelScope.launch {
            try {
                val exito = repository.actualizarPerfil(token, perfil)

                if (exito) {
                    val actualizado = repository.obtenerPerfil(token)
                    perfilLiveData.postValue(actualizado)
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


    // =====================================================
    // NUEVO: CALCULAR METAS SEGÚN PESO + OBJETIVO
    // =====================================================
    fun calcularMetasDiarias(peso: Float, objetivo: String): Triple<Int, Int, Int> {
        return when (objetivo) {

            "subir" -> {
                val prote = (2.0 * peso).toInt()
                val carbo = (4.0 * peso).toInt()
                val vegetal = 80
                Triple(prote, carbo, vegetal)
            }

            "bajar" -> {
                val prote = (1.8 * peso).toInt()
                val carbo = (2.2 * peso).toInt()
                val vegetal = 130
                Triple(prote, carbo, vegetal)
            }

            "mantener" -> {
                val prote = (1.6 * peso).toInt()
                val carbo = (3.0 * peso).toInt()
                val vegetal = 100
                Triple(prote, carbo, vegetal)
            }

            else -> Triple(150, 250, 80)
        }
    }
    fun calcularMetasYAplicarlas(peso: Float?, userId: Long, context: Context) {
        if (peso == null) return

        val objetivo = objetivoLiveData.value ?: "mantener"
        val (p, c, v) = calcularMetasDiarias(peso, objetivo)

        metaProteinaLiveData.postValue(p)
        metaCarboLiveData.postValue(c)
        metaVegetalLiveData.postValue(v)

        guardarMetasEnPrefs(context, userId, p, c, v)
    }


    fun guardarMetasEnPrefs(context: Context, userId: Long, p: Int, c: Int, v: Int) {
        val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("meta_prote_$userId", p)
            .putInt("meta_carbo_$userId", c)
            .putInt("meta_vegetal_$userId", v)
            .apply()
    }
}

