package com.github.minigdx.tiny.lua

import com.github.mingdx.tiny.doc.TinyLib
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction

enum class Note(val frequency: Float) {
    C0(16.35f),
    Cs0(17.32f),
    Db0(17.32f),
    D0(18.35f),
    Ds0(19.45f),
    Eb0(19.45f),
    E0(20.60f),
    F0(21.83f),
    Fs0(23.12f),
    Gb0(23.12f),
    G0(24.50f),
    Gs0(25.96f),
    Ab0(25.96f),
    A0(27.50f),
    As0(29.14f),
    Bb0(29.14f),
    B0(30.87f),

    C1(32.70f),
    Cs1(34.65f),
    Db1(34.65f),
    D1(36.71f),
    Ds1(38.89f),
    Eb1(38.89f),
    E1(41.20f),
    F1(43.65f),
    Fs1(46.25f),
    Gb1(46.25f),
    G1(49.00f),
    Gs1(51.91f),
    Ab1(51.91f),
    A1(55.00f),
    As1(58.27f),
    Bb1(58.27f),
    B1(61.74f),

    C2(65.41f),
    Cs2(69.30f),
    Db2(69.30f),
    D2(73.42f),
    Ds2(77.78f),
    Eb2(77.78f),
    E2(82.41f),
    F2(87.31f),
    Fs2(92.50f),
    Gb2(92.50f),
    G2(98.00f),
    Gs2(103.83f),
    Ab2(103.83f),
    A2(110.00f),
    As2(116.54f),
    Bb2(116.54f),
    B2(123.47f),

    C3(130.81f),
    Cs3(138.59f),
    Db3(138.59f),
    D3(146.83f),
    Ds3(155.56f),
    Eb3(155.56f),
    E3(164.81f),
    F3(174.61f),
    Fs3(185.00f),
    Gb3(185.00f),
    G3(196.00f),
    Gs3(207.65f),
    Ab3(207.65f),
    A3(220.00f),
    As3(233.08f),
    Bb3(233.08f),
    B3(246.94f),

    C4(261.63f),
    Cs4(277.18f),
    Db4(277.18f),
    D4(293.66f),
    Ds4(311.13f),
    Eb4(311.13f),
    E4(329.63f),
    F4(349.23f),
    Fs4(369.99f),
    Gb4(369.99f),
    G4(392.00f),
    Gs4(415.30f),
    Ab4(415.30f),
    A4(440.00f),
    As4(466.16f),
    Bb4(466.16f),
    B4(493.88f),

    C5(523.25f),
    Cs5(554.37f),
    Db5(554.37f),
    D5(587.33f),
    Ds5(622.25f),
    Eb5(622.25f),
    E5(659.26f),
    F5(698.46f),
    Fs5(739.99f),
    Gb5(739.99f),
    G5(783.99f),
    Gs5(830.61f),
    Ab5(830.61f),
    A5(880.00f),
    As5(932.33f),
    Bb5(932.33f),
    B5(987.77f),

    C6(1046.50f),
    Cs6(1108.73f),
    Db6(1108.73f),
    D6(1174.66f),
    Ds6(1244.51f),
    Eb6(1244.51f),
    E6(1318.51f),
    F6(1396.91f),
    Fs6(1479.98f),
    Gb6(1479.98f),
    G6(1567.98f),
    Gs6(1661.22f),
    Ab6(1661.22f),
    A6(1760.00f),
    As6(1864.66f),
    Bb6(1864.66f),
    B6(1975.53f),

    C7(2093.00f),
    Cs7(2217.46f),
    Db7(2217.46f),
    D7(2349.32f),
    Ds7(2489.02f),
    Eb7(2489.02f),
    E7(2637.02f),
    F7(2793.83f),
    Fs7(2959.96f),
    Gb7(2959.96f),
    G7(3135.96f),
    Gs7(3322.44f),
    Ab7(3322.44f),
    A7(3520.00f),
    As7(3729.31f),
    Bb7(3729.31f),
    B7(3951.07f),

    C8(4186.01f),
    Cs8(4434.92f),
    Db8(4434.92f),
    D8(4698.63f),
    Ds8(4978.03f),
    Eb8(4978.03f),
    E8(5274.04f),
    F8(5587.65f),
    Fs8(5919.91f),
    Gb8(5919.91f),
    G8(6271.93f),
    Gs8(6644.88f),
    Ab8(6644.88f),
    A8(7040.00f),
    As8(7458.62f),
    Bb8(7458.62f),
    B8(7902.13f),
}

@TinyLib(
    "notes",
    "List all notes from C0 to B8. " +
        "Please note that bemols are the note with b (ie: Gb2) while sharps are the note with s (ie: As3).",
)
class NotesLib : TwoArgFunction() {

    override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
        val keys = LuaTable()

        Note.values().forEach { note ->
            keys[note.name] = valueOf(note.ordinal)
        }

        arg2["notes"] = keys
        arg2["package"]["loaded"]["notes"] = keys
        return keys
    }
}
