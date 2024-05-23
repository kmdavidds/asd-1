/**
 * 235150200111044 KOMANG DAVID DANANJAYA SUARTANA
 * 235150201111046 MUHAMMAD RIFKI AKBAR
 * 235150207111047 ARIF BINTANG HADITAMA
 * 235150200111047 JOSE PUTRA PERDANA TANEO
 */

import java.util.Scanner;

public class Cuitcuit {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        String[] commands = null;

        int numOfUsers = Integer.parseInt(scanner.nextLine());
        Integer.parseInt(scanner.nextLine());
        Global.graph = new Graph(numOfUsers);

        while (true) {
            input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }
            commands = input.split(" ");
            input = commands[0];
            switch (input) {
                case "insert":
                    if (commands.length != 5) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Features.insert(commands[1], commands[2], commands[3], commands[4]);
                        Global.outputList.addLast(new Node(commands[1] + "_inserted"));
                    }
                    break;

                case "connect":
                    if (commands.length != 3) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Features.connect(commands[1], commands[2]);
                        Global.outputList.addLast(new Node("connect_"+commands[1]+"_"+commands[2]+"_success"));
                    }
                    break;

                case "mostfollowed":
                    if (commands.length != 1) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Global.outputList.addLast(new Node(Features.mostfollowed()));
                    }
                    break;

                case "mincuit":
                    
                    break;

                case "numgroup":
                    if (commands.length != 1) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Global.outputList.addLast(new Node(Integer.toString(Features.grouping())));
                    }
                    break;

                case "grouptopic":
                    
                    break;

                case "suggestfriend":
                    
                    break;

                default:
                    break;
            }
        }
        Global.outputList.print();
        scanner.close();
    }
}

class Global {
    static LinkedList outputList = new LinkedList();
    static Graph graph;
}

class Features {
    static void insert(String username, String minat1, String minat2, String minat3) {
        Global.graph.addUser(new Node(username, minat1, minat2, minat3));
    }
    static void connect(String username1, String username2) {
        Global.graph.addConnection(username1, username2);
    }
    static String mostfollowed() {
        int highestFollowers = 0;
        for (int i = 0; i < Global.graph.currentUsers; i++) {
            highestFollowers = Math.max(highestFollowers, Global.graph.users[i].numberOfFollowers);
        }
        for (int i = 0; i < Global.graph.currentUsers; i++) {
            if (Global.graph.users[i].numberOfFollowers == highestFollowers) {
                return Global.graph.users[i].username;
            }
        }
        return "Tidak ditemukan";
    }
    static int grouping() {
        int n = Global.graph.currentUsers;
        int numgroup = 0;
        boolean[] vis = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!vis[i]) {
                Global.graph.dfs(i, vis);
                numgroup++;
            }
        }
        return numgroup;
    }
}

class Node {
    int nodeIndex;
    Node next;
    String output;
    String username, minat1, minat2, minat3;
    int numberOfFollowers = 0;

    Node(int nodeIndex, Node next) {
        this.nodeIndex = nodeIndex;
        this.next = next;
    }

    public Node(String username, String minat1, String minat2, String minat3) {
        this.username = username;
        this.minat1 = minat1;
        this.minat2 = minat2;
        this.minat3 = minat3;
    }

    Node(String output) {
        this.output = output;
    }
}

class LinkedList {
    Node head, tail;
    int size;

    void addLast(Node node) {
        if (head == null) {
            head = node;
            tail = node;
            size = 1;
            return;
        }
        tail.next = node;
        tail = node;
    }

    void print() {
        for (Node i = head; i != null; i = i.next) {
            System.out.println(i.output);
        }
    }
}

class Graph {
    Node[] adjList;
    Node[] users;
    int maxUsers;
    int currentUsers;

    Graph(int maxUsers) {
        this.maxUsers = maxUsers;
        this.adjList = new Node[maxUsers];
        this.users = new Node[maxUsers];
        this.currentUsers = 0;
    }

    void addUser(Node user) {
        if (currentUsers < maxUsers) {
            users[currentUsers] = user;
            currentUsers++;
        } else {
            System.out.println("Graph penuh");
        }
    }

    void addConnection(String from, String to) {
        int fromIndex = getIndex(from);
        int toIndex = getIndex(to);
        if (fromIndex != -1 && toIndex != -1) {
            adjList[fromIndex] = new Node(toIndex, adjList[fromIndex]);
        } else {
            System.out.println("User tidak ditemukan");
        }
        users[toIndex].numberOfFollowers++;
    }

    int getIndex(String username) {
        for (int i = 0; i < currentUsers; i++) {
            if (users[i].username.equals(username)) {
                return i;
            }
        }
        return -1;
    }

    void dfs(int index, boolean[] vis) {
        if (vis[index]) return;
        vis[index] = true;

        Node cur = adjList[index];
        while (cur != null) {
            dfs(cur.nodeIndex, vis);
            cur = cur.next;
        }

        for (int i = 0; i < Global.graph.currentUsers; i++) {
            Node temp = Global.graph.adjList[i];
            while (temp != null) {
                if (temp.nodeIndex == index) {
                    dfs(i, vis);
                }
                temp = temp.next;
            }
        }
    }
}