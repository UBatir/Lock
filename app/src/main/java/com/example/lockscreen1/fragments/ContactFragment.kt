package com.example.lockscreen1.fragments


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.ui.CallInterface
import com.example.lockscreen1.ui.ContactAdapter
import com.example.lockscreen1.ui.ContactData
import kotlinx.android.synthetic.main.contacts_framgent.*


class ContactFragment : Fragment(R.layout.contacts_framgent), CallInterface {
    var contacts = ArrayList<String?>()
    var addBtn: Button? = null
    private val mAdapter = ContactAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv1.adapter = mAdapter

        val hasReadContactPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }
        if (READ_CONTACTS_GRANTED) {
            loadContacts()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                if (!(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                    READ_CONTACTS_GRANTED = true
                    loadContacts()
                }
                addBtn!!.isEnabled = READ_CONTACTS_GRANTED
            }

        }
    }
    private fun loadContacts() {
        contacts.clear()
        val contentResolver = activity?.contentResolver
        val cursor = contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // получаем каждый контакт
                val contact = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                val numberContact = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                mAdapter.item.add(ContactData(contact, numberContact))

            }
            cursor.close()
        }
    }
    companion object {
        private const val REQUEST_CODE_READ_CONTACTS = 1
        private var READ_CONTACTS_GRANTED = false
    }

    override fun onCallItemClick(number: String) {
        val dial = "tel:${number}"
        startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
    }
}


