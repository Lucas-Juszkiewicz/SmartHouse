package util;

import classes.House;

public class HouseRegistry extends Registry<House> {
    private static final HouseRegistry instance = new HouseRegistry();

    private HouseRegistry() {
        super(House.class);
    }

    public static HouseRegistry getInstance() {
       return instance;
    }
}
