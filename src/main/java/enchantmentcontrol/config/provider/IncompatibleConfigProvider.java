package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

public class IncompatibleConfigProvider {
    public static final Map<Enchantment, Set<Enchantment>> incompatibleEnchantments = new HashMap<>();
    public static final List<Set<Enchantment>> incompatibleGroups = new ArrayList<>();

    public static void onResetConfig(){
        ConfigHandler.incompatibleGroups.values().forEach(group -> incompatibleGroups.add(group.stream().map(Enchantment::getEnchantmentByLocation).filter(Objects::nonNull).collect(Collectors.toSet())));
        Enchantment.REGISTRY.forEach(ench -> incompatibleEnchantments.put(
                ench,
                incompatibleGroups.stream()
                        .filter(group -> group.contains(ench))
                        .reduce(new HashSet<>(), (groupsCollected, addedGroup) -> {
                            groupsCollected.addAll(addedGroup);
                            groupsCollected.remove(ench);
                            return groupsCollected;
                        })
        ));
    }

    public static boolean areCompatible(Enchantment ench, Enchantment other){
        return !incompatibleEnchantments.getOrDefault(ench, new HashSet<>()).contains(other);
    }

    public static void printDefaultIncompatibilities(){
        int n = Enchantment.REGISTRY.getKeys().size();
        boolean[][] incompatMatrix = new boolean[n][n];

        //k: 0-n, v: enchid
        Map<Integer, Integer> idmap = new HashMap<>();

        int i = 0;
        for(Enchantment ench : Enchantment.REGISTRY) {
            int id = Enchantment.getEnchantmentID(ench);
            idmap.put(i, id);

            int j=0;
            for(Enchantment ench2 : Enchantment.REGISTRY)
                incompatMatrix[i][j++] = !ench.isCompatibleWith(ench2);
            i++;
        }

        //Bron-Kerbosch Algorithm
        List<Set<Integer>> groups = findMaximalIncompatibilityGroups(incompatMatrix);
        groups.sort(Comparator.comparingInt(Set::size));

        //Remap to list of strings per group
        Map<String, HashSet<String>> defaultIncompats = new LinkedHashMap<>();
        int counter = 1;
        for(Set<Integer> group : groups) {
            if(group.size() <= 1) continue;
            HashSet<String> groupSet = new HashSet<>();
            for(Integer id : group) {
                Enchantment ench = Enchantment.getEnchantmentByID(idmap.get(id));
                if(ench == null) continue;
                if(ench.getRegistryName() == null) continue;
                groupSet.add(ench.getRegistryName().toString());
            }
            defaultIncompats.put("Group " + (counter++), groupSet);
        }

        ConfigHandler.incompatibleGroups.clear();
        ConfigHandler.incompatibleGroups.putAll(defaultIncompats);
        ConfigHandler.dev.printIncompats = false;
        EnchantmentControl.configNeedsSaving = true;
    }

    public static List<Set<Integer>> findMaximalIncompatibilityGroups(boolean[][] incompatMatrix) {
        int n = incompatMatrix.length;

        // Build the adjacency map
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new HashSet<>());
            for (int j = 0; j < n; j++) {
                if (i != j && incompatMatrix[i][j]) {
                    graph.get(i).add(j);
                }
            }
        }

        List<Set<Integer>> results = new ArrayList<>();
        bron_kerbosch(new HashSet<>(), new HashSet<>(graph.keySet()), new HashSet<>(), graph, results);
        return results;
    }

    private static void bron_kerbosch(Set<Integer> R, Set<Integer> P, Set<Integer> X,
                                      Map<Integer, Set<Integer>> graph,
                                      List<Set<Integer>> results) {
        if (P.isEmpty() && X.isEmpty()) {
            results.add(new HashSet<>(R));
            return;
        }

        Set<Integer> Pcopy = new HashSet<>(P);
        for (Integer v : Pcopy) {
            Set<Integer> neighbors = graph.get(v);
            Set<Integer> newR = new HashSet<>(R);
            newR.add(v);

            Set<Integer> newP = new HashSet<>(P);
            newP.retainAll(neighbors);

            Set<Integer> newX = new HashSet<>(X);
            newX.retainAll(neighbors);

            bron_kerbosch(newR, newP, newX, graph, results);

            P.remove(v);
            X.add(v);
        }
    }
}
