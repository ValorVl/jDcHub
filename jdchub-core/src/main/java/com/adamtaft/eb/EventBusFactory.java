package com.adamtaft.eb;

/**
 * A factory for creating instances of {@link EventBus}.  The most common use
 * case will be to call {@link #getEventBus()}, which returns a {@link BasicEventBus}
 * singleton instance.  Other EventBus implementations can, however, be created and
 * used easily with this factory.
 *
 * @author Adam Taft
 * @deprecated Favor using the {@link EventBusService} instead.
 */
@Deprecated
public class EventBusFactory {

	/**
	 * Creates and returns an {@link EventBus} instances as specified by the
	 * provided event bus class.  Uses {@link Class#newInstance()} to create the
	 * new instance of the event bus.
	 * 
	 * @param eventBusClass The class used to create the event bus instance.
	 * @return  The specified {@link EventBus}
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static EventBus newEventBus(Class<? extends EventBus> eventBusClass) throws InstantiationException, IllegalAccessException {
		return eventBusClass.newInstance();
	}
	
	/**
	 * Creates and returns an {@link EventBus} instance based on the specified
	 * class name.  This will create the bus using the {@link Class#forName(String)}
	 * method.
	 * 
	 * @param eventBusClassName The fully qualified class name.
	 * @return An instance of the specified EventBus class.
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static EventBus newEventBus(String eventBusClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return newEventBus((Class<? extends EventBus>) Class.forName(eventBusClassName));
	}
	
	/**
	 * Creates and returns a <b>new</b> {@link BasicEventBus}.  This would
	 * not usually be very useful unless separate {@link EventBus} instances
	 * were needed (for a more complex application or something).
	 * 
	 * @return A new instance of {@link BasicEventBus}
	 */
	public static EventBus newEventBus() {
		try {
			return newEventBus(BasicEventBus.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static EventBus instance;
	
	
	/**
	 * Creates (if necessary) and returns a singleton instance of a
	 * {@link BasicEventBus}.  This will likely be the most common
	 * use case for most applications.
	 * 
	 * @return A singleton instance of {@link BasicEventBus}
	 */
	public static synchronized EventBus getEventBus() {
		if (instance == null) {
			instance = newEventBus();
		}
		return instance;
	}

}
