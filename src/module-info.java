module edu.damago.secondhand.model {
	requires transitive java.logging;
	requires transitive javax.annotation.api;
	requires transitive java.validation;
	requires transitive java.json.bind;
	
	requires transitive javax.persistence;
	requires transitive eclipselink.minus.jpa;
	requires transitive java.ws.rs;

	exports edu.damago.secondhand.persistence;
	exports edu.damago.secondhand.service;
	exports edu.damago.secondhand.util;

	opens edu.damago.secondhand.persistence;
}