package com.example.fragment

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a container for the fragment
        val container = FrameLayout(this).apply {
            id = android.view.View.generateViewId()
        }
        setContentView(container)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(container.id, MainFragment())
            }
        }
    }
}