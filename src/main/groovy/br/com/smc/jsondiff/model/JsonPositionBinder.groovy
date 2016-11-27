package br.com.smc.jsondiff.model

import br.com.smc.jsondiff.controller.DiffController
import br.com.smc.jsondiff.exception.InvalidJsonPositionException

import java.beans.PropertyEditorSupport

/**
 * Class that will bind a path variable from {@link DiffController#receiveJsonForDiff}
 * to {@link JsonPosition}
 */
class JsonPositionBinder extends PropertyEditorSupport {

	@Override
	public void setAsText(final String text) throws InvalidJsonPositionException {
		try {
			if (text) {
				final String capitalized = text.toUpperCase()
				final JsonPosition position = JsonPosition.valueOf(capitalized)
				setValue(position);
			} else {
				setValue(null);
			}
		} catch (IllegalArgumentException e) {
			throw new InvalidJsonPositionException(e)
		}
	}
}
