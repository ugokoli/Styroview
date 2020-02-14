package com.ugokoli.styroview.samples

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.ugokoli.styroview.R

import kotlinx.android.synthetic.main.activity_finger_selector_sample.*

class FingerSelectorSample : BaseSample() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger_selector_sample)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
