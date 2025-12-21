package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class IncompatibleConfigProvider {
    private static final Map<Enchantment, Set<Enchantment>> incompatibleEnchantments = new HashMap<>();

    public static void applyIncompatsFromConfig(){
        for(Enchantment ench: Enchantment.REGISTRY) {
            incompatibleEnchantments.put(ench, getIncompatibleEnchantmentsFromConfig(ench));
        }
    }

    public static boolean areCompatible(Enchantment ench, Enchantment other){
        return !incompatibleEnchantments.getOrDefault(ench, new HashSet<>()).contains(other);
    }

    private static Set<Enchantment> getIncompatibleEnchantmentsFromConfig(Enchantment thisEnch) {
        Set<Enchantment> incompatEnchs = new HashSet<>();

        if(thisEnch == null) return incompatEnchs;
        ResourceLocation regName = thisEnch.getRegistryName();
        if(regName == null) return incompatEnchs;

        for(String configLine : ConfigHandler.incompatibleGroups) {
            if(configLine.contains(regName.getPath())) {
                //Assumes that config lines are enchantments separated by comma
                String[] enchsInList = configLine.split(EnchantmentControl.SEP);
                for(String lineEntry : enchsInList) {
                    lineEntry = lineEntry.trim();
                    if(lineEntry.isEmpty()) continue;
                    Enchantment incompatEnch = Enchantment.getEnchantmentByLocation(lineEntry);
                    if(incompatEnch == null) EnchantmentControl.LOGGER.warn("could not find incompatible enchantment {}", lineEntry);
                    else incompatEnchs.add(incompatEnch);
                }
            }
        }
        // remove the calling enchant
        // every enchantment is incompatible with itself, this is handled by Enchantment class directly though
        // and thus doesn't need to be in this list
        incompatEnchs.remove(thisEnch);

        return incompatEnchs;
    }

    public static void mapDefaultIncompatibilities(){
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
        List<String> defaultIncompatList = new ArrayList<>();
        for(Set<Integer> group : groups) {
            if(group.size() < 2) continue;
            StringBuilder groupString = new StringBuilder();
            for(Integer id : group) {
                Enchantment ench = Enchantment.getEnchantmentByID(idmap.get(id));
                if(ench == null) continue;
                if(ench.getRegistryName() == null) continue;
                groupString.append(ench.getRegistryName().toString()).append(EnchantmentControl.SEP).append(" ");
            }
            defaultIncompatList.add(groupString.substring(0, groupString.length()-EnchantmentControl.SEP.length()-1));
        }

        String[] out = defaultIncompatList.toArray(new String[0]);

        EnchantmentControl.CONFIG.get("general", "Incompatible Enchantment Groups", ConfigHandler.incompatibleGroups).set(out);
        EnchantmentControl.CONFIG.get("general.first setup", "Read and dump default incompats", ConfigHandler.dev.readIncompats).set(false);
        ConfigHandler.incompatibleGroups = out;
        ConfigHandler.dev.readIncompats = false;
        EnchantmentControl.CONFIG.save();

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
