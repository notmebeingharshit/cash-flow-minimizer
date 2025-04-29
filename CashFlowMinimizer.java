import java.util.*;

// Class representing each Bank
class Bank {
    public String name;
    public int netAmount;
    public Set<String> types;
}

// Generic Pair class
class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() { return key; }
    public void setKey(K key) { this.key = key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }
}

public class CashFlowMinimizer {

    // Helper to find index with minimum net amount
    public static int getMinIndex(Bank[] banks, int n) {
        int min = Integer.MAX_VALUE, index = -1;
        for (int i = 0; i < n; i++) {
            if (banks[i].netAmount != 0 && banks[i].netAmount < min) {
                min = banks[i].netAmount;
                index = i;
            }
        }
        return index;
    }

    // Helper to find index with maximum net amount (ignores payment modes)
    public static int getSimpleMaxIndex(Bank[] banks, int n) {
        int max = Integer.MIN_VALUE, index = -1;
        for (int i = 0; i < n; i++) {
            if (banks[i].netAmount != 0 && banks[i].netAmount > max) {
                max = banks[i].netAmount;
                index = i;
            }
        }
        return index;
    }

    // Helper to find index with max netAmount having a common payment mode
    public static Pair<Integer, String> getMaxIndex(Bank[] banks, int n, int minIndex, int maxNumTypes) {
        int max = Integer.MIN_VALUE, maxIndex = -1;
        String matchingType = "";

        for (int i = 0; i < n; i++) {
            if (banks[i].netAmount <= 0) continue;

            List<String> commonTypes = new ArrayList<>(maxNumTypes);
            for (String type : banks[minIndex].types) {
                if (banks[i].types.contains(type)) {
                    commonTypes.add(type);
                }
            }

            if (!commonTypes.isEmpty() && banks[i].netAmount > max) {
                max = banks[i].netAmount;
                maxIndex = i;
                matchingType = commonTypes.get(0);
            }
        }
        return new Pair<>(maxIndex, matchingType);
    }

    // Display final transaction output
    public static void printAns(List<List<Pair<Integer, String>>> ansGraph, int numBanks, Bank[] input) {
        System.out.println("\nThe Transactions for minimum cash-flow are:\n");
        for (int i = 0; i < numBanks; i++) {
            for (int j = 0; j < numBanks; j++) {
                if (i == j) continue;
                int iToJ = ansGraph.get(i).get(j).getKey();
                int jToI = ansGraph.get(j).get(i).getKey();

                if (iToJ != 0 && jToI != 0) {
                    if (iToJ == jToI) {
                        ansGraph.get(i).get(j).setKey(0);
                        ansGraph.get(j).get(i).setKey(0);
                    } else if (iToJ > jToI) {
                        ansGraph.get(i).get(j).setKey(iToJ - jToI);
                        ansGraph.get(j).get(i).setKey(0);
                        System.out.println(input[i].name + " pays Rs " + ansGraph.get(i).get(j).getKey() +
                                " to " + input[j].name + " via " + ansGraph.get(i).get(j).getValue());
                    } else {
                        ansGraph.get(j).get(i).setKey(jToI - iToJ);
                        ansGraph.get(i).get(j).setKey(0);
                        System.out.println(input[j].name + " pays Rs " + ansGraph.get(j).get(i).getKey() +
                                " to " + input[i].name + " via " + ansGraph.get(j).get(i).getValue());
                    }
                } else if (iToJ != 0) {
                    System.out.println(input[i].name + " pays Rs " + iToJ + " to " +
                            input[j].name + " via " + ansGraph.get(i).get(j).getValue());
                } else if (jToI != 0) {
                    System.out.println(input[j].name + " pays Rs " + jToI + " to " +
                            input[i].name + " via " + ansGraph.get(j).get(i).getValue());
                }

                ansGraph.get(i).get(j).setKey(0);
                ansGraph.get(j).get(i).setKey(0);
            }
        }
    }

