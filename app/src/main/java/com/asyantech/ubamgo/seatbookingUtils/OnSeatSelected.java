package com.asyantech.ubamgo.seatbookingUtils;

import android.view.View;

import java.util.List;

public interface OnSeatSelected {
    void onSeatSelected(View view, int position, List<Integer> selected_position, int count);
}
