package app.com.weatherzone.core;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.com.weatherzone.R;
import app.com.weatherzone.adapter.ImageLoaderAdapter;
import app.com.weatherzone.network.NetworkStateReceiver;
import app.com.weatherzone.pojo.APIResponse;
import app.com.weatherzone.utils.CallbackController;
import app.com.weatherzone.utils.ConnectionUtils;
import app.com.weatherzone.utils.Constants;

/**
 * This is the main entry point of the application. This class is responsible for handling UI
 * construction as well as events
 * Author : Varun
 * Date : 2/11/2016.
 */
public class MainActivity extends AppCompatActivity implements CallbackController,
        SearchView.OnQueryTextListener, NetworkStateReceiver.NetworkStateReceiverListener,
        AbsListView.OnScrollListener{

    //ApiResponse instance that contains all necesarry information in order to construct the UI
    private APIResponse apiResponse;

    //Connection Utils instance that facilitates connectivity with the network
    private ConnectionUtils connectionUtils;

    //Progressbar that is displayed when a time consuming operation (like fetching data off the API
    // url is running in the background
    private ProgressBar imageLoadProgressBar;

    //View that holds all UI elements in the form of a list
    private ListView listView;

    //Error Message displayed when no search items are found
    private TextView errorMessage;

    //This adapter allows for custom list rows to be created
    private ImageLoaderAdapter imageLoaderAdapter;

    //Widget used for search
    private SearchView searchView;

    //This enables resetting the listview to it's default state(in this case data for "weather"
    private SwipeRefreshLayout swipeRefreshLayout;

    //This receiver keeps track of network connectivity
    private NetworkStateReceiver networkStateReceiver;

    //This flag is set to true/false based on the state of network connectivity
    private boolean lostConnectivityFlag;

    //This is the search term used to fetch data off the API
    private String searchTerm;

    //Page number of the current result set
    private int pageNumber;

    //Footer view displayed at the end of the list when data off the next page is being fetched
    private LinearLayout footer;

    //Flag to determine if extra information is being fetched
    private boolean isLoading;

    public void setLostConnectivityFlag(boolean lostConnectivityFlag) {
        this.lostConnectivityFlag = lostConnectivityFlag;
    }

    /**
     * This method is called when the application is first launched.
     * It is the first step of the Android Application Life Cycle
     * @param savedInstanceState - bundle data that contains extra app state information when set
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Disable display of App name in the action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Construct UI elements
        setUpUI();
        //Initialise the connection helper
        connectionUtils=new ConnectionUtils(this,this,apiResponse);
        //Set page number to 1
        pageNumber=1;

        //Fetch data for the default search parameter ="weather"
        fetchData(Constants.DEFAULT_SEARCH_PARAMETER);
        //Register the network state change receiver
        registerReceiver();
    }

    /**
     * This method is responsible for setting up the UI
     */
    private void setUpUI() {
        //Initialse the progress bar
        imageLoadProgressBar = (ProgressBar) findViewById(R.id.imageLoadProgressBar);

        //Initialise the list that holds image objects
        listView = (ListView) findViewById(R.id.resultsview);

        //Set lost connectivity flag to false
        lostConnectivityFlag=false;

        //Initialse the swipe refresh listener to the current activity
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(resetListener);
        swipeRefreshLayout.setRefreshing(false);

        //Initialse the error message and make it invisible(gone)
        errorMessage = (TextView) findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.GONE);
        //Initialise the API Response object
        apiResponse=new APIResponse();
        //Setup the image adapter
        imageLoaderAdapter = new ImageLoaderAdapter(this,apiResponse.getImages());
        listView.setAdapter(imageLoaderAdapter);
    }

    /**
     * This method initialises the network change receiver that keeps tracks of network connectivity
     */
    private void registerReceiver()
    {
        //Initialise network state change receiver
        networkStateReceiver = new NetworkStateReceiver();
        //Add listener and set it to the current activity
        networkStateReceiver.addListener(this);
        //Register the network change receiver
        this.registerReceiver(networkStateReceiver,
                new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }


    /**
     * This method invokes the connection utility to fetch the search parameter passed in
     * @param searchData - search parameter to perform a search call on
     */
    private void fetchData(String searchData) {
        imageLoadProgressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        this.searchTerm=searchData;
        //Fetch and process the image data
        connectionUtils.fetchAndProcessImageData(searchData,pageNumber);
    }

    /**
     * This listener makes a fresh data call to reset the view when pulled to refresh
     */
    private SwipeRefreshLayout.OnRefreshListener resetListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            //Reset pageNumber to 1
            pageNumber=1;
            //Clear all image data
            apiResponse.getImages().clear();
            //Fetch new data
            fetchData(Constants.DEFAULT_SEARCH_PARAMETER);
        }
    };

    /**
     * This method sets up the list of items on the UI
     * @param apiResponse processed apiResponse instance
     */
    @Override
    public void processImageData(APIResponse apiResponse)
    {
        this.apiResponse=apiResponse;
        //Hide the progress bar
        imageLoadProgressBar.setVisibility(View.GONE);
        //Display error message if no data is received
        if(apiResponse.getImages()==null || apiResponse.getImages().isEmpty())
        {
            errorMessage.setVisibility(View.VISIBLE);
        }

        //Remove footer that contains the progress bar that is shown for pagination
        if(footer != null) {
            listView.removeFooterView(footer);
        }
        listView.setOnScrollListener(this);
        //Update the list with new data
        ((ImageLoaderAdapter)((HeaderViewListAdapter)listView.getAdapter()).
                getWrappedAdapter()).notifyDataSetChanged();
        //Set loading to false
        isLoading=false;
        //Set refreshing to false
        if(swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * This method initialises menu items
     * @param menu - instance of menu
     * @return returns true after creation of options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate a search widget
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //Set current activity as Query text listener to be invoked when user enters a search item
        // and hits search
        searchView.setOnQueryTextListener(this);
        return true;
    }

    /**
     * This method is invoked when user enters the search parameter in the search field and hits
     * enter
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {

        //Clear the list
        listView.invalidate();
        ((ImageLoaderAdapter)((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter()).clearData();
        ((ImageLoaderAdapter)((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
        pageNumber=1;
        //Fetch new search data
        fetchData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * This method is invoked when a user's phone loses connectivity mid-way through an API call
     * and the reconnects
     */
    @Override
    public void networkAvailable() {
        //If connection was lost retry connection
        if(lostConnectivityFlag)
        {
            lostConnectivityFlag=false;
            fetchData(searchTerm);
        }

    }

    @Override
    public void networkUnavailable() {
        lostConnectivityFlag=true;
    }

    /**
     * This listener listens for a scroll event invokes a next page data fetch when the user reaches
     * the end of the list
     * @param view - listview
     * @param firstVisibleItem - first list row that is visible
     * @param visibleItemCount - number of items visible
     * @param totalItemCount - total number of items in the list
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        //Skip operation if the list adapter is invalid
        if (imageLoaderAdapter == null)
            return ;
        //Skip operation if the no list items present
        if (imageLoaderAdapter.getCount() == 0)
            return ;


        int numberOfItems = visibleItemCount + firstVisibleItem;
        //If the list has reached it's end and the current page number is less than the total
        //number of pages in the resultset fetch data for the next page number
        if (numberOfItems >= totalItemCount && !isLoading && pageNumber<apiResponse.getTotalPages()) {
            isLoading = true;
            //Create and display a progress bar at the footer of the list
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            footer = (LinearLayout) inflater.inflate(R.layout.footer_layout, null);
            listView.addFooterView(footer);
            //Calculate the next pagenumber
            int nextPageNumber = apiResponse.getCurrentPageNumber();
            pageNumber=++nextPageNumber;
            fetchData(searchTerm);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * This method is called when the activity is destroyed
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //Unregister the network state change receiver
        if(networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
        }
    }

}
