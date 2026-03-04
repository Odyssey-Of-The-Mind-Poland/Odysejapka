package odyseja.odysejapka.async

/**
 * A Runner that supports cooperative cancellation.
 * Implementations should periodically check [isCancelled] and exit gracefully when true.
 */
interface CancellableRunner : Runner {

    /**
     * Request cancellation. The runner should check [isCancelled] and stop as soon as safe.
     */
    fun requestCancel()

    /**
     * Returns true if cancellation has been requested.
     */
    fun isCancelled(): Boolean
}
