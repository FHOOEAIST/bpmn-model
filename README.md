# BPMN Model

BPMN model provides autogenerated BPMN domain classes. These classes are generated using the
XML schema definition files and jax-b.

## Getting Started

To use the classes, simply include the maven dependency on the project.

```xml
<dependency>
    <groupId>science.aist</groupId>
    <artifactId>bpmn-model</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope> <!-- Note: this is default -->
</dependency>
```

Then you are able to load BPMN files the following way: 

```java
XMLRepository<TDefinitions> repository = new BPMNTDefinitionsRepository();

// load 
JAXBElement<TDefinitions> tDefinition = repository.load(inputStream);

// save
repository.save(tDefinition, outputStream);
```

### Building the BPMN model yourself

If you want to build the project yourself, just checkout this git repo, make sure you have jdk 11 or above installed as
well as maven 3.6.0 or above and build the project by running the maven command: `mvn package`. This results in a 
jar-file inside the target folder, which can be used as a dependency in other projects.

## FAQ

If you have any questions, please checkout our [FAQ](https://fhooeaist.github.io/bpmn-model/faq.html) section.

## Contributing

**First make sure to read our [general contribution guidelines](https://fhooeaist.github.io/CONTRIBUTING.html).**
   
## Licence

Copyright (c) 2020 the original author or authors.
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES.

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at https://mozilla.org/MPL/2.0/.

## Research

If you are going to use this project as part of a research paper, we would ask you to reference this project by citing
it. DOI: TBD