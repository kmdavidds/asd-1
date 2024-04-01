
/**
 * 235150200111044 KOMANG DAVID DANANJAYA SUARTANA
 * 235150201111046 MUHAMMAD RIFKI AKBAR
 * 235150207111047 ARIF BINTANG HADITAMA
 * 235150200111047 JOSE PUTRA PERDANA TANEO
 */

import java.util.Scanner;

public class MigrasiAngkasa {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name, tension, input  = "";
        int n, m, num, rating, age = 0;

        n = scanner.nextInt();  
        m = scanner.nextInt();
        scanner.nextLine();

        String[] commands = null;

        Env.vip1List.maxSize = n;
        Env.vip2List.maxSize = n;
        Env.comList.maxSize = m;
        Env.outputList.maxSize = Integer.MAX_VALUE;
        Env.waitList.maxSize = Integer.MAX_VALUE;
        Env.transitionList.maxSize = Integer.MAX_VALUE;

        Env.vipCounter = 1;
        Env.comCounter = 1;

        while (true) {
            input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }
            commands = input.split(" ");
            input = commands[0];
            switch (input) {
                case "REGISTER":
                    name = commands[1];
                    rating = Integer.parseInt(commands[2]);
                    age = Integer.parseInt(commands[3]);
                    tension = commands[4];
                    Command.register(name, rating, age, tension);
                    break;

                case "RESIZE":
                    input = commands[1];
                    switch (input) {
                        case "VIP":
                            num = Integer.parseInt(commands[2]);
                            Command.resizeVIP(num);
                            break;
                            
                        case "COM":
                            num = Integer.parseInt(commands[2]);
                            Command.resizeCOM(num);
                            break;

                        default:
                            break;
                    }
                    break;

                case "INJECT":
                    input = commands[1];
                    switch (input) {
                        case "VIP1":
                            num = Integer.parseInt(commands[2]);
                            Command.injectN(num, "VIP1");
                            break;

                        case "VIP2":
                            num = Integer.parseInt(commands[2]);
                            Command.injectN(num, "VIP2");
                            break;
                    
                        case "COM":
                            num = Integer.parseInt(commands[2]);
                            Command.injectN(num, "COM");
                            break;
                            
                        default:
                            Command.injectTicket(input);
                            break;
                    }
                    break;

                case "SKIP":
                    input = commands[1];
                    Command.skip(input);
                    break;

                case "PRINT_MANIFEST":
                    Command.printManifest();
                    break;
                default:
                    break;
            }
        }
        Env.outputList.print();
        scanner.close();
    }
}

class Env {
    static int vipCounter, comCounter;
    static Queue vip1List = new Queue();
    static Queue vip2List = new Queue();
    static Queue comList = new Queue();
    static Queue waitList = new Queue();
    static Queue transitionList = new Queue();
    static Queue outputList = new Queue();
}

class Command {
    static void register(String name, int rating, int age, String tension) {
        Node passenger = new Node();
        passenger.name = name;
        if (rating < 7) {
            passenger.ticket = name + "#" + "COM-" + Env.comCounter++;
            passenger.priority = 1;
            if (isTensionDangerous(tension)) {
                Env.waitList.enqueueWaitList(passenger);
                Env.outputList.enqueue(new Node("WAITLIST "+name+" "+passenger.ticket.split("#")[1]));
            } else {
                if (Env.comList.size < Env.comList.maxSize) {
                    Env.comList.enqueue(passenger);
                    Env.outputList.enqueue(new Node("COMMON "+name+" "+passenger.ticket.split("#")[1]));
                } else {
                    Env.waitList.enqueueWaitList(passenger);
                    Env.outputList.enqueue(new Node("WAITLIST "+name+" "+passenger.ticket.split("#")[1]));
                }
            }
        } else if (rating < 9) {
            passenger.ticket = name + "#" + "VIP-" + Env.vipCounter++;
            passenger.priority = 2;
            if (isTensionDangerous(tension)) {
                Env.waitList.enqueueWaitList(passenger);
                Env.outputList.enqueue(new Node("WAITLIST "+name+" "+passenger.ticket.split("#")[1]));
            } else {
                if (Env.vip2List.size < Env.vip2List.maxSize) {
                    Env.vip2List.enqueue(passenger);
                    Env.outputList.enqueue(new Node("VIP2 "+name+" "+passenger.ticket.split("#")[1]));
                } else {
                    Env.waitList.enqueueWaitList(passenger);
                    Env.outputList.enqueue(new Node("WAITLIST "+name+" "+passenger.ticket.split("#")[1]));
                }
            }
        } else {
            passenger.ticket = name + "#" + "VIP-" + Env.vipCounter++;
            passenger.priority = 3;
            if (isTensionDangerous(tension)) {
                Env.waitList.enqueueWaitList(passenger);
                Env.outputList.enqueue(new Node("WAITLIST "+name+" "+passenger.ticket.split("#")[1]));
            } else {
                if (Env.vip1List.size < Env.vip1List.maxSize) {
                    Env.vip1List.enqueue(passenger);
                    Env.outputList.enqueue(new Node("VIP1 "+name+" "+passenger.ticket.split("#")[1]));
                } else {
                    Env.waitList.enqueueWaitList(passenger);
                    Env.outputList.enqueue(new Node("WAITLIST "+name+" "+passenger.ticket.split("#")[1]));
                }
            }
        }
    }

