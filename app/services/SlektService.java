package services;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import models.Familie;
import models.Hendelse;
import models.Person;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.GraphDbRepository;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class SlektService {

    private static final Logger LOG = LoggerFactory.getLogger(SlektService.class);

    private static final String CYPHER_FORELDRE = "MATCH (:Person {id:{ID}})-[:FAR|:MOR*%d]->(personer) RETURN personer";
    private static final String CYPHER_FAMILIER = "MATCH (:Person {id:{ID}})-[:EKTEMANN|:HUSTRU]-(familier:Familie)-[r]->(personer:Person) RETURN familier, type(r) AS rolle, personer";
    private static final String CYPHER_BARN = "MATCH (:Person {id:{ID}})-[:EKTEMANN|:HUSTRU]-()-[:BARN]-(personer) RETURN personer";
    private static final String CYPHER_SOSKEN = "MATCH (:Person {id:{ID}})-[:BARN*2]-(personer) RETURN personer";
    private static final String CYPHER_MENNINGER = "MATCH (:Person {id:{ID}})-[:FAR|:MOR*%d]->()-[:BARN*2]-()<-[:FAR|:MOR*%d]-(personer) RETURN personer";

    @Autowired
    private GraphDbRepository repository;

    public Person getPerson(String id) {
        Person person = null;

        try (Transaction tx = repository.getDb().beginTx()) {
            Iterator<Node> result = repository.getDb().findNodesByLabelAndProperty(GraphDbRepository.LBL_PERSON, "id", id).iterator();
            if (result.hasNext()) {
                Node n = result.next();
                person = new Person(n);
                person.hendelser = getHendelser(n, PersonRelasjoner.HENDELSE);
            }
            tx.success();
        }
        return person;
    }

    public Collection<Familie> getFamilier(String id) {
        Map<String, Familie> familier = Maps.newHashMap();

        try (Transaction tx = repository.getDb().beginTx()) {
            ExecutionResult result = repository.getEngine().execute(CYPHER_FAMILIER, ImmutableMap.of("ID", (Object) id));
            ResourceIterator<Map<String, Object>> rs = result.iterator();
            while (rs.hasNext()) {
                Map<String, Object> row = rs.next();
                String fid = row.get("familier").toString();
                Familie familie = familier.get(fid);
                if (familie == null) {
                    familie = new Familie((Node) row.get("familier"));
                    familier.put(fid, familie);
                    familie.hendelser = getHendelser((Node) row.get("familier"), FamilieRelasjoner.HENDELSE);
                }
                Person person = new Person((Node) row.get("personer"));
                if (row.get("rolle").equals("BARN")) {
                    familie.barn.add(person);
                } else {
                    familie.ektefelle = person;
                }
            }
            tx.success();
        }
        return familier.values();
    }

    public List<Hendelse> getHendelser(Node root, RelationshipType relasjonstype) {
        List<Hendelse> hendelser = Lists.newArrayList();

        Iterator<Node> result = repository
                .getDb()
                .traversalDescription()
                .depthFirst()
                .evaluator(Evaluators.toDepth(2))
                .expand(PathExpanders.forTypesAndDirections(
                        relasjonstype, Direction.OUTGOING,
                        HendelseRelasjoner.STED, Direction.OUTGOING))
                .traverse(root)
                .nodes().iterator();

        Hendelse hendelse = null;
        while (result.hasNext()) {
            Node node = result.next();

            if (node.hasLabel(GraphDbRepository.LBL_HENDELSE)) {
                hendelse = new Hendelse(node);
                hendelser.add(hendelse);
            } else if (node.hasLabel(GraphDbRepository.LBL_STED)) {
                hendelse.sted = finnKomplettStedsnavn(node);
            }
        }

        return hendelser;
    }

    private String finnKomplettStedsnavn(Node root) {
        return Joiner.on(", ").join(Iterables.transform(
                repository
                        .getDb()
                        .traversalDescription()
                        .expand(PathExpanders.forTypeAndDirection(StedRelasjoner.PLASSERING, Direction.OUTGOING))
                        .traverse(root)
                        .nodes(),
                stringPropertyMapper("navn")));
    }

    public List<Person> getSosken(String id) {
        return executePersonerQuery(id, CYPHER_SOSKEN);
    }

    public List<Person> getForeldre(String id, Integer nivaa) {
        String cypher = String.format(CYPHER_FORELDRE, nivaa, nivaa);
        return executePersonerQuery(id, cypher);
    }

    public List<Person> getBarn(String id) {
        return executePersonerQuery(id, CYPHER_BARN);
    }

    public List<Person> getMenninger(String id, Integer nivaa) {
        String cypher = String.format(CYPHER_MENNINGER, nivaa - 1, nivaa - 1);
        return executePersonerQuery(id, cypher);
    }

    private List<Person> executePersonerQuery(String id, String cypher) {
        List<Person> personer = Lists.newArrayList();

        try (Transaction tx = repository.getDb().beginTx()) {
            ExecutionResult result = repository.getEngine().execute(cypher, ImmutableMap.of("ID", (Object) id));
            Iterator<Node> personer_column = result.columnAs("personer");
            for (Node node : Lists.newArrayList(IteratorUtil.asIterable(personer_column))) {
                personer.add(new Person(node));
            }
            tx.success();
        }
        return personer;
    }

    private static enum FamilieRelasjoner implements RelationshipType {
        HUSTRU, EKTEMANN, BARN, HENDELSE
    }

    private static enum PersonRelasjoner implements RelationshipType {
        HENDELSE, SITAT, NAVNESITAT, MOR, FAR
    }

    private static enum HendelseRelasjoner implements RelationshipType {
        STED, SITAT
    }

    private static enum StedRelasjoner implements RelationshipType {
        PLASSERING
    }

    private static final Function<Node, String> stringPropertyMapper(final String propertyName) {
        return new Function<Node, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Node node) {
                return node == null ? "" : node.getProperty(propertyName).toString();
            }
        };
    }
}
