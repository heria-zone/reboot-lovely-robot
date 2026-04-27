/**
 * <p>Framework-level variant implementations providing standard variant types.<p>
 * <p>
 * <b>Package Purpose:</b> Contains concrete implementations of variant interfaces
 * that provide common functionality for texture, model, and animator variants.
 * These implementations serve as building blocks for mod-specific variant systems.
 * <p>
 * <b>Architecture:</b> Framework layer provides pure Java implementations with
 * minimal dependencies, enabling easy testing and reuse across different
 * Minecraft versions and mod loaders.
 * <p>
 * <b>Key Classes:</b>
 * <ul>
 * <li>{@link net.heriazone.hzlib.framework.entity.variants.StandardTextureVariant} - Path-based texture variants</li>
 * <li>{@link net.heriazone.hzlib.framework.entity.variants.StandardModelVariant} - Path-based model variants</li>
 * <li>{@link net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant} - Path-based animator variants</li>
 * <li>{@link net.heriazone.hzlib.framework.entity.variants.ColorTextureVariant} - Color palette texture variants</li>
 * </ul>
 */
package net.heriazone.hzlib.framework.entity.variants;