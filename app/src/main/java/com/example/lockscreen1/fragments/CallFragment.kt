package com.example.lockscreen1.fragments

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lockscreen1.R
import com.example.lockscreen1.extentions.addCharacter
import com.example.lockscreen1.extentions.config
import com.example.lockscreen1.extentions.getKeyEvent
import com.example.lockscreen1.helpers.SpeedDial
import com.example.lockscreen1.interfaces.CallContact
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.extensions.performHapticFeedback
import com.simplemobiletools.commons.extensions.value
import com.simplemobiletools.commons.models.SimpleContact
import kotlinx.android.synthetic.main.call_fragment.*
import kotlinx.android.synthetic.main.dialpad.*
import java.util.*


class CallFragment(private val listener: CallContact): Fragment(R.layout.call_fragment) {

    private var allContacts = ArrayList<SimpleContact>()
    private var speedDialValues = ArrayList<SpeedDial>()
    private var privateCursor: Cursor? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speedDialValues = context?.config!!.getSpeedDialValues()
        privateCursor = activity?.getMyContactsCursor()?.loadInBackground()

        dialpad_0_holder.setOnClickListener { dialpadPressed('0', it) }
        dialpad_1.setOnClickListener { dialpadPressed('1', it) }
        dialpad_2.setOnClickListener { dialpadPressed('2', it) }
        dialpad_3.setOnClickListener { dialpadPressed('3', it) }
        dialpad_4.setOnClickListener { dialpadPressed('4', it) }
        dialpad_5.setOnClickListener { dialpadPressed('5', it) }
        dialpad_6.setOnClickListener { dialpadPressed('6', it) }
        dialpad_7.setOnClickListener { dialpadPressed('7', it) }
        dialpad_8.setOnClickListener { dialpadPressed('8', it) }
        dialpad_9.setOnClickListener { dialpadPressed('9', it) }

//        dialpad_1.setOnLongClickListener { speedDial(1); true }
//        dialpad_2.setOnLongClickListener { speedDial(2); true }
//        dialpad_3.setOnLongClickListener { speedDial(3); true }
//        dialpad_4.setOnLongClickListener { speedDial(4); true }
//        dialpad_5.setOnLongClickListener { speedDial(5); true }
//        dialpad_6.setOnLongClickListener { speedDial(6); true }
//        dialpad_7.setOnLongClickListener { speedDial(7); true }
//        dialpad_8.setOnLongClickListener { speedDial(8); true }
//        dialpad_9.setOnLongClickListener { speedDial(9); true }

        dialpad_0_holder.setOnLongClickListener { dialpadPressed('+', null); true }
        dialpad_asterisk.setOnClickListener { dialpadPressed('*', it) }
        dialpad_hashtag.setOnClickListener { dialpadPressed('#', it) }
        dialpad_clear_char.setOnClickListener { clearChar(it) }
        dialpad_clear_char.setOnLongClickListener { clearInput(); true }
        dialpad_call_button.setOnClickListener {
            listener.callContact()
        Toast.makeText(requireContext()," click", Toast.LENGTH_SHORT).show()
        }
       // SimpleContactsHelper(requireContext()).getAvailableContacts(false) { gotContacts(it) }
        disableKeyboardPopping()


    }
    override fun onResume() {
        super.onResume()
        dialpad_clear_char.applyColorFilter(context?.config!!.textColor)
    }


    private fun checkDialIntent(): Boolean {
        return if ((activity?.intent?.action == Intent.ACTION_DIAL || activity?.intent?.action == Intent.ACTION_VIEW) && activity?.intent?.data != null && activity?.intent?.dataString?.contains("tel:") == true) {
            val number = Uri.decode(activity?.intent?.dataString).substringAfter("tel:")
            dialpad_input.setText(number)
            dialpad_input.setSelection(number.length)
            true
        } else {
            false
        }
    }


    private fun dialpadPressed(char: Char, view: View?) {
        dialpad_input.addCharacter(char)
        view?.performHapticFeedback()
    }

    private fun clearChar(view: View) {
        dialpad_input.dispatchKeyEvent(dialpad_input.getKeyEvent(KeyEvent.KEYCODE_DEL))
        view.performHapticFeedback()
    }

    private fun clearInput() {
        dialpad_input.setText("")
    }

    private fun disableKeyboardPopping() {
        dialpad_input.showSoftInputOnFocus = false
    }


//    private fun speedDial(id: Int) {
//        if (dialpad_input.value.isEmpty()) {
//            val speedDial = speedDialValues.firstOrNull { it.id == id }
//            if (speedDial?.isValid() == true) {
//                initCall(speedDial.number)
//            }
//        }
//    }
}
