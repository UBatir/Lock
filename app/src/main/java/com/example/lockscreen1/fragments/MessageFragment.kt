package com.example.lockscreen1.fragments

import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.interfaces.SenderSms
import kotlinx.android.synthetic.main.message_fragment.*
import java.util.*


open class MessageFragment(private val listener: SenderSms) : Fragment(R.layout.message_fragment) {

    private lateinit var mPeopleList:ArrayList<Map<String, String>>
    private lateinit var mAdapter: SimpleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPeopleList = ArrayList()
        populatePeopleList()

        mAdapter= SimpleAdapter(context,mPeopleList,R.layout.auto_complete_tv, arrayOf("Name","Phone"),
            intArrayOf(R.id.tvNameContact))
        etKomu.setAdapter(mAdapter)
        etKomu.setSelection(etKomu.text!!.length)

        etKomu.onItemClickListener =
            AdapterView.OnItemClickListener { av, _, index, _ ->
                val map = av.getItemAtPosition(index) as Map<*, *>
                val name = map["Name"]
                val number=map["Phone"]
                etKomu.setText("$name\n$number")
                etKomu.setSelection(etKomu.text!!.length)
            }



        btnSendSms.setOnClickListener {
            val number = etKomu.text.toString()
            val sms = etSendSms.text.toString()
            if (number.isNotEmpty() && sms.isNotEmpty()){
                listener.sendSms(number, sms)
                etKomu.text.clear()
                etSendSms.text.clear()
            }else{
                Toast.makeText(requireContext(), " Заполните поля ! " , Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun populatePeopleList() {
        mPeopleList.clear()
        val people = activity!!.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (people != null) {
            while (people.moveToNext())
            {
                val contactName = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val contactId = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts._ID)
                )
                val hasPhone = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if ((Integer.parseInt(hasPhone) > 0)){
                    // You know have the number so now query it like this
                        val phones = activity!!.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                    while (phones!!.moveToNext()){
                        //store numbers and display a dialog letting the user select which.
                        val phoneNumber = phones.getString(
                                phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    val namePhoneType = HashMap<String, String>()
                    namePhoneType["Name"] = contactName
                    namePhoneType["Phone"]=phoneNumber
                    mPeopleList.add(namePhoneType)
                    }
                }
            }
            people?.close()
            activity!!.startManagingCursor(people)
        }
    }
}


