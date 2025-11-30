package net.msymbios.llovelyr.config.internal;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Provides structured configuration definitions with comments and default values.</p>
 * <p>
 * <b>Architecture:</b> Implements the DefaultConfig interface to generate formatted
 * configuration file content. Maintains a list of configuration entries with inline
 * comments and default value documentation.
 * <p>
 * <b>Design Intent:</b> Separates configuration definition from file I/O, enabling
 * organized config generation with section headers and descriptive comments for
 * user-friendly configuration files.
 */
public class ConfigProvider implements SimpleConfig.DefaultConfig {

    // -- Variables --
    private String contents = "";

    private final List<Pair> configsList = new ArrayList<>();

    // -- Properties --
    
    /**
     * Returns list of all configuration entries as key-value pairs.
     * <p>
     * <b>Usage:</b> Provides access to config structure for validation or
     * programmatic inspection. Not typically needed for normal config operations.
     * 
     * @return list of Pair objects containing config keys and values
     */
    public List<Pair> getConfigs() {
        return configsList;
    } // getConfigs ()

    // -- Inherited Methods --
    
    /**
     * Generates formatted configuration file content.
     * <p>
     * <b>Implementation:</b> Returns accumulated contents string built through
     * addComment() and addKeyValuePair() calls. Called by SimpleConfig when
     * generating default config file.
     * <p>
     * <i>Note:</i> Namespace parameter is provided by SimpleConfig interface but
     * not used in this implementation.
     * 
     * @param namespace config namespace (typically mod ID)
     * @return complete formatted config file content with comments and defaults
     */
    @Override
    public String get(String namespace) {
        return contents;
    } // get ()

    // -- Custom Methods --
    
    /**
     * Adds a section header comment to organize configuration file.
     * <p>
     * <b>Organization:</b> Section headers create visual separation between
     * functional groups, improving config file readability and maintainability.
     * <p>
     * <b>Format:</b> Automatically prepends '#' character. Empty strings create
     * blank comment lines for spacing.
     * <p>
     * <b>Example:</b>
     * <pre>
     * provider.addComment("===== COMBAT SETTINGS =====");
     * </pre>
     * 
     * @param comment section header text (without # prefix)
     */
    public void addComment(String comment) {
        contents += "# " + comment + "\n";
    } // addComment ()

    /**
     * Adds configuration entry with inline documentation.
     * <p>
     * <b>Format:</b> Generates line: {@code key=value # comment | default: value}
     * <p>
     * <b>Design Decision:</b> Inline format keeps related information together,
     * making it easy for users to understand each option without searching
     * separate documentation.
     * <p>
     * <b>Dual Purpose:</b> Adds to both contents string (for file generation) and
     * configsList (for programmatic access).
     * 
     * @param keyValuePair configuration key-value pair (e.g., Pair.of("MaxHealth", 20))
     * @param comment descriptive comment explaining the configuration option
     */
    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        contents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " # "
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    } // addKeyValuePair ()

    /**
     * Adds configuration entry with inline documentation and custom formatting.
     * <p>
     * <b>Usage:</b> Allows custom spacing or formatting after entry. Typically used
     * to add blank lines between related groups within a section.
     * <p>
     * <b>Example:</b>
     * <pre>
     * provider.addKeyValuePair(Pair.of("Setting", 10), "Description", "\n");
     * </pre>
     * 
     * @param keyValuePair configuration key-value pair
     * @param comment descriptive comment explaining the configuration option
     * @param additional extra text to append (e.g., "\n" for blank line)
     */
    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment, String additional) {
        configsList.add(keyValuePair);
        contents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " # "
                + comment + " | default: " + keyValuePair.getSecond() + "\n" + additional;
    } // addKeyValuePair ()

} // Class: ConfigProvider
