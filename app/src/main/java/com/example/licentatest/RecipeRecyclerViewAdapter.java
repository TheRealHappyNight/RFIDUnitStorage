package com.example.licentatest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "RecipeRecyclerViewAdapt";

    private Context mContext;

    private ArrayList<Recipe> mRecipes;
    private ArrayList<Recipe> mFilteredRecipes;
    private ArrayList<String> mReadys;

    public RecipeRecyclerViewAdapter(Context mContext, ArrayList<Recipe> tmp, ArrayList<String> ready) {
        this.mContext = mContext;
        mRecipes = new ArrayList<>(tmp);
        mFilteredRecipes = new ArrayList<>(mRecipes);
        mReadys = new ArrayList<>(ready);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_item,
                viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .asBitmap()
                .load(mRecipes.get(i).getImageURL())
                .into(viewHolder.recipeImage);

        viewHolder.recipeName.setText(mRecipes.get(i).getName());
        viewHolder.recipeDescription.setText(mRecipes.get(i).getDescription());

        if(!mReadys.isEmpty()) {
            if(mReadys.get(i).equals("Nu se poate prepara")) {
                viewHolder.recipeReady.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            } else {
                viewHolder.recipeReady.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            }
            viewHolder.recipeReady.setText(mReadys.get(i));
        }

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecipeReadingActivity.class);
                intent.putExtra("recipe", mRecipes.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    @Override
    public Filter getFilter() {
        return recipeFilter;
    }

    private Filter recipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(mFilteredRecipes);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Recipe item : mFilteredRecipes) {
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
            mRecipes.clear();
            mRecipes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView recipeImage;
        TextView recipeName;
        TextView recipeDescription;
        TextView recipeReady;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeImage = itemView.findViewById(R.id.recipeImageView);
            recipeName = itemView.findViewById(R.id.recipeNameView);
            recipeDescription = itemView.findViewById(R.id.recipeDescriptionView);
            recipeReady = itemView.findViewById(R.id.recipeReadyView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
         }
    }
}
