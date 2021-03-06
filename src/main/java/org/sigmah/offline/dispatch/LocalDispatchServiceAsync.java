package org.sigmah.offline.dispatch;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.security.SecureDispatchAsync;
import org.sigmah.client.security.SecureDispatchServiceAsync;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Result;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * Dispatches commands to local handlers
 */
@Singleton
public class LocalDispatchServiceAsync implements SecureDispatchServiceAsync {
	
	public static final String LAST_USER_ITEM = "sigmah.last-user";
	
	/**
	 * Map associating a command type to its offline handler.
	 */
    private final Map<Class, AsyncCommandHandler> registry;
	/**
	 * Provides information about the current user.
	 */
	private final AuthenticationProvider authenticationProvider;
    
    public LocalDispatchServiceAsync(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
		this.registry = new HashMap<Class, AsyncCommandHandler>();
    }

    public <C extends Command<R>, R extends Result> void registerHandler(Class<C> commandClass, AsyncCommandHandler<C,R> handler) {
        registry.put(commandClass, handler);
    }

	@Override
	public <C extends Command<R>, R extends Result> void execute(SecureDispatchAsync.CommandExecution<C, R> commandExecution, AsyncCallback<Result> callback) {
		final C command = commandExecution.getCommand();
		Log.info("Local " + command.getClass().getName() + " is pending...");
		
		final AsyncCommandHandler handler = getHandler(command);
		
		if(handler == null) {
			callback.onFailure(new UnavailableCommandException("No handler is registered for command '" + command.getClass().getName() + "'."));
			
		} else {
			final Authentication authentication = authenticationProvider.get();
			
			if(authentication.getUserEmail() == null) {
				// Search the last logged user in the users database
				final Storage storage = Storage.getLocalStorageIfSupported();
				final String email = storage.getItem(LAST_USER_ITEM);
				
				authentication.setUserEmail(email);
			}
			
			final OfflineExecutionContext executionContext = new OfflineExecutionContext(authentication);
			
			handler.execute(command, executionContext, callback);
		}
	}
    
    private <C extends Command<R>, R extends Result> AsyncCommandHandler<C, R> getHandler(C c) {
        final AsyncCommandHandler<C, R> handler = registry.get(c.getClass());
        return handler;
    }
}
