package com.online.languages.study.lang.presentation.section

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.DataModeDialog
import com.online.languages.study.lang.adapters.PremiumDialog
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.NavCategory
import com.online.languages.study.lang.databinding.ActivitySectionReviewListBinding
import com.online.languages.study.lang.presentation.category.CatActivity
import com.online.languages.study.lang.presentation.core.ThemedActivity
import com.online.languages.study.lang.presentation.details.ScrollingActivity
import com.online.languages.study.lang.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SectionReviewActivity : ThemedActivity(), SectionViewActions {
    private var viewController: SectionViewController? = null

    private var mBinding: ActivitySectionReviewListBinding? = null

    var sectionId: String? = null
    private var changeLayoutBtn: MenuItem? = null
    private var dialogStatus = DIALOG_UNLOCKED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySectionReviewListBinding.inflate(layoutInflater )
        openActivity.setOrientation()
        setContentView(mBinding!!.root)
        setupToolbar()
        sectionId = intent.getStringExtra(Constants.EXTRA_SECTION_ID)
        sectionId?.let { initViewController(it) }
        title = viewController?.getTitle()
        openView()
    }

    private fun setupToolbar() {
        val toolbar = mBinding?.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViewController(tSectionID: String) {
        viewController = mBinding?.let {
            SectionViewControllerImpl(
                it,
                appContainer.models.provideSectionViewModel(this),
                this
            ).apply {
                setup(tSectionID)
            }
        }
    }

    private fun openView() {
        mBinding?.itemsList?.visible()
    }

    private fun refreshList(data: String?) {
        data?.let { viewController?.getSectionList(it) }
    }

    private fun openPremiumDialog() {
        val premiumDialog = PremiumDialog(this)
        premiumDialog.createDialog(
            getString(R.string.plus_version_btn),
            getString(R.string.topic_access_in_plus_version)
        )
    }

    private fun openLayoutDialog() {
        viewController?.showLayoutDialog()
    }

    private fun openInfoMessage() {
        val dataModeDialog = DataModeDialog(this)
        dataModeDialog.createDialog(
            getString(R.string.info_txt),
            getString(R.string.info_star_review_txt)
        )
    }

    override fun openCategory(data: NavCategory) {
        openCategoryPage(data)
    }

    override fun openItem(data: DataItem) {
        showAlertDialog(data)
    }

    override fun openPremium() {
        openPremiumDialog()
    }

    override fun applyLayoutStatus(isCompact: Boolean) {
        if (isCompact) {
            changeLayoutBtn?.setIcon(R.drawable.ic_view_list_column)
        } else {
            changeLayoutBtn?.setIcon(R.drawable.ic_view_list_big)
        }
    }

    private fun openCategoryPage(category: NavCategory) {
        val i = Intent(this, CatActivity::class.java)
        i.putExtra(Constants.EXTRA_SECTION_ID, sectionId)
        i.putExtra(Constants.EXTRA_CAT_ID, category.id)
        i.putExtra(Constants.EXTRA_CAT_SPEC, category.spec)
        i.putExtra("cat_title", category.title)
        startActivityForResult(i, CATEGORY_REQUEST_CODE)
        openActivity.pageTransition()
    }

    fun showAlertDialog(dataItem: DataItem) {
        if (dialogStatus == DIALOG_LOCKED) return
        val intent = Intent(this, ScrollingActivity::class.java)
        intent.putExtra("id", dataItem.id)
        intent.putExtra("position", 0)
        intent.putExtra("item", dataItem)
        intent.putExtra(Constants.EXTRA_CAT_ID, dataItem.cat)
        startActivityForResult(intent, DETAIL_DIALOG_REQUEST_CODE)
        openActivity.detailOpenTransition()

        dialogStatus = DIALOG_LOCKED
        lifecycleScope.launch {
            delay(200)
            dialogStatus = DIALOG_UNLOCKED
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_section_review, menu)
        changeLayoutBtn = menu.findItem(R.id.list_layout)
        viewController?.showLayoutStatus()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.info_from_menu -> {
                openInfoMessage()
                return true
            }
            R.id.list_layout -> {
                openLayoutDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        openActivity.pageBackTransition()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialogStatus = DIALOG_UNLOCKED
        if (requestCode == DETAIL_DIALOG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data?.hasExtra(Constants.EXTRA_CAT_ID) == true) {
                    val cat = data.getStringExtra(Constants.EXTRA_CAT_ID)
                    refreshList(cat)
                }
            }
        }
        if (requestCode == CATEGORY_REQUEST_CODE) {
            if (data?.hasExtra(Constants.EXTRA_CAT_ID) == true) {
                val cat = data.getStringExtra(Constants.EXTRA_CAT_ID)
                refreshList(cat)
            }
        }
    }

    companion object {
        private const val CATEGORY_REQUEST_CODE = 2
        private const val DETAIL_DIALOG_REQUEST_CODE = 1
        private const val DIALOG_LOCKED = 1
        private const val DIALOG_UNLOCKED = 0

    }
}

