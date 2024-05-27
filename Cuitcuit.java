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
        int numOfEdges = Integer.parseInt(scanner.nextLine());
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
                    if (commands.length != 3) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Global.outputList.addLast(new Node(Integer.toString(Features.mincuit(commands[1], commands[2]))));
                    }
                    break;

                case "numgroup":
                    if (commands.length != 1) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Global.outputList.addLast(new Node(Integer.toString(Features.grouping())));
                    }
                    break;

                case "grouptopic":
                    if (commands.length != 1) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Features.grouptopic();
                    }
                    break;

                case "suggestfriend":
                    if (commands.length != 2) {
                        Global.outputList.addLast(new Node("WRONG FORMAT"));
                    } else {
                        Global.outputList.addLast(new Node(Features.suggestFriend(commands[1])));
                    }
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
        String names = "";
        for (int i = 0; i < Global.graph.currentUsers; i++) {
            if (Global.graph.users[i].numberOfFollowers == highestFollowers) {
                names += Global.graph.users[i].username + ",";
            }
        }
        // names = "rozi,graphy,"
        String[] namesArray = names.split(",");
        for (int i = 0; i < namesArray.length; i++) {
            for (int j = i + 1; j < namesArray.length; j++) {
                if (namesArray[i].compareTo(namesArray[j]) > 0) {
                    String temp = namesArray[i];
                    namesArray[i] = namesArray[j];
                    namesArray[j] = temp;
                }
            }
        }
        names = "";
        for (String string : namesArray) {
            names += string + ",";
        }
        // names = "graphy,rozi,"
        
        return names.substring(0, names.length() - 1);
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
    static void grouptopic() {
        Global.graph.highestInterestInComponent();
    }
    static int mincuit(String username1, String username2) {
        int index1 = Global.graph.getIndex(username1);
        int index2 = Global.graph.getIndex(username2);
        return Global.graph.shortestDist(index1, index2) - 1;
    }
    static String suggestFriend(String username) {
        int index = Global.graph.getIndex(username);
        return Global.graph.findSuggestedFriends(index);
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
    int currentUsers = 0;

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
            // [0] = 3 -> 2 -> 1
            // [0] = 4 (baru) -> 3 -> 2 -> 1
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

    void highestInterestInComponent() {
        boolean[] vis = new boolean[currentUsers];
        
        for (int i = 0; i < currentUsers; i++) {
            if (!vis[i]) {
                CustomMap interestCount = new CustomMap(currentUsers);
                dfsInterestCount(i, vis, interestCount);
                String highestInterests = "";
                int maxCount = Integer.MIN_VALUE;
                for (Entry entry : interestCount.buckets) {
                    if (entry != null) {
                        maxCount = Math.max(maxCount, entry.value);
                    }
                }
                for (Entry entry : interestCount.buckets) {
                    if (entry != null) {
                        if (entry.value == maxCount) {
                            highestInterests += entry.key + ",";
                        }
                    }
                }
                String[] hIArray = highestInterests.split(",");
                for (int j = 0; j < hIArray.length; j++) {
                    for (int k = j + 1; k < hIArray.length; k++) {
                        if (hIArray[j].compareTo(hIArray[k]) > 0) {
                            String temp = hIArray[j];
                            hIArray[j] = hIArray[k];
                            hIArray[k] = temp;
                        }
                    }
                }
                highestInterests = "";
                for (String string : hIArray) {
                    highestInterests += string + ",";
                }
                Global.outputList.addLast(new Node(highestInterests.substring(0, highestInterests.length() - 1)));
            }
        }
    }

    void dfsInterestCount(int index, boolean[] visited, CustomMap interestCount) {
        if (visited[index]) return;
        visited[index] = true;

        Node user = users[index];
        interestCount.put(user.minat1, interestCount.get(user.minat1) + 1);
        interestCount.put(user.minat2, interestCount.get(user.minat2) + 1);
        interestCount.put(user.minat3, interestCount.get(user.minat3) + 1);

        Node cur = adjList[index];
        while (cur != null) {
            dfsInterestCount(cur.nodeIndex, visited, interestCount);
            cur = cur.next;
        }

        for (int i = 0; i < Global.graph.currentUsers; i++) {
            Node temp = Global.graph.adjList[i];
            while (temp != null) {
                if (temp.nodeIndex == index) {
                    dfsInterestCount(i, visited, interestCount);
                }
                temp = temp.next;
            }
        }
    }

    int shortestDist(int from, int to) {
        if (from == to) {
            return 0;
        }

        int[] distances = new int[currentUsers];
        boolean[] visited = new boolean[currentUsers];

        for (int i = 0; i < currentUsers; i++) {
            distances[i] = Integer.MAX_VALUE;
        }

        distances[from] = 0;
        LinkedList queue = new LinkedList();
        queue.addLast(new Node(from, null));

        while (queue.size != 0) {
            if (queue.head == null) {
                break;
            }
            int u = queue.head.nodeIndex;
            queue.head = queue.head.next;
            Node temp = adjList[u];

            while (temp != null) {
                if (visited[temp.nodeIndex] == false) {
                    queue.addLast(new Node(temp.nodeIndex, null));
                    distances[temp.nodeIndex] = distances[u] + 1;
                    visited[temp.nodeIndex] = true;

                    if (temp.nodeIndex == to) {
                        return distances[temp.nodeIndex];
                    }
                }
                temp = temp.next;
            }
        }

        return 0;
    }

    String findSuggestedFriends(int index) {
        String suggestedFriends = "";
        boolean[] closeFriend = new boolean[currentUsers];
        int[] mutuals = new int[currentUsers];
        Node cur = adjList[index];
        while (cur != null) {
            closeFriend[cur.nodeIndex] = true;
            cur = cur.next;
        }
        cur = adjList[index];
        while (cur != null) {
            for (int i = 0; i < currentUsers; i++) {
                if (i != index) {
                    Node temp = adjList[i];
                    while (temp != null) {
                        if (temp.nodeIndex == cur.nodeIndex && !closeFriend[i]) {
                            mutuals[i]++;
                            suggestedFriends += users[i].username + ",";
                            break;
                        }
                        temp = temp.next;
                    }
                }
            }
            cur = cur.next;
        }
        String[] friendArray = suggestedFriends.split(",");
        for (int i = 0; i < friendArray.length; i++) {
            for (int j = i + 1; j < friendArray.length; j++) {
                if (mutuals[i] > mutuals[j]) {
                    String temp = friendArray[i];
                    friendArray[i] = friendArray[j];
                    friendArray[j] = temp;
                }
            }
        }
        friendArray = suggestedFriends.split(",");
        for (int i = 0; i < friendArray.length; i++) {
            for (int j = i + 1; j < friendArray.length; j++) {
                if (friendArray[i].compareTo(friendArray[j]) > 0) {
                    String temp = friendArray[i];
                    friendArray[i] = friendArray[j];
                    friendArray[j] = temp;
                }
            }
        }
        suggestedFriends = "";
        for (String string : friendArray) {
            suggestedFriends += string + ",";
        }
        return suggestedFriends.substring(0, suggestedFriends.length() - 1);
    }
}

class Entry {
    String key;
    int value;

    Entry(String key, int value) {
        this.key = key;
        this.value = value;
    }
}

class CustomMap {
    // [baca] = 2
    // [memancing] = 1
    Entry[] buckets;
    int size = 0;

    CustomMap(int size) {
        this.buckets = new Entry[size];
    }

    int get(String key) {
        int bucket = getHash(key) % getBucketSize();
        Entry existingElement = buckets[bucket];
        if (existingElement == null) {
            return 0;
        }
        return existingElement.value;
    }

    void put(String key, int value) {
        int bucket = getHash(key) % getBucketSize();
        Entry existingElement = buckets[bucket];

        if (existingElement == null) {
            buckets[bucket] = new Entry(key, value);
            return;
        }

        while (buckets[bucket] == null) {
            bucket = (bucket + 1) % getBucketSize();
        }
        buckets[bucket] = new Entry(key, value);
    }

    int getHash(String key) {
        return key == null ? 0 : Math.abs(key.hashCode());
    }

    int getBucketSize() {
        return buckets.length;
    }
}