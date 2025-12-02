package com.smartbite

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.coroutines.flow.MutableStateFlow
import java.nio.charset.StandardCharsets

class MqttAppService {

    private val server = "161.132.4.188"
    private val port = 1883
    private val username = "smartbite"
    private val password = "tecsup2025"

    private val client = MqttClient.builder()
        .useMqttVersion3() // Protocolo estable
        .identifier("android_smartbite_" + System.currentTimeMillis())
        .serverHost(server)
        .serverPort(port)
        .buildAsync()

    val connected = MutableStateFlow(false)

    private val pendingTopics = mutableListOf<Triple<String, MqttQos, (String) -> Unit>>()

    /** 🔌 CONECTAR */
    fun connect() {
        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(password.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { _, error ->
                if (error == null) {
                    connected.value = true
                    Log.d("MQTT", "Conectado al broker MQTT")

                    //  Suscribir los topics que quedaron pendientes
                    pendingTopics.forEach { (topic, qos, callback) ->
                        subscribe(topic, qos, callback)
                    }
                    pendingTopics.clear()

                } else {
                    Log.e("MQTT", "Error al conectar: ${error.message}")
                }
            }
    }

    /** PUBLICAR */
    fun publish(topic: String, payload: String, qos: MqttQos = MqttQos.AT_LEAST_ONCE) {
        if (!connected.value) {
            Log.e("MQTT", " No conectado, publish cancelado")
            return
        }

        client.publishWith()
            .topic(topic)
            .payload(payload.toByteArray(StandardCharsets.UTF_8))
            .qos(qos)
            .send()

        Log.d("MQTT", " Enviado a $topic → $payload")
    }

    /** SUSCRIBIR */
    fun subscribe(
        topic: String,
        qos: MqttQos = MqttQos.AT_LEAST_ONCE,
        callback: (String) -> Unit
    ) {
        if (!connected.value) {
            // Guardar suscripción para después
            pendingTopics.add(Triple(topic, qos, callback))
            Log.w("MQTT", " Aún no conectado → Suscripción pendiente a $topic")
            return
        }

        client.subscribeWith()
            .topicFilter(topic)
            .qos(qos)
            .callback { message ->
                val text = String(message.payloadAsBytes, StandardCharsets.UTF_8)
                callback(text)
            }
            .send()

        Log.d("MQTT", " Suscrito a $topic")
    }
}
