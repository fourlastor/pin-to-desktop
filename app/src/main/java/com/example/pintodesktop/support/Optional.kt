package com.example.pintodesktop.support

sealed class Optional<out T> {
    fun <R> map(transform: (T) -> R): Optional<R> {
        return when (this) {
            is Present<T> -> Present(transform(value))
            is Absent -> Absent.absent()
        }
    }
}

data class Present<out T>(val value: T) : Optional<T>()

class Absent<out T> private constructor() : Optional<T>() {
    companion object {

        private val INSTANCE = Absent<Any>()

        @Suppress("UNCHECKED_CAST")
        fun <T> absent(): Absent<T> = INSTANCE as Absent<T>
    }
}
