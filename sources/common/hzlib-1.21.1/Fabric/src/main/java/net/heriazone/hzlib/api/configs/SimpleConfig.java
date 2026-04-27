package net.heriazone.hzlib.api.configs;

/*
 * Copyright (c) 2021 magistermaks
 * Slightly modified by Kaupenjoe 2021
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

/**
 * <p>Manages configuration file loading, parsing, and type-safe value retrieval.<p>
 * <p>
 * <b>Architecture:</b> Provides a lightweight properties-based configuration system
 * with automatic file generation, validation, and error recovery. Integrates with
 * Fabric Loader to resolve config directory paths.
 * <p>
 * <b>File Format:</b> Standard .properties format with support for inline comments
 * using '#' character. Comments after values are recognized and stripped during parsing.
 * <p>
 * <b>Error Handling:</b> Missing files trigger automatic generation using DefaultConfig
 * provider. Parse errors and I/O failures set the 'broken' flag, allowing graceful
 * degradation with default values.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Config should be loaded once during mod
 * initialization and accessed read-only thereafter.
 */
public class SimpleConfig {

    private static final Logger LOGGER = LogManager.getLogger("SimpleConfig");
    private final HashMap<String, String> config = new HashMap<>();
    private final ConfigRequest request;
    private boolean broken = false;

    // -- Nested Interfaces --

    /**
     * <p>Provides default configuration content for file generation.<p>
     * <p>
     * <b>Design Intent:</b> Decouples config definition from file handling,
     * allowing flexible config generation strategies.
     */
    public interface DefaultConfig {
        String get( String namespace );

        static String empty( String namespace ) {
            return "";
        }
    } // Interface: DefaultConfig

    // -- Nested Classes --

    /**
     * <p>Builder for config file requests with fluent API.<p>
     * <p>
     * <b>Usage Pattern:</b> Create via SimpleConfig.of(), configure with provider(),
     * then call request() to load the config. Follows builder pattern for clean
     * configuration setup.
     * <p>
     * <b>Design Decision:</b> Builder pattern chosen over constructor parameters to
     * allow optional provider configuration while maintaining immutability of the
     * final SimpleConfig instance.
     * <p>
     * <b>Example:</b>
     * <pre>
     * SimpleConfig config = SimpleConfig.of("mymod")
     *     .provider(new MyConfigProvider())
     *     .request();
     * </pre>
     */
    public static class ConfigRequest {

        private final File file;
        private final String filename;
        private DefaultConfig provider;

        private ConfigRequest(File file, String filename ) {
            this.file = file;
            this.filename = filename;
            this.provider = DefaultConfig::empty;
        } // ConfigRequest()

        /**
         * Sets the default config provider for file generation.
         * <p>
         * <b>Usage:</b> Provider is invoked only when config file is missing,
         * generating default content with comments and default values.
         * <p>
         * <i>Note:</i> If not set, empty config file is created.
         *
         * @param provider default config provider implementing DefaultConfig
         * @return this ConfigRequest for method chaining
         * @see DefaultConfig
         */
        public ConfigRequest provider( DefaultConfig provider ) {
            this.provider = provider;
            return this;
        } // provider()

        /**
         * Loads the config from filesystem or creates it if missing.
         * <p>
         * <b>File Resolution:</b> Uses Fabric Loader config directory,
         * typically {@code .minecraft/config/} in development or game directory.
         * <p>
         * <b>Error Handling:</b> Missing file triggers generation. Parse errors
         * set broken flag. I/O failures are logged and set broken flag.
         *
         * @return SimpleConfig instance with loaded values or broken flag set
         * @see SimpleConfig
         */
        public SimpleConfig request() {
            return new SimpleConfig( this );
        } // request()

        private String getConfig() {
            return provider.get( filename ) + "\n";
        } // getConfig()

    } // Class: ConfigRequest

    // -- Public Static Methods --

