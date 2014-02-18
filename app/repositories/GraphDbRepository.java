package repositories;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@Scope("singleton")
public class GraphDbRepository {

    private static final Logger LOG = LoggerFactory.getLogger(GraphDbRepository.class);

    public static final Label LBL_PERSON = DynamicLabel.label("Person");
    public static final Label LBL_FAMILY = DynamicLabel.label("Familie");
    public static final Label LBL_HENDELSE = DynamicLabel.label("Hendelse");
    public static final Label LBL_STED = DynamicLabel.label("Sted");
    public static final Label LBL_KILDE = DynamicLabel.label("Kilde");

    private GraphDatabaseService graphDb;
    private ExecutionEngine engine;

    @PostConstruct
    public void init() {
        LOG.info("Connecting to database...");
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/cygwin/home/oystein/projects/genealogy/neo4j-test");
        registerShutdownHook(graphDb);
    }

    public GraphDatabaseService getDb() {
        return graphDb;
    }

    public ExecutionEngine getEngine() {
        if ( engine == null) {
            LOG.info("Creating execution engine...");
            engine = new ExecutionEngine(getDb());
        }

        return engine;
    }

    private void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Neo4j shutdown...");
                graphDb.shutdown();
            }
        });
    }
}
