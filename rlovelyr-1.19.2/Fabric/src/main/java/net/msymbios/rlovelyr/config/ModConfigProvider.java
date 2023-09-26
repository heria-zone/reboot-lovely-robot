package net.msymbios.rlovelyr.config;

import com.mojang.datafixers.util.Pair;
import net.msymbios.rlovelyr.config.internal.SimpleConfig;

import java.util.ArrayList;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig {

    // -- Variables --
    private String configContents = "";

    public List<Pair> getConfigsList() {
        return configsList;
    }

    private final List<Pair> configsList = new ArrayList<>();

    // -- Inherited Methods --
    @Override
    public String get(String namespace) {
        return configContents;
    }

    // -- Custom Methods --
    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    } // addKeyValuePair ()

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment, String additional) {
        configsList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default: " + keyValuePair.getSecond() + "\n" + additional;
    } // addKeyValuePair ()

} // Class ModConfigProvider