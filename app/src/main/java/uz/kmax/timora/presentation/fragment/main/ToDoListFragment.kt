package uz.kmax.timora.presentation.fragment.main

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.databinding.FragmentToDoListBinding

class ToDoListFragment : BaseFragmentWC<FragmentToDoListBinding>(FragmentToDoListBinding::inflate) {
    override fun onViewCreated() {
        binding.recycleToDoListView.layoutManager = LinearLayoutManager(requireContext())
//        binding.recycleToDoListView.adapter = adapter
        binding.add.setOnClickListener {
            Toast.makeText(requireContext(), "ADD clicked !", Toast.LENGTH_SHORT).show()
        }
    }
}