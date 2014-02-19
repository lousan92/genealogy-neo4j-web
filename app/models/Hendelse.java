package models;

import com.google.common.collect.ImmutableMap;
import org.neo4j.graphdb.Node;

import java.util.Map;

public class Hendelse {

    public String type;
    public String dato;
    public String beskrivelse;
    public String[] notater;
    public String sted;

    public Hendelse(Node node) {
        type = node.getProperty("type").toString();
        dato = (String) node.getProperty("dato", null);
        beskrivelse = (String) node.getProperty("beskrivelse", null);
        notater = (String[]) node.getProperty("notater", null);
    }
}
