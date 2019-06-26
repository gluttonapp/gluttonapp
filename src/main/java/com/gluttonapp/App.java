package com.gluttonapp;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Scanner;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.both;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

public class App {
    public static void main(String[] args) {
        Cluster cluster = connectToDatabase();
        GraphTraversalSource g = getGraphTraversalSource(cluster);

        System.out.println("Using cluster connection: " + cluster.toString());
        System.out.println("Using traversal source: " + g.toString());

        displayMenu(g);

        cluster.close();
        System.exit(0);
    }

    public static void displayMenu(GraphTraversalSource g) {
        int option = -1;
        while (option != 0)
        {
            option = showMenu();
            switch (option) {
                case 0:
                    break;
                case 1:
                    //Get Vertex Count
                    System.out.println("Vertex count: " + getVertexCount(g));
                    break;
                case 2:
                    //Get Edge Count
                    System.out.println("Edge count: " + getEdgeCount(g));
                    break;
                case 3:
                    //Get Person
                    System.out.println("person Vertex: " + getPerson(g));
                    break;
                case 4:
                    //Add Person
                    System.out.println("new person Vertex: " + addPerson(g));
                    break;
                case 5:
                    //Update Person
                    System.out.println("updated person Vertex: " + updatePerson(g));
                    break;
                case 6:
                    //Delete Person
                    System.out.println("delete person count: " + deletePerson(g));
                    break;
                case 7:
                    //Add is_friends_with Edge
                    System.out.println("added is_friends_with Edge: " + addIsFriendsWithEdge(g));
                    break;
                case 8:
                    //Find Friends
                    System.out.println("friends found: \r\n" + getFriends(g));
                    break;
                case 9:
                    //Find Friends of Friends
                    System.out.println("friends of friends found: \r\n" + getFriendsOfFriends(g));
                    break;
                case 10:
                    //Find Path between Persons
                    System.out.println("Paths found: \r\n" + findPathBetweenPeople(g));
                    break;
                default:
                    System.out.println("Sorry, please enter valid Option");
            }
        }
        System.out.println("Exiting GluttonApp, Bye!");
    }

    public static int showMenu() {

        int option = -1;
        Scanner keyboard = new Scanner(System.in);
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("--------------");
        System.out.println("1) Get Count of the Vertices");
        System.out.println("2) Get Count of the Edges");
        System.out.println("3) Get person Vertex");
        System.out.println("4) Add new person Vertex");
        System.out.println("5) Update person Vertex");
        System.out.println("6) Delete person Vertex");
        System.out.println("7) Add is_friends_with Edge");
        System.out.println("8) Find friends of a person");
        System.out.println("9) Find friends of friends of a person");
        System.out.println("10) Find Paths between two people");
        System.out.println("0) Quit");
        System.out.println("--------------");
        System.out.println("Enter your choice:");
        option = keyboard.nextInt();

        return option;
    }

    public static Long getVertexCount(GraphTraversalSource g) {
        return g.V().count().next();
    }

    public static Long getEdgeCount(GraphTraversalSource g) {
        return g.E().count().next();
    }

    public static String getPerson(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the person to find:");
        String name = keyboard.nextLine();

        // returns of List of the properties
        List properties = g.V().
                has("person", "name", name).
                valueMap().toList();

        return properties.toString();
    }

    public static String addPerson(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the person to add: ");
        String name = keyboard.nextLine();

        // returns a Vertex type
        Vertex newVertex = g.addV("person").property("name", name).next();

        return newVertex.toString();
    }

    public static String updatePerson(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the person to update: ");
        String name = keyboard.nextLine();
        System.out.println("Enter the new name for the person: ");
        String newName = keyboard.nextLine();

        //returns a vertex type
        Vertex vertex = g.V().
                has("person", "name", name).
                property("name", newName).next();

        return vertex.toString();
    }

    public static String addIsFriendsWithEdge(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name for the person at the edge start: ");
        String fromName = keyboard.nextLine();
        System.out.println("Enter the name for the person at the edge end: ");
        String toName = keyboard.nextLine();

        // returns Edge type
        Edge newEdge = g.V().has("person", "name", fromName).
                addE("is_friends_with").
                  to(__.V().has("person", "name", toName)).
                next();

        return newEdge.toString();
    }

    public static String deletePerson(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name for the person to delete:");
        String name = keyboard.nextLine();

        // returns a count of the vertices dropped
        Long vertexCount = g.V().has("person", "name", name).
                sideEffect(__.drop().iterate()).
                count().next();

        return vertexCount.toString();
    }

    public static String getFriends(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name for the person to find their friends:");
        String name = keyboard.nextLine();

        // list of objects
        List<Object> friends = g.V().
                has("person", "name", name).
                both("is_friends_with").dedup().
                values("name").toList();

        return StringUtils.join(friends, "\r\n");
    }

    public static String getFriendsOfFriends(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the person to find the friends of their friends:");
        String name = keyboard.nextLine();

        // List of objects
        List<Object> foff = g.V().
                has("person", "name", name).
                repeat(
                        out("is_friends_with")
                ).times(2).
                dedup().values("name").toList();

        return StringUtils.join(foff, "\r\n");
    }

    public static String findPathBetweenPeople(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name for the person at the start: ");
        String fromName = keyboard.nextLine();
        System.out.println("Enter the name for the person at the end: ");
        String toName = keyboard.nextLine();

        //List of path objects representing the path between the two persons
        List<Path> friends = g.V().
                has("person", "name", fromName).
                until(has("person", "name", toName)).
                repeat(
                        both("is_friends_with").simplePath()
                ).
                path().toList();

        return StringUtils.join(friends, "\r\n");
    }

    public static Cluster connectToDatabase() {
        Cluster.Builder builder = Cluster.build();
        builder.addContactPoint("localhost");
        builder.port(8182);

        return builder.create();
    }

    public static GraphTraversalSource getGraphTraversalSource(Cluster cluster) {
        return traversal().withRemote(DriverRemoteConnection.using(cluster));
    }
}
