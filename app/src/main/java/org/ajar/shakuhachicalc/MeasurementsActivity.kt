package org.ajar.shakuhachicalc

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import org.ajar.shakuhachicalc.databinding.ActivityMeasurementsBinding

class MeasurementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(MeasurementViewModel::class.java)
        val measurementBinding = DataBindingUtil.setContentView<ActivityMeasurementsBinding>(this, R.layout.activity_measurements)

        measurementBinding.lifecycleOwner = this
        measurementBinding.viewModel = viewModel
    }
}
