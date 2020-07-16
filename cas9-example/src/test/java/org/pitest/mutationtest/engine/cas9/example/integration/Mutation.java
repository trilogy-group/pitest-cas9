package org.pitest.mutationtest.engine.cas9.example.integration;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(FIELD)
class Mutation {

  @XmlAttribute
  private String status;

  @XmlElement
  private String mutator;

  @XmlTransient
  String getOperator() {
    final int beginIndex = mutator.lastIndexOf('.') + 1;
    return mutator.substring(beginIndex, beginIndex + 3);
  }

  public String getStatus() {
    return status;
  }
}
