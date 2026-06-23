package com.stdy4u.study4u.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.stdy4u.study4u.MainActivity
import com.stdy4u.study4u.Study4UApplication
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class FocusTimerService : Service() {

    private val binder = LocalBinder()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private val _secondsLeft = MutableStateFlow(0)
    val secondsLeft: StateFlow<Int> = _secondsLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _currentPhase = MutableStateFlow("focus")
    val currentPhase: StateFlow<String> = _currentPhase.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): FocusTimerService = this@FocusTimerService
    }

    override fun onCreate() {
        super.onCreate()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val totalSeconds = intent.getIntExtra(EXTRA_SECONDS, 25 * 60)
                val phase = intent.getStringExtra(EXTRA_PHASE) ?: "focus"
                startForeground(NOTIFICATION_ID, createNotification(phase, totalSeconds))
                startTimer(totalSeconds, phase)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(totalSeconds: Int, phase: String) {
        timerJob?.cancel()
        _currentPhase.value = phase
        _secondsLeft.value = totalSeconds
        _isRunning.value = true

        timerJob = scope.launch {
            var remaining = totalSeconds
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining--
                _secondsLeft.value = remaining
                updateNotification(phase, remaining)
            }
            if (remaining <= 0) {
                _isRunning.value = false
                playCompletionSound()
                vibrate()
                stopSelf()
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    private fun resumeTimer() {
        _isRunning.value = true
        val remaining = _secondsLeft.value
        val phase = _currentPhase.value
        startTimer(remaining, phase)
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _secondsLeft.value = 0
        _isRunning.value = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(phase: String, secondsLeft: Int) {
        val notification = createNotification(phase, secondsLeft)
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(phase: String, totalSeconds: Int): Notification {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)

        val pauseIntent = PendingIntent.getService(
            this, 0,
            Intent(this, FocusTimerService::class.java).apply { action = ACTION_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, FocusTimerService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = PendingIntent.getActivity(
            this, 2,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val phaseLabel = when (phase) {
            "focus" -> "Focus Time"
            "short_break" -> "Short Break"
            "long_break" -> "Long Break"
            else -> "Focus Timer"
        }

        return NotificationCompat.Builder(this, Study4UApplication.CHANNEL_FOCUS_TIMER)
            .setContentTitle("stdy4u - $phaseLabel")
            .setContentText("$timeText remaining")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_media_pause, "Pause", pauseIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopIntent)
            .build()
    }

    private fun playCompletionSound() {
        try {
            mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.NOTIFICATION_SOUND)
            mediaPlayer?.setVolume(0.5f, 0.5f)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener { it.release() }
        } catch (_: Exception) {}
    }

    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(1000)
            }
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        scope.cancel()
        mediaPlayer?.release()
    }

    companion object {
        const val ACTION_START = "com.stdy4u.action.START_TIMER"
        const val ACTION_PAUSE = "com.stdy4u.action.PAUSE_TIMER"
        const val ACTION_RESUME = "com.stdy4u.action.RESUME_TIMER"
        const val ACTION_STOP = "com.stdy4u.action.STOP_TIMER"
        const val EXTRA_SECONDS = "extra_seconds"
        const val EXTRA_PHASE = "extra_phase"
        const val NOTIFICATION_ID = 1001
    }
}
