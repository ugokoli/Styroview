package com.ugokoli.styroview.constants

/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/17/2020
 */

enum class Hand {
    RIGHT, LEFT;

    companion object {
        fun valueOf(value: Int) = values().first{ it.ordinal == value }
    }
}

enum class Finger {
    THUMB, INDEX, MIDDLE, RING, PINKY, NONE;

    companion object {
        fun valueOf(value: Int) = values().first{ it.ordinal == value }
    }
}
