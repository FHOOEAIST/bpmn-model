/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.bpmn.model;

import lombok.experimental.UtilityClass;

import javax.xml.namespace.QName;

/**
 * <p>Helper class for BPMN related stuff</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@UtilityClass
public class BPMNHelper {
    public final String BPMN_MODEL_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    /**
     * Creates a Qname in the BPMN namespace
     *
     * @param localPart the local part of the qname
     * @return the resulting qname element with namespace = bpmn and localpart = localPart parameter
     */
    public QName createQName(String localPart) {
        return new QName(BPMN_MODEL_NAMESPACE, localPart);
    }
}
