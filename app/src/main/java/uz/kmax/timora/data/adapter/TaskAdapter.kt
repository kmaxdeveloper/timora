package uz.kmax.timora.data.adapter

import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.timora.databinding.ItemToDoListBinding
import uz.kmax.timora.domain.data.main.TaskData

class TaskAdapter : BaseRecycleViewDU<ItemToDoListBinding,TaskData>(ItemToDoListBinding::inflate) {
    override fun areContentsTheSame(oldItem: TaskData, newItem: TaskData) = oldItem == newItem

    override fun areItemsTheSame(oldItem: TaskData, newItem: TaskData) = oldItem.id == newItem.id

    override fun bind(binding: ItemToDoListBinding, item: TaskData) {
        binding.taskCheckBox.setOnClickListener {

        }

        binding.taskTitle.text = item.title
    }
}