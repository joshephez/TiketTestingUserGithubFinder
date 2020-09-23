package com.gudangada.tikettesting.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout
import com.gudangada.tikettesting.apiconfig.UserAPI as Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*

import android.os.Handler
import android.view.View
import com.gudangada.tikettesting.Logger
import com.gudangada.tikettesting.R
import com.gudangada.tikettesting.adapter.AdapterUserList
import com.gudangada.tikettesting.model.User
import com.gudangada.tikettesting.scrollconfig.ScrollListener
import com.gudangada.tikettesting.view.EdittextConfig


class MainActivity : BaseActivity() {

    private val user by lazy { Api.create() }
    private val delay: Long = 1500
    private val perPage : Int = 100
    private var adapter: AdapterUserList? = null
    private var totalPage : Int = 0
    private var currentPage : Int = 1
    private var nextPage : Boolean = false
    private var isSearch : Boolean = false
    private var queryCurrent : String = ""
    private var querySearched : Int = 0
    private var lastTextChanged: Long = 0

    private var userLinearLayout = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setInit()
    }

    private fun setInit() {
        recyclerView.layoutManager = userLinearLayout
        setListener()
    }

    private fun setListener() {
        recyclerView.addOnScrollListener(object : ScrollListener(userLinearLayout) {
            override fun onLoadMore(current_page: Int) {
                if(nextPage) {
                    currentPage++
                    doSearch(queryCurrent, currentPage, perPage)
                }
            }

        })

        textSearch.onChange()
    }

    private fun doInitList(data: List<User>, q: String) {
        errorLayout.visibility = View.GONE

        if(adapter == null) {
            adapter = AdapterUserList(data.toMutableList(), data.toMutableList())
            recyclerView.adapter = adapter
        }else{
            if(queryCurrent != q || (data.count() == 1 && currentPage == 1))
                adapter!!.clear()

            adapter!!.addAll(data)
            adapter!!.notifyItemRangeChanged(0, adapter!!.itemCount)
        }

        Logger.e("count on query trigger ${adapter?.itemCount}")
    }

    private fun doClearList() {
        if(adapter != null) {
            errorLayout.visibility = View.GONE
            queryCurrent = ""
            totalPage = 0
            currentPage = 1
            adapter!!.clear()
            adapter!!.notifyItemRangeChanged(0, 0)
        }
    }

    private fun doSearch(q: String, p: Int, pP: Int) {
        textSearch.isEnabled = true
        if(!isSearch) {
            loading(true)
            Logger.e("Query ${q}, ${p}")
            disposable = user.search(q, p, pP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        loading(false)
                        val count = result.items.count()
                        if(count > 0){
                            Logger.e("count on query changed ${result.totalCount}")
                            totalPage += count
                            doInitList(result.items, q)
                            nextPage = (result.totalCount!! > totalPage && result.totalCount > perPage)

                        }else{
                            doClearList()
                            doInitNotFound(getString(R.string.text_404), getString(R.string.text_data_is_not_found))
                        }
                    },
                    { error ->
                        loading(false)
                        Logger.e("${error.message}")
                        doClearList()
                        doInitNotFound(getString(R.string.text_403), getString(R.string.text_api_rate_is_limit))

                        Handler().postDelayed(
                            { doSearch(textSearch.text.toString(), currentPage, perPage) },
                            60000)

                        doStartTick()
                    }
                )

            queryCurrent = q
        }


    }

    private val handlerSearch = Runnable {
        if (System.currentTimeMillis() > lastTextChanged + delay - 500 && textSearch.text.toString().isNotEmpty()) {
            doClearList()
            doSearch(textSearch.text.toString(), currentPage, perPage)
        }
    }

    private fun EdittextConfig.onChange() {
        this.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty())
                    doClearList()
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    if(querySearched > textSearch.text.toString().length)
                        doSearchTick(handler, false)
                    else{
                        if(adapter != null && totalPage > 1) {
                            if(!nextPage)
                                adapter?.filter?.filter(textSearch.text.toString())
                            else
                                doSearchTick(handler, true)
                        }else{
                            doSearchTick(handler, false)
                        }
                    }
                } else {
                    doClearList()
                }

                querySearched = textSearch.text.toString().length
            }
        })
    }

    private fun doSearchTick(handler: Handler, filtered: Boolean) {
        if(adapter != null && adapter?.itemCount!! > 0 && filtered) {
            adapter?.filter?.filter(textSearch.text.toString())
            if(adapter?.itemCount!! == 0) {
                doClearList()
                doInitNotFound(getString(R.string.text_404), getString(R.string.text_data_is_not_found))
            }
        }else{
            lastTextChanged = System.currentTimeMillis()
            handler.postDelayed(handlerSearch, delay)
        }
    }

    private fun doStartTick() {
        textSearch.isEnabled = false
        Handler().postDelayed({
            val count = Integer.parseInt(textCode.text.toString()) - 1
            textCode.text = count.toString()
            if(count > 0)
                doStartTick()

        },1000)
    }

    private fun loading(b: Boolean) {
        errorLayout.visibility = View.GONE
        isSearch = b

        if(b)
            loadingLayout.visibility = View.VISIBLE
        else
            loadingLayout.visibility = View.GONE
    }

    private fun doInitNotFound(c: String, m: String) {
        errorLayout.visibility = View.VISIBLE
        textCode.text = c
        textDescription.text = m
    }


}
