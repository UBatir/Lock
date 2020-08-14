package com.example.lockscreen1.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.interfaces.CallInterface
import com.example.lockscreen1.ui.ContactAdapter
import com.example.lockscreen1.data.ContactData
import com.example.lockscreen1.extentions.config
import com.example.lockscreen1.extentions.getAvailableSIMCardLabels
import com.example.lockscreen1.ui.LockScreenActivity
import com.simplemobiletools.commons.extensions.telecomManager
import kotlinx.android.synthetic.main.contacts_framgent.*


class ContactFragment : Fragment(R.layout.contacts_framgent),
    CallInterface {
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

                mAdapter.item.add(
                    ContactData(
                        contact,
                        numberContact
                    )
                )

            }
            cursor.close()
        }
    }
    companion object {
        private const val REQUEST_CODE_READ_CONTACTS = 1
        private var READ_CONTACTS_GRANTED = false
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCallItemClick(number: String) {
        val mFragment = InLineCall()
        val mBundle = Bundle()
        mBundle.putString("number", number)
        mFragment.arguments = mBundle
        activity?.supportFragmentManager?.beginTransaction()!!
            .replace(R.id.fragment_container, mFragment).commit()
        Toast.makeText(
            requireContext(),
            "Идет набор на номер : $number",
            Toast.LENGTH_SHORT
        ).show()
        if (number.isNotEmpty()) {
            val telecomManager = context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val uri = Uri.fromParts("tel", number, null)
            //   val extras = Bundle()
            startInitCall(activity?.intent,number){
                Bundle().apply {
                    putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, it)
                    putBoolean(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, false)
                    putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, false)
                    telecomManager.placeCall(uri, this)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startInitCall(intent: Intent?, phoneNumber: String, callback: (handle: PhoneAccountHandle) -> Unit){
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val defaultHandle = activity?.telecomManager?.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)


        when {
            intent?.hasExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE) == true -> callback(intent.getParcelableExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE)!!)
            activity?.config?.getCustomSIM(phoneNumber)?.isNotEmpty() == true -> {
                val storedLabel = Uri.decode(activity?.config?.getCustomSIM(phoneNumber))
                val availableSIMs = activity?.getAvailableSIMCardLabels()
                val firstornull = availableSIMs?.firstOrNull { it.label == storedLabel }?.handle ?: availableSIMs?.first()?.handle
                if (firstornull != null) {
                    callback(firstornull)
                }
            }
            defaultHandle != null -> callback(defaultHandle)
            else -> {
                SelectSIMDialog(requireActivity() as LockScreenActivity, phoneNumber) { handle ->
                    callback(handle)
                }
            }
        }
    }
}


