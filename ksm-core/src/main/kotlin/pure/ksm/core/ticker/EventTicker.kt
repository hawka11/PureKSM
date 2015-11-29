package pure.ksm.core.ticker

import pure.ksm.core.Transition
import pure.ksm.core.TransitionRepository
import pure.ksm.core.WithinLock
import java.util.*
import kotlin.concurrent.schedule

class EventTicker(val timer: Timer, val repository: TransitionRepository) {

    public fun schedule(delay: Long, f: (transition: Transition) -> Transition) {

        timer.schedule(delay, {
            repository.ids().forEach {
                WithinLock.update(it, repository, f)
            }
        })
    }

    companion object {
        fun timeout(repository: TransitionRepository) = EventTicker(Timer("pure-timeout-ticker"), repository)
    }
}