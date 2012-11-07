package org.openmrs.module.chits;

import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;

/**
 * Dummy form template serializer: needed as a placeholder when adding entities to the serialized_object table.
 */
public class FormTemplateSerializer implements OpenmrsSerializer {
	@Override
	public String serialize(Object o) throws SerializationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T deserialize(String serializedObject, Class<? extends T> clazz) throws SerializationException {
		throw new UnsupportedOperationException();
	}
}
