package pure.ksm.core

import kotlin.reflect.KClass

public data class Context(val data: List<Any>) {

    fun <T : Any> mostRecent(ofType: KClass<T>): T? {
        return data.findLast { it.javaClass.isAssignableFrom(ofType.java) } as T?
    }

    fun append(extra: Any): Context {
        val all: MutableList<Any> = arrayListOf()
        all.addAll(data)
        all.add(extra)

        return this.copy(data = all)
    }
}