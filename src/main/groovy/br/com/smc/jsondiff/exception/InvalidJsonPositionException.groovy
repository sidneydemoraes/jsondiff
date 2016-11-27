package br.com.smc.jsondiff.exception

/**
 * Created by Sidney de Moraes on 27/11/16.
 */
class InvalidJsonPositionException extends RuntimeException {

	public InvalidJsonPositionException(Exception e) {
		super(e)
	}
}
