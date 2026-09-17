package xyz.kyngs.aquaticproxy.api.network.protocol;

public record ProtocolVersion(int version, String... friendlyNames) {

    public static final ProtocolVersion UNKNOWN = new ProtocolVersion(-1, "Unknown");
    public static final ProtocolVersion MINECRAFT_1_7_2 = new ProtocolVersion(4,
            "1.7.2", "1.7.3", "1.7.4", "1.7.5");
    public static final ProtocolVersion MINECRAFT_1_7_6 = new ProtocolVersion(5,
            "1.7.6", "1.7.7", "1.7.8", "1.7.9", "1.7.10");
    public static final ProtocolVersion MINECRAFT_1_8 = new ProtocolVersion(47,
            "1.8", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9");
    public static final ProtocolVersion MINECRAFT_1_9 = new ProtocolVersion(107, "1.9");
    public static final ProtocolVersion MINECRAFT_1_9_1 = new ProtocolVersion(108, "1.9.1");
    public static final ProtocolVersion MINECRAFT_1_9_2 = new ProtocolVersion(109, "1.9.2");
    public static final ProtocolVersion MINECRAFT_1_9_4 = new ProtocolVersion(110, "1.9.3", "1.9.4");
    public static final ProtocolVersion MINECRAFT_1_10 = new ProtocolVersion(210, "1.10", "1.10.1", "1.10.2");
    public static final ProtocolVersion MINECRAFT_1_11 = new ProtocolVersion(315, "1.11");
    public static final ProtocolVersion MINECRAFT_1_11_1 = new ProtocolVersion(316, "1.11.1", "1.11.2");
    public static final ProtocolVersion MINECRAFT_1_12 = new ProtocolVersion(335, "1.12");
    public static final ProtocolVersion MINECRAFT_1_12_1 = new ProtocolVersion(338, "1.12.1");
    public static final ProtocolVersion MINECRAFT_1_12_2 = new ProtocolVersion(340, "1.12.2");
    public static final ProtocolVersion MINECRAFT_1_13 = new ProtocolVersion(393, "1.13");
    public static final ProtocolVersion MINECRAFT_1_13_1 = new ProtocolVersion(401, "1.13.1");
    public static final ProtocolVersion MINECRAFT_1_13_2 = new ProtocolVersion(404, "1.13.2");
    public static final ProtocolVersion MINECRAFT_1_14 = new ProtocolVersion(477, "1.14");
    public static final ProtocolVersion MINECRAFT_1_14_1 = new ProtocolVersion(480, "1.14.1");
    public static final ProtocolVersion MINECRAFT_1_14_2 = new ProtocolVersion(485, "1.14.2");
    public static final ProtocolVersion MINECRAFT_1_14_3 = new ProtocolVersion(490, "1.14.3");
    public static final ProtocolVersion MINECRAFT_1_14_4 = new ProtocolVersion(498, "1.14.4");
    public static final ProtocolVersion MINECRAFT_1_15 = new ProtocolVersion(573, "1.15");
    public static final ProtocolVersion MINECRAFT_1_15_1 = new ProtocolVersion(575, "1.15.1");
    public static final ProtocolVersion MINECRAFT_1_15_2 = new ProtocolVersion(578, "1.15.2");
    public static final ProtocolVersion MINECRAFT_1_16 = new ProtocolVersion(735, "1.16");
    public static final ProtocolVersion MINECRAFT_1_16_1 = new ProtocolVersion(736, "1.16.1");
    public static final ProtocolVersion MINECRAFT_1_16_2 = new ProtocolVersion(751, "1.16.2");
    public static final ProtocolVersion MINECRAFT_1_16_3 = new ProtocolVersion(753, "1.16.3");
    public static final ProtocolVersion MINECRAFT_1_16_4 = new ProtocolVersion(754, "1.16.4", "1.16.5");
    public static final ProtocolVersion MINECRAFT_1_17 = new ProtocolVersion(755, "1.17");
    public static final ProtocolVersion MINECRAFT_1_17_1 = new ProtocolVersion(756, "1.17.1");
    public static final ProtocolVersion MINECRAFT_1_18 = new ProtocolVersion(757, "1.18", "1.18.1");
    public static final ProtocolVersion MINECRAFT_1_18_2 = new ProtocolVersion(758, "1.18.2");
    public static final ProtocolVersion MINECRAFT_1_19 = new ProtocolVersion(759, "1.19");
    public static final ProtocolVersion MINECRAFT_1_19_1 = new ProtocolVersion(760, "1.19.1", "1.19.2");
    public static final ProtocolVersion MINECRAFT_1_19_3 = new ProtocolVersion(761, "1.19.3");
    public static final ProtocolVersion MINECRAFT_1_19_4 = new ProtocolVersion(762, "1.19.4");
    public static final ProtocolVersion MINECRAFT_1_20 = new ProtocolVersion(763, "1.20", "1.20.1");
    public static final ProtocolVersion MINECRAFT_1_20_2 = new ProtocolVersion(764, "1.20.2");
    public static final ProtocolVersion MINECRAFT_1_20_3 = new ProtocolVersion(765, "1.20.3", "1.20.4");
    public static final ProtocolVersion MINECRAFT_1_20_5 = new ProtocolVersion(766, "1.20.5", "1.20.6");
    public static final ProtocolVersion MINECRAFT_1_21 = new ProtocolVersion(767, "1.21", "1.21.1");
    public static final ProtocolVersion MINECRAFT_1_21_2 = new ProtocolVersion(768, "1.21.2", "1.21.3");
    public static final ProtocolVersion MINECRAFT_1_21_4 = new ProtocolVersion(769, "1.21.4");
    public static final ProtocolVersion MINECRAFT_1_21_5 = new ProtocolVersion(770, "1.21.5");
    public static final ProtocolVersion MINECRAFT_1_21_6 = new ProtocolVersion(771, "1.21.6");
    public static final ProtocolVersion MINECRAFT_1_21_7 = new ProtocolVersion(772, "1.21.7", "1.21.8");
    public static final ProtocolVersion MINECRAFT_1_21_9 = new ProtocolVersion(773, "1.21.9", "1.21.10");
    public static final ProtocolVersion MINECRAFT_1_21_11 = new ProtocolVersion(774, "1.21.11");
    public static final ProtocolVersion MINECRAFT_26_1 = new ProtocolVersion(775, "26.1", "26.1.1", "26.1.2");
    public static final ProtocolVersion MINECRAFT_26_2 = new ProtocolVersion(776, "26.2");

    public static final ProtocolVersion LATEST = MINECRAFT_26_2;

    public boolean isNewerThan(ProtocolVersion other) {
        return this.version > other.version;
    }

    public String getNewestFriendlyName() {
        if (friendlyNames.length == 0) {
            return String.valueOf(version);
        }
        return friendlyNames[friendlyNames.length - 1];
    }

    public static ProtocolVersion fromId(int version) {
        return switch (version) {
            case -1 -> UNKNOWN;
            case 4 -> MINECRAFT_1_7_2;
            case 5 -> MINECRAFT_1_7_6;
            case 47 -> MINECRAFT_1_8;
            case 107 -> MINECRAFT_1_9;
            case 108 -> MINECRAFT_1_9_1;
            case 109 -> MINECRAFT_1_9_2;
            case 110 -> MINECRAFT_1_9_4;
            case 210 -> MINECRAFT_1_10;
            case 315 -> MINECRAFT_1_11;
            case 316 -> MINECRAFT_1_11_1;
            case 335 -> MINECRAFT_1_12;
            case 338 -> MINECRAFT_1_12_1;
            case 340 -> MINECRAFT_1_12_2;
            case 393 -> MINECRAFT_1_13;
            case 401 -> MINECRAFT_1_13_1;
            case 404 -> MINECRAFT_1_13_2;
            case 477 -> MINECRAFT_1_14;
            case 480 -> MINECRAFT_1_14_1;
            case 485 -> MINECRAFT_1_14_2;
            case 490 -> MINECRAFT_1_14_3;
            case 498 -> MINECRAFT_1_14_4;
            case 573 -> MINECRAFT_1_15;
            case 575 -> MINECRAFT_1_15_1;
            case 578 -> MINECRAFT_1_15_2;
            case 735 -> MINECRAFT_1_16;
            case 736 -> MINECRAFT_1_16_1;
            case 751 -> MINECRAFT_1_16_2;
            case 753 -> MINECRAFT_1_16_3;
            case 754 -> MINECRAFT_1_16_4;
            case 755 -> MINECRAFT_1_17;
            case 756 -> MINECRAFT_1_17_1;
            case 757 -> MINECRAFT_1_18;
            case 758 -> MINECRAFT_1_18_2;
            case 759 -> MINECRAFT_1_19;
            case 760 -> MINECRAFT_1_19_1;
            case 761 -> MINECRAFT_1_19_3;
            case 762 -> MINECRAFT_1_19_4;
            case 763 -> MINECRAFT_1_20;
            case 764 -> MINECRAFT_1_20_2;
            case 765 -> MINECRAFT_1_20_3;
            case 766 -> MINECRAFT_1_20_5;
            case 767 -> MINECRAFT_1_21;
            case 768 -> MINECRAFT_1_21_2;
            case 769 -> MINECRAFT_1_21_4;
            case 770 -> MINECRAFT_1_21_5;
            case 771 -> MINECRAFT_1_21_6;
            case 772 -> MINECRAFT_1_21_7;
            case 773 -> MINECRAFT_1_21_9;
            case 774 -> MINECRAFT_1_21_11;
            case 775 -> MINECRAFT_26_1;
            case 776 -> MINECRAFT_26_2;
            default -> new ProtocolVersion(version, "Unknown Version ID " + version);
        };
    }
}
