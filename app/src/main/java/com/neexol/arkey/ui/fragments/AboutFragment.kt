package com.neexol.arkey.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neexol.arkey.R
import com.neexol.arkey.utils.mainActivity
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.view_toolbar.*

class AboutFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_about, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity().enableNavigateButton(toolbar)
        toolbar.title = getString(R.string.about_app)

        setListeners()
    }

    private fun setListeners() {
        vkTV.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.vk_url))
            }
            startActivity(browserIntent)
        }

        mailTV.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${getString(R.string.author_email_address)}")
            }
            startActivity(emailIntent)
        }

        githubTV.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.source_code_url))
            }
            startActivity(browserIntent)
        }
    }
}