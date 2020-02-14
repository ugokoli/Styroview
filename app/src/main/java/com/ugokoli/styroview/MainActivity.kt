package com.ugokoli.styroview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ugokoli.styroview.samples.FingerSelectorSample
import com.ugokoli.styroview.samples.SplitEditTextSample
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSplitEditText.setOnClickListener(this)
        btnFingerSelector.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val intent = Intent()

        when (v?.id) {
            R.id.btnSplitEditText -> {
                intent.setClass(this, SplitEditTextSample::class.java)
            }
            R.id.btnFingerSelector -> {
                intent.setClass(this, FingerSelectorSample::class.java)
            }
        }

        startActivity(intent)
    }
}
