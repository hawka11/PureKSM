package pure.ksm.core

import kotlin.reflect.KClass

public data class Context(val data: List<Any>) {

    fun <T : Any> mostRecent(ofType: KClass<T>): T? {
        return ofType(ofType).last()
    }

    fun <T : Any> ofType(ofType: KClass<T>): List<T> {
        return data.filter { it.javaClass.isAssignableFrom(ofType.java) } as List<T>
    }

    fun append(extra: Any): Context {
        return this.copy(data = data + extra)
    }
}