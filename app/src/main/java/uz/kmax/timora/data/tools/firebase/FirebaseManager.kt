package uz.kmax.timora.data.tools.firebase

import com.google.firebase.database.*

class FirebaseManager() {

    private val database = FirebaseDatabase.getInstance().getReference("Timora")

    // Bir martalik ma'lumot o'qish
    fun <T> readData(path: String, clazz: Class<T>, onComplete: (T?, String?) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(clazz)
                onComplete(data, null)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null, error.message)
            }
        })
    }

    // single adder to firebase Real-time Database
    fun <T> writeData(path: String, data: T, onComplete: (Boolean, String?) -> Unit) {
        database.child(path).setValue(data)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { error ->
                onComplete(false, "Exception Error ${error.message}")
            }
    }

    // Real vaqtda ma'lumot qabul qilish //
    fun <T> observeList(path: String, clazz: Class<T>, onDataChange: (ArrayList<T>?) -> Unit) {
        database.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<T>()
                for (child in snapshot.children) {
                    val item = child.getValue(clazz)
                    if (item != null) {
                        list.add(item)
                    }
                }
                onDataChange(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(null)
            }
        })
    }

    fun <T> readSingleList(path: String, clazz: Class<T>, onDataChange: (ArrayList<T>?) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<T>()
                for (child in snapshot.children) {
                    val item = child.getValue(clazz)
                    if (item != null) {
                        list.add(item)
                    }
                }
                onDataChange(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(null)
            }
        })
    }

    fun observeListVisibly(path: String, onDataChange: (Boolean) -> Unit) {
        database.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.children.count()
                if (count > 0){
                    onDataChange(true)
                }else{
                    onDataChange(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(false)
            }
        })
    }

    fun getChildCount(path: String,onDataChange: (count : Long) -> Unit) {
        database.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange(snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(0)
            }
        })
    }

    fun checkExist(path: String,onComplete: (Boolean) -> Unit){
        database.child(path).addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                onComplete(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(false)
            }

        })
    }

    fun deleteData(path: String, onComplete: (Boolean, String?) -> Unit) {
        database.child(path).removeValue()
            .addOnSuccessListener {
                onComplete(true, null) // Muvaffaqiyatli o'chirildi
            }
            .addOnFailureListener { error ->
                onComplete(false, "Exception Error ${error.message}") // Xatolikni qaytarish
            }
    }

}
