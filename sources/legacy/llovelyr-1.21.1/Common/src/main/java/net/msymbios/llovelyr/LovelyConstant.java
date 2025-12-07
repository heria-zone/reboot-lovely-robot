package net.msymbios.llovelyr;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class LovelyConstant {

    // -- Constants --

    /**
     * Mod identifier used across all registrations and resource locations.
     * <p>
     * <b>Naming Convention:</b> "llovelyr" = Legacy Lovely Robot variant.
     */
    public static final String MODID = "llovelyr";

    /**
     * SLF4J logger for mod-wide logging with automatic mod name prefixing.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final net.msymbios.llovelyr.framework.utils.Version VERSION = new net.msymbios.llovelyr.framework.utils.Version("1.0");

    public static class Version {

        public static final net.msymbios.llovelyr.framework.utils.Version CURRENT = new net.msymbios.llovelyr.framework.utils.Version("1.0");

    } // Class: Version

} // LovelyConstant