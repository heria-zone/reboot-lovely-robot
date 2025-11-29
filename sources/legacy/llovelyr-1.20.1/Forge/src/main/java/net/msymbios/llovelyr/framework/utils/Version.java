package net.msymbios.llovelyr.common.util.internal;

import org.jetbrains.annotations.NotNull;

/**
 * Semantic version parser with comparison operations for NBT upgrade paths.
 * <p>
 * <b>Architecture:</b> Supports entity data migration between mod versions by
 * enabling version-aware NBT deserialization. Allows backward compatibility
 * when entity structure evolves across releases.
 * <p>
 * <b>Format:</b> Accepts X.X.X.X format (major.minor.patch.build) with
 * flexible component count (1-4 segments).
 */
public class Version {

    // -- Variables --

    @NotNull
    private String versionString;

    // -- Constructors --

    /**
     * @param versionString the version string in X.X.X.X format
     */
    public Version(@NotNull String versionString) {
        this.versionString = versionString;
    } // Constructor: Version()

    // -- Inherited Methods --

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version version) {
            if (getMajor() == version.getMajor()) return true;
            else if (getMinor() == version.getMinor()) return true;
            else if (getPatch() == version.getPatch()) return true;
            else if (getBuild() == version.getBuild()) return true;
        }
        return super.equals(obj);
    } // equals()

    @Override
    public String toString() {
        return versionString;
    } // toString()

    @Override
    public int hashCode() {
        return versionString.hashCode();
    } // hashCode()

    // -- Custom Methods --

    /**
     * Validates version string format against semantic versioning rules.
     * 
     * @param versionString the string to test
     * @return true if the string is a valid version string
     */
    public boolean isValidVersion(@NotNull String versionString) {
        String[] splitVersionString = versionString.split("\\.");

        if (splitVersionString.length < 1 || splitVersionString.length > 4) return false;

        for (String subVersionString : splitVersionString) {
            if (!subVersionString.matches("[0-9]+")) return false;
        }
        return true;
    } // isValidVersion()

    /**
     * Compares version hierarchy to determine if upgrade path is needed.
     * 
     * @param version the version to compare to
     * @return true if this version is older than the given version
     */
    public boolean isOlderThan(@NotNull Version version) {
        if (getMajor() < version.getMajor()) return true;
        else if (getMinor() < version.getMinor()) return true;
        else if (getPatch() < version.getPatch()) return true;
        else if (getBuild() < version.getBuild()) return true;
        return false;
    } // isOlderThan()

    /**
     * Compares version hierarchy to determine if data is from newer version.
     * 
     * @param version the version to compare to
     * @return true if this version is newer than the given version
     */
    public boolean isNewerThan(@NotNull Version version) {
        if (getMajor() > version.getMajor()) return true;
        else if (getMinor() > version.getMinor()) return true;
        else if (getPatch() > version.getPatch()) return true;
        else if (getBuild() > version.getBuild()) return true;
        return false;
    } // isNewerThan()

    /**
     * @return the major version number
     */
    public int getMajor() {
        return Integer.parseInt(versionString.split("\\.")[0]);
    } // getMajor()

    /**
     * @return the minor version number
     */
    public int getMinor() {
        String[] splitVersionString = versionString.split("\\.");
        if (splitVersionString.length < 2) return 0;
        return Integer.parseInt(splitVersionString[1]);
    } // getMinor()

    /**
     * @return the patch version number
     */
    public int getPatch() {
        String[] splitVersionString = versionString.split("\\.");
        if (splitVersionString.length < 3) return 0;
        return Integer.parseInt(splitVersionString[2]);
    } // getPatch()

    /**
     * @return the build version number
     */
    public int getBuild() {
        String[] splitVersionString = versionString.split("\\.");
        if (splitVersionString.length < 4) return 0;
        return Integer.parseInt(splitVersionString[3]);
    } // getBuild()

    /**
     * Updates version string with validation.
     * 
     * @param versionString the new version string
     * @throws IllegalArgumentException if version string format is invalid
     */
    public void setVersion(@NotNull String versionString) throws IllegalArgumentException {
        if (isValidVersion(versionString)) this.versionString = versionString;
        else throw new IllegalArgumentException("Invalid version string: " + versionString);
    } // setVersion()

} // Class: Version
