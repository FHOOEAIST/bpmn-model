/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.bpmn.model;

import org.omg.spec.bpmn.model.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import science.aist.bpmn.model.impl.BPMNTDefinitionsRepository;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiPredicate;

import static science.aist.bpmn.model.BPMNHelper.createQName;

/**
 * <p>Tests the autogenerated bpmn classes if they work as expected</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class TestBpmn {


    private final XMLRepository<TDefinitions> repository = new BPMNTDefinitionsRepository();

    @Test
    public void testCreateBPMN() {
        // given
        ObjectFactory objectFactory = new ObjectFactory();
        TDefinitions definitions = objectFactory.createTDefinitions();
        TProcess process = objectFactory.createTProcess();

        TStartEvent startEvent = objectFactory.createTStartEvent();
        startEvent.setId("start");
        startEvent.setName("start");

        TEndEvent endEvent = objectFactory.createTEndEvent();
        endEvent.setId("end");
        endEvent.setName("end");

        TUserTask task1 = objectFactory.createTUserTask();
        task1.setId("t1");
        task1.setName("Task 1");

        TUserTask task2 = objectFactory.createTUserTask();
        task2.setId("t2");
        task2.setName("Task 2");

        TSequenceFlow sequenceFlowStartTask1 = objectFactory.createTSequenceFlow();
        sequenceFlowStartTask1.setId("sf_start_t1");
        sequenceFlowStartTask1.setSourceRef(startEvent);
        sequenceFlowStartTask1.setTargetRef(task1);

        TSequenceFlow sequenceFlowTask1Task2 = objectFactory.createTSequenceFlow();
        sequenceFlowTask1Task2.setId("sf_t1_t2");
        sequenceFlowTask1Task2.setSourceRef(task1);
        sequenceFlowTask1Task2.setTargetRef(task2);

        TSequenceFlow sequenceFlowTask3End = objectFactory.createTSequenceFlow();
        sequenceFlowTask3End.setId("sf_t2_end");
        sequenceFlowTask3End.setSourceRef(task2);
        sequenceFlowTask3End.setTargetRef(endEvent);

        startEvent.getOutgoing().add(createQName(sequenceFlowStartTask1.getId()));
        task1.getIncoming().add(createQName(sequenceFlowStartTask1.getId()));
        task1.getOutgoing().add(createQName(sequenceFlowTask1Task2.getId()));
        task2.getIncoming().add(createQName(sequenceFlowTask1Task2.getId()));
        task2.getOutgoing().add(createQName(sequenceFlowTask3End.getId()));
        endEvent.getIncoming().add(createQName(sequenceFlowTask3End.getId()));

        process.getFlowElement().add(objectFactory.createStartEvent(startEvent));
        process.getFlowElement().add(objectFactory.createUserTask(task1));
        process.getFlowElement().add(objectFactory.createUserTask(task2));
        process.getFlowElement().add(objectFactory.createEndEvent(endEvent));
        process.getFlowElement().add(objectFactory.createSequenceFlow(sequenceFlowStartTask1));
        process.getFlowElement().add(objectFactory.createSequenceFlow(sequenceFlowTask1Task2));
        process.getFlowElement().add(objectFactory.createSequenceFlow(sequenceFlowTask3End));

        definitions.getRootElement().add(objectFactory.createProcess(process));

        JAXBElement<TDefinitions> definitionsJaxB = objectFactory.createDefinitions(definitions);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // when
        repository.save(definitionsJaxB, baos);

        // then
        String res = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        Document doc = createDocumentFromXmlString(res);
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions)"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process)"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/startEvent[@name='start' and @id='start'])"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/startEvent[@name='start' and @id='start']/outgoing = 'sf_start_t1')"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/userTask[@name='Task 1' and @id='t1'])"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/userTask[@name='Task 1' and @id='t1']/incoming = 'sf_start_t1')"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/userTask[@name='Task 1' and @id='t1']/outgoing = 'sf_t1_t2')"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/userTask[@name='Task 2' and @id='t2'])"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/userTask[@name='Task 2' and @id='t2']/incoming = 'sf_t1_t2')"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/userTask[@name='Task 2' and @id='t2']/outgoing = 'sf_t2_end')"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/endEvent[@name='end' and @id='end'])"));
        Assert.assertTrue(booleanXpath.test(doc, "boolean(/definitions/process/endEvent[@name='end' and @id='end']/incoming = 'sf_t2_end')"));
    }

    @Test
    public void testLoadBPMN() {
        // given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:ns2=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:ns3=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:ns4=\"http://www.omg.org/spec/DD/20100524/DC\">\n" +
                "    <process>\n" +
                "        <startEvent name=\"start\" id=\"start\">\n" +
                "            <outgoing>sf_start_t1</outgoing>\n" +
                "        </startEvent>\n" +
                "        <userTask name=\"Task 1\" id=\"t1\">\n" +
                "            <incoming>sf_start_t1</incoming>\n" +
                "            <outgoing>sf_t1_t2</outgoing>\n" +
                "        </userTask>\n" +
                "        <userTask name=\"Task 2\" id=\"t2\">\n" +
                "            <incoming>sf_t1_t2</incoming>\n" +
                "            <outgoing>sf_t2_end</outgoing>\n" +
                "        </userTask>\n" +
                "        <endEvent name=\"end\" id=\"end\">\n" +
                "            <incoming>sf_t2_end</incoming>\n" +
                "        </endEvent>\n" +
                "        <sequenceFlow sourceRef=\"start\" targetRef=\"t1\" id=\"sf_start_t1\"/>\n" +
                "        <sequenceFlow sourceRef=\"t1\" targetRef=\"t2\" id=\"sf_t1_t2\"/>\n" +
                "        <sequenceFlow sourceRef=\"t2\" targetRef=\"end\" id=\"sf_t2_end\"/>\n" +
                "    </process>\n" +
                "</definitions>";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes());

        // when
        JAXBElement<TDefinitions> load = repository.load(byteArrayInputStream);

        // then
        Assert.assertNotNull(load);
        TDefinitions definitions = load.getValue();
        Assert.assertNotNull(definitions);
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();
        Assert.assertNotNull(process);
        Optional<TStartEvent> first = process.getFlowElement().stream().map(JAXBElement::getValue).filter(e -> e instanceof TStartEvent).map(e -> (TStartEvent) e).findFirst();
        Assert.assertTrue(first.isPresent());
        Assert.assertEquals(first.get().getId(), "start");
        Assert.assertEquals(first.get().getName(), "start");
        Optional<QName> startFirstIncomming = first.get().getOutgoing().stream().findFirst();
        Assert.assertTrue(startFirstIncomming.isPresent());
        Assert.assertEquals(startFirstIncomming.get().getLocalPart(), "sf_start_t1");
        // We do not want to test the saxparser, so if the start node is working we can assume that the others are as well.
        Assert.assertEquals(process.getFlowElement().stream().map(JAXBElement::getValue).filter(e -> e instanceof TUserTask).count(), 2);
        Assert.assertEquals(process.getFlowElement().stream().map(JAXBElement::getValue).filter(e -> e instanceof TSequenceFlow).count(), 3);
        Assert.assertEquals(process.getFlowElement().stream().map(JAXBElement::getValue).filter(e -> e instanceof TEndEvent).count(), 1);
    }

    private static Document createDocumentFromXmlString(String xmlString) {
        try {
            InputSource source = new InputSource(new StringReader(xmlString));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(source);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    private static final BiPredicate<Document, String> booleanXpath = (document, xPathString) -> {
        try {
            String result = xpathFactory.newXPath().evaluate(xPathString, document);
            return (result != null && !result.isEmpty()) && Boolean.parseBoolean(result); // condition
        } catch (Exception e) {
            return false;
        }
    };
}

