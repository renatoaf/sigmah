package org.sigmah.client.cache;

import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.organization.OrganizationDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Stores data widely used on client-side for the current user.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @deprecated [TO DELETE] use the command each time it's necessary.
 */
@Singleton
@Deprecated
public class UserLocalCache {

	/**
	 * The dispatcher.
	 */
	private final DispatchAsync dispatch;

	/**
	 * The authentication provider.
	 */
	private final AuthenticationProvider authenticationProvider;

	/**
	 * Cache of the countries.
	 */
	private LocalCachedCollection<CountryDTO> countries;

	/**
	 * Cache of the users (for the current organization only).
	 */
	private LocalCachedCollection<UserDTO> users;

	/**
	 * Cache of the organization.
	 */
	private LocalCachedOrganization organization;

	/**
	 * Flag set to {@code true} once local client-side cache has been initialized.
	 */
	private boolean initialized;

	@Inject
	public UserLocalCache(final DispatchAsync dispatch, final AuthenticationProvider authenticationProvider) {
		this.dispatch = dispatch;
		this.authenticationProvider = authenticationProvider;
		this.countries = new LocalCachedCollection<CountryDTO>();
		this.users = new LocalCachedCollection<UserDTO>();
		this.organization = new LocalCachedOrganization();
	}

	/**
	 * Gets the cache of the countries.
	 * 
	 * @return The cache of the countries.
	 */
	public LocalCachedCollection<CountryDTO> getCountryCache() {
		return countries;
	}

	/**
	 * Gets the cache of the current organization members.
	 * 
	 * @return The cache of the current organization members.
	 */
	public LocalCachedCollection<UserDTO> getUserCache() {
		return users;
	}

	/**
	 * Gets the cache of the current organization.
	 * 
	 * @return The cache of the current organization.
	 */
	public LocalCachedOrganization getOrganizationCache() {
		return organization;
	}

	/**
	 * Initializes the local cache.<br>
	 * Does nothing if executed more than once.
	 */
	public void init() {

		if (authenticationProvider.isAnonymous()) {

			if (Log.isDebugEnabled()) {
				Log.debug("[init] Anonymous user ; clearing local cache data.");
			}

			countries.set(null);
			users.set(null);
			organization.set(null, null);

			initialized = false;
			return;
		}

		if (initialized) {
			if (Log.isDebugEnabled()) {
				Log.debug("[init] Local cache has already been initialized ; aborting initialization command.");
			}
			return;
		}

		if (Log.isDebugEnabled()) {
			Log.debug("[init] Initializes local cache.");
		}

		// Gets countries list.
		dispatch.execute(new GetCountries(), new CommandResultHandler<ListResult<CountryDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				Log.error("[init] Error while getting the countries list for the local cache.", e);
				countries.set(null);
			}

			@Override
			public void onCommandSuccess(final ListResult<CountryDTO> result) {

				final List<CountryDTO> list = result.getData();
				countries.set(list);

				if (Log.isDebugEnabled()) {
					Log.debug("[init] The local cache of the countries has been set (" + list.size() + " countries cached).");
				}
			}
		});

		// Gets users list.
		dispatch.execute(new GetUsersByOrganization(authenticationProvider.get().getOrganizationId(), null), new CommandResultHandler<ListResult<UserDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				Log.error("[init] Error while getting the users list for the local cache.", e);
				users.set(null);
			}

			@Override
			public void onCommandSuccess(final ListResult<UserDTO> result) {

				final List<UserDTO> list = result.getList();
				users.set(list);

				if (Log.isDebugEnabled()) {
					Log.debug("[init] The cache of the users has been set (" + list.size() + " users cached).");
				}
			}
		});

		// Gets the organization.
		refreshOrganization(null);

		initialized = true;
	}

	/**
	 * Refreshes the cached {@code OrganizationDTO} and executes the given {@code callback} once refresh process is
	 * complete.
	 * 
	 * @param callback
	 *          If not {@code null}, the callback is executed once {@code OrganizationDTO} has been loaded.
	 */
	public void refreshOrganization(final AsyncCallback<OrganizationDTO> callback) {

		final Integer organizationId = authenticationProvider.get().getOrganizationId();
		final Integer orgUnitId = authenticationProvider.get().getOrgUnitId();

		// Gets the organization.
		dispatch.execute(new GetOrganization(OrganizationDTO.Mode.WITH_ROOT, organizationId), new CommandResultHandler<OrganizationDTO>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				Log.error("[init] Error while getting the organization for the local cache.", e);
				if (callback != null) {
					callback.onFailure(e);
				}
			}

			@Override
			public void onCommandSuccess(final OrganizationDTO result) {

				organization.set(result, orgUnitId);

				if (Log.isDebugEnabled()) {
					Log.debug("[init] The cache of the organization has been set.");
				}

				if (callback != null) {
					callback.onSuccess(result);
				}
			}
		});
	}

}
