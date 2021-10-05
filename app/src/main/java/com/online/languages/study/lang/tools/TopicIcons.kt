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
        resourcesMap["meet"] = R.drawable.ic_topic_meet
        resourcesMap["people"] = R.drawable.ic_topic_people
        resourcesMap["form"] = R.drawable.ic_topic_form
        resourcesMap["family"] = R.drawable.ic_topic_family
        resourcesMap["job"] = R.drawable.ic_topic_job
        resourcesMap["profession"] = R.drawable.ic_topic_tool
        resourcesMap["language"] = R.drawable.ic_topic_language
        resourcesMap["transport"] = R.drawable.ic_topic_transport
        resourcesMap["sightseeing"] = R.drawable.ic_topic_museum
        resourcesMap["clothes"] = R.drawable.ic_topic_clothes
        resourcesMap["colors"] = R.drawable.ic_topic_colors
        resourcesMap["size"] = R.drawable.ic_topic_rule
        resourcesMap["quality"] = R.drawable.ic_topic_good
        resourcesMap["utensil"] = R.drawable.ic_topic_ustensils
        resourcesMap["roadsign"] = R.drawable.ic_topic_roadsign
        resourcesMap["sad"] = R.drawable.ic_topic_sad
        resourcesMap["police"] = R.drawable.ic_topic_police
        resourcesMap["quest"] = R.drawable.ic_topic_quest
        resourcesMap["house"] = R.drawable.ic_topic_house
        resourcesMap["arm"] = R.drawable.ic_topic_arm
        resourcesMap["organ"] = R.drawable.ic_topic_organ
        resourcesMap["pill"] = R.drawable.ic_topic_pill
        resourcesMap["person"] = R.drawable.ic_topic_person



    }



}