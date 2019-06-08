package com.example.keyboardshortcut

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keyboardshortcut.customextensions.showToast
import com.example.keyboardshortcut.databinding.ActivityMainBinding
import com.example.keyboardshortcut.model.ShortCut
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    lateinit var keys:MutableList<String>
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_save -> saveKey()
            R.id.bt_addkey -> changeKey()
        }
    }

    fun changeKey()
    {
        if(TextUtils.isEmpty(activityMainBinding.mainContainer.actKeys.text))
        {
            showToast("Please Enter Key")
        }
        else
        {
            val changeKeys = activityMainBinding.mainContainer.actKeys.text.toString()
            val newKey = activityMainBinding.mainContainer.textView.text.toString()+changeKeys+" "
            keys.add(changeKeys)
            activityMainBinding.mainContainer.textView.text = newKey
            activityMainBinding.mainContainer.actKeys.text=null
        }
    }

    fun saveKey()
    {
        if(TextUtils.isEmpty(activityMainBinding.mainContainer.textView.text))
        {
            showToast("Please Enter show Keys")
        }
        else
        {
            val shortcut = ShortCut(activityMainBinding.mainContainer.etshortcutkeyname.text.toString(),keys)
            println(shortcut)
        }
    }
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.onClickListener = this
        keys = ArrayList<String>()
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}
