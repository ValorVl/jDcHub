package com.adamtaft.eb;

import java.util.ServiceLoader;

/**
 * An {@link EventBus} factory that will return a singleton implementation loaded
 * via the Java 6 {@link ServiceLoader}. By default, without changes to the jar,
 * a {@link BasicEventBus} implementation will be returned via the factory methods.
 * <p>
 * This static factory also includes the same methods as EventBus which will delegate
 * to the ServiceLoader loaded instance.  Thus, the class creates a convenient single
 * location for which client code can be hooked to the configured EventBus.
 *
 * @author Adam Taft
 */
public final class EventBusService {

	private static final EventBus eventBus;
	static {
		ServiceLoader<EventBus> ldr = ServiceLoader.load(EventBus.class);
		eventBus = ldr.iterator().next();
	}
	
	public static EventBus getInstance() {
		return eventBus;
	}
	
	public static void subscribe(Object subscriber) {
		eventBus.subscribe(subscriber);
	}
	
	public static void unsubscribe(Object subscriber) {
		eventBus.unsubscribe(subscriber);
	}
	
	public static void publish(Object event) {
		eventBus.publish(event);
	}
	
	public static boolean hasPendingEvents() {
		return eventBus.hasPendingEvents();
	}
	
}