    static void resizeVIP(int num) {
        Env.outputList.enqueue(new Node("RESIZE_SUCCESS VIP "+Env.vip1List.maxSize+" TO "+num));
        if (num < Env.vip1List.maxSize) {
            int counter = 1;
            for (Node i = Env.vip1List.front; i != null; i = i.next) {
                if (counter > num) {
                    Env.transitionList.enqueue(i);
                }
                counter++;
            }
            for (int i = 0; i < Env.vip1List.maxSize - num; i++) {
                if (Env.transitionList.size == 0) break;
                Env.vip1List.dequeueFromRear();
                Env.waitList.enqueue(Env.transitionList.front);
                Env.transitionList.dequeue();
            }
            
            counter = 1;
            for (Node i = Env.vip2List.front; i != null; i = i.next) {
                if (counter > num) {
                    Env.transitionList.enqueue(i);
                }
                counter++;
            }
            for (int i = 0; i < Env.vip2List.maxSize - num; i++) {
                if (Env.transitionList.size == 0) break;
                Env.vip2List.dequeueFromRear();
                Env.waitList.enqueue(Env.transitionList.front);
                Env.transitionList.dequeue();
            }

            Env.vip1List.maxSize = num;
            Env.vip2List.maxSize = num;
        } else {
            int counter = num-Env.vip1List.maxSize;

            Env.vip1List.maxSize = num;
            Env.vip2List.maxSize = num;
            
            for (Node i = Env.waitList.front; i != null; i = i.next) {
                if (counter == 0) break;
                if (i.priority == 3) {
                    Env.vip1List.enqueue(i);
                    Env.waitList.removeNode(i);
                    counter--;
                }
            }

            for (Node i = Env.waitList.front; i != null; i = i.next) {
                if (counter == 0) break;
                if (i.priority == 2) {
                    Env.vip2List.enqueue(i);
                    Env.waitList.removeNode(i);
                    counter--;
                }
            }
        }
    }

    static void resizeCOM(int num) {
        Env.outputList.enqueue(new Node("RESIZE_SUCCESS COM "+Env.comList.maxSize+" TO "+num));
        if (num < Env.comList.maxSize) {
            int counter = 1;
            for (Node i = Env.comList.front; i != null; i = i.next) {
                if (counter > num) {
                    Env.transitionList.enqueue(i);
                }
                counter++;
            }
            for (int i = 0; i < Env.comList.maxSize - num; i++) {
                if (Env.transitionList.size == 0) break;
                Env.comList.dequeueFromRear();
                Env.waitList.enqueue(Env.transitionList.front);
                Env.transitionList.dequeue();
            }
            
            Env.comList.maxSize = num;
        } else {
            int counter = num-Env.comList.maxSize;

            Env.comList.maxSize = num;
            
            for (Node i = Env.waitList.front; i != null; i = i.next) {
                if (counter == 0) break;
                if (i.priority == 1) {
                    Env.comList.enqueue(i);
                    Env.waitList.removeNode(i);
                    counter--;
                }
            }
        }
    }

    static void injectN(int num, String catergory) {
        String message = "FINSIHED_INJECT";
        switch (catergory) {
            case "VIP1":
                if (Env.vip1List.size == 0) {
                    Env.outputList.enqueue(new Node("FINSIHED_INJECT NONE"));
                    return;
                }
                for (int i = 0; i < num; i++) {
                    if (Env.vip1List.size == 0) break;
                    message += " "+Env.vip1List.front.name;
                    Env.vip1List.dequeue();
                }
                for (Node i = Env.waitList.front; i != null; i = i.next) {
                    if (i.priority == 3) {
                        Env.vip1List.enqueue(i);
                        Env.waitList.removeNode(i);
                        num--;
                    }
                    if (num == 0) break;
                }
                Env.outputList.enqueue(new Node(message));
                break;
        
            case "VIP2":
                if (Env.vip2List.size == 0) {
                    Env.outputList.enqueue(new Node("FINSIHED_INJECT NONE"));
                    return;
                }
                for (int i = 0; i < num; i++) {
                    if (Env.vip2List.size == 0) break;
                    message += " "+Env.vip1List.front.name;
                    Env.vip2List.dequeue();
                }
                for (Node i = Env.waitList.front; i != null; i = i.next) {
                    if (i.priority == 2) {
                        Env.vip2List.enqueue(i);
                        Env.waitList.removeNode(i);
                        num--;
                    }
                    if (num == 0) break;
                }
                Env.outputList.enqueue(new Node(message));
                break;

            case "COM":
                if (Env.comList.size == 0) {
                    Env.outputList.enqueue(new Node("FINSIHED_INJECT NONE"));
                    return;
                }
                for (int i = 0; i < num; i++) {
                    if (Env.comList.size == 0) break;
                    message += " "+Env.comList.front.name;
                    Env.comList.dequeue();
                }
                for (Node i = Env.waitList.front; i != null; i = i.next) {
                    if (i.priority == 1) {
                        Env.comList.enqueue(i);
                        Env.waitList.removeNode(i);
                        num--;
                    }
                    if (num == 0) break;
                }
                Env.outputList.enqueue(new Node(message));
                break;
            default:
                break;
        }
    }

