package com.geomat.openeclassclient.ui.Login.ExternalAuth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geomat.openeclassclient.databinding.FragmentExternalAuthBinding
import timber.log.Timber


class ExternalAuthFragment : Fragment() {

    val args: ExternalAuthFragmentArgs by navArgs()
    private lateinit var binding: FragmentExternalAuthBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExternalAuthBinding.inflate(inflater)
        if (args.authName.isNotBlank()) (activity as AppCompatActivity).supportActionBar?.title = args.authName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val navController  = findNavController()

        class CustomWebViewClient: WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.contains("eclass-token:")) {
                    Timber.i("Login Response: $url")

                    // Ex eclass-token:a961162%7C28207656262415267923679599
                    //    eclass-token:[username]%7C[        token        ]

                    val usernameAndToken = url.replace("eclass-token:","").split("%7C")

                    val username = usernameAndToken[0]
                    val token = usernameAndToken[1]

                    requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                        .putString("username", username).apply()
                    requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                        .putBoolean("hasLoggedIn", true).apply()
                    requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                        .putString("token", token).apply()

                    navController.navigate(ExternalAuthFragmentDirections.actionWebViewFragmentToMainActivity())
                    CookieManager.getInstance().removeAllCookies {}
                    return true
                }
                return false
            }
        }

        binding.webView.webViewClient = CustomWebViewClient()

        binding.webView.loadUrl(args.url)


    }
}