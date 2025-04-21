package uz.kmax.timora.data.adapter

import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.timora.R
import uz.kmax.timora.databinding.ItemNotificationListBinding
import uz.kmax.timora.domain.data.main.NotificationData

class NotificationListAdapter : BaseRecycleViewDU<ItemNotificationListBinding,NotificationData>(ItemNotificationListBinding::inflate) {
    override fun areContentsTheSame(
        oldItem: NotificationData,
        newItem: NotificationData
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: NotificationData,
        newItem: NotificationData
    ) = oldItem.title == newItem.title

    override fun bind(binding: ItemNotificationListBinding, item: NotificationData) {
        binding.notifyName.text = item.title
        binding.editNotify.setOnClickListener {
            sendMessage(2,"EDIT")
        }

        binding.deleteNotify.setOnClickListener {
            sendMessage(3,item.id)
        }

        binding.notificationLayout.setOnClickListener {
            sendMessage(1,"MAIN")
        }

        binding.imageNotification.setImageResource(R.drawable.app_logo)
    }
}