    static void injectTicket(String ticket) {
        for (Node i = Env.vip1List.front; i != null; i = i.next) {
            if (i.ticket.split("#")[1].equals(ticket)) {
                Env.outputList.enqueue(new Node("INJECT SUCCESS "+i.name));
                Env.vip1List.removeNode(i);
                for (Node j = Env.waitList.front; j != null; j = j.next) {
                    if (j.priority == 3) {
                        Env.vip1List.enqueue(j);
                        Env.waitList.removeNode(j);
                        return;
                    }
                }
                return;
            }
        }
        for (Node i = Env.vip2List.front; i != null; i = i.next) {
            if (i.ticket.split("#")[1].equals(ticket)) {
                Env.outputList.enqueue(new Node("INJECT SUCCESS "+i.name));
                Env.vip2List.removeNode(i);
                for (Node j = Env.waitList.front; j != null; j = j.next) {
                    if (j.priority == 2) {
                        Env.vip2List.enqueue(j);
                        Env.waitList.removeNode(j);
                        return;
                    }
                }
                return;
            }
        }
        for (Node i = Env.comList.front; i != null; i = i.next) {
            if (i.ticket.split("#")[1].equals(ticket)) {
                Env.outputList.enqueue(new Node("INJECT SUCCESS "+i.name));
                Env.comList.removeNode(i);
                for (Node j = Env.waitList.front; j != null; j = j.next) {
                    if (j.priority == 1) {
                        Env.comList.enqueue(j);
                        Env.waitList.removeNode(j);
                        return;
                    }
                }
                return;
            }
        }
    }

    static void skip(String ticket) {
        for (Node i = Env.vip1List.front; i != null; i = i.next) {
            if (i.ticket.split("#")[1].equals(ticket)) {
                for (Node j = Env.waitList.front; j != null; j = j.next) {
                    if (j.priority == 3) {
                        Env.vip1List.enqueueIgnoreSize(j);
                        Env.waitList.removeNode(j);
                        break;
                    }
                }
                Env.waitList.enqueueWaitList(i);
                Env.vip1List.removeNode(i);
                Env.outputList.enqueue(new Node("SKIP SUCCESS"));
                return;
            }
        }
        for (Node i = Env.vip2List.front; i != null; i = i.next) {
            if (i.ticket.split("#")[1].equals(ticket)) {
                for (Node j = Env.waitList.front; j != null; j = j.next) {
                    if (j.priority == 2) {
                        Env.vip2List.enqueueIgnoreSize(j);
                        Env.waitList.removeNode(j);
                        break;
                    }
                }
                Env.waitList.enqueueWaitList(i);
                Env.vip2List.removeNode(i);
                Env.outputList.enqueue(new Node("SKIP SUCCESS"));
                return;
            }
        }
        for (Node i = Env.comList.front; i != null; i = i.next) {
            if (i.ticket.split("#")[1].equals(ticket)) {
                for (Node j = Env.waitList.front; j != null; j = j.next) {
                    if (j.priority == 1) {
                        Env.comList.enqueueIgnoreSize(j);
                        Env.waitList.removeNode(j);
                        break;
                    }
                }
                Env.waitList.enqueueWaitList(i);
                Env.comList.removeNode(i);
                Env.outputList.enqueue(new Node("SKIP SUCCESS"));
                return;
            }
        }
        Env.outputList.enqueue(new Node("SKIP FAILED"));
    }

