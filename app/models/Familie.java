package models;

import com.google.common.collect.Lists;
import org.neo4j.graphdb.Node;

import java.util.List;

public class Familie {
    public String id;
    public Person ektefelle;
    public List<Person> barn = Lists.newArrayList();
    public List<Hendelse> hendelser;

    public Familie(Node node) {
        id = node.getProperty("id").toString();
    }
}
