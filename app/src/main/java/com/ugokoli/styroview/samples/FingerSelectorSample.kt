package com.ugokoli.styroview.samples

import android.os.Bundle
import android.widget.RadioGroup
import com.ugokoli.styroview.FingerSelector
import com.ugokoli.styroview.R
import com.ugokoli.styroview.constants.Finger
import com.ugokoli.styroview.constants.Hand
import kotlinx.android.synthetic.main.activity_finger_selector_sample.*

class FingerSelectorSample : BaseSample(), RadioGroup.OnCheckedChangeListener, FingerSelector.FingerSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger_selector_sample)

        radioFinger.setOnCheckedChangeListener(this)
        fingerSelector.fingerSelectedListener = this

        onFingerSelected(fingerSelector.hand, fingerSelector.finger)
    }

    override fun onCheckedChanged(radioGroup: RadioGroup?, i: Int) {
        if (i == R.id.radioLeftThumb) {
            fingerSelector.hand = Hand.LEFT
        } else if (i == R.id.radioRightThumb) {
            fingerSelector.hand = Hand.RIGHT
        }
    }

    override fun onFingerSelected(hand: Hand, finger: Finger) {
        selectedTxt.setText(String.format(
                getString(R.string.select_finger),
                hand.toString(),
                finger.toString()
        ))

        when (hand) {
            Hand.LEFT -> {
                radioFinger.check(R.id.radioLeftThumb)
            }
            Hand.RIGHT -> {
                radioFinger.check(R.id.radioRightThumb)
            }
            else -> {
            }
        }
    }
}
