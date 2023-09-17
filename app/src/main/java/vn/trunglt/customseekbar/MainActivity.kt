package vn.trunglt.customseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.trunglt.customseekbar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            binding.seekBar.value = binding.edt.text.toString().toFloat()
        }
    }
}