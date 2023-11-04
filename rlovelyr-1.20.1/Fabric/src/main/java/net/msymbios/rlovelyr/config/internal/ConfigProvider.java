package net.msymbios.rlovelyr.config.internal;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConfigProvider implements SimpleConfig.DefaultConfig {

    // -- Variables --
    private String contents = "";

    private final List<Pair> configsList = new ArrayList<>();

    // -- Properties --
    public List<Pair> getConfigs() {
        return configsList;
    } // getConfigs ()

    // -- Inherited Methods --
    @Override
    public String get(String namespace) {
        return contents;
    } // get ()

    // -- Custom Methods --
    public void addComment(String comment) {
        contents += "# " + comment + "\n";
    } // addComment ()

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        contents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " # "
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    } // addKeyValuePair ()

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment, String additional) {
        configsList.add(keyValuePair);
        contents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " # "
                + comment + " | default: " + keyValuePair.getSecond() + "\n" + additional;
    } // addKeyValuePair ()

} // Class ConfigProvider