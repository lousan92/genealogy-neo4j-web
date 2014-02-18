package models;


import org.neo4j.graphdb.Node;

import java.util.List;

public class Person {
    public String id;
    public String navn;
    public Kjonn kjonn;
    public List<Hendelse> hendelser;

    public Person(Node node) {
        id = node.getProperty("id").toString();
        navn = ((String[])node.getProperty("navn"))[0];
        kjonn = node.getProperty("kjonn").equals("M") ? Kjonn.MANN : Kjonn.KVINNE;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Person ").append(id).append(' ').append(navn).append(" (").append(kjonn).append(")");
        return sb.toString();
    }

    public enum Kjonn {
        MANN, KVINNE
    }
}