    /**
     * Creates new config request for the specified filename.
     * <p>
     * <b>File Location:</b> Resolves to Fabric config directory using FabricLoader API.
     * Automatically appends .properties extension to filename.
     * <p>
     * <b>Naming Convention:</b> Filename should typically match mod ID for consistency
     * and easy identification in config directory.
     * <p>
     * <b>Example:</b> {@code SimpleConfig.of("mymod")} creates {@code config/mymod.properties}
     *
     * @param filename name of the config file without extension (e.g., "llovelyr")
     * @return new ConfigRequest builder for fluent configuration
     */
    public static ConfigRequest of( String filename ) {
        Path path = FabricLoader.getInstance().getConfigDir();
        return new ConfigRequest( path.resolve( filename + ".properties" ).toFile(), filename );
    } // of()

    // -- Constructors --

    private SimpleConfig(ConfigRequest request ) {
        this.request = request;
        String identifier = "Config '" + request.filename + "'";

        if( !request.file.exists() ) {
            LOGGER.info( identifier + " is missing, generating default one at: " + request.file.getAbsolutePath() );

            try {
                createConfig();
                LOGGER.info( identifier + " created successfully with default values" );
            } catch (IOException e) {
                LOGGER.error( identifier + " failed to generate at path: " + request.file.getAbsolutePath(), e );
                broken = true;
            }
        }

        if( !broken ) {
            try {
                LOGGER.info( identifier + " loading from: " + request.file.getAbsolutePath() );
                loadConfig();
                LOGGER.info( identifier + " loaded successfully" );
            } catch (IOException e) {
                LOGGER.error( identifier + " failed to read from disk: " + request.file.getAbsolutePath(), e );
                broken = true;
            } catch (Exception e) {
                LOGGER.error( identifier + " failed to parse, using default values", e );
                broken = true;
            }
        }

    } // SimpleConfig()

    // -- Public Methods --

    /**
     * Queries raw string value from config.
     * <p>
     * <b>Deprecated:</b> Use type-safe {@link #getOrDefault} methods instead to
     * ensure proper validation and fallback behavior.
     *
     * @param key configuration key to query
     * @return value corresponding to the given key, or null if missing
     * @see SimpleConfig#getOrDefault
     */
    @Deprecated
    public String get( String key ) {
        return config.get( key );
    } // get()

    /**
     * Retrieves string value with automatic fallback.
     * <p>
     * <b>Null Safety:</b> Never returns null - missing keys return default value.
     *
     * @param key configuration key to query
     * @param def default value if key is missing
     * @return value from config or default if key not found
     */
    public String getOrDefault( String key, String def ) {
        String val = get(key);
        return val == null ? def : val;
    } // getOrDefault()

    /**
     * Retrieves integer value with validation and fallback.
     * <p>
     * <b>Validation:</b> Parses string value to integer. Invalid formats trigger
     * warning log and return default value, preventing crashes from user typos.
     * <p>
     * <b>Error Recovery:</b> Parse failures are non-fatal - default value ensures
     * config system remains operational despite invalid user input.
     *
     * @param key configuration key to query
     * @param def default value if key is missing or invalid
     * @return parsed integer value or default if missing/invalid
     */
    public int getOrDefault( String key, int def ) {
        try {
            String value = get(key);
            if (value == null) {
                return def;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid integer value for config key '{}': '{}', using default: {}", key, get(key), def);
            return def;
        }
    } // getOrDefault()

    /**
     * Retrieves boolean value with case-insensitive parsing.
     * <p>
     * <b>Parsing:</b> Accepts "true" or "false" (case-insensitive). Invalid values
     * trigger warning and return default, preventing boolean config errors.
     *
     * @param key configuration key to query
     * @param def default value if key is missing or invalid
     * @return parsed boolean value or default if missing/invalid
     */
    public boolean getOrDefault( String key, boolean def ) {
        String val = get(key);
        if( val != null ) {
            if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
                return val.equalsIgnoreCase("true");
            } else {
                LOGGER.warn("Invalid boolean value for config key '{}': '{}', using default: {}", key, val, def);
                return def;
            }
        }

        return def;
    } // getOrDefault()

