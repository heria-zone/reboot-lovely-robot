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
    private int indentLevel = 0;
    
    // -- Nested Classes --
    
    /**
     * Fluent builder for config entries matching Forge's API style.
     * <p>
     * <b>Usage Pattern:</b> Chain comment() calls, then call define() to complete:
     * <pre>
     * provider.comment("First line", "Second line")
     *         .define("property-name", defaultValue);
     * </pre>
     */
    public class ConfigBuilder {
        private final List<String> comments = new ArrayList<>();
        
        public ConfigBuilder comment(String... commentLines) {
            for (String line : commentLines) {
                comments.add(line);
            }
            return this;
        }
        
        public <T> void define(String key, T value) {
            Pair<String, T> pair = Pair.of(key, value);
            configsList.add(pair);
            
            String indent = getIndent();
            for (String comment : comments) {
                contents += indent + "# " + comment + "\n";
            }
            contents += indent + key + "=" + value + "\n";
        }
    } // Class: ConfigBuilder

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
     * Returns current indentation string based on nesting level.
     * 
     * @return indentation string (tabs)
     */
    private String getIndent() {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indent.append("\t");
        }
        return indent.toString();
    } // getIndent()
    
    /**
     * Adds a section header comment to organize configuration file.
     * <p>
     * <b>Organization:</b> Section headers create visual separation between
     * functional groups, improving config file readability and maintainability.
     * <p>
     * <b>Format:</b> Automatically prepends '# ' (hash with space). Empty strings
     * create blank lines for spacing.
     * <p>
     * <b>Example:</b>
     * <pre>
     * provider.addComment("===== COMBAT SETTINGS =====");
     * </pre>
     * 
     * @param comment section header text (without # prefix)
     */
    public void addComment(String comment) {
        if (comment.isEmpty()) {
            contents += "\n";
        } else {
            contents += getIndent() + "# " + comment + "\n";
        }
    } // addComment ()

    /**
     * Adds configuration entry with inline documentation.
     * <p>
     * <b>Format:</b> Generates multi-line format matching Forge style:
     * <pre>
     * # Description of the option.
     * # Example: [value]
     * key=value
     * </pre>
     * <p>
     * <b>Design Decision:</b> Multi-line format with comment above property
     * provides better readability and matches Forge config style.
     * <p>
     * <b>Dual Purpose:</b> Adds to both contents string (for file generation) and
     * configsList (for programmatic access).
     * 
     * @param keyValuePair configuration key-value pair (e.g., Pair.of("MaxHealth", 20))
     * @param comment descriptive comment explaining the configuration option
     */
    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        String indent = getIndent();
        contents += indent + "# " + comment + "\n";
        contents += indent + "# Example: [" + keyValuePair.getSecond() + "]\n";
        contents += indent + keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + "\n";
    } // addKeyValuePair ()

    /**
     * Adds configuration entry with multiple comment lines for detailed documentation.
     * <p>
     * <b>Format:</b> Generates multi-line format matching Forge style with multiple comment lines:
     * <pre>
     * # First comment line.
     * # Second comment line.
     * # Range: X to Y
     * # Example: [value]
     * key=value
     * </pre>
     * <p>
     * <b>Design Decision:</b> Supports detailed multi-line comments to match Forge's
     * comprehensive documentation style, improving user understanding of config options.
     * <p>
     * <b>Usage:</b> Pass array of comment strings, each becoming a separate comment line.
     * 
     * @param keyValuePair configuration key-value pair (e.g., Pair.of("max-health", 20))
     * @param comments array of comment lines to display above the property
     */
    public void addKeyValuePair(Pair<String, ?> keyValuePair, String[] comments) {
        configsList.add(keyValuePair);
        String indent = getIndent();
        for (String comment : comments) {
            contents += indent + "# " + comment + "\n";
        }
        contents += indent + keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + "\n";
    } // addKeyValuePair ()

    /**
     * Adds section header in Forge-style format as a comment.
     * <p>
     * <b>Format:</b> Generates section header as comment with proper indentation:
     * <pre>
     * # [Section Name]
     * </pre>
     * <p>
     * <b>Design Decision:</b> Properties format doesn't support actual sections,
     * so sections are rendered as comments for visual organization.
     * <p>
     * <b>Usage:</b> Use for major category headers to organize config file.
     * 
     * @param sectionName name of the configuration section
     */
    public void addSection(String sectionName) {
        String indent = getIndent();
        contents += "\n" + indent + "# [" + sectionName + "]\n";
    } // addSection ()
    
    /**
     * Starts a new config section with Forge-style push/pop semantics.
     * <p>
     * <b>Forge Compatibility:</b> Mimics Forge's BUILDER.push() for section nesting.
     * Creates section header and increases indentation level for nested content.
     * 
     * @param sectionName name of the configuration section
     */
    public void push(String sectionName) {
        addSection(sectionName);
        indentLevel++;
    } // push()
    
    /**
     * Ends current config section with Forge-style push/pop semantics.
     * <p>
     * <b>Forge Compatibility:</b> Mimics Forge's BUILDER.pop() for API consistency.
     * Decreases indentation level to return to parent section.
     */
    public void pop() {
        if (indentLevel > 0) {
            indentLevel--;
        }
    } // pop()
    
    /**
     * Creates new fluent config builder for Forge-style API.
     * <p>
     * <b>Usage:</b>
     * <pre>
     * provider.comment("Description line 1", "Description line 2")
     *         .define("property-name", defaultValue);
     * </pre>
     * 
     * @param commentLines initial comment lines
     * @return ConfigBuilder for fluent chaining
     */
    public ConfigBuilder comment(String... commentLines) {
        ConfigBuilder builder = new ConfigBuilder();
        return builder.comment(commentLines);
    } // comment()

} // Class: ConfigProvider
