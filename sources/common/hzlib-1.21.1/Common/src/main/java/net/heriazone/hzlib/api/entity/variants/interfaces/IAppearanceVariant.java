package net.heriazone.hzlib.api.entity.variants.interfaces;

import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Composite variant that bundles texture, model, and animator into one addressable unit.
 * <p>
 * <b>Deprecated name:</b> Retained as a backward-compatibility alias for
 * {@link ICompositeAppearance}. New code should use {@link ICompositeAppearance} directly.
 * This interface will remain until all call sites are migrated.
 *
 * @deprecated Use {@link ICompositeAppearance} — identical contract, aligned naming.
 */
@Deprecated
public interface IAppearanceVariant extends ICompositeAppearance {
    // All methods inherited from ICompositeAppearance.
} // Interface: IAppearanceVariant