package pure.ksm.core

public data class Context(val data: List<Any>) {

    //TODO: test
    fun <T> mostRecent(ofType: Class<T>): T? {
        val found = data.findLast { it.javaClass.isAssignableFrom(ofType) }
        return if (found != null) ofType.cast(found) else null
    }

    fun append(extra: Any): Context {
        val all: MutableList<Any> = arrayListOf()
        all.addAll(data)
        all.add(extra)

        return this.copy(data = all)
    }
}