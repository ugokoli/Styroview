package com.ugokoli.styroview.samples

import android.os.Bundle
import com.ugokoli.styroview.FingerSelector
import com.ugokoli.styroview.R
import com.ugokoli.styroview.constants.Finger
import com.ugokoli.styroview.constants.Hand
import kotlinx.android.synthetic.main.activity_finger_selector_sample.*

class FingerSelectorSample : BaseSample(), FingerSelector.FingerSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger_selector_sample)
    }

    override fun onFingerSelected(hand: Hand, finger: Finger) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
