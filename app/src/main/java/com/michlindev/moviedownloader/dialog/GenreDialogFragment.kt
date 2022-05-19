package com.michlindev.moviedownloader.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.michlindev.moviedownloader.databinding.FragmentGenreDialogBinding

class GenreDialogFragment : DialogFragment() {

    private val viewModel: GenreDialogViewModel by activityViewModels()

    companion object {

        const val TAG = "GenreDialogFragment"
        private const val GENRES = "GENRES"

        fun newInstance(title: Array<String>): GenreDialogFragment {
            val args = Bundle()
            args.putStringArray(GENRES, title)
            val fragment = GenreDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentGenreDialogBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = GenreItemAdapter(listOf(), viewModel)
        viewModel.allEnabled.observe(viewLifecycleOwner) { it2 ->
            viewModel.itemList.value?.forEach {
                it.enabled.postValue(it2)
            }
            binding.adapter?.notifyDataSetChanged()
        }

        viewModel.dismissDialog.observe(viewLifecycleOwner){
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.populate(arguments?.getStringArray(GENRES))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

}