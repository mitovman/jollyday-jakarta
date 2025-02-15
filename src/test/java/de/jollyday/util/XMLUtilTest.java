/**
 * Copyright 2012 Sven Diedrichsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package de.jollyday.util;

import de.jollyday.config.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class XMLUtilTest {

	@Mock
	XMLUtil.JAXBContextCreator contextCreator;
	@Mock
	InputStream inputStream;

	@InjectMocks
	XMLUtil xmlUtil = new XMLUtil();

	@Test
	public void testUnmarshallConfigurationNullCheck() {
		assertThrows(IllegalArgumentException.class, () -> xmlUtil.unmarshallConfiguration(null));
	}

	@Test
	public void testUnmarshallConfigurationException() throws IOException, JAXBException {
		when(contextCreator.create(eq(XMLUtil.PACKAGE), any(ClassLoader.class))).thenThrow(new JAXBException(""))
				.thenThrow(new JAXBException(""));
		assertThrows(IllegalStateException.class, () -> xmlUtil.unmarshallConfiguration(inputStream));
		verify(inputStream, never()).close();
	}

	@Test
	public void testUnmarshallConfiguration() throws IOException, JAXBException {
		JAXBContext ctx = mock(JAXBContext.class);
		Unmarshaller unmarshaller = mock(Unmarshaller.class);
		@SuppressWarnings("unchecked")
		JAXBElement<Configuration> element = mock(JAXBElement.class);
		when(contextCreator.create(eq(XMLUtil.PACKAGE), any(ClassLoader.class))).thenReturn(null).thenReturn(ctx);
		when(ctx.createUnmarshaller()).thenReturn(unmarshaller);
		when(unmarshaller.unmarshal(inputStream)).thenReturn(element);
		xmlUtil.unmarshallConfiguration(inputStream);
		verify(element).getValue();
	}
}
