package net.msymbios.rlovelyr.util.event;

/**
 * A cancellable event.
 */
public class CancelableEvent extends HandleableEvent {

    // -- Variables --

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled = false;

    // -- Methods --

    /**
     * @return whether the event is cancelled.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets whether the event is cancelled.
     */
    public void setIsCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        setIsHandled(true);
    } // setIsCancelled ()

} // Class CancelableEvent