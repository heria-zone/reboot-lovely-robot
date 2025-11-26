package net.msymbios.llovelyr;

import net.fabricmc.api.ModInitializer;

import net.msymbios.llovelyr.common.util.ObjectUtil;
import net.msymbios.llovelyr.common.util.internal.Version;
import net.msymbios.llovelyr.source.commands.LovelyCommandArguments;
import net.msymbios.llovelyr.source.entity.LovelyEntities;
import net.msymbios.llovelyr.source.events.LovelyEvents;
import net.msymbios.llovelyr.source.groups.LovelyGroups;
import net.msymbios.llovelyr.source.items.LovelyItems;
import net.msymbios.llovelyr.source.recipes.LovelyRecipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

/**
 * Main mod class for Legacy variant (Fabric loader).
 * <p>
 * <b>Architecture:</b> Implements Fabric's ModInitializer for mod entry point,
 * coordinating registration of all mod content (items, entities, creative tabs,
 * events) through direct registry calls and event system.
 * <p>
 * <b>Registration Flow:</b> Single onInitialize method handles all registration
 * in explicit order: GeckoLib → creative tabs → items → events → entities.
 * Simpler than Forge's multiphase lifecycle.
 * <p>
 * <b>Mod ID:</b> "llovelyr" - Must match fabric.mod.json entry for Fabric
 * to recognize and load the mod.
 */
public class LovelyLegacy implements ModInitializer {

	// -- Constants --

	/**
	 * Mod identifier used across all registrations and resource locations.
	 * <p>
	 * <b>Naming Convention:</b> "llovelyr" = Legacy Lovely Robot variant.
	 */
	public static final String MODID = "llovelyr";

	/**
	 * Mod version for NBT migration and compatibility tracking.
	 * <p>
	 * <b>Dynamic Resolution:</b> Reads version from package specification metadata
	 * (populated by build system from fabric.mod.json), falling back to "99999.0.0.0"
	 * for development environments where package metadata is unavailable.
	 * <p>
	 * <b>Usage:</b> Stored in entity NBT data to enable version-aware deserialization
	 * when entity structure changes between releases. Supports backward compatibility
	 * by allowing migration logic based on data version.
	 * <p>
	 * <b>Fallback Behavior:</b> Development fallback "99999.0.0.0" ensures dev builds
	 * always appear "newer" than release builds, preventing false migration triggers
	 * during testing.
	 * <p>
	 * <i>Note:</i> Version format follows semantic versioning (major.minor.patch.build)
	 * matching fabric.mod.json specification for consistency with Fabric mod metadata.
	 */
	public static final Version VERSION = new Version(ObjectUtil.coalesce(LovelyLegacy.class.getPackage().getSpecificationVersion(), "99999.0.0.0"));

	/**
	 * SLF4J logger for mod-wide logging with automatic mod name prefixing.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// -- Inherited Methods --

	/**
	 * Mod initialization invoked by Fabric during mod loading phase.
	 * <p>
	 * <b>Registration Order:</b>
	 * 1. GeckoLib animation library initialization
	 * 2. Creative tabs (must precede items for tab population)
	 * 3. Items (registers with Fabric registry)
	 * 4. Event handlers (crafting callbacks, mixins)
	 * 5. Entities (robot entity types and attributes)
	 * <p>
	 * <b>Design Decision:</b> Explicit ordering ensures dependencies are
	 * satisfied (e.g., tabs exist before items reference them). Fabric's
	 * single-phase initialization requires careful sequencing.
	 * <p>
	 * <b>GeckoLib:</b> Must initialize before any GeckoLib-dependent classes
	 * (animated entities, models) are loaded to prevent animation system errors.
	 */
	@Override
	public void onInitialize() {
		GeckoLib.initialize();

		LovelyGroups.register();
		LovelyItems.register();
		LovelyEvents.register();
		LovelyEntities.register();
		LovelyRecipes.register();

		LovelyCommandArguments.register();
		LOGGER.info("Hello Fabric world!");
	} // onInitialize()

} // Class: LovelyLegacy