package com.geomat.openeclassclient.screens.Login.InternalAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geomat.openeclassclient.databinding.FragmentInternalAuthBinding
import com.geomat.openeclassclient.screens.Login.ServerSelect.Server
import com.google.android.material.snackbar.Snackbar

class InternalAuthFragment : Fragment() {


    val args: InternalAuthFragmentArgs by navArgs()
    private lateinit var binding: FragmentInternalAuthBinding
    private lateinit var viewModel: InternalAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(InternalAuthViewModel::class.java)
        binding = FragmentInternalAuthBinding.inflate(inflater)
        if (args.authName.isNotBlank()) (activity as AppCompatActivity).supportActionBar?.title = args.authName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateSelectedServer(Server(args.serverName,args.url))

        // Login Successful Event Observer
        viewModel.loginSuccessful.observe(viewLifecycleOwner, Observer { loginSuccessful ->
            if (loginSuccessful) {
                findNavController().navigate(InternalAuthFragmentDirections.actionLoginFragmentToMainActivity())
                viewModel.resetLoginSuccessful()
            }
        })

        // Show SnackBar Event Observer
        viewModel.showSnackBarString.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    it,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.resetSnackbarString()
            }
        })

        binding.loginButton.setOnClickListener {
            val username = binding.userText.editText?.text.toString()
            val password = binding.passText.editText?.text.toString()
            viewModel.login(username,password)
        }

    }
}