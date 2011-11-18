package com.adamtaft.eb;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;



/**
 * A simple Event Bus implementation which receives events or messages from
 * various sources and distributes them to all subscribers of the event type.
 * This is highly useful for programs which are event driven. Swing applications
 * in particular can benefit from an event bus architecture, as opposed to the
 * traditional event listener architecture it employs.
 * <p>
 * The BasicEventBus class is thread safe and uses a background thread to notify the
 * subscribers of the event. The subscribers are notified in a serial fashion,
 * and only one event will be published at a time. Though, the
 * {@link #publish(Object)} method is done in a non-blocking way.
 * <p>
 * Subscribers subscribe to the EventBus using the {@link #subscribe(Object)}
 * method. A specific subscriber type is not required, but the subscriber will
 * be reflected to find all methods annotated with the {@link EventHandler}
 * annotations. These methods will be invoked as needed by the event bus based
 * on the type of the first parameter to the annotated method.
 * <p>
 * An event handler can indicate that it can veto events by setting the
 * {@link EventHandler#canVeto()} value to true.  This will inform the EventBus
 * of the subscriber's desire to veto the event.  A vetoed event will not be
 * sent to the regular subscribers.
 * <p>
 * During publication of an event, all veto EventHandler methods will be notified
 * first and allowed to throw a {@link VetoException} indicating that the event
 * has been vetoed and should not be published to the remaining event handlers.
 * If no vetoes have been made, the regular subscriber handlers will be notified
 * of the event.
 * <p>
 * Subscribers are stored using a {@link WeakReference} such that a memory leak
 * can be avoided if the client fails to unsubscribe at the end of the use.
 * However, calling the {@link #unsubscribe(Object)} method is highly
 * recommended none-the-less.
 * 
 * @author Adam Taft
 */
public final class BasicEventBus implements EventBus {

	private final List<HandlerInfo> handlers = new CopyOnWriteArrayList<HandlerInfo>();
	private final BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();
	private final BlockingQueue<HandlerInfo> killQueue = new LinkedBlockingQueue<HandlerInfo>();
	
	/**
	 * The ExecutorService used to handle event delivery to the event handlers.
	 */
	private final ExecutorService executorService;
	
	/**
	 * Should the event bus wait for the regular handlers to finish processing
	 * the event messages before continuing to the next event.  Defaults to
	 * 'false' which is sensible for most use cases.
	 */
	private final boolean waitForHandlers;
	
	
	/**
	 * Default constructor sets up the executorService property to use the
	 * {@link Executors#newCachedThreadPool()} implementation.  The configured
	 * ExecutorService will have a custom ThreadFactory such that the threads
	 * returned will be daemon threads (and thus not block the application
	 * from shutting down).
	 */
	public BasicEventBus() {
		this(Executors.newCachedThreadPool(new ThreadFactory() {
			private final ThreadFactory delegate = Executors.defaultThreadFactory();
			@Override
			public Thread newThread(Runnable r) {
				Thread t = delegate.newThread(r);
				t.setDaemon(true);
				return t;
			}
		}), false);
	}
	
	public BasicEventBus(ExecutorService executorService, boolean waitForHandlers) {
		// start the background daemon consumer thread.
		Thread eventQueueThread = new Thread(new EventQueueRunner(), "EventQueue Consumer Thread");
		eventQueueThread.setDaemon(true);
		eventQueueThread.start();
		
		Thread killQueueThread = new Thread(new KillQueueRunner(), "KillQueue Consumer Thread");
		killQueueThread.setDaemon(true);
		killQueueThread.start();
		
		this.executorService = executorService;
		this.waitForHandlers = waitForHandlers;
	}

