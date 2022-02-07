package com.sedat.englishvocabularybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sedat.englishvocabularybook.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    @Inject
    lateinit var baseFragmentFactory: BaseFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        supportFragmentManager.fragmentFactory = baseFragmentFactory
        setContentView(view)

        val navController = findNavController(R.id.fragment_container_view)
        viewBinding.bottomNavView.setupWithNavController(navController)
    }
}