    /**
     * Retrieves double value with validation and fallback.
     * <p>
     * <b>Precision:</b> Supports full double precision for high-accuracy numeric
     * config values. Parse failures return default with warning.
     *
     * @param key configuration key to query
     * @param def default value if key is missing or invalid
     * @return parsed double value or default if missing/invalid
     */
    public double getOrDefault( String key, double def ) {
        try {
            String value = get(key);
            if (value == null) {
                return def;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid double value for config key '{}': '{}', using default: {}", key, get(key), def);
            return def;
        }
    } // getOrDefault()

    /**
     * Retrieves float value with validation and fallback.
     * <p>
     * <b>Usage:</b> Preferred for game values (speeds, ranges) where float precision
     * is sufficient and matches Minecraft's internal numeric types.
     *
     * @param key configuration key to query
     * @param def default value if key is missing or invalid
     * @return parsed float value or default if missing/invalid
     */
    public float getOrDefault( String key, float def ) {
        try {
            String value = get(key);
            if (value == null) {
                return def;
            }
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid float value for config key '{}': '{}', using default: {}", key, get(key), def);
            return def;
        }
    } // getOrDefault()

    /**
     * Checks if config is in broken state due to load or parse errors.
     * <p>
     * <b>Broken State:</b> Set when file creation fails, I/O errors occur during
     * reading, or parse errors prevent config interpretation. Indicates config
     * values are unreliable and defaults should be used.
     * <p>
     * <b>Recovery:</b> Call {@link #delete()} to remove broken config, then restart
     * to regenerate with defaults.
     *
     * @return true if config failed to load or parse correctly
     */
    public boolean isBroken() {
        return broken;
    } // isBroken()

    /**
     * Deletes the config file from filesystem for regeneration.
     * <p>
     * <b>Use Case:</b> Remove broken or corrupted config files. Next game start
     * will regenerate config with default values from ConfigProvider.
     * <p>
     * <b>Warning:</b> Destructive operation - user customizations are lost. Only
     * use when config is broken or user explicitly requests reset.
     *
     * @return true if file deletion succeeded, false otherwise
     */
    public boolean delete() {
        LOGGER.warn( "Config '" + request.filename + "' was removed from existence! Restart the game to regenerate it." );
        return request.file.delete();
    } // delete()

    // -- Private Methods --

    private void createConfig() throws IOException {
        try {
            // try creating missing files
            File parentDir = request.file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Failed to create config directory: " + parentDir.getAbsolutePath());
                }
            }

            Files.createFile( request.file.toPath() );

            // write default config data
            PrintWriter writer = new PrintWriter(request.file, "UTF-8");
            writer.write( request.getConfig() );
            writer.close();
        } catch (IOException e) {
            throw new IOException("Failed to create config file: " + request.file.getAbsolutePath(), e);
        }

    } // createConfig()

    private void loadConfig() throws IOException {
        Scanner reader = null;
        try {
            reader = new Scanner( request.file );
            for( int line = 1; reader.hasNextLine(); line ++ ) {
                parseConfigEntry( reader.nextLine(), line );
            }
        } catch (IOException e) {
            throw new IOException("Failed to read config file: " + request.file.getAbsolutePath(), e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    } // loadConfig()

    // Modification by Kaupenjoe
    private void parseConfigEntry( String entry, int line ) {
        if( !entry.isEmpty() && !entry.trim().startsWith( "#" ) ) {
            String[] parts = entry.split("=", 2);
            if( parts.length == 2 ) {
                // Recognizes comments after a value
                String value = parts[1].split(" #")[0].trim();
                String key = parts[0].trim();
                config.put( key, value );
            }else{
                throw new RuntimeException("Syntax error in config file on line " + line + "!");
            }
        }
    } // parseConfigEntry()

} // Class: SimpleConfig