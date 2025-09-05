package io.lmy6268.deviceshaking

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ViewModel(), SensorEventListener {
    private val sensorManager: SensorManager =
        applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var lastTime: Long = 0

    companion object {
        private const val SHAKE_THRESHOLD = 500
        private const val SHAKE_TIMEOUT = 1000L // 1초간 입력 없으면 "멈춤"
    }


    // 흔들림 멈춤 이벤트
    private val _shakeStoppedEvents = MutableSharedFlow<Unit>()
    val shakeStoppedEvents = _shakeStoppedEvents.asSharedFlow()

    private var shakeTimeoutJob: Job? = null

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val curTime = System.currentTimeMillis()
        if (curTime - lastTime > 100) {
            val diffTime = curTime - lastTime
            lastTime = curTime

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

            if (speed > SHAKE_THRESHOLD) {
                // 흔들림이 발생하면 타이머 리셋
                resetShakeTimeout()
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    private fun resetShakeTimeout() {
        // 기존 타이머 취소 후 다시 시작
        shakeTimeoutJob?.cancel()
        shakeTimeoutJob = viewModelScope.launch {
            delay(SHAKE_TIMEOUT)
            // 일정 시간동안 흔들림 없으면 "멈춤 이벤트"
            _shakeStoppedEvents.emit(Unit)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

}