    static void printManifest() {
        Node message = new Node("VIP1_LIST");
        if (Env.vip1List.size == 0) message.name += " -\n";
        else {
            for (Node i = Env.vip1List.front; i != null; i = i.next) {
                message.name += " "+i.ticket;
            }
            message.name += "\n";
        }

        message.name += "VIP2_LIST";
        if (Env.vip2List.size == 0) message.name += " -\n";
        else {
            for (Node i = Env.vip2List.front; i != null; i = i.next) {
                message.name += " "+i.ticket;
            }
            message.name += "\n";
        }

        message.name += "COM_LIST";
        if (Env.comList.size == 0) message.name += " -\n";
        else {
            for (Node i = Env.comList.front; i != null; i = i.next) {
                message.name += " "+i.ticket;
            }
            message.name += "\n";
        }

        message.name += "WAIT_LIST";
        if (Env.waitList.size == 0) message.name += " -";
        else {
            for (Node i = Env.waitList.front; i != null; i = i.next) {
                message.name += " "+i.ticket;
            }
        }
        Env.outputList.enqueue(message);
    }

    static boolean isTensionDangerous(String tension) {
        String[] splitTension = tension.split("/");
        int sistolic = Integer.parseInt(splitTension[0]);
        int diastolic = Integer.parseInt(splitTension[1]);
        if (sistolic >= 130 && diastolic >= 80) return true;
        if (sistolic <= 90 && diastolic <= 60) return true;
        return false;
    }
}

class Node {
    Node next, prev;
    String name, ticket;
    int priority;

    Node() {}

    Node(String name) {
        this.name = name;
    }
}

class Queue {
    Node front, rear;
    int size, maxSize;

    void addFront(Node newNode) {
        if (size == 0) {
            front = newNode;
            rear = newNode;
            size++;
            return;
        }
        newNode.next = front;
        front.prev = newNode;
        front = newNode;
        size++;
        return;
    }

    void addRear(Node newNode) {
        if (size == 0) {
            addFront(newNode);
            return;
        }
        rear.next = newNode;
        newNode.prev = rear;
        rear = newNode;
        size++;
        return;
    }

    void insertBefore(Node cur, Node newNode) {
        if (size == 0) {
            addFront(newNode);
            return;
        }
        if (cur == front) {
            addFront(newNode);
            return;
        }
        newNode.next = cur;
        newNode.prev = cur.prev;
        cur.prev.next = newNode;
        cur.prev = newNode;
        size++;
        return;
    }

    void removeNode(Node cur) {
        if (size == 0) return;
        if (size == 1) {
            front = null;
            rear = null;
            size--;
            return;
        }
        if (cur == front) {
            front = front.next;
            front.prev = null;
            size--;
            return;
        }
        if (cur == rear) {
            rear = rear.prev;
            rear.next = null;
            size--;
            return;
        }
        cur.prev.next = cur.next;
        cur.next.prev = cur.prev;
        cur = null;
        size--;
        return;
    }

    void enqueue(Node old_passenger) {
        Node passenger = new Node();
        passenger.name = old_passenger.name;
        passenger.ticket = old_passenger.ticket;
        passenger.priority = old_passenger.priority;
        if (size == 0 && maxSize > 0) {
            addFront(passenger);
            return;
        }
        if (size == maxSize) {
            enqueueWaitList(passenger);
            return;
        }
        rear.next = passenger;
        passenger.prev = rear;
        rear = passenger;
        size++;
        return;
    }

    void enqueueIgnoreSize(Node old_passenger) {
        Node passenger = new Node();
        passenger.name = old_passenger.name;
        passenger.ticket = old_passenger.ticket;
        passenger.priority = old_passenger.priority;
        if (size == 0) {
            addFront(passenger);
            return;
        }
        rear.next = passenger;
        passenger.prev = rear;
        rear = passenger;
        size++;
        return;
    }

    void enqueueWaitList(Node old_passenger) {
        Node passenger = new Node();
        passenger.name = old_passenger.name;
        passenger.ticket = old_passenger.ticket;
        passenger.priority = old_passenger.priority;
        if (Env.waitList.size == 0) {
            Env.waitList.addFront(passenger);
            return;
        }
        if (size == 1) {
            if (passenger.priority > Env.waitList.front.priority) {
                addFront(passenger);
                return;
            }
            addRear(passenger);
            return;
        }
        for (Node i = Env.waitList.front; i != null; i = i.next) {
            if (passenger.priority > i.priority) {
                if (i == front) {
                    addFront(passenger);
                    return;
                }
                insertBefore(i, passenger);
                return;
            }
        }
        addRear(passenger);
        return;
    }

    void dequeueFromRear() {
        if (size == 0) return;
        if (size == 1) {
            front = null;
            rear = null;
            size--;
            return;
        }
        rear = rear.prev;
        rear.next = null;
        size--;
        return;
    }

    void dequeue() {
        if (size == 0) return;
        if (size == 1) {
            front = null;
            rear = null;
            size--;
            return;
        }
        front = front.next;
        front.prev = null;
        size--;
        return;
    }

    void print() {
        for (Node i = front; i != null; i = i.next) {
            System.out.println(i.name);
        }
    }
}