package net.msymbios.rlovelyr.common.util.internal;

import javax.annotation.Nonnull;

/**
 * Represents a version string in the format X.X.X.X with added functionality.
 */
public class Version {

    // -- Variables --

    /**
     * The version string
     */
    @Nonnull
    private String versionString;

    // -- Constructors --

    /**
     * @param versionString the version string.
     */
    public Version(@Nonnull String versionString)
    {
        this.versionString = versionString;
    }

    // -- Inherited Methods --

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version version = (Version) obj;

            if (getMajor() == version.getMajor()) {
                return true;
            } else if (getMinor() == version.getMinor()) {
                return true;
            } else if (getPatch() == version.getPatch()) {
                return true;
            } else if (getBuild() == version.getBuild()) {
                return true;
            }
        }

        return super.equals(obj);
    } // equals ()

    @Override
    public String toString()
    {
        return versionString;
    }

    @Override
    public int hashCode()
    {
        return versionString.hashCode();
    }

    // -- Custom Methods --

    /**
     * @param versionString the string to test.
     * @return true if the string is a valid version string.
     */
    public boolean isValidVersion(@Nonnull String versionString) {
        String[] splitVersionString = versionString.split("\\.");

        if (splitVersionString.length < 1 || splitVersionString.length > 4) return false;

        for (String subVersionString : splitVersionString) {
            if (!subVersionString.matches("[0-9]+")) return false;
        }
        return true;
    } // isValidVersion ()

    /**
     * @param version the version to compare to.
     * @return true if this version is older than the given version.
     */
    public boolean isOlderThan(@Nonnull Version version) {
        if (getMajor() < version.getMajor()) {
            return true;
        } else if (getMinor() < version.getMinor()) {
            return true;
        } else if (getPatch() < version.getPatch()) {
            return true;
        } else if (getBuild() < version.getBuild()) {
            return true;
        }
        return false;
    } // isOlderThan ()

    /**
     * @param version the version to compare to.
     * @return true if this version is newer than the given version.
     */
    public boolean isNewerThan(@Nonnull Version version) {
        if (getMajor() > version.getMajor()) {
            return true;
        } else if (getMinor() > version.getMinor()) {
            return true;
        } else if (getPatch() > version.getPatch()) {
            return true;
        } else if (getBuild() > version.getBuild()) {
            return true;
        }
        return false;
    } // isNewerThan ()

    /**
     * @return the major version number.
     */
    public int getMajor()
    {
        return Integer.parseInt(versionString.split("\\.")[0]);
    }

    /**
     * @return the minor version number.
     */
    public int getMinor() {
        String[] splitVersionString = versionString.split("\\.");
        if (splitVersionString.length < 2) return 0;
        return Integer.parseInt(splitVersionString[1]);
    } // getMinor ()

    /**
     * @return the patch version number.
     */
    public int getPatch() {
        String[] splitVersionString = versionString.split("\\.");
        if (splitVersionString.length < 3) return 0;
        return Integer.parseInt(splitVersionString[2]);
    } // getPatch ()

    /**
     * @return the build version number.
     */
    public int getBuild() {
        String[] splitVersionString = versionString.split("\\.");
        if (splitVersionString.length < 4) return 0;
        return Integer.parseInt(splitVersionString[3]);
    } // getBuild ()

    /**
     * Sets the version string to the given string.
     */
    public void setVersion(@Nonnull String versionString) throws IllegalArgumentException {
        if (isValidVersion(versionString)) {
            this.versionString = versionString;
        } else {
            throw new IllegalArgumentException("Invalid version string: " + versionString);
        }
    } // setVersion ()

} // Class Version