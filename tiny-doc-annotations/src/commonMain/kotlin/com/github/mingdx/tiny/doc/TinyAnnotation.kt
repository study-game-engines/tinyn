package com.github.mingdx.tiny.doc

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class TinyLib(
    /**
     * Name of the Library.
     * By default, will use the name of the class.
     */
    val name: String = "",
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class TinyFunction(
    /**
     * Name of the function.
     * By default, will use the name of the class.
     */
    val name: String = "",
    /**
     * Description of the function.
     */
    val description: String = "",
    /**
     * LUA Code use as example.
     * This code will be injected in the web documentation.
     */
    val example: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class TinyArg(
    val name: String,
    val description: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class TinyArgs(
    val names: Array<String>,
    val documentations: Array<String> = [],
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TinyCall(
    val documentation: String = "",
    val mainCall: Boolean = false
)