package com.github.minigdx.tiny.platform.glfw

import com.github.minigdx.tiny.Seconds
import com.github.minigdx.tiny.input.InputHandler
import com.github.minigdx.tiny.sound.MidiSound
import com.github.minigdx.tiny.sound.SoundManager
import com.github.minigdx.tiny.sound.SoundManager.Companion.SAMPLE_RATE
import com.github.minigdx.tiny.sound.WaveGenerator
import java.io.ByteArrayInputStream
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.experimental.and

class JavaMidiSound(private val data: ByteArray) : MidiSound {

    private var sequencer: Sequencer? = null

    private fun _play(loop: Int) {
        val seq: Sequencer = MidiSystem.getSequencer()

        seq.open()

        val sequence = MidiSystem.getSequence(ByteArrayInputStream(data))
        seq.sequence = sequence

        sequencer = seq

        seq.loopCount = loop
        seq.start()
    }

    override fun play() {
        _play(0)
    }

    override fun loop() {
        _play(LOOP_CONTINUOUSLY)
    }

    override fun stop() {
        sequencer?.run {
            if (isRunning) {
                stop()
            }
            if (isOpen) {
                close()
            }
        }
    }
}

class JavaMidiSoundManager : SoundManager {

    // When closing the application, switch isActive to false to stop the background thread.
    private var isActive = true

    private val bufferQueue: BlockingQueue<ByteArray> = ArrayBlockingQueue(10)

    private val backgroundAudio = object : Thread() {
        override fun run() {
            val notesLine = AudioSystem.getSourceDataLine(
                AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    SoundManager.SAMPLE_RATE.toFloat(),
                    16,
                    1, // TODO: set 2 to get Stereo
                    2,
                    SoundManager.SAMPLE_RATE.toFloat(),
                    false,
                ),
            )

            notesLine.open()
            notesLine.start()

            while (isActive) {
                val nextBuffer = bufferQueue.take()
                notesLine.write(nextBuffer, 0, nextBuffer.size)
            }
            notesLine.close()
        }
    }

    override fun initSoundManager(inputHandler: InputHandler) {
        backgroundAudio.start()
    }

    override suspend fun createSound(data: ByteArray): MidiSound {
        return JavaMidiSound(data)
    }

    override fun playNotes(notes: List<WaveGenerator>, longestDuration: Seconds) {
        if (notes.isEmpty()) return

        val buffer = generateAudioBuffer(longestDuration, notes)

        bufferQueue.offer(buffer)
    }

    private fun generateAudioBuffer(
        longestDuration: Seconds,
        notes: List<WaveGenerator>,
    ): ByteArray {
        val numSamples: Int = (SAMPLE_RATE * longestDuration).toInt()
        val buffer = ByteArray(numSamples * 2)
        val fadeOutIndex = getFadeOutIndex(longestDuration)

        for (i in 0 until numSamples) {
            val sample = fadeOut(mix(i, notes), i, fadeOutIndex, numSamples)

            val sampleValue: Float = (sample * Short.MAX_VALUE)
            val clippedValue = sampleValue.coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat())
            val result = clippedValue.toInt().toShort()

            buffer[2 * i] = (result and 0xFF).toByte()
            buffer[2 * i + 1] = (result.toInt().shr(8) and 0xFF).toByte()
        }
        return buffer
    }

    override fun playSfx(notes: List<WaveGenerator>) {
        if (notes.isEmpty()) return

        val numSamples: Int = (SAMPLE_RATE * notes.first().duration * notes.size).toInt()
        val sfxBuffer = ByteArray(numSamples * 2)
        var currentIndex = 0
        notes.forEach {
            val buffer = generateAudioBuffer(it.duration, listOf(it))
            buffer.copyInto(sfxBuffer, destinationOffset = currentIndex)
            currentIndex += buffer.size
        }

        bufferQueue.offer(sfxBuffer)
    }
}
