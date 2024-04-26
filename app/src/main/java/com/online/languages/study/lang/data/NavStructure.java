package com.online.languages.study.lang.data;

import static com.online.languages.study.lang.Constants.CAT_SPEC_MAPS;
import static com.online.languages.study.lang.Constants.UD_PREFIX;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.online.languages.study.lang.R;

import java.util.ArrayList;
import java.util.HashSet;

public class NavStructure implements Parcelable {
    public ArrayList<NavSection> sections;
    public ArrayList<NavCategory> categories;

    private Context context;

    public NavStructure(Context context) {
        sections = new ArrayList<>();
        categories = new ArrayList<>();
        this.context = context;
    }

    public ArrayList<NavCategory> getUniqueCats() {
        categories = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        for (NavSection section: sections) {
            for (NavCategory cat: section.uniqueCategories) {
                if (!cat.type.equals("group") && !cat.type.equals("set") && !cat.spec.equals(CAT_SPEC_MAPS)) {
                    if (!set.contains(cat.id)) {
                        categories.add(cat);
                        set.add(cat.id);
                    }
                }
            }
        }

        return categories;
    }


    public ArrayList<NavCategory> getNavCatListFromParent(String parent, String section_id) {
        ArrayList<NavCategory> list = new ArrayList<>();

        for (NavSection navSection: sections) {

            if (navSection.id.equals(section_id)) {
                for (NavCategory category: navSection.navCategories) {
                    if (category.parent.equals(parent)) {
                        list.add(category);
                    }
                }
                break;
            }
        }

        return list;

    }

    public NavSection getNavSectionByID (String sectionID) {
        NavSection navSection = new NavSection();
        for (NavSection section : sections) {
            if (section.id.equals(sectionID)) {
                navSection = section;
            }
         }

        return navSection;
    }


    public NavStructure(Parcel parcel){
        sections = new ArrayList<>();
        parcel.readTypedList(sections, NavSection.CREATOR);
    }


    public NavCategory getNavCatFromSection(String sectionId, String catId) {
        NavSection navSection = getNavSectionByID(sectionId);
        NavCategory navCategory = new NavCategory();

        for (NavCategory category: navSection.navCategories){
            if (category.id.equals(catId)) navCategory = category;
        }

        return  navCategory;
    }


    public void addUserContent () { // used only for test results

        String title = context.getString(R.string.user_content_title);
        NavSection userSection = new NavSection();
        userSection.id = UD_PREFIX;
        userSection.title = title;
        
        NavCategory userCategory = new NavCategory();
        userCategory.id = UD_PREFIX;
        userCategory.title = title;

        userSection.navCategories.add(userCategory);

        sections.add(userSection);
    }


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(sections);
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NavStructure createFromParcel(Parcel source) {
            return new NavStructure(source);
        }

        public NavStructure[] newArray(int size) {
            return new NavStructure[size];
        }
    };






}
