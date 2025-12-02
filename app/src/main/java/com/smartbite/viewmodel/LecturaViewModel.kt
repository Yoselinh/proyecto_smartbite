package com.smartbite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbite.MqttAppService
import com.smartbite.model.LecturaSensor
import com.smartbite.model.LecturaRequest
import com.smartbite.repository.LecturaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.intPreferencesKey

class LecturaViewModel(
    private val repository: LecturaRepository
) : ViewModel() {

    private var mqttIniciado = false
    private lateinit var mqtt: MqttAppService

    var tokenUser: String = ""
    var idUsuarioActual: Long = 0L  // ← ahora guardamos ID del usuario

    fun cargarToken(context: Context) {
        val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)
        tokenUser = prefs.getString("auth_token", "") ?: ""
        idUsuarioActual = prefs.getLong("user_id", 0L)

        if (tokenUser.isEmpty() || idUsuarioActual == 0L) {
            _mensaje.value = "Error: sesión expirada o ID de usuario no encontrado."
        }
    }

    val nombreProteina = MutableStateFlow("")
    val nombreCarbohidrato = MutableStateFlow("")
    val nombreVegetal = MutableStateFlow("")

    fun actualizarNombreProteina(valor: String) {
        nombreProteina.value = valor
    }

    fun actualizarNombreCarbohidrato(valor: String) {
        nombreCarbohidrato.value = valor
    }

    fun actualizarNombreVegetal(valor: String) {
        nombreVegetal.value = valor
    }

    private val _misLecturas = MutableStateFlow<List<LecturaSensor>>(emptyList())
    val misLecturas: StateFlow<List<LecturaSensor>> = _misLecturas

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    val ultimoPesoProteina = MutableStateFlow(0.0)
    val ultimoPesoCarbohidrato = MutableStateFlow(0.0)
    val ultimoPesoVegetal = MutableStateFlow(0.0)

    fun iniciarMQTT() {
        if (!mqttIniciado) {
            mqtt = MqttAppService()
            mqtt.connect()
            mqttIniciado = true
        }

        mqtt.subscribe("smartbite/sensor/lectura") { message ->
            try {
                val json = JSONObject(message)

                ultimoPesoProteina.value = json.getDouble("pesoProteina")
                ultimoPesoCarbohidrato.value = json.getDouble("pesoCarbohidrato")
                ultimoPesoVegetal.value = json.getDouble("pesoVegetal")

                _mensaje.value = "Recibiendo pesos del plato "

            } catch (e: Exception) {
                _mensaje.value = "Error procesando JSON: ${e.message}"
            }
        }
    }


    // ============================================
    // CARGAR LECTURAS DEL BACKEND
    // ============================================
    fun cargarMisLecturas() {
        viewModelScope.launch {
            if (tokenUser.isEmpty()) {
                _mensaje.value = "Token vacío, inicia sesión"
                return@launch
            }

            val lista = repository.obtenerMisLecturas(tokenUser)
            if (lista != null) {
                _misLecturas.value = lista
            } else {
                _mensaje.value = "No se pudieron cargar las lecturas"
            }
        }
    }

    fun obtenerResumenDeHoy(): Triple<Double, Double, Double> {
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val hoyLecturas = misLecturas.value.filter { it.fechaHora.startsWith(hoy) }

        var totalP = 0.0
        var totalC = 0.0
        var totalV = 0.0

        hoyLecturas.forEach {
            totalP += it.pesoProteina
            totalC += it.pesoCarbohidrato
            totalV += it.pesoVegetal
        }

        return Triple(totalP, totalC, totalV)
    }

    fun registrarNuevaLectura() {
        viewModelScope.launch {

            if (tokenUser.isEmpty() || idUsuarioActual == 0L) {
                _mensaje.value = "Error: sesión inválida o ID de usuario no encontrado."
                return@launch
            }

            val request = LecturaRequest(
                usuarioId = idUsuarioActual,
                pesoProteina = ultimoPesoProteina.value,
                pesoCarbohidrato = ultimoPesoCarbohidrato.value,
                pesoVegetal = ultimoPesoVegetal.value,
                nombreProteina = nombreProteina.value,
                nombreCarbohidrato = nombreCarbohidrato.value,
                nombreVegetal = nombreVegetal.value
            )

            val ok = repository.registrarLectura(tokenUser, request)

            _mensaje.value = "Lectura registrada con éxito"
            cargarMisLecturas()

        }
    }
}
