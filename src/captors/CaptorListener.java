package captors;

import java.util.EventListener;

public interface CaptorListener extends EventListener{
	default void captorValueChanged(CaptorSocket c) {};
	default void captorDisconnected(CaptorSocket c) {};
	default void captorConnected(CaptorSocket c) {};
	default void captorMinMaxChanged(Captor c) {};
}