	/**
	 * Subscribe the specified instance as a potential event subscriber.
	 * The subscriber must annotate a method (or two) with the {@link EventHandler}
	 * annotation if it expects to receive notifications.
	 * <p>
	 * Note that the EventBus maintains a {@link WeakReference} to the subscriber,
	 * but it is still adviced to call the {@link #unsubscribe(Object)} method
	 * if the subscriber does not wish to receive events any longer.
	 * 
	 * @param subscriber The subscriber object which will receive notifications on {@link EventHandler} annotated methods.
	 */
	public void subscribe(Object subscriber) {
		// lookup to see if we have any subscriber instances already
		boolean subscribedAlready = false;
		for (HandlerInfo info : handlers) {
			Object otherSubscriber = info.getSubscriber();
			if (otherSubscriber == null) {
				try {
					killQueue.put(info);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			if (subscriber == otherSubscriber) {
				subscribedAlready = true;
			}
		}
		if (subscribedAlready) {
			return;
		}
		
		Method[] methods = subscriber.getClass().getDeclaredMethods();
		for (Method method : methods) {
			// look for the EventHandler annotation on the method, if it exists
			// if not, this returns null, and go to the next method
			EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null)
				continue;

			// evaluate the parameters of the method.
			// only a single parameter of the Object type is allowed for the handler method.
			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length != 1) {
				throw new IllegalArgumentException("EventHandler methods must specify a single Object paramter.");
			}

			// add the subscriber to the list
			HandlerInfo info = new HandlerInfo(parameters[0], method, subscriber, eh.canVeto());
			handlers.add(info);
		}
	}

	
	/***
	 * Unsubscribe the specified subscriber from receiving future published
	 * events.
	 * 
	 * @param subscriber The object to unsubcribe from future events.
	 */
	public void unsubscribe(Object subscriber) {
		List<HandlerInfo> killList = new ArrayList<HandlerInfo>();
		for (HandlerInfo info : handlers) {
			Object obj = info.getSubscriber();
			if (obj == null || obj == subscriber) {
				killList.add(info);
			}
		}
		for (HandlerInfo kill : killList) {
			handlers.remove(kill);
		}
	}

	
	/**
	 * Publish the specified event to the event bus.  Based on the type
	 * of the event, the EventBus will publish the event to the subscribing
	 * objects.
	 * 
	 * @param event The event to publish on the event bus.
	 */
	public void publish(Object event) {
		try {
			queue.put(event);

		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Returns if the event bus has pending events.
	 * 
	 * @return Returns true if the event bus has pending events to publish.
	 */
	public boolean hasPendingEvents() {
		return queue.size() > 0;
	}

	
	// called on the background thread.
	private void notifySubscribers(final Object evt) {
		// roll through the subscribers
		// we find the veto handlers, regular handlers
		final List<HandlerInfoCallable> vetoList = new ArrayList<HandlerInfoCallable>();
		final List<HandlerInfoCallable> reguList = new ArrayList<HandlerInfoCallable>();

		for (final HandlerInfo info : handlers) {
			if (! info.matchesEvent(evt)) continue;
			
			HandlerInfoCallable hc = new HandlerInfoCallable(info, evt);
			
			if (info.isVetoHandler()) {
				vetoList.add(hc);
			} else {
				reguList.add(hc);
			}
		}
		
		// used to keep track if a veto was called.
		// if so, the regular list won't be processed.
		boolean vetoCalled = false;
		
		// submit the veto calls to the executor service
		try {
			for (Future<Boolean> f : executorService.invokeAll(vetoList)) {
				if (f.get().booleanValue()) {
					vetoCalled = true;
				}
			}			
		} catch (Exception e) {
			// this only happens if the executorService is interrupted,
			// and by default, that shouldn't really ever happen.
			// or, if the callable sneaks out an exception, which again
			// shouldn't happen.
			vetoCalled = true;
			e.printStackTrace();
		}
		
		// VetoEvents cannot be vetoed, sorry. :)
		if (vetoCalled && evt instanceof VetoEvent) {
			vetoCalled = false;
		}
		
		// simply return if a veto has occured
		if (vetoCalled) {
			return;
		}
		
		// ExecutorService.invokeAll() blocks until all the results are computed.
		// for the regular handlers, we need to check if the waitForHandlers
		// property is true.  Otherwise (by default) we don't want invokeAll()
		// to block.  We don't care about the results, because no vetoes are
		// accounted for here and exceptions really shouldn't be thrown.
		if (waitForHandlers) {
			try {
				executorService.invokeAll(reguList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						executorService.invokeAll(reguList);
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			});
		}
		
		
	}	
		

	// the background thread consumer, simply extracts
	// any events from the queue and publishes them.
	private class EventQueueRunner implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					notifySubscribers(queue.take());
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	
	// consumer runnable to remove handler infos from the subscription list
	// if they are null.  this is if the GC has collected them.
	private class KillQueueRunner implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					HandlerInfo info = killQueue.take();
					if (info.getSubscriber() == null) {
						handlers.remove(info);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	
	// used to hold the subscriber details
	private static class HandlerInfo {
		private final Class<?> eventClass;
		private final Method method;
		private final WeakReference<?> subscriber;
		private final boolean vetoHandler;

		public HandlerInfo(Class<?> eventClass, Method method, Object subscriber, boolean vetoHandler) {
			this.eventClass = eventClass;
			this.method = method;
			this.subscriber = new WeakReference<Object>(subscriber);
			this.vetoHandler = vetoHandler;
		}

		public boolean matchesEvent(Object event) {
			return event.getClass().equals(eventClass);
		}

		public Method getMethod() {
			return method;
		}

		public Object getSubscriber() {
			return subscriber.get();
		}

		public boolean isVetoHandler() {
			return vetoHandler;
		}
				
	}
	
	// callable used to actually invoke the task
	// it eats any exception thrown and publishes an event back onto the bus
	private class HandlerInfoCallable implements Callable<Boolean> {
		private final HandlerInfo handlerInfo;
		private final Object event;
		
		public HandlerInfoCallable(HandlerInfo handlerInfo, Object event) {
			this.handlerInfo = handlerInfo;
			this.event = event;
		}
		
		/**
		 * Invokes the HandlerInfo's callback handler method.  If any exeptions
		 * are thrown, besides a VetoException, a {@link BusExceptionEvent} will
		 * be published to the bus with the root cause of the problem.  If a
		 * {@link VetoException} is thrown from the invoked method, a {@link VetoEvent}
		 * will be published to the bus and the call will return true.
		 * <p>
		 * The call has been modified to not throw any Exceptions.  It will not,
		 * unlike the interface defintion, throw an exception.  All exceptions are
		 * handled locally.
		 * 
		 * @return True if the invoke was vetoed, false otherwise.
		 */
		@Override
		public Boolean call() {
			try {
				Object subscriber = handlerInfo.getSubscriber();
				if (subscriber == null) {
					killQueue.put(handlerInfo);
					return false;
				}
				
				handlerInfo.getMethod().invoke(subscriber, event);
				return false;
				
			} catch (Exception e) {
				Throwable cause = e;
				
				// find the root cause
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}
				
				if (cause instanceof VetoException) {
					publish(new VetoEvent(event));
					return true;
				}
				
				publish(new BusExceptionEvent(handlerInfo, cause));
				
				// TODO It will be nice to do something more useful than just printStackTrace.
				// probably do some logging or something.  Or, maybe just the BusExceptionEvent
				// is good enough.
				cause.printStackTrace();
				return false;
			}
		}
	}
	

}
