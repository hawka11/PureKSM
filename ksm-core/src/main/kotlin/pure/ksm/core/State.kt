package pure.ksm.core

interface State {

    fun handle(context: Context, event: Event): Transition
}