package com.asyantech.ubamgo.seatbookingUtils;

public class EdgeItem extends AbstractItem {

    public EdgeItem(String label) {
        super(label);
    }

    @Override
    public int getType() {
        return TYPE_EDGE;
    }

}
