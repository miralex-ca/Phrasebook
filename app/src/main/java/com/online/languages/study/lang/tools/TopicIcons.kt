package com.online.languages.study.lang.tools

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ShareCompat
import com.online.languages.study.lang.BuildConfig
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.RateDialog

class TopicIcons {


    private val resourcesMap = HashMap<String, Int>()

    init {
        prepareIcons()
    }

    fun getIcon(topicTag: String ) : Int {

        var topicIcon = R.drawable.ic_topic_list

        val icon = resourcesMap[topicTag]

        if (icon != null) topicIcon = icon

        return topicIcon
    }

    fun prepareIcons() {

        resourcesMap["contact"] = R.drawable.ic_topic_contact
        resourcesMap["greeting"] = R.drawable.ic_topic_greeting
        resourcesMap["agree"] = R.drawable.ic_topic_agree
        resourcesMap["plane"] = R.drawable.ic_topic_plane
        resourcesMap["hotel"] = R.drawable.ic_topic_hotel
        resourcesMap["city"] = R.drawable.ic_topic_city
        resourcesMap["town"] = R.drawable.ic_topic_town
        resourcesMap["store"] = R.drawable.ic_topic_store
        resourcesMap["cart"] = R.drawable.ic_topic_cart
        resourcesMap["resto"] = R.drawable.ic_topic_resto
        resourcesMap["meat"] = R.drawable.ic_topic_meat
        resourcesMap["food"] = R.drawable.ic_topic_food
        resourcesMap["fish"] = R.drawable.ic_topic_fish
        resourcesMap["vegetable"] = R.drawable.ic_topic_vegetable
        resourcesMap["fruit"] = R.drawable.ic_topic_cherry
        resourcesMap["dessert"] = R.drawable.ic_topic_dessert
        resourcesMap["drink"] = R.drawable.ic_topic_drink
        resourcesMap["sick"] = R.drawable.ic_topic_sick
        resourcesMap["pharmacy"] = R.drawable.ic_topic_pharmacy
        resourcesMap["medicine"] = R.drawable.ic_topic_plaster
        resourcesMap["sign"] = R.drawable.ic_topic_sign
        resourcesMap["time"] = R.drawable.ic_topic_time
        resourcesMap["timer"] = R.drawable.ic_topic_timer
        resourcesMap["calendar"] = R.drawable.ic_topic_calendar
        resourcesMap["day"] = R.drawable.ic_topic_day
        resourcesMap["number"] = R.drawable.ic_topic_number
        resourcesMap["counter"] = R.drawable.ic_topic_counter



    }



}