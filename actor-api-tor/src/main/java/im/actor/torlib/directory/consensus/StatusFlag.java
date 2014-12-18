package im.actor.torlib.directory.consensus;

/**
 * Created by ex3ndr on 13.12.14.
 */
public enum StatusFlag {
    AUTHORITY(1), BAD_EXIT(2), EXIT(3), FAST(4), GUARD(5), HS_DIR(6), RUNNING(7), STABLE(8), V2_DIR(9), VALID(10);

    private int val;

    StatusFlag(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static StatusFlag parse(int flag) {
        switch (flag) {
            case 1:
                return AUTHORITY;
            case 2:
                return BAD_EXIT;
            case 3:
                return EXIT;
            case 4:
                return FAST;
            case 5:
                return GUARD;
            case 6:
                return HS_DIR;
            case 7:
                return RUNNING;
            case 8:
                return STABLE;
            case 9:
                return V2_DIR;
            case 10:
                return VALID;
        }
        return null;
    }

    public static StatusFlag parse(String flag) {
        if ("Authority".equals(flag)) {
            return AUTHORITY;
        } else if ("BadExit".equals(flag)) {
            return BAD_EXIT;
        } else if ("Exit".equals(flag)) {
            return EXIT;
        } else if ("Fast".equals(flag)) {
            return FAST;
        } else if ("Guard".equals(flag)) {
            return GUARD;
        } else if ("HSDir".equals(flag)) {
            return HS_DIR;
        } else if ("Running".equals(flag)) {
            return RUNNING;
        } else if ("Stable".equals(flag)) {
            return STABLE;
        } else if ("V2Dir".equals(flag)) {
            return V2_DIR;
        } else if ("Valid".equals(flag)) {
            return VALID;
        } else {
            return null;
        }
    }
}
