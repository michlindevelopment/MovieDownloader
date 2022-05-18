package com.michlindev.moviedownloader.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.databinding.FragmentGenreDialogBinding
import com.michlindev.moviedownloader.main.MovieItemAdapter

class GenreDialogFragment : DialogFragment() {
    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Hello")
            .setPositiveButton(getString(R.string.ok)) { _,_ -> }
            .create()*/
    private val viewModel: GenreDialogViewModel by activityViewModels()

    companion object {

        const val TAG = "GenreDialogFragment"

        private const val GENRES = "GENRES"

        fun newInstance(title: Array<String>): GenreDialogFragment {
            val args = Bundle()
            args.putStringArray(GENRES, title)
            //args.putString(KEY_SUBTITLE, subTitle)
            val fragment = GenreDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentGenreDialogBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.adapter = GenreItemAdapter(listOf(), viewModel)
       /* viewModel.itemList.observe(viewLifecycleOwner) {
            binding.adapter?.notifyDataSetChanged()
        }*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupView(view)
        //setupClickListeners(view)

        val foo1 = mutableListOf<Genre>()

        val foo2 = arguments?.getStringArray(GENRES)
        val foo3 = MutableLiveData(false)


        if (foo2 != null) {
            foo2.forEach {
                foo1.add(Genre(
                    genre = it,
                    enabled = false))
            }
        }

        viewModel.itemList.postValue(foo1)
        DLog.d("")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun setupView(view: View) {
        //view.tvTitle.text = arguments?.getString(KEY_TITLE)
        //view.tvSubTitle.text = arguments?.getString(KEY_SUBTITLE)
    }

    private fun setupClickListeners(view: View) {
        /*view.btnPositive.setOnClickListener {
            // TODO: Do some task here
            dismiss()
        }
        view.btnNegative.setOnClickListener {
            // TODO: Do some task here
            dismiss()
        }*/
    }
}