package com.example.licentatest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "ProductRecyclerViewAdap";
    private static final String BASKET_FILE_NAME = "Basket.txt";

    private ArrayList<ProductItem> showList;
    private ArrayList<ProductItem> filterList;
    private Context mContext;

//    public ProductRecyclerViewAdapter(ArrayList<String> mProductNames,
//                                      ArrayList<String> mProductImages,
//                                      ArrayList<String> mProductQuantity,
//                                      ArrayList<Integer> mProductPrices,
//                                      ArrayList<String> mProductExpDate,
//                                      Context mContext) {
//        this.mContext = mContext;
//
//        showList = new ArrayList<>();
//        if(!mProductPrices.isEmpty()) {
//            for (int i = 0; i < mProductNames.size(); i++) {
//                ProductItem tmp = new ProductItem(mProductNames.get(i),
//                        mProductImages.get(i),
//                        null,
//                        mProductQuantity.get(i),
//                        mProductPrices.get(i),
//                        mProductExpDate.get(i));
//
//                showList.add(tmp);
//            }
//        } else {
//            for (int i = 0; i < mProductNames.size(); i++) {
//                ProductItem tmp = new ProductItem(mProductNames.get(i),
//                        mProductImages.get(i),
//                        null,
//                        mProductQuantity.get(i),
//                        mProductExpDate.get(i));
//
//                showList.add(tmp);
//            }
//        }
//
//        filterList = new ArrayList<>(showList);
//    }


    public ProductRecyclerViewAdapter(ArrayList<ProductItem> tmp, Context mContext) {
        this.mContext = mContext;
        showList = new ArrayList<>(tmp);
        filterList = new ArrayList<>(tmp);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item,
                viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .asBitmap()
                .load(showList.get(i).getURL())
                .into(viewHolder.productImage);

        viewHolder.productName.setText(showList.get(i).getName());

        viewHolder.productQuantity.setText(showList.get(i).getQuantity());

        viewHolder.productExpDate.setText(showList.get(i).getExpDate());

        if(showList.get(i).getPrice() == -1.2390)
            viewHolder.productPrice.setText("Fericirea");
        else
            viewHolder.productPrice.setText(Double.toString(showList.get(i).getPrice()) + " RON");

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(mContext);

                d.setContentView(R.layout.product_popup_layout);
                Button addProduct = d.getWindow().findViewById(R.id.addProductButton);
                Button changeDate = d.getWindow().findViewById(R.id.changeExpDateButton);
                Button changePrice = d.getWindow().findViewById(R.id.changePrice);

                addProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductItem item = new ProductItem();
                        item.setName(showList.get(i).getName());
                        item.setURL(showList.get(i).getURL());
                        item.setQuantity(showList.get(i).getQuantity());
                        item.setPrice(showList.get(i).getPrice());
                        item.setExpDate(showList.get(i).getExpDate());

                        ArrayList<ProductItem> tmp = loadFromFile(BASKET_FILE_NAME);
                        tmp.add(item);
                        saveBasketToFile(BASKET_FILE_NAME, tmp);

                        Toast.makeText(mContext, item.getName() + " adăugat", Toast.LENGTH_SHORT).show();
                    }
                });

                changeDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        final int day = calendar.get(Calendar.DAY_OF_MONTH);
                        new DatePickerDialog(mContext,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        String datestr;
                                        month++;
                                        if(dayOfMonth < 10) {
                                            datestr = "0" + Integer.toString(dayOfMonth) + ".";
                                        } else {
                                            datestr = Integer.toString(dayOfMonth) + ".";
                                        }
                                        if(month < 10) {
                                            datestr += "0" + Integer.toString(month) + ".";
                                        } else {
                                            datestr += Integer.toString(month) + ".";
                                        }
                                        datestr += Integer.toString(year);

                                        ProductDatabase productDB = OpenHelperManager.getHelper(mContext, ProductDatabase.class);
                                        RuntimeExceptionDao<Product, Integer> productDao = productDB.getProductRuntimeExceptionDao();

                                        UpdateBuilder<Product, Integer> updateProductDao = productDao.updateBuilder();
                                        try {
                                            updateProductDao.where().eq("produs", showList.get(i).getName());
                                            updateProductDao.updateColumnValue("data", datestr);
                                            updateProductDao.update();
                                        } catch (SQLException e) {
                                            Log.e(TAG, "onDateSet: failed");
                                        }

                                        OpenHelperManager.releaseHelper();
                                    }
                                }, year, month, day).show();
                    }
                });

                changePrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                        alert.setTitle("Introduceți preț nou:");
                        alert.setMessage("Un număr despărțit prin . dacă este cazul");

                        final EditText input = new EditText(mContext);
                        alert.setView(input);

                        alert.setPositiveButton("Gata", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String value = input.getText().toString();

                                if(value.matches(Globals.getInstance().fpRegex)) {
                                    double newPrice = Double.parseDouble(value);

                                    ProductDatabase productDB = OpenHelperManager.getHelper(mContext, ProductDatabase.class);
                                    RuntimeExceptionDao<Product, Integer> productDao = productDB.getProductRuntimeExceptionDao();

                                    UpdateBuilder<Product, Integer> updateProductDao = productDao.updateBuilder();
                                    try {
                                        updateProductDao.where().eq("produs", showList.get(i).getName());
                                        updateProductDao.updateColumnValue("pret", newPrice);
                                        updateProductDao.update();
                                    } catch (SQLException e) {
                                        Log.e(TAG, "onDateSet: failed");
                                    }

                                    OpenHelperManager.releaseHelper();
                                }
                            }
                        });

                        alert.setNegativeButton("Renunță", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        alert.show();
                    }
                });

                d.show();
            }
        });
    }

    private ArrayList<ProductItem> loadFromFile(String FILENAME) {
        ArrayList<ProductItem> data = new ArrayList<>();
        FileInputStream fis;
        File file = new File(MainActivity.FILE_PATH + FILENAME);

        if (file.exists()) {
            try {
                fis = mContext.openFileInput(FILENAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String text;

                while ((text = br.readLine()) != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(text, ",");

                    String name = stringTokenizer.nextToken();
                    String URL = stringTokenizer.nextToken();
                    String cod = stringTokenizer.nextToken();
                    String quantity = stringTokenizer.nextToken();
                    String priceString = stringTokenizer.nextToken();
                    double price = 0;
                    if (priceString.matches(Globals.getInstance().fpRegex))
                        price = Double.parseDouble(priceString);
                    String Date = stringTokenizer.nextToken();

                    ProductItem item = new ProductItem(name, URL, cod, quantity, price, Date);
                    data.add(item);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private void saveBasketToFile(String FILENAME, ArrayList<ProductItem> data) {
        ArrayList<String> text = new ArrayList<>();
        for(ProductItem item : data) {
            String tmp = item.getName() + "," +
                    item.getURL() + "," +
                    item.getCod() + "," +
                    item.getQuantity() + "," +
                    item.getPrice() + "," +
                    item.getExpDate() + "\n";

            text.add(tmp);
        }

        FileOutputStream fos = null;
        File file = new File(MainActivity.FILE_PATH + FILENAME);

        try {
            if(!file.exists())
                if(!file.createNewFile())
                    Log.e(TAG, "saveBasketToFile: file creation failed" + FILENAME);

            fos = mContext.openFileOutput(FILENAME, MODE_PRIVATE);
            for(String str : text)
                fos.write(str.getBytes());
            Log.d(TAG, "onPause: Files saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductItem> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(ProductItem item : filterList) {
                    if(item.getName().toLowerCase().startsWith(filterPattern))
                        filteredList.add(item);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            showList.clear();
            showList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView productImage;
        TextView productName;
        TextView productExpDate;
        TextView productQuantity;
        TextView productPrice;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImageView);
            productName = itemView.findViewById(R.id.productNameView);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productPrice = itemView.findViewById(R.id.priceView);
            productExpDate = itemView.findViewById(R.id.productExpDateView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
