package id.web.go_cak.sewa;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin on 28/02/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    List<EndangeredItem> mItems;
    private Activity mContext;
    private FragmentTransaction fragmentTransaction;
    private TextView titleToolbar;
    private Toolbar sendToolbar;

    public GridAdapter(Activity contexts,FragmentTransaction fragmentTransaction,TextView titleToolbar,Toolbar sendToolbar) {
        super();
        mItems = new ArrayList<EndangeredItem>();
        EndangeredItem species = new EndangeredItem();
        species.setName("Antar Jemput");
        species.setThumbnail(R.drawable.bentor);
        mItems.add(species);

        species = new EndangeredItem();
        species.setName("Gocak Services");
        species.setThumbnail(R.drawable.paket);
        mItems.add(species);
        this.mContext = contexts;
        this.fragmentTransaction = fragmentTransaction;
        this.titleToolbar = titleToolbar;
        this.sendToolbar = sendToolbar;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;*/
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.grid_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EndangeredItem nature = mItems.get(position);
        holder.tvspecies.setText(nature.getName());
        holder.imgThumbnail.setImageResource(nature.getThumbnail());

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                /*if (isLongClick) {
                    Toast.makeText(mContext, "#" + position + " - " + mItems.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "#" + position + " - " + mItems.get(position), Toast.LENGTH_SHORT).show();
                }*/

                //fragmentTransaction.remove(R.id.fragment).commit();

                Fragment fragment = null;

                String i ;
                if(position==0) {
                    //fragment = Transport.newInstance(titleToolbar,sendToolbar);
                    fragment = new Transport();
                    i = "Transport";
                } else if(position==1) {
                    //fragment = Transport.newInstance(titleToolbar,sendToolbar);
                    Toast.makeText(mContext,"Fitur ini akan segera di lauch",Toast.LENGTH_LONG).show();
                    i = "kuliner";
                } else if(position==2) {
                    Toast.makeText(mContext,"Fitur ini akan segera di lauch",Toast.LENGTH_LONG).show();
                    //fragment = Transport.newInstance(titleToolbar,sendToolbar);
                    i = "paket";
                } else {
                    //fragment = Transport.newInstance(titleToolbar,sendToolbar);
                    Toast.makeText(mContext,"Fitur ini akan segera di lauch",Toast.LENGTH_LONG).show();
                    i = "jejak";
                }

                if (fragment != null) {
                    fragmentTransaction.replace(R.id.fragment, fragment, i);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(mContext, "Cooming soon", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        public ImageView imgThumbnail;
        public TextView tvspecies;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            tvspecies = (TextView)itemView.findViewById(R.id.tv_species);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getLayoutPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getLayoutPosition(), true);
            return true;
        }

    }
}