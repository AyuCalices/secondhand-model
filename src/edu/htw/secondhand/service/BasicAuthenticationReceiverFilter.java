package edu.htw.secondhand.service;

import edu.htw.secondhand.persistence.Person;
import edu.htw.secondhand.util.HashCodes;
import edu.htw.secondhand.util.RestJpaLifecycleProvider;

import javax.annotation.Priority;
import javax.persistence.EntityManager;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


/**
 * JAX-RS filter provider that performs HTTP "Basic" authentication on any REST service request
 * within an HTTP server environment. This aspect-oriented design swaps "Authorization" headers
 * for "X-Requester-Identity" headers within any REST service request being received.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthenticationReceiverFilter implements ContainerRequestFilter {

	/**
	 * HTTP request header for the authenticated requester's identity.
	 */
	static public final String REQUESTER_IDENTITY = "X-Requester-Identity";


	/**
	 * Performs HTTP "basic" authentication by calculating a password hash from the password contained in the request's
	 * "Authorization" header, and comparing it to the one stored in the person matching said header's username. The
	 * "Authorization" header is consumed in any case, and upon success replaced by a new "Requester-Identity" header that
	 * contains the authenticated person's identity. The filter chain is aborted in case of a problem.
	 * @param requestContext the request context
	 * @throws NullPointerException if the given argument is {@code null}
	 */
	public void filter (final ContainerRequestContext requestContext) throws NullPointerException {
		@SuppressWarnings("unused")
		final MultivaluedMap<String,String> headers = requestContext.getHeaders();

		// TODO:
		// - Throw a ClientErrorException(Status.BAD_REQUEST) if the given context's headers map already contains a
		//   "Requester-Identity" key, in order to prevent spoofing attacks.
		// - Remove the "Authorization" header from said map and store the first of it's values in a variable
		//   "textCredentials", or null if the header value is either null or empty.
		// - if the "textCredentials" variable is not null, parse it either programmatically using the decoder of
		//   class java.util.Base64.
		// - Perform the PQL-Query "select p from Person as p where p.email = :email"), using the name of
		//   the parsed credentials as email address. Note that this query will go to the second level cache
		//   before hitting the database if the Person#email field is annotated using @CacheIndex! 
		// - if the resulting people list contains exactly one element, calculate the hex-string representation
		//   (i.e. 2 digits per byte) of the SHA2-256 hash code of the credential's password either programmatically,
		//   or using HashCodes.sha2HashText(256, text).
		// - if this hash representation is equal to queried person's password hash, add a new "Requester-Identity"
		//   header to the request headers, using the person's identity (converted to String) as value, and return
		//   from this method.
		// - in all other cases, abort the request using requestContext.abortWith() in order to challenging the client
		//   to provide HTTP Basic credentials (i.e. status code 401, and "WWW-Authenticate" header value "Basic").
		//   Note that the alternative of throwing NotAuthorizedException("Basic") comes with the disadvantage that
		//   failed authentication attempts clutter the server log with stack traces.
		if (headers.containsKey(REQUESTER_IDENTITY)) {
			throw new ClientErrorException(Status.BAD_REQUEST);
		}
		final String textCredentials = headers.remove("Authorization").get(0);
		if (textCredentials != null) {
			String encodedCredentials = textCredentials.substring("Basic".length()).trim();
			String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
			final EntityManager entityManager = RestJpaLifecycleProvider.entityManager("secondhand");

			final List<Person> foundPersons = entityManager
					.createQuery("SELECT p FROM Person AS p WHERE p.email = :email", Person.class)
					.setParameter("email", decodedCredentials.split(":")[0])
					.getResultList()
					.stream()
					.collect(Collectors.toList());
			if (foundPersons.size() == 1) {
				int indexOfFirstColon = decodedCredentials.indexOf(":");
				String password = decodedCredentials.substring(indexOfFirstColon + 1);
				String passwordHexRepresentation = HashCodes.sha2HashText(256, password);
				if (passwordHexRepresentation.equals(foundPersons.get(0).getPasswordHash())) {
					headers.add(REQUESTER_IDENTITY, Long.toString(foundPersons.get(0).getIdentity()));
					return;
				}
			}
		}
		final Response response = Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic").build();
		requestContext.abortWith(response);
	}
}