    // Core logic to minimize transactions
    public static void minimizeCashFlow(int numBanks, Bank[] input, Map<String, Integer> indexOf,
                                        int[][] graph, int maxNumTypes) {
        Bank[] netBanks = new Bank[numBanks];
        for (int i = 0; i < numBanks; i++) {
            netBanks[i] = new Bank();
            netBanks[i].name = input[i].name;
            netBanks[i].types = new HashSet<>(input[i].types);
            int netAmount = 0;

            for (int j = 0; j < numBanks; j++) {
                netAmount += graph[j][i] - graph[i][j];
            }
            netBanks[i].netAmount = netAmount;
        }

        List<List<Pair<Integer, String>>> ansGraph = new ArrayList<>(numBanks);
        for (int i = 0; i < numBanks; i++) {
            List<Pair<Integer, String>> row = new ArrayList<>();
            for (int j = 0; j < numBanks; j++) {
                row.add(new Pair<>(0, ""));
            }
            ansGraph.add(row);
        }

        int settled = 0;
        while (settled < numBanks) {
            int minIndex = getMinIndex(netBanks, numBanks);
            if (minIndex == -1) break;

            Pair<Integer, String> maxAns = getMaxIndex(netBanks, numBanks, minIndex, maxNumTypes);
            int maxIndex = maxAns.getKey();

            if (maxIndex == -1) {
                int simpleMax = getSimpleMaxIndex(netBanks, numBanks);
                int amt = Math.abs(netBanks[minIndex].netAmount);

                ansGraph.get(minIndex).get(0).setKey(amt);
                ansGraph.get(minIndex).get(0).setValue(input[minIndex].types.iterator().next());

                ansGraph.get(0).get(simpleMax).setKey(amt);
                ansGraph.get(0).get(simpleMax).setValue(input[simpleMax].types.iterator().next());

                netBanks[simpleMax].netAmount += netBanks[minIndex].netAmount;
                netBanks[minIndex].netAmount = 0;
            } else {
                int transactionAmount = Math.min(Math.abs(netBanks[minIndex].netAmount),
                        netBanks[maxIndex].netAmount);
                ansGraph.get(minIndex).get(maxIndex).setKey(transactionAmount);
                ansGraph.get(minIndex).get(maxIndex).setValue(maxAns.getValue());

                netBanks[minIndex].netAmount += transactionAmount;
                netBanks[maxIndex].netAmount -= transactionAmount;
            }

            settled = 0;
            for (Bank b : netBanks) {
                if (b.netAmount == 0) settled++;
            }
        }

        printAns(ansGraph, numBanks, input);
    }

    // Main entry point
    public static void main(String[] args) {
        System.out.println("\nWelcome to CASH FLOW MINIMIZER\n- by Harshit Singh Yadav\n");
        System.out.println("This program minimizes the number of transactions processed among multiple banks that use different payment modes.");
        System.out.println("A World Bank with all payment modes acts as an intermediary.\n");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of banks: ");
        int numBanks = scanner.nextInt();

        Bank[] input = new Bank[numBanks];
        Map<String, Integer> indexOf = new HashMap<>();
        int maxNumTypes = 0;

        for (int i = 0; i < numBanks; i++) {
            System.out.print((i == 0 ? "World Bank" : "Bank " + i) + ": ");
            String name = scanner.next();
            int numTypes = scanner.nextInt();
            Set<String> types = new HashSet<>();

            for (int j = 0; j < numTypes; j++) {
                types.add(scanner.next());
            }

            input[i] = new Bank();
            input[i].name = name;
            input[i].types = types;
            indexOf.put(name, i);

            if (i == 0) maxNumTypes = numTypes;
        }

        System.out.print("Enter number of transactions: ");
        int numTransactions = scanner.nextInt();
        int[][] graph = new int[numBanks][numBanks];

        System.out.println("Enter each transaction (from_bank to_bank amount):");
        for (int i = 0; i < numTransactions; i++) {
            String from = scanner.next();
            String to = scanner.next();
            int amount = scanner.nextInt();

            Integer fromIndex = indexOf.get(from);
            Integer toIndex = indexOf.get(to);
            if (fromIndex != null && toIndex != null) {
                graph[fromIndex][toIndex] = amount;
            } else {
                System.out.println("Invalid bank names: " + from + ", " + to);
            }
        }

        minimizeCashFlow(numBanks, input, indexOf, graph, maxNumTypes);
        scanner.close();
    }
}
