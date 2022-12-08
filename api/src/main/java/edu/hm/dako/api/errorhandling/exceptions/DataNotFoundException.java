package edu.hm.dako.api.errorhandling.exceptions;

/**
 * exception that will be thrown if id doesn't exist -> will lead to http 404
 *
 * @author Linus Englert
 */
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(Class<?> data, Long id) {
        super("Could not find " + data.getSimpleName() + " " + id + "!");
    }
}