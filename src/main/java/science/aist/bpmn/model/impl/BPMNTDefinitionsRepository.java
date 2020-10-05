/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.bpmn.model.impl;

import lombok.SneakyThrows;
import org.omg.spec.bpmn.model.TDefinitions;
import science.aist.bpmn.model.XMLRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Repository for {@link TDefinitions}</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class BPMNTDefinitionsRepository implements XMLRepository<TDefinitions> {
    private final JAXBContext jaxbContext;

    @SneakyThrows
    public BPMNTDefinitionsRepository() {
        jaxbContext = JAXBContext.newInstance(TDefinitions.class);
    }

    @SneakyThrows
    @Override
    public void save(JAXBElement<TDefinitions> jaxbElement, OutputStream os) {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(jaxbElement, os);
    }

    @SneakyThrows
    @Override
    public JAXBElement<TDefinitions> load(InputStream is) {
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(new StreamSource(is), TDefinitions.class);
    }
}
