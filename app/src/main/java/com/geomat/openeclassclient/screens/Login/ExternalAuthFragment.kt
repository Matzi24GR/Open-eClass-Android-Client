package com.geomat.openeclassclient.screens.Login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geomat.openeclassclient.R
import timber.log.Timber


class ExternalAuthFragment : Fragment() {

    val args: ExternalAuthFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_external_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val webView = view.findViewById<WebView>(R.id.webView)

        val navController  = findNavController()

        class CustomWebViewClient: WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                Toast.makeText(requireContext(),url,Toast.LENGTH_SHORT).show()
                if (url.contains("eclass-token:")) {
                    Timber.i("Login Response: $url")

                    // Ex eclass-token:a961162%7C28207agpmpvl1islpr8sobmm8n
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

                    webView.clearCache(true)
                    navController.navigate(ExternalAuthFragmentDirections.actionWebViewFragmentToMainActivity())
                    return true
                }
                return false
            }
        }

        webView.webViewClient = CustomWebViewClient()


        webView.loadUrl(args.url)


    }
}