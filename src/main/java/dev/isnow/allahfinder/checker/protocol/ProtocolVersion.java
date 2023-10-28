package dev.isnow.allahfinder.checker.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ProtocolVersion {
    private static final HashMap<Integer, ProtocolVersion> VERSIONS = new HashMap<>();
    public static final ProtocolVersion v1_7_6 = register(5, "1.7.10", new VersionRange("1.7", 6, 10));
    public static final ProtocolVersion v1_8 = register(47, "1.8/1.8.1/1.8.2/1.8.3/1.8.4/1.8.5/1.8.6/1.8.7/1.8.8/1.8.9");
    public static final ProtocolVersion v1_9 = register(107, "1.9");
    public static final ProtocolVersion v1_9_1 = register(108, "1.9.1");
    public static final ProtocolVersion v1_9_2 = register(109, "1.9.2");
    public static final ProtocolVersion v1_9_3 = register(110, "1.9.3/1.9.4", new VersionRange("1.9", 3, 4));
    public static final ProtocolVersion v1_10 = register(210, "1.10/1.10.1/1.10.2");
    public static final ProtocolVersion v1_11 = register(315, "1.11");
    public static final ProtocolVersion v1_11_1 = register(316, "1.11.1/1.11.2", new VersionRange("1.11", 1, 2));
    public static final ProtocolVersion v1_12 = register(335, "1.12");
    public static final ProtocolVersion v1_12_1 = register(338, "1.12.1");
    public static final ProtocolVersion v1_12_2 = register(340, "1.12.2");
    public static final ProtocolVersion v1_13 = register(393, "1.13");
    public static final ProtocolVersion v1_13_1 = register(401, "1.13.1");
    public static final ProtocolVersion v1_13_2 = register(404, "1.13.2");
    public static final ProtocolVersion v1_14 = register(477, "1.14");
    public static final ProtocolVersion v1_14_1 = register(480, "1.14.1");
    public static final ProtocolVersion v1_14_2 = register(485, "1.14.2");
    public static final ProtocolVersion v1_14_3 = register(490, "1.14.3");
    public static final ProtocolVersion v1_14_4 = register(498, "1.14.4");
    public static final ProtocolVersion v1_15 = register(573, "1.15");
    public static final ProtocolVersion v1_15_1 = register(575, "1.15.1");
    public static final ProtocolVersion v1_15_2 = register(578, "1.15.2");
    public static final ProtocolVersion v1_16 = register(735, "1.16");
    public static final ProtocolVersion v1_16_1 = register(736, "1.16.1");
    public static final ProtocolVersion v1_16_2 = register(751, "1.16.2");
    public static final ProtocolVersion v1_16_3 = register(753, "1.16.3");
    public static final ProtocolVersion v1_16_4 = register(754, "1.16.4/1.16.5", new VersionRange("1.16", 4, 5));
    public static final ProtocolVersion v1_17 = register(755, "1.17");
    public static final ProtocolVersion v1_17_1 = register(756, "1.17.1");
    public static final ProtocolVersion v1_18 = register(757, "1.18.1", new VersionRange("1.18", 0, 1));
    public static final ProtocolVersion v1_18_2 = register(758, "1.18.2");
    public static final ProtocolVersion v1_19 = register(759, "1.19");
    public static final ProtocolVersion v1_19_1 = register(760, "1.19.1/1.19.2", new VersionRange("1.19", 1, 2));
    public static final ProtocolVersion v1_19_3 = register(761, "1.19.3");
    public static final ProtocolVersion v1_19_4 = register(762, "1.19.4");
    public static final ProtocolVersion v1_20 = register(763, "1.20/1.20.1");

    public static final ProtocolVersion v1_20_2 = register(764, "1.20.2");


    public static final ProtocolVersion unknown = register(-1, "UNKNOWN");

    private final int version;
    private final String name;
    private final Set<String> includedVersions;

    public static ProtocolVersion register(int version, String name) {
        return register(version, name, null);
    }
    public static ProtocolVersion register(int version, String name, VersionRange versionRange) {
        ProtocolVersion protocol = new ProtocolVersion(version, name, versionRange);
        VERSIONS.put(protocol.getVersion(), protocol);

        return protocol;
    }

    public static ProtocolVersion getProtocolByName(String name) {
        for (ProtocolVersion protocolVersion : VERSIONS.values()) {
            if(name.equals("1.18")) {
                return ProtocolVersion.v1_18;
            }
            if(name.equals("1.15")) {
                return ProtocolVersion.v1_15;
            }
            if(name.equals("1.16")) {
                return ProtocolVersion.v1_16;
            }
            if (protocolVersion.name.contains(name)) {
                return protocolVersion;
            }
        }
        return ProtocolVersion.v1_8;
    }

    public ProtocolVersion(int version, String name, VersionRange versionRange) {
        this.version = version;
        this.name = name;

        if (versionRange != null) {
            this.includedVersions = new LinkedHashSet<>();

            for(int i = versionRange.rangeFrom(); i <= versionRange.rangeTo(); ++i) {
                if (i == 0) {
                    this.includedVersions.add(versionRange.baseVersion());
                }

                this.includedVersions.add(versionRange.baseVersion() + "." + i);
            }
        } else {
            this.includedVersions = Collections.singleton(name);
        }

    }

    public int getVersion() {
        return this.version;
    }
}
class VersionRange {
    private final String baseVersion;
    private final int rangeFrom;
    private final int rangeTo;

    public VersionRange(String baseVersion, int rangeFrom, int rangeTo) {
        this.baseVersion = baseVersion;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }

    public String baseVersion() {
        return this.baseVersion;
    }

    public int rangeFrom() {
        return this.rangeFrom;
    }

    public int rangeTo() {
        return this.rangeTo;
    }
}
