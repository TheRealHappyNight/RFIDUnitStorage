package com.example.licentatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.bumptech.glide.util.LogTime;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private static final String TAG = "RecipeListActivity";


    ArrayList<Recipe> mRecipes;
    ArrayList<Recipe> mRecipesNotAvail;
    ArrayList<String> mRecipeReady = new ArrayList<>();
    ArrayList<String> mRecipeReady2 = new ArrayList<>();

    RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;
    RecipeRecyclerViewAdapter recipeRecyclerViewAdapter2;

    public static final String INVENTORIES = "Inventories";
    public static final String INVENTORY = "Inventory";
    ArrayList<ProductItem> mInventory;

    private static final String RECIPESMAIN = "getRecipes";
    private static final String RECIPES = "getRecipe";

    //Looping vars
    Handler mDataHandler = new Handler();
    Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecipes = new ArrayList<>();
        mRecipesNotAvail = new ArrayList<>();

        mInventory = new ArrayList<>();

        //customCommands();
    }

    private BroadcastReceiver mGetInventory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mInventory = intent.getParcelableArrayListExtra(INVENTORY);
            Log.d(TAG, "onReceive: Inventories received");
        }
    };

    private void customCommands() {

        RecipeDatabase recipeDatabase = RecipeDatabase.getHelper(this);
        Dao<Recipe, Integer> recipeDao = recipeDatabase.getRecipeDao();

        try {
//            TableUtils.dropTable(recipeDao, true);
//            TableUtils.createTable(recipeDao);
//            recipeDao.createOrUpdate(new Recipe("Omleta",
//                    "https://extranews.ro/wp-content/uploads/2017/01/4-Trucuri-pentru-o-omleta-pufoasa-si-gustoasa.jpg",
//                    "Sunt multe persoane care prefera omleta din 2 oua, insa din punctul meu de vedere, omleta trebuie sa aiba 3 oua. E cantitatea optima !\n" +
//                            "Spargem ouale in castron si adaugam condimente dupa plac. Eu ma voi rezuma la sare si piper.\n" +
//                            "Acum ouale trebuie batute cu telul putin sau cu o furculita.\n" +
//                            "Ouăle trebuie să fie bătute bine, cu forţă. Astfel, omleta va prinde aer şi va căpăta volum.\n" +
//                            "Amestecă-le chiar înainte de a le pune în tigaie. Dacă laşi compoziţia să se aşeze (până încingi tigaia, de exemplu), omleta nu va ieşi pufoasă – la tigaile de o calitate mai slaba se poate si lipi, pentru ca avem de-a face cu proteina.\n" +
//                            "Incingem tigaia pe plita, pastrand un foc mediu.\n" +
//                            "Untul este foarte important atunci cand facem omleta. Untul face omleta pufoasa ! Il pun in tigaie si il plimb pana se topeste. Apoi adaug ouale. In primele 20 de sec, putem sa plimbam compozitia putin pentru a o aseza uniform in tigaie. Apoi putem observa ca se gateste deja pe margine. O sa dau usor la o parte marginea, si mai plimb putin din compozitia care a ramas deasupra.\n" +
//                            "Si daca vrei sa duci omleta la urmatorul nivel, ii poti rade niste cascaval, parmezan sau branza cheddar pe deasupra. Eu o sa pun niste cascaval ras si-o sa-l las sa se topeasca.\n" +
//                            "In acest moment, cu ajutorul spatulei, o sa o impaturesc in jumate. Omleta nu trebuie să stea prea mult pe foc. Ia tigaia de pe aragaz cu puţin timp înainte de a fi gata. Procesul va continua şi fără flacără.\n" +
//                            "Omleta e gata si priveste cat este de pufoasa !\n" +
//                            "O sa o si tai ca sa va arat textura. Untul joaca un rol foarte important.",
//                    "Oua,Unt",
//                    1));
//            recipeDao.createOrUpdate(new Recipe("Crema de zahar ars",
//                    "https://savoriurbane.com/wp-content/uploads/2016/03/Crema-de-zahar-ars-reteta-veche-a-bunicii-explicata-pas-cu-pas-savori-urbane.jpg",
//                    "Pentru crema de zahar ars este ideala o cratita de metal, de cca 3-4 l volum, de 22 cm Ø si cu margini inalte de 10 cm, care poate fi introdusa si in cuptor.\n" +
//                            "Asadar, daca folosim o cratita de metal, punem zaharul in cratita, pe foc mic, si il topim pana capata o culoare aurie. Amestecam din cand in cand zaharul, astfel incat sa nu se arda.\n" +
//                            "Dupa ce zaharul este complet topit, stingem focul si rotim cratita, cu foarte mare grija, pana cand fundul si peretii acesteia sunt imbracati, pe jumatate, in caramel.\n" +
//                            "Crema de zahar ars preparare\n" +
//                            "Lasam cratita la temperatura camerei pana cand caramelul se raceste.\n" +
//                            "Preparare compozitie pentru crema de zahar ars\n" +
//                            "Punem ouale intr-un vas, adaugam sarea, zaharul si semintele pastaii de vanilie si mixam cca 3-4 minute, la viteza medie.\n" +
//                            "Crema de zahar ars preparare crema\n" +
//                            "Adaugam laptele rece si mai mixam cca 1 minut la viteza medie.\n" +
//                            "Crema de zahar ars compozitie oua si lapte\n" +
//                            "Fierbere crema de zahar ars\n" +
//                            "Turnam compozitia de oua in cratita cu caramelul rece. O asezam intr-o alta cratita, putin mai mare, umpluta pana la jumatate cu apa rece.\n" +
//                            "Crema de zahar ars compozitie in vas\n" +
//                            "Introducem cu grija crema de zahar ars in cuptorul preincalzit la 180°C si o fierbem cca 50-60 minute.\n" +
//                            "Este foarte important ca o data la 15 minute, sa adaugam cca 50 ml de apa rece in cratita cu apa. In acest fel impiedicam fierberea in clocote. Daca apa, in care este asezata cratita cu zahar ars, fierbe in clocote, vom obtine o crema de zahar ars cu gauri.\n" +
//                            "Stingem cuptorul, lasam crema caramel inca 10 minute in bain-marie (in cratita cu apa), apoi o scoatem si o lasam sa se raceasca complet la temperatura camerei. Dupa ce s-a racit, o punem in frigider si o lasam pana a doua zi.",
//                    "Lapte,Oua",
//                    3));
//            recipeDao.createOrUpdate(new Recipe("Cascaval Pane",
//                    "https://www.gustos.ro/assets/recipes_images/2010/04/22/51555/tn4_e7df99446b8833f5e5d41a3ca4d8ccaf_1_.jpg",
//                    "Cascavalul se taie in felii uniforme , groase de circa 1 cm si se presara cu piper.Se trece fiecare felie prin faina, ou batut si apoi prin pesmet.Se incinge ulei intr-o tigaie si se rumenesc pe ambele parti.\n" +
//                            "Se servesc fierbinti ca atare sau cu diferite garnituri si salate.\n" +
//                            "Pofta buna!",
//                    "Cascaval,Oua",
//                    2));

            List<Recipe> a = recipeDao.queryForAll();
            Log.e(TAG, "customCommands: " + a.toString());
            recipeDatabase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDataHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager.getInstance(RecipeListActivity.this).registerReceiver(mGetInventory, new IntentFilter(INVENTORIES));
                if(mInventory != null)
                    init();

                mDataHandler.postDelayed(mRunnable, MainActivity.mDataDelay);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGetInventory);
        mDataHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    private void init() {
        Log.d(TAG, "init: recipes init called");

        RecipeDatabase recipeDatabase = RecipeDatabase.getHelper(this);
        Dao<Recipe, Integer> recipeDao = recipeDatabase.getRecipeDao();

        RawRowMapper<String> mapper = new RawRowMapper<String>() {
            @Override
            public String mapRow(String[] strings, String[] strings1) throws SQLException {
                return strings1[0];
            }
        };

        try {
            ArrayList<String> mRecipeNames;
            ArrayList<String> mRecipeImagesUrls;
            ArrayList<String> mRecipeDescriptions;
            ArrayList<String> mRecipeProducts;
            ArrayList<String> mRecipeTypes;

            GenericRawResults<String> nume = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("name")
                    .prepareStatementString(), mapper);
            List<String> tmp = nume.getResults();
            mRecipeNames = new ArrayList<>(tmp);

            GenericRawResults<String> url = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("imageURL")
                    .prepareStatementString(), mapper);
            tmp = url.getResults();
            mRecipeImagesUrls = new ArrayList<>(tmp);

            GenericRawResults<String> description = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("description")
                    .prepareStatementString(), mapper);
            tmp = description.getResults();
            mRecipeDescriptions = new ArrayList<>(tmp);

            GenericRawResults<String> type = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("type")
                    .prepareStatementString(), mapper);
            List<String> types = type.getResults();
            mRecipeTypes = new ArrayList<>(types);

            GenericRawResults<String> products = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("products")
                    .prepareStatementString(), mapper);
            List<String> Products = products.getResults();
            mRecipeProducts = new ArrayList<>(Products);

            ArrayList<String> availableProducts = new ArrayList<>();
            for(ProductItem i : mInventory) {
                availableProducts.add(i.getName());
            }

            mRecipeReady.clear();
            for(String list : Products) {
                String[] parts = list.split(",");
                boolean ready = true;
                for(String i : parts) {
                    if(!availableProducts.contains(i)) {
                        mRecipeReady.add("Nu se poate prepara");
                        ready = false;
                        break;
                    }
                }
                if(ready) {
                    mRecipeReady.add("Se poate prepara");
                }
            }

            mRecipes.clear();
            for(int i = 0; i < mRecipeNames.size(); i++) {
                Recipe toBeAdded = new Recipe(mRecipeNames.get(i),
                        mRecipeImagesUrls.get(i),
                        mRecipeDescriptions.get(i),
                        mRecipeProducts.get(i),
                        Integer.parseInt(mRecipeTypes.get(i)));

                mRecipes.add(toBeAdded);
            }

            Log.d(TAG, "verifyProducts: Sending recipes");
            Intent intent = new Intent(RECIPESMAIN);
            intent.putParcelableArrayListExtra(RECIPES ,mRecipes);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            mRecipesNotAvail.clear();
            for(int i = 0; i < mRecipeReady.size(); i++) {
                if(mRecipeReady.get(i).equals("Nu se poate prepara")) {
                    Recipe toBeAdded = new Recipe(mRecipes.get(i).getName(),
                            mRecipes.get(i).getImageURL(),
                            mRecipes.get(i).getDescription(),
                            mRecipes.get(i).getProducts(),
                            mRecipes.get(i).getType());
                    mRecipesNotAvail.add(toBeAdded);

                    mRecipeReady2.add("Nu se poate prepara");

                    mRecipes.remove(i);
                    mRecipeReady.remove(i);
                    i--;
                }
            }
        } catch(SQLException e){
            Log.e(TAG, "recipe init: failed");
        }

        recipeDatabase.close();

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: called in recipe");

        RecyclerView recyclerView = findViewById(R.id.recipeListView);
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(this, mRecipes, mRecipeReady);

        recyclerView.setAdapter(recipeRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView recyclerView2 = findViewById(R.id.recipeNoListView);
        recipeRecyclerViewAdapter2 = new RecipeRecyclerViewAdapter(this, mRecipesNotAvail, mRecipeReady2);

        recyclerView2.setAdapter(recipeRecyclerViewAdapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recipeRecyclerViewAdapter.getFilter().filter(s);
                recipeRecyclerViewAdapter2.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }
}
