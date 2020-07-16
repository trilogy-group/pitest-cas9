package org.pitest.mutationtest.engine.cas9.example.integration;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(FIELD)
class Mutations {

  @XmlElement(name = "mutation")
  private List<Mutation> mutations;

  static Map<String, Map<String, Long>> loadFromXml(File file) throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(Mutations.class);
    final Mutations report = (Mutations) context
        .createUnmarshaller()
        .unmarshal(file);

    return report.mutations.stream()
        .collect(groupingBy(
            Mutation::getOperator,
            groupingBy(Mutation::getStatus, counting())));
  }
}
