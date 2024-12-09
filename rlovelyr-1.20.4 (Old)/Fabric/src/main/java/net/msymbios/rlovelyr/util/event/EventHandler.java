package net.msymbios.rlovelyr.util.event;

import net.msymbios.rlovelyr.util.interfaces.IEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to handle events.
 */
public class EventHandler<T extends IEvent> {

    // -- Variables --

    /**
     * The list of subscribed event handlers.
     */
    private final List<Handler<T>> handlers = new ArrayList<>();

    // -- Methods --

    /**
     * Forwards the event to all subscribed event handlers until it is handled.
     */
    public T handle(T event) {
        for (Handler<T> handler : handlers) {
            handler.handle(event);

            if (event instanceof HandleableEvent) {
                if (((HandleableEvent) event).isHandled()) {
                    return event;
                }
            }
        }

        return event;
    }

    /**
     * Subscribes an event handler.
     */
    public void subscribe(Handler<T> handler) {
        this.handlers.add(handler);
    }

    /**
     * Subscribes an event handler and assigns it the highest priority.
     */
    public void subscribeFirst(Handler<T> handler) {
        this.handlers.add(0, handler);
    }

    /**
     * Unsubscribes an event handler.
     */
    public void unsubscribe(Handler<T> handler) {
        this.handlers.remove(handler);
    }

    // -- Interfaces --

    /**
     * An event handler.
     *
     * @param <T> the type of event.
     */
    @FunctionalInterface
    public interface Handler<T extends IEvent> {
        void handle(T event);
    } // Interface Handler

} // Class EventHandler