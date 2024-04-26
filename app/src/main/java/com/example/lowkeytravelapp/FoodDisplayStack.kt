package com.example.lowkeytravelapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.example.lowkeytravelapp.R.layout
import com.example.lowkeytravelapp.R.string
import com.google.android.material.navigation.NavigationView
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.RewindAnimationSetting
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FoodDisplayStackFragment : Fragment(), CardStackListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter
    private lateinit var foodNames: ArrayList<String>
    private var listener: OnFragmentClosedListener? = null
    private var index = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("FOOD DISPLAY STACK")

        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.foodstack_layout, container, false)
        foodNames = arguments?.getStringArrayList("food")!!
        println("foodNames $foodNames")

        // Initialize views
        drawerLayout = view.findViewById(R.id.drawer_layout)
        cardStackView = view.findViewById(R.id.card_stack_view)

        manager = CardStackLayoutManager(requireContext(), this)

        adapter = CardStackAdapter()

        lifecycleScope.launch {
            val adapter = initializeAdapter()
            cardStackView.adapter = adapter
        }

        view?.let { setupNavigation(it) }
        view?.let { setupButton(it) }
        initialize()

        setupNavigation(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Handle back button press here
            // For example, you can close the drawer if it's open
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = true
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
        println("swiped my guy: $index : ${foodNames[index]}")
        if (direction.toString() == "Right") {
            closeFragmentWithData(foodNames[index])
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        val imageview = view.findViewById<ImageView>(R.id.item_image)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun setupButton(view: View) {
        val skip = view.findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
            index ++
        }

        val rewind = view.findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
            index --
        }

        val like = view.findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
            index ++
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private suspend fun initializeAdapter(): CardStackAdapter {
        return suspendCoroutine { continuation ->
            createSpots { spots ->
                val adapter = CardStackAdapter(spots)
                continuation.resume(adapter)
            }
        }
    }

    private fun paginate() {
        val old = adapter.getSpots()

        // Call createSpots asynchronously
        createSpots { newSpots ->
            val new = old + newSpots // Combine old spots with newly created spots
            val callback = SpotDiffCallback(old, new)
            val result = DiffUtil.calculateDiff(callback)
            adapter.setSpots(new)
            result.dispatchUpdatesTo(adapter)
        }
    }


    private fun reload() {
        val old = adapter.getSpots()

        // Call createSpots asynchronously
        createSpots { new ->
            val callback = SpotDiffCallback(old, new)
            val result = DiffUtil.calculateDiff(callback)
            adapter.setSpots(new)
            result.dispatchUpdatesTo(adapter)
        }
    }

    private fun addFirst(size: Int) {
        val old = adapter.getSpots()

        // Track the number of spots created asynchronously
        var spotsCreated = 0
        val newSpots = mutableListOf<Spot>()

        // Function to check if all spots have been created and update the adapter
        fun checkAndUpdateAdapter() {
            spotsCreated++
            if (spotsCreated == size) {
                val new = mutableListOf<Spot>().apply {
                    addAll(newSpots)
                    addAll(old)
                }
                val callback = SpotDiffCallback(old, new)
                val result = DiffUtil.calculateDiff(callback)
                adapter.setSpots(new)
                result.dispatchUpdatesTo(adapter)
            }
        }

        // Create spots asynchronously
        repeat(size) {
            createSpot { spot ->
                newSpots.add(spot)
                checkAndUpdateAdapter()
            }
        }
    }


    private fun addLast(size: Int) {
        val old = adapter.getSpots()

        // Track the number of spots created asynchronously
        var spotsCreated = 0
        val newSpots = mutableListOf<Spot>()

        // Function to check if all spots have been created and update the adapter
        fun checkAndUpdateAdapter() {
            spotsCreated++
            if (spotsCreated == size) {
                val new = mutableListOf<Spot>().apply {
                    addAll(old)
                    addAll(newSpots)
                }
                val callback = SpotDiffCallback(old, new)
                val result = DiffUtil.calculateDiff(callback)
                adapter.setSpots(new)
                result.dispatchUpdatesTo(adapter)
            }
        }

        // Create spots asynchronously
        repeat(size) {
            createSpot { spot ->
                newSpots.add(spot)
                checkAndUpdateAdapter()
            }
        }
    }

    private fun removeFirst(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeLast(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun replace() {
        val old = adapter.getSpots()
        val positionToRemove = manager.topPosition

        createSpot { newSpot ->
            val new = mutableListOf<Spot>().apply {
                addAll(old)
                removeAt(positionToRemove)
                add(positionToRemove, newSpot)
            }
            adapter.setSpots(new)
            adapter.notifyItemChanged(positionToRemove)
        }
    }


    private fun swap() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun createSpot(callback: (Spot) -> Unit) {
        // Make an asynchronous call to fetch the image URL and create the Spot
        fetchImageURL("Bread") { imageUrl ->
            val spot = Spot(name = "Bread", url = imageUrl)
            callback(spot)
        }
    }

    private fun createSpot(foods: List<String>, callback: (List<Spot>) -> Unit) {
        val spots = mutableListOf<Spot>()
        var processedCount = 0

        for (food in foods) {
            fetchImageURL(food) { imageUrl ->
                val spot = Spot(name = food, url = imageUrl)
                spots.add(spot)

                processedCount++
                if (processedCount == foods.size) {
                    callback(spots)
                }
            }
        }
    }

    private fun createSpots(callback: (List<Spot>) -> Unit) {
        createSpot(foodNames, callback)
    }

    private fun setupNavigation(view: View) {
        // Toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        // setSupportActionBar(toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar, string.open_drawer, string.close_drawer)
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        val navigationView = view.findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.reload -> reload()
                R.id.add_spot_to_first -> addFirst(1)
                R.id.add_spot_to_last -> addLast(1)
                R.id.remove_spot_from_first -> removeFirst(1)
                R.id.remove_spot_from_last -> removeLast(1)
                R.id.replace_first_spot -> replace()
                R.id.swap_first_for_last -> swap()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun fetchImageURL(food: String, callback: (String) -> Unit) {
        val apiKey = BuildConfig.GOOGLE_CLOUD_API_KEY
        val cx = "62bb355ee52724715"
        val url = "https://www.googleapis.com/customsearch/v1?key=$apiKey&cx=$cx&q=$food&searchType=image&google_domain=com&gl=us&hl=en"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(response: Response?) {
                val responseBodyString = response?.body()?.string()
                if (!responseBodyString.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(responseBodyString)
                        if (json.has("items")) {
                            val items = json.getJSONArray("items")
                            val firstItem = items.getJSONObject(0)
                            val imageUrl = firstItem.getString("link")
                            callback(imageUrl)
                        } else {
                            println("No items found in the response.")
                        }
                    } catch (e: JSONException) {
                        println("Error parsing JSON: ${e.message}")
                    }
                } else {
                    println("Empty response body.")
                }
            }

            override fun onFailure(request: Request?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }



    fun setOnFragmentClosedListener(listener: OnFragmentClosedListener) {
        this.listener = listener
    }

    fun closeFragmentWithData(data: String) {
        // Call the interface method to send data back to the activity
        (activity as? OnFragmentClosedListener)?.onFragmentClosed(data)
        // Remove the fragment from the activity
        fragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    interface OnFragmentClosedListener {
        fun onFragmentClosed(data: String)
    }
}
