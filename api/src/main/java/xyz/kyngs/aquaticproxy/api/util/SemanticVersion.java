package xyz.kyngs.aquaticproxy.api.util;

import org.jspecify.annotations.NonNull;

/**
 * A basic record to hold semantic version information.
 *
 * @param major The major version.
 * @param minor The minor version.
 * @param patch The patch version.
 * @param snapshot Whether this is a snapshot version.
 * @author kyngs
 */
public record SemanticVersion(int major, int minor, int patch, boolean snapshot) implements Comparable<SemanticVersion> {

    /**
     * Parses a semantic version from a string with format major.minor.patch(-SNAPSHOT)
     *
     * @param version The string to parse.
     * @return The parsed semantic version.
     * @throws IllegalArgumentException If the string is not a valid semantic version.
     */
    public static SemanticVersion parse(String version) throws IllegalArgumentException {
        var split = version.replace("-SNAPSHOT", "").split("\\.");
        if (split.length != 3) {
            throw new IllegalArgumentException("Invalid semantic version: " + version);
        }
        try {
            return new SemanticVersion(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), version.endsWith("-SNAPSHOT"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid semantic version: " + version, e);
        }
    }

    @Override
    public @NonNull String toString() {
        return major + "." + minor + "." + patch + (snapshot ? "-SNAPSHOT" : "");
    }

    /**
     * Compares this version with the specified version.
     *
     * @param other the version to compare to
     * @return -1 if this version is less than the specified version, 0 if they are equal, and 1 if this version is greater
     */
    @Override
    public int compareTo(@NonNull SemanticVersion other) {
        if (major != other.major) {
            return major < other.major ? -1 : 1;
        }
        if (minor != other.minor) {
            return minor < other.minor ? -1 : 1;
        }
        if (patch != other.patch) {
            return patch < other.patch ? -1 : 1;
        }
        return snapshot ?
                (other.snapshot ? 0 : -1)
                :
                (other.snapshot ? 1 : 0);
    }
}
